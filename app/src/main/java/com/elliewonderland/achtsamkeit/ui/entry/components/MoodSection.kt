package com.elliewonderland.achtsamkeit.ui.entry.components

import androidx.compose.foundation.clickable
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
    "excitement" to "🌅 Vorfreude / Elan – Voller Tatendrang, motiviert & bereit für den Tag",
    "peace"      to "🍃 Gelassenheit / Frieden – Ruhig, zentriert & im Einklang mit mir",
    "tiredness"  to "☕ Trägheit / Erschöpfung – Noch sehr müde, schwerfällig, sehne mich nach Ruhe",
    "anxiety"    to "🌪️ Sorge / Anspannung – Unruhig, besorgt wegen anstehender Aufgaben oder Hürden",
    "melancholy" to "🌧️ Schwermut / Lustlosigkeit – Bedrückt, nachdenklich, mir fehlt gerade der Antrieb",
)

private val eveningOptions = listOf(
    "satisfaction" to "🥰 Zufriedenheit / Erfüllung – Dankbar für den Tag, glücklich mit kleinen Momenten",
    "relief"       to "🍃 Erleichterung / Entspannung – Der Tag ist geschafft, ich komme endlich zur Ruhe",
    "exhaustion"   to "🔋 Erschöpfung / Müdigkeit – Körperlich oder mental völlig ausgelaugt vom Tag",
    "overwhelmed"  to "🌀 Überforderung / Unruhe – Viele kreisende Gedanken, gestresst, kann schwer abschalten",
    "loneliness"   to "🌧️ Traurigkeit / Einsamkeit – Fühle mich missverstanden, allein gelassen oder melancholisch",
)

@Composable
fun MoodSection(type: String, selected: String, onSelect: (String) -> Unit) {
    val title = if (type == "morning") "Welches Gefühl begleitet mich heute Morgen?" else "Welches Grundgefühl hat meinen Tag heute dominiert?"
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
