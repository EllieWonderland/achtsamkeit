package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.model.Entry
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class HistoryRepository {

    private val db = Firebase.firestore

    suspend fun getEntries(userId: String, limit: Long = 50): List<Entry> {
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .limit(limit)
            .get().await()
        return snap.documents.mapNotNull { it.toEntry() }
    }

    suspend fun getEntriesByTag(userId: String, tag: String): List<Entry> {
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereArrayContains("tags", tag)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get().await()
        return snap.documents.mapNotNull { it.toEntry() }
    }

    suspend fun getEntryById(userId: String, entryId: String): Entry? {
        val doc = db.collection("users").document(userId)
            .collection("entries").document(entryId)
            .get().await()
        return doc.toEntry()
    }

    suspend fun updateEntry(userId: String, entryId: String, guidedAnswer: String, freeText: String) {
        db.collection("users").document(userId)
            .collection("entries").document(entryId)
            .update(mapOf("guided_answer" to guidedAnswer, "free_text" to freeText))
            .await()
    }

    suspend fun deleteEntry(userId: String, entryId: String) {
        db.collection("users").document(userId)
            .collection("entries").document(entryId)
            .delete().await()
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
        )
    }.getOrNull()
}
