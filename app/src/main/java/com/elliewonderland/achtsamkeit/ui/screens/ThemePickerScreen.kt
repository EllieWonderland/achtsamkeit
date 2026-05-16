package com.elliewonderland.achtsamkeit.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.data.repository.PremiumRepository
import com.elliewonderland.achtsamkeit.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ThemePickerScreen(
    current: ThemeChoice,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = AppTheme.colors

    var isPremium by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isPremium = PremiumRepository.isPremium() }

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
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

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "STIL",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Variant.entries.forEach { v ->
                    val isLocked = !isPremium && v != Variant.HAIN
                    VariantCard(
                        variant  = v,
                        selected = v == current.variant,
                        palette  = current.palette,
                        isLocked = isLocked,
                        modifier = Modifier.weight(1f),
                        onClick  = {
                            if (isLocked) {
                                showUpgradeDialog = true
                            } else {
                                scope.launch { ThemePreferences.setVariant(ctx, v) }
                            }
                        },
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "FARBPALETTE",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
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

    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = {
                Text("Premium-Stil", style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Text(
                    "Velvet und Aura sind Premium-Stile. Upgrade auf Premium, um alle Stile freizuschalten.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.inkSoft,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUpgradeDialog = false
                        scope.launch {
                            val activity = ctx as? Activity ?: return@launch
                            val success = PremiumRepository.purchase(activity)
                            if (success) isPremium = true
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.accent),
                ) {
                    Text("Upgrade")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpgradeDialog = false }) {
                    Text("Später")
                }
            },
        )
    }
}

@Composable
private fun VariantCard(
    variant: Variant,
    selected: Boolean,
    palette: Palette,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val ac = AppTheme.colors
    val preview = remember(variant, palette) { buildAppColors(variant, palette) }

    Box(modifier) {
        Column(
            Modifier
                .fillMaxWidth()
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

        if (isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(ac.accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Lock,
                    contentDescription = "Premium",
                    tint               = ac.onAccent,
                    modifier           = Modifier.size(13.dp),
                )
            }
        }
    }
}

@Composable
private fun PaletteCard(
    palette: Palette,
    selected: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val ac = AppTheme.colors
    val (a1, a2, a3) = remember(palette, isDark) { accentTriplet(palette, isDark) }

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
