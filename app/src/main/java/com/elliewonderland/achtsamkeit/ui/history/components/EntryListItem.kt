package com.elliewonderland.achtsamkeit.ui.history.components

import androidx.compose.foundation.layout.Arrangement
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
import com.elliewonderland.achtsamkeit.model.Entry
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EntryListItem(entry: Entry, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatDate(entry.dateStr),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.inkSoft,
                )
                Text(
                    text = typeLabel(entry.type),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.accent,
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val emoji = moodEmoji(entry.mood)
                if (emoji.isNotEmpty()) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.width(8.dp))
                }
                val preview = entry.freeText.ifBlank { entry.guidedAnswer }
                Text(
                    text = if (preview.length > 60) "${preview.take(60)}…" else preview.ifBlank { "—" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.ink,
                    maxLines = 1,
                )
            }

            if (entry.tags.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = entry.tags.joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.inkSoft,
                )
            }
        }
    }
}

internal fun formatDate(dateStr: String): String {
    return runCatching {
        val date = LocalDate.parse(dateStr)
        date.format(DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale("de")))
    }.getOrDefault(dateStr)
}

private fun typeLabel(type: String): String = when (type) {
    "morning"        -> "☀️"
    "evening"        -> "🌙"
    "weekly_review"  -> "Wochenrückblick"
    "monthly_review" -> "Monatsrückblick"
    "yearly_review"  -> "Jahresrückblick"
    else             -> type
}

private fun moodEmoji(mood: String): String = when (mood) {
    "joy"     -> "☀️"
    "stress"  -> "🌩️"
    "balance" -> "🌿"
    "sadness" -> "🌧️"
    else      -> ""
}
