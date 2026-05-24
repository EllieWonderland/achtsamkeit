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
    "full"   to "⚡ Voller Akku – Klarer Kopf, erholt und bereit für alles.",
    "medium" to "🔋 Solide Basis – Ganz okay, bereit anzufangen (aber erst mal einen Kaffee).",
    "low"    to "🪫 Im Schonmodus – Schwerfällig, Akku recht niedrig, ich gehe es langsam an.",
    "empty"  to "🚨 Komplett leer – Ausgelaugt, jede Bewegung kostet Kraft, brauche dringend Pausen.",
)

private val eveningOptions = listOf(
    "satisfied_tired" to "🌙 Zufrieden erschöpft – Angenehm müde nach einem produktiven oder ereignisreichen Tag.",
    "wired"           to "🔌 Unter Strom – Körperlich müde, aber mein Geist rattert noch und steht unter Spannung.",
    "low"             to "🪫 Im roten Bereich – Sehr geringe Restenergie, der Tag war anstrengend und kräftezehrend.",
    "empty"           to "🚨 Völlig ausgebrannt – Absolut leer, ich sehne mich nur noch nach Schlaf, Dunkelheit und Ruhe.",
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
