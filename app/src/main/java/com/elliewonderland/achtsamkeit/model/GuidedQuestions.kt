package com.elliewonderland.achtsamkeit.model

import kotlinx.serialization.Serializable

@Serializable
data class GuidedQuestions(
    val morning: List<String>,
    val evening: List<String>,
)
