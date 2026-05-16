package com.elliewonderland.achtsamkeit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.Palette
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.elliewonderland.achtsamkeit.ui.theme.ThemePreferences
import com.elliewonderland.achtsamkeit.ui.theme.Variant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initial = ThemeChoice(Variant.HAIN, Palette.SALBEI)
        setContent {
            val choice by ThemePreferences.flow(this).collectAsStateWithLifecycle(initial)
            AppTheme(variant = choice.variant, palette = choice.palette) {
                AppNavHost(choice)
            }
        }
    }
}

// Stub — wird in Phase 3 durch den echten NavHost ersetzt.
@androidx.compose.runtime.Composable
fun AppNavHost(choice: ThemeChoice) {
    // TODO Phase 3: Navigation einbauen
}
