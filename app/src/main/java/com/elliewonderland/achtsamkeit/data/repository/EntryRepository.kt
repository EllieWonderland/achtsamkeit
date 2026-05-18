package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.model.Entry
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EntryRepository {

    private val db = Firebase.firestore

    fun deriveTags(entry: Entry): List<String> {
        val tags = mutableListOf<String>()
        when (entry.mood) {
            "stress"  -> { tags.add("Stress"); tags.add("Angst") }
            "joy"     -> tags.add("Freude")
            "balance" -> tags.add("Ausgeglichenheit")
            "sadness" -> { tags.add("Traurigkeit"); tags.add("Trauer") }
        }
        when (entry.energyLevel) {
            "full"  -> tags.add("Energie")
            // "empty" nur als Stress taggen wenn auch Stimmung == Stress, sonst
            // bekommt jemand der erschöpft-aber-ruhig ist Stress-Sprüche ausgespielt
            "empty" -> if (entry.mood == "stress") tags.add("Stress")
        }
        if ("achievement" in entry.gratitudeAreas) tags.add("Dankbarkeit")
        if (entry.dayRating >= 3)                  tags.add("Dankbarkeit")
        if ("breathing" in entry.selfCare ||
            "outside"   in entry.selfCare)         tags.add("Selbstfürsorge")
        return tags.distinct()
    }

    suspend fun saveEntry(userId: String, entry: Entry): String {
        val now = System.currentTimeMillis()
        val map = mapOf(
            "type"               to entry.type,
            "created_at"         to now,
            "date_str"           to LocalDate.now().toString(),
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
        ref.set(map)  // fire-and-forget: ID wird lokal generiert, Firestore synct im Hintergrund
        updateStreak(userId)
        return ref.id
    }

    private suspend fun updateStreak(userId: String) {
        val today         = LocalDate.now().toString()
        val yesterday     = LocalDate.now().minusDays(1).toString()
        val currentMonth  = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val userRef       = db.collection("users").document(userId)
        val snap          = userRef.get().await()
        val lastDate      = snap.getString("last_entry_date") ?: ""
        val currentStreak = (snap.getLong("current_streak") ?: 0L).toInt()
        val freezeUsedMonth = snap.getString("streak_freeze_used_month") ?: ""

        val updates = when (lastDate) {
            today     -> mapOf("last_entry_date" to today, "current_streak" to currentStreak)
            yesterday -> mapOf("last_entry_date" to today, "current_streak" to currentStreak + 1)
            else -> {
                // Streak wäre gebrochen — prüfe ob Freeze noch verfügbar
                if (currentStreak > 0 && freezeUsedMonth != currentMonth) {
                    mapOf(
                        "last_entry_date"          to today,
                        "current_streak"           to currentStreak,
                        "streak_freeze_used_month" to currentMonth,
                    )
                } else {
                    mapOf("last_entry_date" to today, "current_streak" to 1)
                }
            }
        }
        userRef.set(updates, SetOptions.merge()).await()
    }

    suspend fun hasEntryToday(userId: String, type: String): Boolean {
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereEqualTo("type", type)
            .whereGreaterThan("created_at", startOfCurrentJournalDay())
            .get().await()
        return !snap.isEmpty
    }

    suspend fun getTodayEntry(userId: String, type: String): Entry? {
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereEqualTo("type", type)
            .whereGreaterThan("created_at", startOfCurrentJournalDay())
            .get().await()
        return snap.documents.firstOrNull()?.toEntry()
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

// Tagesbeginn um 4:00 Uhr: wer zwischen 0:00–3:59 Uhr einen Eintrag macht, gehört noch zum Vortag.
private fun startOfCurrentJournalDay(): Long {
    val now = LocalDateTime.now()
    val journalDate = if (now.hour < 4) now.toLocalDate().minusDays(1) else now.toLocalDate()
    return journalDate.atTime(4, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
