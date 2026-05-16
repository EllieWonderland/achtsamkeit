package com.elliewonderland.achtsamkeit.ui.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

val ALL_TAGS = listOf(
    "Stress",
    "Freude",
    "Ausgeglichenheit",
    "Traurigkeit",
    "Energie",
    "Dankbarkeit",
    "Selbstfürsorge",
)

@Composable
fun TagFilterChips(selectedTag: String?, onTagSelected: (String?) -> Unit) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = AppTheme.colors.accent,
        selectedLabelColor = AppTheme.colors.onAccent,
        labelColor = AppTheme.colors.inkSoft,
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedTag == null,
                onClick = { onTagSelected(null) },
                label = { Text("Alle", style = MaterialTheme.typography.labelMedium) },
                colors = chipColors,
            )
        }
        items(ALL_TAGS) { tag ->
            FilterChip(
                selected = selectedTag == tag,
                onClick = { onTagSelected(if (selectedTag == tag) null else tag) },
                label = { Text(tag, style = MaterialTheme.typography.labelMedium) },
                colors = chipColors,
            )
        }
    }
}
