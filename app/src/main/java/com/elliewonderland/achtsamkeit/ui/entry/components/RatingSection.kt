package com.elliewonderland.achtsamkeit.ui.entry.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

private val options = listOf(
    5 to "⭐⭐⭐⭐⭐\nGroßartig",
    3 to "⭐⭐⭐\nOkay",
    1 to "⭐\nMorgen besser",
)

@Composable
fun RatingSection(selected: Int, onSelect: (Int) -> Unit) {
    SectionCard(title = "Wie bewerte ich den heutigen Tag insgesamt?") {
        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            options.forEach { (rating, label) ->
                val isSelected = selected == rating
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = if (isSelected) AppTheme.colors.accent else AppTheme.colors.surfaceAlt,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clickable { onSelect(rating) },
                ) {
                    Column(
                        modifier            = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text      = label,
                            textAlign = TextAlign.Center,
                            style     = MaterialTheme.typography.bodySmall,
                            color     = if (isSelected) AppTheme.colors.onAccent else AppTheme.colors.ink,
                        )
                    }
                }
            }
        }
    }
}
