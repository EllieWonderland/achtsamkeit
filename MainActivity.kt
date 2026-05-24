// handoff/MainActivity.kt
// ───────────────────────────────────────────────────────────────────────────
// Skeleton showing how to wire the theme up at the root. Adapt to your
// existing MainActivity / Navigation setup.
// ───────────────────────────────────────────────────────────────────────────

package com.elliewonderland.achtsamkeit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.Palette
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.elliewonderland.achtsamkeit.ui.theme.ThemePreferences
import com.elliewonderland.achtsamkeit.ui.theme.Variant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initial = ThemeChoice(Variant.HAIN, Palette.SALBEI)
        setContent {
            val choice by ThemePreferences.flow(this).collectAsStateWithLifecycle(initial)
            AppTheme(variant = choice.variant, palette = choice.palette) {
                AppNavHost(choice)
            }
        }
    }
}

// Stub — replace with your NavHost / Scaffold + BottomNavigation.
@androidx.compose.runtime.Composable
fun AppNavHost(choice: ThemeChoice) {
    // TODO: real navigation
}
