package com.elliewonderland.achtsamkeit.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

class ReviewRepository {
    private val db = Firebase.firestore

    suspend fun isWeeklyReviewUnlocked(userId: String): Boolean {
        val monday = LocalDate.now()
            .with(DayOfWeek.MONDAY)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val snap = db.collection("users").document(userId)
            .collection("entries")
            .whereGreaterThan("created_at", monday)
            .get().await()
        val dailyCount = snap.documents.count {
            it.getString("type") in listOf("morning", "evening")
        }
        return dailyCount >= 3
    }

    fun isMonthlyReviewUnlocked(): Boolean {
        val today = LocalDate.now()
        return today.dayOfMonth >= today.lengthOfMonth() - 6
    }

    suspend fun saveReview(userId: String, type: String, answers: List<Pair<String, String>>) {
        val text = answers.joinToString("\n\n") { (q, a) -> "$q\n${a.ifBlank { "—" }}" }
        val map = mapOf(
            "type"       to type,
            "created_at" to System.currentTimeMillis(),
            "date_str"   to LocalDate.now().toString(),
            "free_text"  to text,
        )
        db.collection("users").document(userId)
            .collection("entries").document()
            .set(map).await()
    }
}
