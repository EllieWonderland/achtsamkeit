package com.elliewonderland.achtsamkeit.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = Firebase.auth
    private val db   = Firebase.firestore

    suspend fun loginWithEmail(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        ensureUserDocument(auth.currentUser!!)
    }

    suspend fun registerWithEmail(email: String, password: String, name: String): Result<Unit> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
        ensureUserDocument(auth.currentUser!!, name)
    }

    suspend fun isOnboardingComplete(uid: String): Boolean {
        val snap = db.collection("users").document(uid).get().await()
        return snap.getBoolean("onboarding_complete") ?: false
    }

    suspend fun completeOnboarding(uid: String) {
        db.collection("users").document(uid)
            .update("onboarding_complete", true).await()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    fun logout() = auth.signOut()

    private suspend fun ensureUserDocument(user: FirebaseUser, name: String = "") {
        val doc  = db.collection("users").document(user.uid)
        val snap = doc.get().await()
        if (!snap.exists()) {
            doc.set(mapOf(
                "email"                to user.email,
                "display_name"         to (name.ifBlank { user.displayName ?: "" }),
                "created_at"           to FieldValue.serverTimestamp(),
                "onboarding_complete"  to false,
                "notification_morning" to "08:00",
                "notification_evening" to "21:00",
            )).await()
        }
    }
}
