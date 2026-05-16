package com.elliewonderland.achtsamkeit.model

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteQuote(
    val id: String = "",
    val text: String = "",
    val savedAt: Long = 0L,
)
