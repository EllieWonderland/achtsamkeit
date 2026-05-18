package com.elliewonderland.achtsamkeit.ui.heute

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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

@Composable
fun StreakPill(streak: Int) {
    val colors = AppTheme.colors
    val label  = if (streak == 1) "1 Tag in Folge" else "$streak Tage in Folge"

    Row(
        modifier = Modifier
            .wrapContentWidth(Alignment.Start)
            .clip(RoundedCornerShape(999.dp))
            .background(colors.accent.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment      = Alignment.CenterVertically,
        horizontalArrangement  = Arrangement.spacedBy(8.dp),
    ) {
        Text("🔥", fontSize = 15.sp)
        Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp), color = colors.ink)
        Text("· dranbleiben", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium, fontSize = 13.sp), color = colors.inkSoft)
    }
}
