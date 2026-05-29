package com.elliewonderland.achtsamkeit.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elliewonderland.achtsamkeit.model.EnergyKey
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic

private data class EnergyGroup(val keys: List<String>, val label: String, val color: Color)

@Composable
fun EnergyBarChart(distribution: Map<String, Int>) {
    val colors = AppTheme.colors
    val energyGroups = remember(colors) {
        listOf(
            EnergyGroup(listOf(EnergyKey.FULL, EnergyKey.SATISFIED_TIRED), "Voll",    colors.accent),
            EnergyGroup(listOf(EnergyKey.MEDIUM, EnergyKey.WIRED),         "Mittel",  colors.accent2),
            EnergyGroup(listOf(EnergyKey.LOW),                              "Niedrig", colors.accent3),
            EnergyGroup(listOf(EnergyKey.EMPTY),                            "Leer",    colors.inkSoft.copy(alpha = 0.45f)),
        )
    }

    val groupCounts = remember(distribution, energyGroups) {
        energyGroups.map { group -> group.keys.sumOf { distribution[it] ?: 0 } }
    }
    val maxValue   = remember(groupCounts) { groupCounts.maxOrNull()?.coerceAtLeast(1) ?: 1 }
    val hairColor  = colors.hair
    val inkSoft    = colors.inkSoft
    val hasAnyData = remember(groupCounts) { groupCounts.any { it > 0 } }

    if (!hasAnyData) {
        Text(
            text  = "Noch keine Einträge im gewählten Zeitraum.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.inkSoft,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(vertical = 10.dp)
        ) {
            val barCount    = energyGroups.size
            val slotWidth   = size.width / barCount
            val barWidth    = slotWidth * 0.45f
            val chartHeight = size.height

            // Elegant horizontal dotted/dashed gridlines at 0%, 50%, 100% of height
            val gridLevels = listOf(0.0f, 0.5f, 1.0f)
            gridLevels.forEach { ratio ->
                val y = chartHeight * (1f - ratio)
                drawLine(
                    color       = hairColor,
                    start       = Offset(0f, y),
                    end         = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect  = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                )
            }

            energyGroups.forEachIndexed { i, group ->
                val count     = groupCounts[i]
                if (count > 0) {
                    val barHeight = (count.toFloat() / maxValue) * chartHeight
                    val left      = i * slotWidth + (slotWidth - barWidth) / 2f
                    val top       = chartHeight - barHeight

                    // Subtle premium vertical gradient brush
                    val gradientBrush = Brush.verticalGradient(
                        colors = listOf(group.color, group.color.copy(alpha = 0.55f)),
                        startY = top,
                        endY   = chartHeight
                    )

                    drawRoundRect(
                        brush        = gradientBrush,
                        topLeft      = Offset(left, top),
                        size         = Size(barWidth, barHeight.coerceAtLeast(6.dp.toPx())),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            energyGroups.forEachIndexed { i, group ->
                val count = groupCounts[i]
                Column(
                    modifier            = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text      = group.label,
                        style     = MaterialTheme.typography.labelSmall,
                        color     = inkSoft,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text      = count.toString(),
                        style     = SerifItalic.copy(fontSize = 18.sp),
                        color     = colors.ink,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

