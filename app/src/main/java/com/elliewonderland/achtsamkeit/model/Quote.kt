package com.elliewonderland.achtsamkeit.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val tags: List<String>,
)
