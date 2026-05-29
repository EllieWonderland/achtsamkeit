package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
import com.elliewonderland.achtsamkeit.model.FavoriteQuote
import com.elliewonderland.achtsamkeit.model.Lifehack
import com.elliewonderland.achtsamkeit.model.Quote
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate

class QuoteRepository(private val loader: QuoteLoader) {
    private val db = Firebase.firestore

    suspend fun getOrPickQuoteOfDay(userId: String, tags: List<String>): Quote {
        val today  = LocalDate.now().toString()
        val snap   = db.collection("users").document(userId).get().await()
        val savedDate = snap.getString("quote_of_day_date") ?: ""
        val savedId   = snap.getString("quote_of_day_id")   ?: ""
        if (savedDate == today && savedId.isNotBlank()) {
            getQuoteById(savedId)?.let { return it }
        }
        val quote = pickQuote(userId, tags)
        runCatching {
            db.collection("users").document(userId).update(mapOf(
                "quote_of_day_date" to today,
                "quote_of_day_id"   to quote.id,
            )).await()
        }
        return quote
    }

    suspend fun pickQuote(userId: String, tags: List<String>): Quote {
        val allQuotes  = withContext(Dispatchers.IO) { loader.quotes }
        val cooldowns  = loadCooldowns(userId)
        val cooldownMs = 90L * 24 * 60 * 60 * 1000
        val now        = System.currentTimeMillis()

        val eligible = allQuotes.filter { q ->
            (now - (cooldowns[q.id] ?: 0L)) > cooldownMs
        }
        val matching = eligible.filter { q -> q.tags.any { it in tags } }
        val pool     = matching.ifEmpty { eligible }.ifEmpty { allQuotes }

        val picked = pool.random()
        saveCooldown(userId, picked.id)
        return picked
    }

    suspend fun getQuoteById(id: String): Quote? = withContext(Dispatchers.IO) {
        loader.quotes.firstOrNull { it.id == id }
    }

    suspend fun isFavorite(userId: String, quoteId: String): Boolean {
        return db.collection("users").document(userId)
            .collection("favorites").document(quoteId)
            .get().await().exists()
    }

    suspend fun toggleFavorite(userId: String, quote: Quote) {
        val ref = db.collection("users").document(userId)
            .collection("favorites").document(quote.id)
        if (ref.get().await().exists()) {
            ref.delete().await()
        } else {
            ref.set(mapOf(
                "saved_at"   to FieldValue.serverTimestamp(),
                "quote_text" to quote.text,
            )).await()
        }
    }

    suspend fun toggleFavoriteLifehack(userId: String, lifehack: Lifehack) {
        val ref = db.collection("users").document(userId)
            .collection("favorites").document(lifehack.id)
        if (ref.get().await().exists()) {
            ref.delete().await()
        } else {
            ref.set(mapOf(
                "saved_at"      to FieldValue.serverTimestamp(),
                "lifehack_text" to lifehack.text,
            )).await()
        }
    }

    suspend fun getFavorites(userId: String): List<FavoriteQuote> {
        val snap = db.collection("users").document(userId)
            .collection("favorites")
            .orderBy("saved_at", Query.Direction.DESCENDING)
            .get().await()
        return snap.documents.map { doc ->
            val isHack = doc.contains("lifehack_text")
            FavoriteQuote(
                id         = doc.id,
                text       = doc.getString(if (isHack) "lifehack_text" else "quote_text") ?: "",
                savedAt    = doc.getTimestamp("saved_at")?.toDate()?.time ?: 0L,
                isLifehack = isHack,
            )
        }
    }

    private suspend fun loadCooldowns(userId: String): Map<String, Long> {
        val snap = db.collection("users").document(userId)
            .collection("quote_cooldowns").get().await()
        return snap.documents.associate {
            it.id to (it.getTimestamp("shown_at")?.toDate()?.time ?: 0L)
        }
    }

    private suspend fun saveCooldown(userId: String, quoteId: String) {
        db.collection("users").document(userId)
            .collection("quote_cooldowns").document(quoteId)
            .set(mapOf("shown_at" to FieldValue.serverTimestamp())).await()
    }
}
