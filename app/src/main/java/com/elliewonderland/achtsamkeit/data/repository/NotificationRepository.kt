package com.elliewonderland.achtsamkeit.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val db = Firebase.firestore

    suspend fun getNotificationTimes(userId: String): Pair<String, String> {
        val snap = db.collection("users").document(userId).get().await()
        val morning = snap.getString("notification_morning") ?: "08:00"
        val evening = snap.getString("notification_evening") ?: "21:00"
        return Pair(morning, evening)
    }

    suspend fun saveNotificationTimes(userId: String, morning: String, evening: String) {
        db.collection("users").document(userId)
            .update(mapOf(
                "notification_morning" to morning,
                "notification_evening" to evening,
            )).await()
    }
}
