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
