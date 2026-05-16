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
    "present" to "Ja, ich bin angekommen — präsent und geerdet.",
    "future"  to "Teilweise — meine Gedanken wandern schon zu den Aufgaben des Tages.",
    "past"    to "Kaum — ich bin in Gedanken noch bei gestern.",
)

private val morningPauseOptions = listOf(
    "yes_pure"       to "Ja, ganz bewusst (Kaffee, Stille, Stretching).",
    "yes_distracted" to "Ein bisschen, aber schon mit Handy o.Ä.",
    "no"             to "Nein, ich bin direkt in den Trubel gestartet.",
)

private val eveningFocusOptions = listOf(
    "past"    to "In der Vergangenheit (gegrübelt oder in Erinnerungen geschwelgt)",
    "future"  to "In der Zukunft (geplant oder gesorgt)",
    "present" to "Überwiegend im gegenwärtigen Moment",
)

private val eveningPauseOptions = listOf(
    "yes_pure"       to "Ja, ganz ohne Ablenkung (kein Handy, kein TV).",
    "yes_distracted" to "Ja, aber nebenbei etwas anderes gemacht.",
    "no"             to "Nein, ich war im Dauer-Rotations-Modus.",
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
    val pauseQuestion = if (type == "morning") "Habe ich mir heute Morgen einen ruhigen Moment gegönnt?" else "Habe ich heute bewusst eine Pause eingelegt?"
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
