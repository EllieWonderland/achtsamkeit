package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.model.Entry
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime

class EntryRepository {

    private val db = Firebase.firestore

    fun deriveTags(entry: Entry): List<String> {
        val tags = mutableListOf<String>()

        // Stimmung → Tags (alte + neue Keys)
        when (entry.mood) {
            "stress", "anxiety", "overwhelmed"            -> { tags.add("Stress"); tags.add("Angst") }
            "joy", "excitement", "satisfaction", "relief" -> tags.add("Freude")
            "balance", "peace"                            -> tags.add("Ausgeglichenheit")
            "sadness", "melancholy", "loneliness"         -> { tags.add("Traurigkeit"); tags.add("Trauer") }
        }

        // Energie → Tags (neue Abend-Keys; "empty" nur bei Stress-Stimmung)
        when (entry.energyLevel) {
            "full", "satisfied_tired" -> tags.add("Energie")
            "empty" -> if (entry.mood in listOf("stress", "anxiety", "overwhelmed")) tags.add("Stress")
        }

        // Dankbarkeit → Tags (alte + neue Keys)
        val gratitudeKeys = entry.gratitudeAreas
        if ("achievement" in gratitudeKeys || "encounter" in gratitudeKeys ||
            "relations"   in gratitudeKeys || "opportunity" in gratitudeKeys) tags.add("Dankbarkeit")
        if ("self_compassion" in gratitudeKeys || "learning" in gratitudeKeys ||
            "comfort_received" in gratitudeKeys)                              tags.add("Selbstfürsorge")
        if (entry.dayRating >= 3) tags.add("Dankbarkeit")

        // Selbstfürsorge → Tags (alte + neue Keys)
        val selfCareKeys = entry.selfCare
        if ("breathing"  in selfCareKeys || "outside"   in selfCareKeys ||
            "stillness"  in selfCareKeys || "compassion" in selfCareKeys ||
            "release"    in selfCareKeys || "forgiveness" in selfCareKeys) tags.add("Selbstfürsorge")

        // Schwere Tage → mitfühlende Sprüche (Trost, Selbstfürsorge, Traurigkeit)
        if ("struggled" in gratitudeKeys || "none" in gratitudeKeys ||
            "neglected" in selfCareKeys  || "no_energy" in selfCareKeys) {
            tags.add("Trost")
            tags.add("Selbstfürsorge")
            tags.add("Traurigkeit")
        }

        return tags.distinct()
    }

    suspend fun saveEntry(userId: String, entry: Entry): String {
        val now = System.currentTimeMillis()
        val journalDate = if (java.time.LocalDateTime.now().hour < 4) LocalDate.now().minusDays(1) else LocalDate.now()
        val map = mapOf(
            "type"               to entry.type,
            "created_at"         to now,
            "date_str"           to journalDate.toString(),
            "energy_level"       to entry.energyLevel,
            "mood"               to entry.mood,
            "gratitude_areas"    to entry.gratitudeAreas,
            "day_rating"         to entry.dayRating,
            "self_care"          to entry.selfCare,
            "mindfulness_focus"  to entry.mindfulnessFocus,
            "mindfulness_pause"  to entry.mindfulnessPause,
            "tags"               to deriveTags(entry),
            "guided_question"    to entry.guidedQuestion,
            "guided_answer"      to entry.guidedAnswer,
            "free_text"          to entry.freeText,
        )
        val ref = db.collection("users").document(userId)
            .collection("entries").document()
        ref.set(map).await()
        return ref.id
    }

    suspend fun hasEntryToday(userId: String, type: String): Boolean {
        val today = todayJournalDate()
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereEqualTo("date_str", today)
            .get().await()
        return snap.documents.mapNotNull { it.toEntry() }.any { it.type == type }
    }

    suspend fun getTodayEntry(userId: String, type: String): Entry? {
        val today = todayJournalDate()
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereEqualTo("date_str", today)
            .get().await()
        return snap.documents.mapNotNull { it.toEntry() }.firstOrNull { it.type == type }
    }

    suspend fun getEntriesForMonth(userId: String, year: Int, month: Int): List<Entry> {
        val prefix = "%04d-%02d".format(year, month)
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereGreaterThanOrEqualTo("date_str", "$prefix-01")
            .whereLessThanOrEqualTo("date_str", "$prefix-31")
            .get().await()
        return snap.documents.mapNotNull { it.toEntry() }
    }

    suspend fun getEntriesForWeek(userId: String, weekStart: LocalDate): List<Entry> {
        val weekEnd = weekStart.plusDays(6)
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereGreaterThanOrEqualTo("date_str", weekStart.toString())
            .whereLessThanOrEqualTo("date_str", weekEnd.toString())
            .get().await()
        return snap.documents.mapNotNull { it.toEntry() }
    }

    suspend fun updateEntryQuoteId(userId: String, entryId: String, quoteId: String) {
        db.collection("users").document(userId)
            .collection("entries").document(entryId)
            .update("quote_id", quoteId).await()
    }
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

private fun todayJournalDate(): String {
    val now = LocalDateTime.now()
    return (if (now.hour < 4) now.toLocalDate().minusDays(1) else now.toLocalDate()).toString()
}
