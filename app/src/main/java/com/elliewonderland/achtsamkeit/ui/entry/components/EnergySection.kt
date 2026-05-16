package com.elliewonderland.achtsamkeit.ui.entry.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

private val morningOptions = listOf(
    "full"   to "Akku voll – ich starte mit voller Kraft.",
    "medium" to "Solides Mittelfeld.",
    "low"    to "Im Energiesparmodus.",
    "empty"  to "Komplett leer – ich brauche Ruhe.",
)

private val eveningOptions = listOf(
    "full"   to "Akku noch voll – ein produktiver Tag.",
    "medium" to "Solides Mittelfeld.",
    "low"    to "Im Energiesparmodus.",
    "empty"  to "Komplett leer – ich brauche dringend Erholung.",
)

@Composable
fun EnergySection(type: String, selected: String, onSelect: (String) -> Unit) {
    val title = if (type == "morning") "Wie starte ich heute in den Tag?" else "Wie fühle ich mich jetzt nach dem Tag?"
    val options = if (type == "morning") morningOptions else eveningOptions
    SectionCard(title = title) {
        options.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(value) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selected == value,
                    onClick  = { onSelect(value) },
                    colors   = RadioButtonDefaults.colors(selectedColor = AppTheme.colors.accent),
                )
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.ink)
            }
        }
    }
}
