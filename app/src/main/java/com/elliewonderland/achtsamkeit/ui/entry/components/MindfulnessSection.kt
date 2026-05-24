package com.elliewonderland.achtsamkeit.ui.entry.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

private val morningFocusOptions = listOf(
    "present" to "🍃 Ganz im Jetzt – Ich spüre meinen Körper, atme ruhig und bin präsent.",
    "future"  to "📅 Aufgaben-Tunnel – Meine Gedanken kreisen schon hektisch um die To-Dos des Tages.",
    "past"    to "💭 Gedankenschwere – Ich hänge emotional noch bei gestrigen Erlebnissen oder Sorgen fest.",
)

private val morningPauseOptions = listOf(
    "yes_pure"       to "🧘 Bewusster Start – Ja, in vollkommener Stille oder bei Tee/Kaffee ohne Ablenkung.",
    "yes_distracted" to "📱 Nebengeschäftigt – Ein bisschen, aber mit Handy, Nachrichten oder Podcasts nebenbei.",
    "no"             to "🏃 Direkt im Trubel – Nein, ich bin direkt vom Bett in den Autopiloten und die Hektik gesprungen.",
)

private val eveningFocusOptions = listOf(
    "present"    to "🍃 Überwiegend im Jetzt – Ich war aufmerksam und konnte den Tag bewusst erleben.",
    "future"     to "📅 In der Zukunft – Ich war gedanklich schon bei morgen, habe geplant oder mir Sorgen gemacht.",
    "past"       to "💭 In der Vergangenheit – Ich habe viel gegrübelt, Erlebnisse analysiert oder bereut.",
    "autopilot"  to "🤖 Im Autopiloten – Der Tag ist wie ein Film an mir vorbeigezogen, ohne dass ich richtig anwesend war.",
)

private val eveningPauseOptions = listOf(
    "yes_pure"       to "🌲 Echte Pause – Ja, eine handyfreie, stille Auszeit genommen (Natur, Atmen, Nichtstun).",
    "yes_distracted" to "📱 Konsum-Pause – Ja, aber abgelenkt mit Social Media, Mails oder Podcasts.",
    "no"             to "🏃 Dauer-Rauschen – Nein, ich war durchgehend aktiv und im Dauer-Rotations-Modus.",
)

@Composable
fun MindfulnessSection(
    type: String,
    selectedFocus: String,
    selectedPause: String,
    onFocusSelect: (String) -> Unit,
    onPauseSelect: (String) -> Unit,
) {
    val focusQuestion = if (type == "morning") "Bin ich heute Morgen im Moment angekommen?" else "Wo waren meine Gedanken heute die meiste Zeit?"
    val pauseQuestion = if (type == "morning") "Habe ich mir heute Morgen einen bewussten, ruhigen Moment gegönnt?" else "Habe ich heute tagsüber bewusst eine kurze Pause eingelegt?"
    val focusOptions  = if (type == "morning") morningFocusOptions else eveningFocusOptions
    val pauseOptions  = if (type == "morning") morningPauseOptions else eveningPauseOptions

    SectionCard(title = "Achtsamkeit im Hier und Jetzt") {
        Text(
            focusQuestion,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.inkSoft,
        )
        Spacer(Modifier.height(6.dp))
        focusOptions.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFocusSelect(value) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedFocus == value,
                    onClick  = { onFocusSelect(value) },
                    colors   = RadioButtonDefaults.colors(selectedColor = AppTheme.colors.accent),
                )
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.ink)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            pauseQuestion,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.inkSoft,
        )
        Spacer(Modifier.height(6.dp))
        pauseOptions.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPauseSelect(value) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedPause == value,
                    onClick  = { onPauseSelect(value) },
                    colors   = RadioButtonDefaults.colors(selectedColor = AppTheme.colors.accent),
                )
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.ink)
            }
        }
    }
}
