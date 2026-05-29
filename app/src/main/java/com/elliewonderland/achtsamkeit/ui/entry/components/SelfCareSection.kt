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
import com.elliewonderland.achtsamkeit.model.SelfCareKey
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

private val morningOptions = listOf(
    SelfCareKey.PHYSICAL      to "💧 Körper & Pflege – Ausreichend Wasser trinken, gesund essen, sanft bewegen",
    SelfCareKey.BOUNDARIES    to "🛑 Gesunde Grenzen – Bewusst 'Nein' sagen, Überlastung vermeiden, rechtzeitig stoppen",
    SelfCareKey.DIGITAL_DETOX to "📱 Digitaler Schutz – Den Morgen/Tag ohne sinnloses Scrollen auf dem Handy verbringen",
    SelfCareKey.SOUL          to "🎨 Seelennahrung – Etwas tun, das mir Freude bringt (Musik, Lesen, Hobby, Kreativität)",
    SelfCareKey.STILLNESS     to "🧘 Ruhemomente – Tiefes Durchatmen, eine kurze Meditation oder Dehnen einbauen",
    SelfCareKey.COMPASSION    to "🕊️ Selbstmitgefühl – Nett zu mir selbst sprechen, mir Fehler verzeihen, Druck rausnehmen",
    SelfCareKey.NO_ENERGY     to "🪫 Keine Kraft für Vorsätze – Ich bin heute im reinen Überlebensmodus und erlege mir keinen Druck auf.",
)

private val eveningOptions = listOf(
    SelfCareKey.NEEDS_MET       to "💧 Bedürfnisse geachtet – Genug getrunken, gegessen oder meinem Körper Ruhe gegönnt",
    SelfCareKey.BOUNDARIES_KEPT to "🛑 Grenzen gesetzt – Mich abgegrenzt (z. B. rechtzeitig Feierabend gemacht, 'Nein' gesagt)",
    SelfCareKey.UNPLUGGED       to "📱 Abschaltzeit gegönnt – Offline-Zeit genossen, bewusst Abstand zu Bildschirmen gehalten",
    SelfCareKey.JOYFUL_MOMENT   to "🎨 Seelenbalsam – Zeit mit Dingen verbracht, die mir Spaß machen und mich nähren",
    SelfCareKey.RELEASE         to "🌬️ Druck abgelassen – Bewusst durchgeatmet, Stress abgeschüttelt, Tränen zugelassen oder gedehnt",
    SelfCareKey.FORGIVENESS     to "🕊️ Selbstvergebung – Mich so akzeptiert, wie ich heute war – auch mit Fehlern und ohne Perfektion",
    SelfCareKey.NEGLECTED       to "🚨 Mich selbst vernachlässigt – Keine Zeit oder Kraft für mich gehabt, eigene Bedürfnisse übergangen.",
)

@Composable
fun SelfCareSection(type: String, selected: List<String>, onToggle: (String) -> Unit) {
    val title = if (type == "morning") "Was nehme ich mir heute vor, um gut für mich zu sorgen?" else "Wie habe ich heute für mein Wohlbefinden gesorgt?"
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
