package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.model.Entry
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class StatsRepository {

    private val db = Firebase.firestore

    suspend fun getEntries(userId: String, days: Int): List<Entry> {
        val since = System.currentTimeMillis() - days * 86_400_000L
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereGreaterThan("created_at", since)
            .get().await()
        return snap.documents.mapNotNull { it.toEntry() }
    }

    suspend fun getMoodDistribution(userId: String, days: Int): Map<String, Int> {
        val entries = getEntries(userId, days)
        return entries.groupingBy { it.mood }.eachCount().filter { it.key.isNotEmpty() }
    }

    suspend fun getGratitudeDistribution(userId: String, days: Int): Map<String, Int> {
        val entries = getEntries(userId, days)
        val counts = mutableMapOf<String, Int>()
        entries.forEach { entry ->
            entry.gratitudeAreas.forEach { area ->
                counts[area] = (counts[area] ?: 0) + 1
            }
        }
        return counts
    }

    suspend fun getEnergyDistribution(userId: String, days: Int): Map<String, Int> {
        val entries = getEntries(userId, days)
        return entries.groupingBy { it.energyLevel }.eachCount().filter { it.key.isNotEmpty() }
    }

    private fun DocumentSnapshot.toEntry(): Entry? = runCatching {
        Entry(
            id               = id,
            type             = getString("type") ?: "",
            createdAt        = getLong("created_at") ?: 0L,
            dateStr          = getString("date_str") ?: "",
            energyLevel      = getString("energy_level") ?: "",
            mood             = getString("mood") ?: "",
            gratitudeAreas   = (get("gratitude_areas") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            dayRating        = (getLong("day_rating") ?: 0L).toInt(),
            selfCare         = (get("self_care") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            mindfulnessFocus = getString("mindfulness_focus") ?: "",
            mindfulnessPause = getString("mindfulness_pause") ?: "",
            tags             = (get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            guidedQuestion   = getString("guided_question") ?: "",
            guidedAnswer     = getString("guided_answer") ?: "",
            freeText         = getString("free_text") ?: "",
            quoteId          = getString("quote_id") ?: "",
        )
    }.getOrNull()
}

