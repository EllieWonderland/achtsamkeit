package com.elliewonderland.achtsamkeit.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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

    suspend fun loginWithGoogle(idToken: String): Result<Unit> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
        ensureUserDocument(auth.currentUser!!)
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

    suspend fun getUserPhotoUrl(userId: String): String {
        val snap = db.collection("users").document(userId).get().await()
        return snap.getString("photo_url") ?: ""
    }

    suspend fun updatePhotoUrl(userId: String, url: String) {
        db.collection("users").document(userId)
            .update("photo_url", url).await()
    }

    suspend fun getPhotoCropParams(userId: String): Triple<Float, Float, Float> {
        val snap = db.collection("users").document(userId).get().await()
        val scale = snap.getDouble("photo_scale")?.toFloat() ?: 1.0f
        val x = snap.getDouble("photo_offset_x")?.toFloat() ?: 0.0f
        val y = snap.getDouble("photo_offset_y")?.toFloat() ?: 0.0f
        return Triple(scale, x, y)
    }

    suspend fun updatePhotoCropParams(userId: String, scale: Float, x: Float, y: Float) {
        db.collection("users").document(userId).update(mapOf(
            "photo_scale" to scale,
            "photo_offset_x" to x,
            "photo_offset_y" to y
        )).await()
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

    suspend fun getUserProfile(userId: String): Map<String, Boolean> {
        val snap = db.collection("users").document(userId).get().await()
        val profileField = snap.get("profile") as? Map<*, *>
        return profileField?.mapNotNull { (k, v) ->
            if (k != null) k.toString() to (v as? Boolean ?: false) else null
        }?.toMap() ?: emptyMap()
    }

    suspend fun saveUserProfile(userId: String, profile: Map<String, Boolean>) {
        db.collection("users").document(userId)
            .update("profile", profile).await()
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
                "profile"                   to emptyMap<String, Boolean>(),
            )).await()
        }
    }
}
