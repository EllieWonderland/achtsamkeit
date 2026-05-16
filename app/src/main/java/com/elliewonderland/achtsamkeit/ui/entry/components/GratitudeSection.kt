package com.elliewonderland.achtsamkeit.ui.entry.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

private val options = listOf(
    "people"      to "Mitmenschen (Familie, Freunde, nette Begegnung)",
    "body"        to "Mein Körper / Meine Gesundheit",
    "achievement" to "Ein kleiner Erfolg / Eine erledigte Aufgabe",
    "nature"      to "Die Natur (Sonne, frische Luft, Tiere)",
    "pleasure"    to "Ein Genussmoment (z. B. ein richtig guter Kaffee)",
)

@Composable
fun GratitudeSection(type: String, selected: List<String>, onToggle: (String) -> Unit) {
    val title = if (type == "morning") "Wofür bin ich heute oder generell dankbar?" else "Aus welchem Bereich kam mein heutiger Dankbarkeits-Moment?"
    SectionCard(title = title) {
        options.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(value) }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked         = value in selected,
                    onCheckedChange = { onToggle(value) },
                    colors          = CheckboxDefaults.colors(checkedColor = AppTheme.colors.accent),
                )
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.ink)
            }
        }
    }
}
