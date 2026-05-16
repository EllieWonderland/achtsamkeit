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
    "water"     to "Ausreichend Wasser getrunken.",
    "outside"   to "Mich an der frischen Luft bewegt.",
    "food"      to "Etwas gegessen, das mir wirklich gutgetan hat.",
    "hobby"     to "Zeit für ein Hobby oder mich selbst genommen.",
    "breathing" to "Bewusst und tief durchgeatmet.",
)

@Composable
fun SelfCareSection(selected: List<String>, onToggle: (String) -> Unit) {
    SectionCard(title = "Was habe ich heute für mein Wohlbefinden getan?") {
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
