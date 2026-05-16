// handoff/ui/theme/Shape.kt
// ───────────────────────────────────────────────────────────────────────────
// Shape tokens. The design uses generous rounding — pill buttons (radius 16),
// rounded cards (radius 22), and circle wrappers for icons.
// ───────────────────────────────────────────────────────────────────────────

package com.elliewonderland.achtsamkeit.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),    // chips, pills
    small      = RoundedCornerShape(12.dp),   // small buttons
    medium     = RoundedCornerShape(16.dp),   // primary buttons, search bar
    large      = RoundedCornerShape(22.dp),   // cards, modals
    extraLarge = RoundedCornerShape(28.dp),   // hero cards, sheets
)

object AppRadius {
    val Chip   = 14
    val Button = 16
    val Card   = 22
    val Hero   = 28
    val Circle = 1000   // pill / circle
}
