package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.data.local.LifehackLoader
import com.elliewonderland.achtsamkeit.model.Lifehack
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class LifehackRepository(private val loader: LifehackLoader) {
    private val db = Firebase.firestore

    suspend fun getOrPickLifehackOfDay(userId: String): Lifehack? {
        val today = LocalDate.now().toString()
        val snap = db.collection("users").document(userId).get().await()
        val savedDate = snap.getString("lifehack_of_day_date") ?: ""
        val savedId = snap.getString("lifehack_of_day_id") ?: ""

        if (savedDate == today && savedId.isNotBlank()) {
            getLifehackById(savedId)?.let { return it }
        }

        val lifehack = pickLifehack(userId) ?: return null

        runCatching {
            db.collection("users").document(userId).update(mapOf(
                "lifehack_of_day_date" to today,
                "lifehack_of_day_id" to lifehack.id
            )).await()
        }
        return lifehack
    }

    suspend fun dislikeLifehack(userId: String, lifehackId: String) {
        db.collection("users").document(userId)
            .collection("disliked_lifehacks").document(lifehackId)
            .set(mapOf("disliked_at" to FieldValue.serverTimestamp())).await()
    }

    suspend fun loadDislikedLifehacks(userId: String): Set<String> {
        val snap = db.collection("users").document(userId)
            .collection("disliked_lifehacks").get().await()
        return snap.documents.map { it.id }.toSet()
    }

    suspend fun pickLifehack(userId: String): Lifehack? {
        val allLifehacks = loader.lifehacks
        if (allLifehacks.isEmpty()) return null

        val disliked = loadDislikedLifehacks(userId)
        val profile = getUserProfile(userId)
        val activeSpecificKeys = profile.filterValues { it == true }.keys

        val pool = allLifehacks.filter { lh ->
            lh.id !in disliked && (lh.tags.all { it == "Alltag" || it == "Haushalt" } || lh.tags.any { tag ->
                val key = when (tag) {
                    "Arbeit" -> "arbeit"
                    "Mama" -> "mama"
                    "Alleinerziehend" -> "alleinerziehend"
                    "CareArbeit" -> "care_arbeit"
                    "Oma" -> "oma"
                    "Scheidung" -> "scheidung"
                    "Studium" -> "studium"
                    else -> null
                }
                key != null && key in activeSpecificKeys
            })
        }

        val finalPool = pool.ifEmpty { allLifehacks.filter { it.id !in disliked } }.ifEmpty { allLifehacks }
        if (finalPool.isEmpty()) return null

        return finalPool.random()
    }

    fun getLifehackById(id: String): Lifehack? =
        loader.lifehacks.firstOrNull { it.id == id }

    suspend fun getUserProfile(userId: String): Map<String, Boolean> {
        val snap = db.collection("users").document(userId).get().await()
        val profileField = snap.get("profile") as? Map<*, *>
        return profileField?.mapNotNull { (k, v) ->
            if (k != null) k.toString() to (v as? Boolean ?: false) else null
        }?.toMap() ?: emptyMap()
    }

    suspend fun saveUserProfile(userId: String, profile: Map<String, Boolean>) {
        db.collection("users").document(userId)
            .update("profile", profile).await()
    }
}
