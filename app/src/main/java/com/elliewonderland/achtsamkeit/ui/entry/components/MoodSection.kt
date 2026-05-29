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
import com.elliewonderland.achtsamkeit.model.MoodKey
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

private val morningOptions = listOf(
    MoodKey.EXCITEMENT to "🌅 Vorfreude / Elan – Voller Tatendrang, motiviert & bereit für den Tag",
    MoodKey.PEACE      to "🍃 Gelassenheit / Frieden – Ruhig, zentriert & im Einklang mit mir",
    MoodKey.TIREDNESS  to "☕ Trägheit / Erschöpfung – Noch sehr müde, schwerfällig, sehne mich nach Ruhe",
    MoodKey.ANXIETY    to "🌪️ Sorge / Anspannung – Unruhig, besorgt wegen anstehender Aufgaben oder Hürden",
    MoodKey.MELANCHOLY to "🌧️ Schwermut / Lustlosigkeit – Bedrückt, nachdenklich, mir fehlt gerade der Antrieb",
)

private val eveningOptions = listOf(
    MoodKey.SATISFACTION to "🥰 Zufriedenheit / Erfüllung – Dankbar für den Tag, glücklich mit kleinen Momenten",
    MoodKey.RELIEF       to "🍃 Erleichterung / Entspannung – Der Tag ist geschafft, ich komme endlich zur Ruhe",
    MoodKey.EXHAUSTION   to "🔋 Erschöpfung / Müdigkeit – Körperlich oder mental völlig ausgelaugt vom Tag",
    MoodKey.OVERWHELMED  to "🌀 Überforderung / Unruhe – Viele kreisende Gedanken, gestresst, kann schwer abschalten",
    MoodKey.LONELINESS   to "🌧️ Traurigkeit / Einsamkeit – Fühle mich missverstanden, allein gelassen oder melancholisch",
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
