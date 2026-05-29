package com.elliewonderland.achtsamkeit.model

import kotlinx.serialization.Serializable

@Serializable
data class MindfulImpulse(
    val range: String,
    val text: String,
)
