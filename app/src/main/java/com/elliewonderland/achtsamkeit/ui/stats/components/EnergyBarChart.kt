package com.elliewonderland.achtsamkeit.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

private data class EnergyGroup(val keys: List<String>, val label: String, val color: Color)

// "satisfied_tired" (Abend-Vollenergie) → Voll, "wired" (Körper müde aber Geist wach) → Mittel
private val energyGroups = listOf(
    EnergyGroup(listOf("full", "satisfied_tired"), "Voll",    Color(0xFF7DC27A)),
    EnergyGroup(listOf("medium", "wired"),         "Mittel",  Color(0xFFF2C57C)),
    EnergyGroup(listOf("low"),                     "Niedrig", Color(0xFFE8A07A)),
    EnergyGroup(listOf("empty"),                   "Leer",    Color(0xFFC4BED2)),
)

@Composable
fun EnergyBarChart(distribution: Map<String, Int>) {
    val groupCounts = energyGroups.map { group -> group.keys.sumOf { distribution[it] ?: 0 } }
    val maxValue    = groupCounts.maxOrNull()?.coerceAtLeast(1) ?: 1
    val hairColor   = AppTheme.colors.hair
    val hasAnyData  = groupCounts.any { it > 0 }

    if (!hasAnyData) {
        Text(
            text  = "Noch keine Einträge im gewählten Zeitraum.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.inkSoft,
        )
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val barCount    = energyGroups.size
            val slotWidth   = size.width / barCount
            val barWidth    = slotWidth * 0.5f
            val chartHeight = size.height

            drawLine(
                color       = hairColor,
                start       = Offset(0f, chartHeight),
                end         = Offset(size.width, chartHeight),
                strokeWidth = 1.dp.toPx(),
            )

            energyGroups.forEachIndexed { i, group ->
                val count     = groupCounts[i]
                val barHeight = (count.toFloat() / maxValue) * chartHeight
                val left      = i * slotWidth + (slotWidth - barWidth) / 2f
                val top       = chartHeight - barHeight

                drawRoundRect(
                    color        = group.color,
                    topLeft      = Offset(left, top),
                    size         = Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                )
            }
        }

        Spacer(Modifier.height(6.dp))

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
                        color     = AppTheme.colors.inkSoft,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text      = count.toString(),
                        style     = MaterialTheme.typography.labelMedium,
                        color     = AppTheme.colors.ink,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
