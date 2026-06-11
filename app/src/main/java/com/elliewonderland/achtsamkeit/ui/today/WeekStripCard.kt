package com.elliewonderland.achtsamkeit.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

import java.time.LocalDate

@Composable
fun WeekStripCard(
    weekDays: List<WeekDayStatus>,
    completedCount: Int,
    maxCount: Int,
    title: String,
    onDayClick: (LocalDate) -> Unit,
) {
    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, colors.hair, RoundedCornerShape(22.dp))
            .background(colors.surface)
            .padding(vertical = 16.dp, horizontal = 14.dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            Text(
                "$completedCount / $maxCount",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                color = colors.accent,
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (day.isToday)
                                Modifier.background(colors.accent.copy(alpha = 0.12f))
                            else Modifier
                        )
                        .clickable { onDayClick(day.date) }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        day.dayLabel,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = colors.inkSoft,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        day.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize   = 14.sp,
                            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                        ),
                        color = colors.ink,
                    )
                    Spacer(Modifier.height(8.dp))
                    // Morning dot
                    DayDot(done = day.morningDone, isMorning = true)
                    Spacer(Modifier.height(3.dp))
                    // Evening dot
                    DayDot(done = day.eveningDone, isMorning = false)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.accent3)
            )
            Spacer(Modifier.width(4.dp))
            Text("Morgen", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = colors.inkSoft)
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.accent)
            )
            Spacer(Modifier.width(4.dp))
            Text("Abend", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = colors.inkSoft)
        }
    }
}

@Composable
private fun DayDot(done: Boolean, isMorning: Boolean) {
    val colors = AppTheme.colors
    val dotColor = if (isMorning) colors.accent3 else colors.accent

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (done)
                    Modifier.background(dotColor)
                else
                    Modifier
                        .background(colors.background)
                        .border(1.dp, colors.hair, RoundedCornerShape(4.dp))
            )
    )
}
