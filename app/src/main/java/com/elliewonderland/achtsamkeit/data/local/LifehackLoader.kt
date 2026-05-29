package com.elliewonderland.achtsamkeit.data.local

import android.content.Context
import com.elliewonderland.achtsamkeit.model.Lifehack
import kotlinx.serialization.json.Json

class LifehackLoader(private val context: Context) {
    val lifehacks: List<Lifehack> by lazy {
        runCatching {
            val raw = context.assets.open("lifehacks.json").bufferedReader().readText()
            Json.decodeFromString<List<Lifehack>>(raw)
        }.getOrDefault(emptyList())
    }
}
