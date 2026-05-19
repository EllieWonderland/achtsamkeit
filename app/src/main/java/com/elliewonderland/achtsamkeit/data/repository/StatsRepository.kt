package com.elliewonderland.achtsamkeit.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

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

    suspend fun getEnergyDistribution(userId: String, days: Int): Map<String, Int> {
        val since = System.currentTimeMillis() - days * 86_400_000L
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereGreaterThan("created_at", since)
            .get().await()
        return snap.documents
            .groupingBy { it.getString("energy_level") ?: "" }
            .eachCount()
            .filter { it.key.isNotEmpty() }
    }
}
