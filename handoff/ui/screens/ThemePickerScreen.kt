// handoff/ui/screens/ThemePickerScreen.kt
// ───────────────────────────────────────────────────────────────────────────
// In-app picker: 3 visual styles × 4 accent palettes = 12 themes.
//
// UX
//   - Top section: 3 chips for Variant (Hain / Velvet / Aura) with a live
//     mini-preview of the corresponding style.
//   - Bottom section: 4 swatch cards for Palette. Each card shows the three
//     accent colors as overlapping circles.
//   - A tap immediately commits to DataStore and re-themes the whole app
//     (because we collected the choice as state at the root).
//
// Drop this into your Profil/Settings tab.
// ───────────────────────────────────────────────────────────────────────────

package com.elliewonderland.achtsamkeit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ThemePickerScreen(
    current: ThemeChoice,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = AppTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        // ─── Header ───────────────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Aussehen",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            Text(
                "Dein Look",
                style = MaterialTheme.typography.displaySmall,
                color = colors.ink,
            )
        }

        // ─── Variant chooser ──────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "STIL",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Variant.entries.forEach { v ->
                    VariantCard(
                        variant = v,
                        selected = v == current.variant,
                        palette = current.palette,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch { ThemePreferences.setVariant(ctx, v) }
                        },
                    )
                }
            }
        }

        // ─── Palette chooser ──────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "FARBPALETTE",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            // 2x2 grid, no LazyGrid needed at this size
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Palette.SALBEI to Palette.LAVENDEL,
                    Palette.NEBEL  to Palette.PFIRSICH,
                ).forEach { (a, b) ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PaletteCard(a, current.palette == a, current.variant.isDark,
                            Modifier.weight(1f)) {
                            scope.launch { ThemePreferences.setPalette(ctx, a) }
                        }
                        PaletteCard(b, current.palette == b, current.variant.isDark,
                            Modifier.weight(1f)) {
                            scope.launch { ThemePreferences.setPalette(ctx, b) }
                        }
                    }
                }
            }
        }
    }
}

// ─── Variant card with miniature preview ────────────────────────────────────
@Composable
private fun VariantCard(
    variant: Variant,
    selected: Boolean,
    palette: Palette,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val ac = AppTheme.colors
    // Build preview colors using the same factory so the chip really shows
    // what the user is about to pick.
    val preview = remember(variant, palette) { buildAppColors(variant, palette) }

    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) ac.ink else ac.surface)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = ac.hair,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // mini preview swatch — bg + accent dot
        Box(
            Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(preview.background),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(preview.accent)
            )
        }
        Text(
            variant.displayName,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) ac.background else ac.ink,
        )
    }
}

// ─── Palette swatch card ────────────────────────────────────────────────────
@Composable
private fun PaletteCard(
    palette: Palette,
    selected: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val ac = AppTheme.colors
    // Pull the appropriate accent triplet directly.
    val (a1, a2, a3) = remember(palette, isDark) {
        // Reuse the same helper Theme.kt uses for assembly.
        // (Made internal so we can read it from this screen.)
        accentTriplet(palette, isDark)
    }

    Row(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ac.surface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) a1 else ac.hair,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Stacked offset swatches
        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(22.dp)
                    .offset(x = (-8).dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(a3)
            )
            Box(
                Modifier
                    .size(22.dp)
                    .offset(x = 8.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(a2)
            )
            Box(
                Modifier
                    .size(26.dp)
                    .offset(y = 6.dp)
                    .clip(CircleShape)
                    .background(a1)
            )
        }
        Text(
            palette.displayName,
            style = MaterialTheme.typography.titleMedium,
            color = ac.ink,
        )
    }
}

// Tiny re-export of the private accent triplet so this screen doesn't have
// to know about the Light/Dark members on each Palette object. Keep this
// in sync with Theme.kt's `accentLight`/`accentDark`.
private fun accentTriplet(palette: Palette, dark: Boolean): Triple<Color, Color, Color> {
    return if (dark) when (palette) {
        Palette.SALBEI   -> Triple(Salbei.DarkAccent,   Salbei.DarkAccent2,   Salbei.DarkAccent3)
        Palette.LAVENDEL -> Triple(Lavendel.DarkAccent, Lavendel.DarkAccent2, Lavendel.DarkAccent3)
        Palette.NEBEL    -> Triple(Nebel.DarkAccent,    Nebel.DarkAccent2,    Nebel.DarkAccent3)
        Palette.PFIRSICH -> Triple(Pfirsich.DarkAccent, Pfirsich.DarkAccent2, Pfirsich.DarkAccent3)
    } else when (palette) {
        Palette.SALBEI   -> Triple(Salbei.LightAccent,   Salbei.LightAccent2,   Salbei.LightAccent3)
        Palette.LAVENDEL -> Triple(Lavendel.LightAccent, Lavendel.LightAccent2, Lavendel.LightAccent3)
        Palette.NEBEL    -> Triple(Nebel.LightAccent,    Nebel.LightAccent2,    Nebel.LightAccent3)
        Palette.PFIRSICH -> Triple(Pfirsich.LightAccent, Pfirsich.LightAccent2, Pfirsich.LightAccent3)
    }
}
