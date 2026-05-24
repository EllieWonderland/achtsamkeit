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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic

@Composable
fun SelfCarePillarsList(distribution: Map<String, Int>) {
    val colors = AppTheme.colors
    val labels = remember {
        mapOf(
            "physical"        to "💧 Körper & Pflege",
            "boundaries"      to "🛑 Gesunde Grenzen",
            "digital_detox"   to "📱 Digitaler Schutz",
            "soul"            to "🎨 Seelennahrung",
            "stillness"       to "🧘 Ruhemomente",
            "compassion"      to "🕊️ Selbstmitgefühl",
            "no_energy"       to "🪫 Akzeptanz (keine Kraft)",
            
            "needs_met"       to "💧 Bedürfnisse geachtet",
            "boundaries_kept" to "🛑 Grenzen gesetzt",
            "unplugged"       to "📱 Abschaltzeit gegönnt",
            "joyful_moment"   to "🎨 Seelenbalsam",
            "release"         to "🌬️ Druck abgelassen",
            "forgiveness"     to "🕊️ Selbstvergebung",
            "neglected"       to "🚨 Akzeptanz (schwerer Tag)",
        )
    }

    val topItems = remember(distribution) {
        distribution.filterKeys { it.isNotEmpty() && labels.containsKey(it) }
            .toList()
            .sortedByDescending { it.second }
            .take(4)
    }

    if (topItems.isEmpty()) {
        Text(
            text  = "Noch keine Selbstfürsorge-Vorsätze eingetragen.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.inkSoft,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        return
    }

    val maxCount = remember(topItems) { topItems.firstOrNull()?.second?.coerceAtLeast(1) ?: 1 }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        topItems.forEach { (key, count) ->
            val label = labels[key] ?: key
            val progress = count.toFloat() / maxCount

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.ink,
                    )
                    Text(
                        text  = "$count mal",
                        style = SerifItalic.copy(fontSize = 16.sp),
                        color = colors.accent,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.surfaceAlt)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colors.accent)
                    )
                }
            }
        }
    }
}
