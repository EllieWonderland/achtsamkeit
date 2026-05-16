package com.elliewonderland.achtsamkeit.ui.entry.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@Composable
fun GuidedQuestionSection(question: String, answer: String, onAnswerChange: (String) -> Unit) {
    SectionCard(title = question) {
        OutlinedTextField(
            value         = answer,
            onValueChange = onAnswerChange,
            modifier      = Modifier.fillMaxWidth(),
            placeholder   = { Text("Deine Gedanken dazu…", color = AppTheme.colors.inkSoft) },
            minLines      = 3,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppTheme.colors.accent,
                unfocusedBorderColor = AppTheme.colors.hair,
                focusedTextColor     = AppTheme.colors.ink,
                unfocusedTextColor   = AppTheme.colors.ink,
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
        )
    }
}
