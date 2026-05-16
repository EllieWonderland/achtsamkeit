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

private data class EnergyBar(val key: String, val label: String, val color: Color)

private val energyBars = listOf(
    EnergyBar("full",   "Voll",    Color(0xFF7DC27A)),
    EnergyBar("medium", "Mittel",  Color(0xFFF2C57C)),
    EnergyBar("low",    "Niedrig", Color(0xFFE8A07A)),
    EnergyBar("empty",  "Leer",    Color(0xFFC4BED2)),
)

@Composable
fun EnergyBarChart(distribution: Map<String, Int>) {
    val maxValue   = energyBars.maxOf { distribution[it.key] ?: 0 }.coerceAtLeast(1)
    val hairColor  = AppTheme.colors.hair
    val hasAnyData = energyBars.any { (distribution[it.key] ?: 0) > 0 }

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
            val barCount    = energyBars.size
            val slotWidth   = size.width / barCount
            val barWidth    = slotWidth * 0.5f
            val chartHeight = size.height

            drawLine(
                color       = hairColor,
                start       = Offset(0f, chartHeight),
                end         = Offset(size.width, chartHeight),
                strokeWidth = 1.dp.toPx(),
            )

            energyBars.forEachIndexed { i, bar ->
                val count     = distribution[bar.key] ?: 0
                val barHeight = (count.toFloat() / maxValue) * chartHeight
                val left      = i * slotWidth + (slotWidth - barWidth) / 2f
                val top       = chartHeight - barHeight

                drawRoundRect(
                    color        = bar.color,
                    topLeft      = Offset(left, top),
                    size         = Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            energyBars.forEach { bar ->
                val count = distribution[bar.key] ?: 0
                Column(
                    modifier            = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text      = bar.label,
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
