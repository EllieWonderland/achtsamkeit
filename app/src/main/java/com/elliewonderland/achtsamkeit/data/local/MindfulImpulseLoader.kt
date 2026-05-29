package com.elliewonderland.achtsamkeit.data.local

import android.content.Context
import com.elliewonderland.achtsamkeit.model.MindfulImpulse
import kotlinx.serialization.json.Json

class MindfulImpulseLoader(private val context: Context) {
    val impulses: List<MindfulImpulse> by lazy {
        runCatching {
            val raw = context.assets.open("mindful_impulses.json").bufferedReader().readText()
            Json.decodeFromString<List<MindfulImpulse>>(raw)
        }.getOrDefault(emptyList())
    }

    fun getImpulseForRating(avgRating: Double): String {
        val list = impulses
        if (list.isEmpty()) {
            return "Nimm dir heute 3 Minuten Zeit, um einfach nur zu atmen. Kein Ziel, keine Leistung, nur du."
        }

        return when {
            avgRating <= 0.0 -> list.firstOrNull { it.range == "<=0.0" }?.text
            avgRating < 3.0 -> list.firstOrNull { it.range == "<3.0" }?.text
            avgRating < 4.0 -> list.firstOrNull { it.range == "<4.0" }?.text
            else -> list.firstOrNull { it.range == "else" }?.text
        } ?: list.firstOrNull { it.range == "else" }?.text ?: list.first().text
    }
}
