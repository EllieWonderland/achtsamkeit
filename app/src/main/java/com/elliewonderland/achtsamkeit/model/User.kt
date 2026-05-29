package com.elliewonderland.achtsamkeit.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val profile: Map<String, Boolean> = emptyMap(),
)
