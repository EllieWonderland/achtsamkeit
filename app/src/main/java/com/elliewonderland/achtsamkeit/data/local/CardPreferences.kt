package com.elliewonderland.achtsamkeit.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.cardDataStore by preferencesDataStore(name = "achtsam_cards")

data class CardConfig(val id: String, val visible: Boolean)

object CardPreferences {
    private val KEY_HEUTE_CARDS = stringPreferencesKey("heute_cards_config")
    private val KEY_STATS_CARDS = stringPreferencesKey("stats_cards_config")

    val defaultHeuteCards = listOf(
        CardConfig("mood_trend", true),
        CardConfig("quote", true),
        CardConfig("lifehack", true),
        CardConfig("routines", true),
        CardConfig("week_strip", true),
        CardConfig("reviews", true)
    )

    val defaultStatsCards = listOf(
        CardConfig("kompass", true),
        CardConfig("mood_dist", true),
        CardConfig("energy", true),
        CardConfig("focus", true),
        CardConfig("gratitude", true),
        CardConfig("self_care", true),
        CardConfig("impulse", true)
    )

    fun getHeuteCards(context: Context): Flow<List<CardConfig>> {
        return context.cardDataStore.data.map { prefs ->
            val str = prefs[KEY_HEUTE_CARDS]
            if (str.isNullOrBlank()) {
                defaultHeuteCards
            } else {
                parseConfig(str, defaultHeuteCards)
            }
        }
    }

    suspend fun saveHeuteCards(context: Context, cards: List<CardConfig>) {
        context.cardDataStore.edit { prefs ->
            prefs[KEY_HEUTE_CARDS] = serializeConfig(cards)
        }
    }

    fun getStatsCards(context: Context): Flow<List<CardConfig>> {
        return context.cardDataStore.data.map { prefs ->
            val str = prefs[KEY_STATS_CARDS]
            if (str.isNullOrBlank()) {
                defaultStatsCards
            } else {
                parseConfig(str, defaultStatsCards)
            }
        }
    }

    suspend fun saveStatsCards(context: Context, cards: List<CardConfig>) {
        context.cardDataStore.edit { prefs ->
            prefs[KEY_STATS_CARDS] = serializeConfig(cards)
        }
    }

    private fun parseConfig(str: String, defaults: List<CardConfig>): List<CardConfig> {
        val parts = str.split(",")
        val parsed = parts.mapNotNull { part ->
            val subParts = part.split(":")
            if (subParts.size == 2) {
                val id = subParts[0]
                val visible = subParts[1].toBooleanStrictOrNull() ?: true
                CardConfig(id, visible)
            } else null
        }
        val parsedIds = parsed.map { it.id }.toSet()
        val missing = defaults.filter { it.id !in parsedIds }
        return parsed + missing
    }

    private fun serializeConfig(cards: List<CardConfig>): String {
        return cards.joinToString(",") { "${it.id}:${it.visible}" }
    }
}
