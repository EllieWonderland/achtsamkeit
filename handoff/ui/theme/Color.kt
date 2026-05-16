// handoff/ui/theme/Color.kt
// ───────────────────────────────────────────────────────────────────────────
// Raw color tokens for the 3 visual styles × 4 accent palettes = 12 themes.
//
// File generated from the design canvas in `index.html`.
// Keep this file in sync with `design-tokens.json` — it is the single source
// of truth for both Compose and any future export pipeline.
//
// Naming convention
//   <Variant><Slot> = static base color for that variant
//   <Palette>Accent<n> = swappable accent for that named palette
//
// The 12 themes are assembled in Theme.kt by combining a variant base
// (Hain / Velvet / Aura) with a palette accent set (Salbei / Lavendel /
// Nebel / Pfirsich).
//
// Author: design handoff. Do not edit values by hand without updating
// design-tokens.json — re-run any token export to overwrite.
// ───────────────────────────────────────────────────────────────────────────

package com.elliewonderland.achtsamkeit.ui.theme

import androidx.compose.ui.graphics.Color

// ─── HAIN — light, paper-soft ──────────────────────────────────────────────
object HainColors {
    val Background    = Color(0xFFEEF1ED)
    val Surface       = Color(0xFFF7F9F5)
    val SurfaceAlt    = Color(0xFFE4E9E2)
    val Ink           = Color(0xFF2A322D)
    val InkSoft       = Color(0xFF6A7268)
    val Hair          = Color(0x1A2A322D) // 10 % over ink
}

// ─── VELVET — dark off-black ───────────────────────────────────────────────
object VelvetColors {
    val Background    = Color(0xFF1A1C22)
    val Surface       = Color(0xFF23262E)
    val SurfaceAlt    = Color(0xFF2D3038)
    val Ink           = Color(0xFFECE5D6)
    val InkSoft       = Color(0x9EECE5D6) // 62 %
    val Hair          = Color(0x1AECE5D6) // 10 %
}

// ─── AURA — light with gradient washes ─────────────────────────────────────
object AuraColors {
    val Background    = Color(0xFFF6F3FB)
    val Surface       = Color(0xFFFFFFFF)
    val SurfaceAlt    = Color(0xFFEEEBF5)
    val Ink           = Color(0xFF2A2440)
    val InkSoft       = Color(0xFF6E6884)
    val Hair          = Color(0x142A2440) // 8 %
}

// ─── ACCENT PALETTES — same family across all 3 variants ───────────────────
// Each palette has a `Light` and `Dark` variant so dark mode (Velvet) gets
// a slightly brighter accent for contrast.

object Salbei {
    val LightAccent   = Color(0xFF7C9180)
    val LightAccent2  = Color(0xFFA8B5C9)
    val LightAccent3  = Color(0xFFC4BED2)
    val DarkAccent    = Color(0xFFA8C0AC)
    val DarkAccent2   = Color(0xFFA8B5C9)
    val DarkAccent3   = Color(0xFFC4BED2)
}

object Lavendel {
    val LightAccent   = Color(0xFF9D8AC4)
    val LightAccent2  = Color(0xFFC4BED2)
    val LightAccent3  = Color(0xFFE7B5B5)
    val DarkAccent    = Color(0xFFC4B0E0)
    val DarkAccent2   = Color(0xFFC4BED2)
    val DarkAccent3   = Color(0xFFE7B5B5)
}

object Nebel {
    val LightAccent   = Color(0xFF7AA8BD)
    val LightAccent2  = Color(0xFFA8B5C9)
    val LightAccent3  = Color(0xFFC4BED2)
    val DarkAccent    = Color(0xFFA5C5D6)
    val DarkAccent2   = Color(0xFFA8B5C9)
    val DarkAccent3   = Color(0xFFC4BED2)
}

object Pfirsich {
    val LightAccent   = Color(0xFFD49A9A)
    val LightAccent2  = Color(0xFFE7B5B5)
    val LightAccent3  = Color(0xFFF2C57C)
    val DarkAccent    = Color(0xFFE7B5B5)
    val DarkAccent2   = Color(0xFFF2C57C)
    val DarkAccent3   = Color(0xFFC4A8DD)
}

// ─── MOOD COLORS — chart slots shared across all themes ────────────────────
// Used for stimmungs- / energy-categorisation in stats.
object MoodColors {
    val Joy     = Color(0xFFF2C57C) // freude / dankbarkeit
    val Calm    = Color(0xFF7C9180) // ausgeglichenheit
    val Mist    = Color(0xFFA8B5C9) // nachdenklich / abend
    val Soft    = Color(0xFFC4BED2) // traurig / sanft
}
