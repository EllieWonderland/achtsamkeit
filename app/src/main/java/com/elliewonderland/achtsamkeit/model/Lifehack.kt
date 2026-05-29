package com.elliewonderland.achtsamkeit.model

import kotlinx.serialization.Serializable

@Serializable
data class Lifehack(
    val id: String,
    val text: String,
    val tags: List<String>,
)
