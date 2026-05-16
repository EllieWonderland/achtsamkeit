package com.elliewonderland.achtsamkeit.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

object AppRadius {
    val Chip   = 14
    val Button = 16
    val Card   = 22
    val Hero   = 28
    val Circle = 1000
}
