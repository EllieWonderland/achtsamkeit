package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.model.Entry
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId

class EntryRepository {

    private val db = Firebase.firestore

    fun deriveTags(entry: Entry): List<String> {
        val tags = mutableListOf<String>()
        when (entry.mood) {
            "stress"  -> tags.add("Stress")
            "joy"     -> tags.add("Freude")
            "balance" -> tags.add("Ausgeglichenheit")
            "sadness" -> tags.add("Traurigkeit")
        }
        when (entry.energyLevel) {
            "full"  -> tags.add("Energie")
            "empty" -> tags.add("Stress")
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
        ref.set(map).await()
        return ref.id
    }

    suspend fun hasEntryToday(userId: String, type: String): Boolean {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereEqualTo("type", type)
            .whereGreaterThan("created_at", startOfDay)
            .get().await()
        return !snap.isEmpty
    }
}
