package com.elliewonderland.achtsamkeit.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic

private data class FocusSegment(val key: String, val label: String, val color: Color)

@Composable
fun MindfulnessFocusChart(distribution: Map<String, Int>) {
    val colors = AppTheme.colors
    val segments = remember(colors) {
        listOf(
            FocusSegment("present",   "Im Jetzt",    colors.accent),
            FocusSegment("future",    "Zukunft",     colors.accent2),
            FocusSegment("past",      "Vergangenheit", colors.accent3),
            FocusSegment("autopilot", "Autopilot",   colors.inkSoft.copy(alpha = 0.45f)),
        )
    }

    val total = remember(distribution) { distribution.values.sum() }

    if (total == 0) {
        Text(
            text  = "Noch keine Einträge im gewählten Zeitraum.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.inkSoft,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surfaceAlt)
        ) {
            segments.forEach { segment ->
                val count = distribution[segment.key] ?: 0
                if (count > 0) {
                    val weight = count.toFloat() / total
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weight)
                            .background(segment.color)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            segments.forEach { segment ->
                val count = distribution[segment.key] ?: 0
                val pct = if (total > 0) (count * 100) / total else 0
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(segment.color)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = segment.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = colors.inkSoft,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$pct%",
                        style = SerifItalic.copy(fontSize = 16.sp),
                        color = colors.ink,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
