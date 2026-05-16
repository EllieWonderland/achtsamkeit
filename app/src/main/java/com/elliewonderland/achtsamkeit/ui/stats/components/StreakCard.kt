package com.elliewonderland.achtsamkeit.ui.stats.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@Composable
fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceAlt),
    ) {
        Row(
            modifier          = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("🔥", style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text  = if (streak == 1) "1 Tag in Folge" else "$streak Tage in Folge",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppTheme.colors.ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = if (streak == 0) "Fang heute an!" else "Weiter so — du bist auf einem guten Weg!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.inkSoft,
                )
            }
        }
    }
}
