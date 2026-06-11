package com.elliewonderland.achtsamkeit.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MoodMonthCard(
    moodMonth: List<MoodPoint?>,
    moodTrendPct: Int?,
    today: LocalDate,
    onClick: () -> Unit,
) {
    val colors     = AppTheme.colors
    val todayIndex = today.dayOfMonth - 1
    val lastDay    = today.lengthOfMonth()
    val monthName  = today.format(DateTimeFormatter.ofPattern("MMMM", Locale.GERMAN))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, colors.hair, RoundedCornerShape(22.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 18.dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                "Stimmung · $monthName",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            if (moodTrendPct != null) {
                val sign = if (moodTrendPct >= 0) "+" else ""
                Text(
                    "$sign$moodTrendPct %",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.accent,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier              = Modifier.fillMaxWidth().height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment     = Alignment.Bottom,
        ) {
            for (i in 0 until lastDay) {
                val score    = moodMonth.getOrNull(i)?.score
                val fraction = if (score != null) (score / 100f).coerceAtLeast(0.09f) else 0.09f
                val barColor = when {
                    i == todayIndex -> colors.accent
                    i < todayIndex  -> colors.accent2
                    else            -> colors.surfaceAlt.copy(alpha = 0.55f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(3.dp))
                        .background(barColor)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("1.", style = MaterialTheme.typography.bodySmall, color = colors.inkSoft)
            Text(
                "heute · ${today.dayOfMonth}.",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.accent,
            )
            Text("$lastDay.", style = MaterialTheme.typography.bodySmall, color = colors.inkSoft)
        }
    }
}
