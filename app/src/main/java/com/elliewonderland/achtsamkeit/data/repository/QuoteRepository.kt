package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
import com.elliewonderland.achtsamkeit.model.Quote
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class QuoteRepository(private val loader: QuoteLoader) {
    private val db = Firebase.firestore

    suspend fun pickQuote(userId: String, tags: List<String>): Quote {
        val cooldowns  = loadCooldowns(userId)
        val cooldownMs = 90L * 24 * 60 * 60 * 1000
        val now        = System.currentTimeMillis()

        val eligible = loader.quotes.filter { q ->
            (now - (cooldowns[q.id] ?: 0L)) > cooldownMs
        }
        val matching = eligible.filter { q -> q.tags.any { it in tags } }
        val pool     = matching.ifEmpty { eligible }.ifEmpty { loader.quotes }

        val picked = pool.random()
        saveCooldown(userId, picked.id)
        return picked
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
