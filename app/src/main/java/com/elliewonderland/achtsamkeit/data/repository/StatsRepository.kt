package com.elliewonderland.achtsamkeit.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsRepository {

    private val db = Firebase.firestore

    suspend fun getMoodDistribution(userId: String, days: Int): Map<String, Int> {
        val since = System.currentTimeMillis() - days * 86_400_000L
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereGreaterThan("created_at", since)
            .get().await()
        return snap.documents
            .groupingBy { it.getString("mood") ?: "" }
            .eachCount()
            .filter { it.key.isNotEmpty() }
    }

    suspend fun getGratitudeDistribution(userId: String, days: Int): Map<String, Int> {
        val since = System.currentTimeMillis() - days * 86_400_000L
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereGreaterThan("created_at", since)
            .get().await()
        val counts = mutableMapOf<String, Int>()
        snap.documents.forEach { doc ->
            val areas = (doc.get("gratitude_areas") as? List<*>)
                ?.filterIsInstance<String>() ?: emptyList()
            areas.forEach { area -> counts[area] = (counts[area] ?: 0) + 1 }
        }
        return counts
    }

    suspend fun getCurrentStreak(userId: String): Int {
        val snap = db.collection("users").document(userId).get().await()
        return (snap.getLong("current_streak") ?: 0L).toInt()
    }

    suspend fun getEnergyDistribution(userId: String, days: Int): Map<String, Int> {
        val since = System.currentTimeMillis() - days * 86_400_000L
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereGreaterThan("created_at", since)
            .get().await()
        return snap.documents
            .groupingBy { it.getString("energy") ?: "" }
            .eachCount()
            .filter { it.key.isNotEmpty() }
    }

    suspend fun isStreakFreezeAvailableThisMonth(userId: String): Boolean {
        val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val snap = db.collection("users").document(userId).get().await()
        val freezeUsedMonth = snap.getString("streak_freeze_used_month") ?: ""
        return freezeUsedMonth != currentMonth
    }
}
