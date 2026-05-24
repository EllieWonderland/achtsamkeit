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

private val morningOptions = listOf(
    "relations"       to "👥 Menschen & Beziehungen – Familie, enge Freunde, Liebe, eine treue Partnerschaft",
    "comfort"         to "🏡 Sicherheit & Komfort – Ein warmes Bett, ein sicheres Zuhause, Frieden, Privilegien",
    "health"          to "🩺 Gesundheit & Vitalität – Körperliche Gesundheit, atmen können, schmerzfreier Zustand",
    "nature"          to "🌲 Natur & Umgebung – Morgensonne, Vogelgezwitscher, frische Luft, Jahreszeiten",
    "opportunity"     to "🚀 Chancen & Neubeginn – Ein neuer Tag, die Möglichkeit zu lernen, zu arbeiten oder zu gestalten",
    "self_compassion" to "🌸 Selbstannahme & eigener Weg – Die eigene Resilienz, gemachte Fortschritte, Geduld mit sich selbst",
    "struggled"       to "🌧️ Dankbarkeit fällt mir heute schwer – Es gibt Tage, an denen alles grau ist. Das ist völlig okay.",
)

private val eveningOptions = listOf(
    "encounter"        to "💬 Wertvolle Begegnung – Ein tiefes Gespräch, ein Lächeln, unerwartete Hilfe, nette Gesten",
    "micro_joys"       to "☕ Kleine Alltagsfreuden – Ein gutes Essen, warme Dusche, Lieblingslied, gemütliche Decke",
    "achievement"      to "🏆 Erfolg & Fortschritt – Ein gelöstes Problem, etwas Erledigtes, Stolz auf das eigene Schaffen",
    "learning"         to "💡 Erkenntnis & Wachstum – Etwas Wichtiges gelernt (auch aus Fehlern oder schweren Zeiten)",
    "comfort_received" to "🛡️ Trost & Beistand – Ein sicherer Hafen, Mitgefühl erhalten, die Hürde wurde bewältigt",
    "connection"       to "🤝 Gelungenes Miteinander – Ein geklärtes Missverständnis, tiefe Verbundenheit mit Partner/Familie",
    "none"             to "🌧️ Keiner – mir fiel Dankbarkeit heute extrem schwer – Heute gab es keinen Lichtblick. Das ist okay.",
)

@Composable
fun GratitudeSection(type: String, selected: List<String>, onToggle: (String) -> Unit) {
    val title = if (type == "morning") "Wofür bin ich heute oder generell dankbar?" else "Aus welchem Bereich kam mein heutiger Dankbarkeits-Moment?"
    val options = if (type == "morning") morningOptions else eveningOptions
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
