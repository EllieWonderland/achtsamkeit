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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.MoodColors

private data class MoodGroup(val keys: List<String>, val label: String, val color: Color)

// Mehrere Keys pro Gruppe — alte und neue Stimmungs-Keys werden zusammengefasst
private val moodGroups = listOf(
    MoodGroup(listOf("joy", "excitement", "satisfaction", "relief"), "Positiv",       MoodColors.Joy),
    MoodGroup(listOf("balance", "peace"),                            "Ausgegl.",       MoodColors.Calm),
    MoodGroup(listOf("stress", "anxiety", "overwhelmed"),            "Herausf.",       MoodColors.Mist),
    MoodGroup(listOf("sadness", "melancholy", "loneliness",
                     "tiredness", "exhaustion"),                     "Schwer",         MoodColors.Soft),
)

@Composable
fun MoodBarChart(distribution: Map<String, Int>) {
    val groupCounts = moodGroups.map { group -> group.keys.sumOf { distribution[it] ?: 0 } }
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
            val barCount    = moodGroups.size
            val slotWidth   = size.width / barCount
            val barWidth    = slotWidth * 0.5f
            val chartHeight = size.height

            drawLine(
                color       = hairColor,
                start       = Offset(0f, chartHeight),
                end         = Offset(size.width, chartHeight),
                strokeWidth = 1.dp.toPx(),
            )

            moodGroups.forEachIndexed { i, group ->
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
            moodGroups.forEachIndexed { i, group ->
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
