package com.elliewonderland.achtsamkeit.data.repository

import com.elliewonderland.achtsamkeit.data.local.LifehackLoader
import com.elliewonderland.achtsamkeit.model.Lifehack
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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

    suspend fun pickLifehack(userId: String): Lifehack? {
        val allLifehacks = loader.lifehacks
        if (allLifehacks.isEmpty()) return null

        val profile = getUserProfile(userId)
        val activeSpecificKeys = profile.filterValues { it == true }.keys

        val pool = allLifehacks.filter { lh ->
            lh.tags.all { it == "Alltag" || it == "Haushalt" } || lh.tags.any { tag ->
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
            }
        }

        if (pool.isEmpty()) return null

        val dayIndex = LocalDate.now().dayOfYear
        return pool[dayIndex % pool.size]
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
