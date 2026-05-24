package com.elliewonderland.achtsamkeit.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import kotlin.math.min

private data class GratitudeGroup(val keys: List<String>, val label: String, val color: Color)

// 7 übergeordnete Dankbarkeits-Säulen; alte Keys (people, body, pleasure) bleiben rückwärtskompatibel
private val gratitudeGroups = listOf(
    GratitudeGroup(
        listOf("relations", "encounter", "connection", "people"),
        "Beziehungen", Color(0xFF7C9180),
    ),
    GratitudeGroup(
        listOf("health", "body"),
        "Körper & Gesundheit", Color(0xFFA8B5C9),
    ),
    GratitudeGroup(
        listOf("achievement", "opportunity"),
        "Erfolg & Fortschritt", Color(0xFFF2C57C),
    ),
    GratitudeGroup(
        listOf("nature"),
        "Natur & Umgebung", Color(0xFFC4BED2),
    ),
    GratitudeGroup(
        listOf("comfort", "micro_joys", "pleasure"),
        "Genuss & Alltag", Color(0xFFE7B5B5),
    ),
    GratitudeGroup(
        listOf("self_compassion", "learning", "comfort_received"),
        "Selbstfürsorge & Erkenntnis", Color(0xFFE8C9A0),
    ),
    GratitudeGroup(
        listOf("struggled", "none"),
        "Schwere Tage", Color(0xFF9E9E9E),
    ),
)

@Composable
fun GratitudePieChart(distribution: Map<String, Int>) {
    val groupCounts = gratitudeGroups.map { group -> group.keys.sumOf { distribution[it] ?: 0 } }
    val total       = groupCounts.sum()

    if (total == 0) {
        Text(
            text  = "Noch keine Einträge im gewählten Zeitraum.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.inkSoft,
        )
        return
    }

    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val diameter   = min(size.width, size.height) * 0.9f
            val topLeft    = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize    = Size(diameter, diameter)
            var startAngle = -90f

            gratitudeGroups.forEachIndexed { i, group ->
                val count = groupCounts[i]
                val sweep = (count.toFloat() / total) * 360f
                if (sweep > 0f) {
                    drawArc(
                        color      = group.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter  = false,
                        topLeft    = topLeft,
                        size       = arcSize,
                        style      = Stroke(width = 28.dp.toPx()),
                    )
                    startAngle += sweep
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            gratitudeGroups.forEachIndexed { i, group ->
                val count = groupCounts[i]
                if (count > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(10.dp),
                            shape    = CircleShape,
                            color    = group.color,
                        ) {}
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text  = "${group.label} ($count)",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.inkSoft,
                        )
                    }
                }
            }
        }
    }
}
