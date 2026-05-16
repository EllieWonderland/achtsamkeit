// handoff/ui/theme/ThemePreferences.kt
// ───────────────────────────────────────────────────────────────────────────
// Persists the user's Variant + Palette choice in DataStore so it survives
// app restarts. Exposes a `Flow<ThemeChoice>` you can collectAsState() in
// the root composable.
//
// You'll need this in build.gradle.kts:
//   implementation("androidx.datastore:datastore-preferences:1.1.1")
//
// Usage in MainActivity:
//
//   val choice by ThemePreferences.flow(context).collectAsStateWithLifecycle(
//       initialValue = ThemeChoice(Variant.HAIN, Palette.SALBEI)
//   )
//   AppTheme(variant = choice.variant, palette = choice.palette) {
//       AppNavHost(choice)
//   }
//
// And from your settings screen, call:
//   scope.launch { ThemePreferences.setVariant(context, Variant.AURA) }
//   scope.launch { ThemePreferences.setPalette(context, Palette.LAVENDEL) }
// ───────────────────────────────────────────────────────────────────────────

package com.elliewonderland.achtsamkeit.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "achtsam_theme")

private val KEY_VARIANT = stringPreferencesKey("variant")
private val KEY_PALETTE = stringPreferencesKey("palette")

data class ThemeChoice(val variant: Variant, val palette: Palette)

object ThemePreferences {

    fun flow(context: Context): Flow<ThemeChoice> =
        context.themeDataStore.data.map { prefs ->
            val v = prefs[KEY_VARIANT]?.let { runCatching { Variant.valueOf(it) }.getOrNull() }
                ?: Variant.HAIN
            val p = prefs[KEY_PALETTE]?.let { runCatching { Palette.valueOf(it) }.getOrNull() }
                ?: Palette.SALBEI
            ThemeChoice(v, p)
        }

    suspend fun setVariant(context: Context, variant: Variant) {
        context.themeDataStore.edit { it[KEY_VARIANT] = variant.name }
    }

    suspend fun setPalette(context: Context, palette: Palette) {
        context.themeDataStore.edit { it[KEY_PALETTE] = palette.name }
    }

    suspend fun set(context: Context, choice: ThemeChoice) {
        context.themeDataStore.edit {
            it[KEY_VARIANT] = choice.variant.name
            it[KEY_PALETTE] = choice.palette.name
        }
    }
}
