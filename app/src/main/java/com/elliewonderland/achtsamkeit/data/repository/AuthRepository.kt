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
    fun getUserEmail(): String = auth.currentUser?.email ?: ""
    fun logout() = auth.signOut()

    suspend fun getUserDisplayName(userId: String): String {
        val snap = db.collection("users").document(userId).get().await()
        return snap.getString("display_name")
            ?: auth.currentUser?.email?.substringBefore("@")
            ?: ""
    }

    suspend fun updateDisplayName(userId: String, name: String) {
        db.collection("users").document(userId)
            .update("display_name", name.trim()).await()
    }

    suspend fun resetAllData(userId: String) {
        deleteSubcollection(userId, "entries")
        deleteSubcollection(userId, "quote_cooldowns")
        deleteSubcollection(userId, "favorites")
        db.collection("users").document(userId).update(mapOf(
            "current_streak"           to 0,
            "last_entry_date"          to "",
            "streak_freeze_used_month" to "",
        )).await()
    }

    suspend fun deleteAccount(userId: String) {
        deleteSubcollection(userId, "entries")
        deleteSubcollection(userId, "quote_cooldowns")
        deleteSubcollection(userId, "favorites")
        db.collection("users").document(userId).delete().await()
        auth.currentUser?.delete()?.await()
    }

    private suspend fun deleteSubcollection(userId: String, name: String) {
        val ref = db.collection("users").document(userId).collection(name)
        while (true) {
            val snap = ref.limit(500).get().await()
            if (snap.isEmpty) break
            val batch = db.batch()
            snap.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
            if (snap.size() < 500) break
        }
    }

    private suspend fun ensureUserDocument(user: FirebaseUser, name: String = "") {
        val doc  = db.collection("users").document(user.uid)
        val snap = doc.get().await()
        if (!snap.exists()) {
            doc.set(mapOf(
                "email"                     to user.email,
                "display_name"              to (name.ifBlank { user.displayName ?: "" }),
                "created_at"                to FieldValue.serverTimestamp(),
                "onboarding_complete"       to false,
                "notification_morning"      to "08:00",
                "notification_evening"      to "21:00",
                "current_streak"            to 0,
                "last_entry_date"           to "",
                "streak_freeze_used_month"  to "",
            )).await()
        }
    }
}
