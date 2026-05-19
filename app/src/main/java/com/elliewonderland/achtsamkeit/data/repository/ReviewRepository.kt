package com.elliewonderland.achtsamkeit.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate

class ReviewRepository {

    private val db = Firebase.firestore


    fun isWeeklyReviewUnlocked(): Boolean {
        val day = LocalDate.now().dayOfWeek
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
    }

    fun isMonthlyReviewUnlocked(): Boolean {
        val today = LocalDate.now()
        return today.dayOfMonth >= today.lengthOfMonth() - 6
    }

    fun isYearlyReviewUnlocked(): Boolean = LocalDate.now().monthValue == 12

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
