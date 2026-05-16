package com.elliewonderland.achtsamkeit.model

import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val id: String = "",
    val type: String = "",
    val createdAt: Long = 0L,
    val dateStr: String = "",
    val energyLevel: String = "",
    val mood: String = "",
    val gratitudeAreas: List<String> = emptyList(),
    val dayRating: Int = 0,
    val selfCare: List<String> = emptyList(),
    val mindfulnessFocus: String = "",
    val mindfulnessPause: String = "",
    val tags: List<String> = emptyList(),
    val guidedQuestion: String = "",
    val guidedAnswer: String = "",
    val freeText: String = "",
)
