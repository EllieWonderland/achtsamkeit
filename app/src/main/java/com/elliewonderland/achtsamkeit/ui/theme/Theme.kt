package com.elliewonderland.achtsamkeit.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Selection enums ────────────────────────────────────────────────────────

enum class Variant(val displayName: String, val isDark: Boolean) {
    HAIN  ("Hain",   false),
    VELVET("Velvet", true),
    AURA  ("Aura",   false),
}

enum class Palette(val displayName: String) {
    SALBEI  ("Salbei"),
    LAVENDEL("Lavendel"),
    NEBEL   ("Nebel"),
    PFIRSICH("Pfirsich"),
}

// ─── Extra tokens beyond Material 3 ────────────────────────────────────────
@Immutable
data class AppColors(
    val background: Color,
    val surface:    Color,
    val surfaceAlt: Color,
    val ink:        Color,
    val inkSoft:    Color,
    val hair:       Color,
    val accent:     Color,
    val accent2:    Color,
    val accent3:    Color,
    val onAccent:   Color,
    val moodJoy:    Color,
    val moodCalm:   Color,
    val moodMist:   Color,
    val moodSoft:   Color,
)

val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("AppColors not provided — wrap content in AppTheme { … }")
}

val LocalVariant = compositionLocalOf { Variant.HAIN }
val LocalPalette = compositionLocalOf { Palette.SALBEI }

object AppTheme {
    val colors: AppColors @Composable get() = LocalAppColors.current
    val variant: Variant   @Composable get() = LocalVariant.current
    val palette: Palette   @Composable get() = LocalPalette.current
}

// ─── Assembly ──────────────────────────────────────────────────────────────

private fun accentLight(p: Palette): Triple<Color, Color, Color> = when (p) {
    Palette.SALBEI   -> Triple(Salbei.LightAccent,   Salbei.LightAccent2,   Salbei.LightAccent3)
    Palette.LAVENDEL -> Triple(Lavendel.LightAccent, Lavendel.LightAccent2, Lavendel.LightAccent3)
    Palette.NEBEL    -> Triple(Nebel.LightAccent,    Nebel.LightAccent2,    Nebel.LightAccent3)
    Palette.PFIRSICH -> Triple(Pfirsich.LightAccent, Pfirsich.LightAccent2, Pfirsich.LightAccent3)
}

private fun accentDark(p: Palette): Triple<Color, Color, Color> = when (p) {
    Palette.SALBEI   -> Triple(Salbei.DarkAccent,   Salbei.DarkAccent2,   Salbei.DarkAccent3)
    Palette.LAVENDEL -> Triple(Lavendel.DarkAccent, Lavendel.DarkAccent2, Lavendel.DarkAccent3)
    Palette.NEBEL    -> Triple(Nebel.DarkAccent,    Nebel.DarkAccent2,    Nebel.DarkAccent3)
    Palette.PFIRSICH -> Triple(Pfirsich.DarkAccent, Pfirsich.DarkAccent2, Pfirsich.DarkAccent3)
}

fun buildAppColors(variant: Variant, palette: Palette): AppColors {
    val (a1, a2, a3) = if (variant.isDark) accentDark(palette) else accentLight(palette)
    return when (variant) {
        Variant.HAIN -> AppColors(
            background = HainColors.Background,
            surface    = HainColors.Surface,
            surfaceAlt = HainColors.SurfaceAlt,
            ink        = HainColors.Ink,
            inkSoft    = HainColors.InkSoft,
            hair       = HainColors.Hair,
            accent     = a1, accent2 = a2, accent3 = a3,
            onAccent   = HainColors.Ink,
            moodJoy    = MoodColors.Joy, moodCalm = MoodColors.Calm,
            moodMist   = MoodColors.Mist, moodSoft = MoodColors.Soft,
        )
        Variant.VELVET -> AppColors(
            background = VelvetColors.Background,
            surface    = VelvetColors.Surface,
            surfaceAlt = VelvetColors.SurfaceAlt,
            ink        = VelvetColors.Ink,
            inkSoft    = VelvetColors.InkSoft,
            hair       = VelvetColors.Hair,
            accent     = a1, accent2 = a2, accent3 = a3,
            onAccent   = VelvetColors.Background,
            moodJoy    = MoodColors.Joy, moodCalm = MoodColors.Calm,
            moodMist   = MoodColors.Mist, moodSoft = MoodColors.Soft,
        )
        Variant.AURA -> AppColors(
            background = AuraColors.Background,
            surface    = AuraColors.Surface,
            surfaceAlt = AuraColors.SurfaceAlt,
            ink        = AuraColors.Ink,
            inkSoft    = AuraColors.InkSoft,
            hair       = AuraColors.Hair,
            accent     = a1, accent2 = a2, accent3 = a3,
            onAccent   = Color.White,
            moodJoy    = MoodColors.Joy, moodCalm = MoodColors.Calm,
            moodMist   = MoodColors.Mist, moodSoft = MoodColors.Soft,
        )
    }
}

fun AppColors.toColorScheme(isDark: Boolean): ColorScheme {
    val scheme = if (isDark) darkColorScheme() else lightColorScheme()
    return scheme.copy(
        primary            = accent,
        onPrimary          = onAccent,
        primaryContainer   = accent.copy(alpha = 0.18f).compositeOver(surface),
        onPrimaryContainer = ink,
        secondary          = accent2,
        onSecondary        = ink,
        tertiary           = accent3,
        onTertiary         = ink,
        background         = background,
        onBackground       = ink,
        surface            = surface,
        onSurface          = ink,
        surfaceVariant     = surfaceAlt,
        onSurfaceVariant   = inkSoft,
        outline            = hair,
        outlineVariant     = hair,
    )
}

private fun Color.compositeOver(bg: Color): Color {
    val a = alpha
    return Color(
        red   = red   * a + bg.red   * (1 - a),
        green = green * a + bg.green * (1 - a),
        blue  = blue  * a + bg.blue  * (1 - a),
        alpha = 1f,
    )
}

// ─── The provider you wrap your app in ─────────────────────────────────────
@Composable
fun AppTheme(
    variant: Variant = Variant.HAIN,
    palette: Palette = Palette.SALBEI,
    content: @Composable () -> Unit,
) {
    val colors = buildAppColors(variant, palette)
    val scheme = colors.toColorScheme(isDark = variant.isDark)

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalVariant   provides variant,
        LocalPalette   provides palette,
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography  = AppTypography,
            shapes      = AppShapes,
            content     = content,
        )
    }
}
