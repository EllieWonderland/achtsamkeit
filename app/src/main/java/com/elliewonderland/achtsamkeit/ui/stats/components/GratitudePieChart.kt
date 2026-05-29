package com.elliewonderland.achtsamkeit.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elliewonderland.achtsamkeit.model.GratitudeKey
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import kotlin.math.min

private data class GratitudeGroup(val keys: List<String>, val label: String, val color: Color)

@Composable
fun GratitudePieChart(distribution: Map<String, Int>) {
    val colors = AppTheme.colors
    val gratitudeGroups = remember(colors) {
        listOf(
            GratitudeGroup(
                listOf(GratitudeKey.RELATIONS, GratitudeKey.ENCOUNTER, GratitudeKey.CONNECTION, GratitudeKey.PEOPLE),
                "Beziehungen & Begegnungen", colors.accent,
            ),
            GratitudeGroup(
                listOf(GratitudeKey.HEALTH, GratitudeKey.BODY),
                "Gesundheit & Körper", colors.accent2,
            ),
            GratitudeGroup(
                listOf(GratitudeKey.ACHIEVEMENT, GratitudeKey.OPPORTUNITY),
                "Erfolg & Chancen", colors.accent3,
            ),
            GratitudeGroup(
                listOf(GratitudeKey.NATURE),
                "Natur & Umgebung", colors.accent.copy(alpha = 0.65f),
            ),
            GratitudeGroup(
                listOf(GratitudeKey.COMFORT, GratitudeKey.MICRO_JOYS, GratitudeKey.PLEASURE),
                "Genuss & Alltagsfreuden", colors.accent2.copy(alpha = 0.65f),
            ),
            GratitudeGroup(
                listOf(GratitudeKey.SELF_COMPASSION, GratitudeKey.LEARNING, GratitudeKey.COMFORT_RECEIVED),
                "Selbstfürsorge & Erkenntnis", colors.accent3.copy(alpha = 0.65f),
            ),
            GratitudeGroup(
                listOf(GratitudeKey.STRUGGLED, GratitudeKey.NONE),
                "Schwere Tage", colors.inkSoft.copy(alpha = 0.45f),
            ),
        )
    }

    val groupCounts = remember(distribution, gratitudeGroups) {
        gratitudeGroups.map { group -> group.keys.sumOf { distribution[it] ?: 0 } }
    }
    val total = remember(groupCounts) { groupCounts.sum() }
    val inkSoft = colors.inkSoft

    if (total == 0) {
        Text(
            text  = "Noch keine Einträge im gewählten Zeitraum.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.inkSoft,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        return
    }

    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(140.dp)) {
                val diameter   = min(size.width, size.height) * 0.95f
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
                            style      = Stroke(width = 24.dp.toPx()),
                        )
                        startAngle += sweep
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text  = total.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = colors.ink,
                    lineHeight = 28.sp
                )
                Text(
                    text  = if (total == 1) "Moment" else "Momente",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = inkSoft,
                )
            }
        }

        Spacer(Modifier.width(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            gratitudeGroups.forEachIndexed { i, group ->
                val count = groupCounts[i]
                if (count > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier.size(10.dp),
                            shape    = CircleShape,
                            color    = group.color,
                        ) {}
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text      = "${group.label} ($count)",
                            style     = MaterialTheme.typography.bodySmall,
                            color     = colors.ink,
                            modifier  = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

