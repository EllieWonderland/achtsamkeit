package com.elliewonderland.achtsamkeit.data.local

import android.content.Context
import com.elliewonderland.achtsamkeit.model.Quote
import kotlinx.serialization.json.Json

class QuoteLoader(private val context: Context) {
    val quotes: List<Quote> by lazy {
        val raw = context.assets.open("sprueche.json").bufferedReader().readText()
        Json.decodeFromString<List<Quote>>(raw)
    }
}
