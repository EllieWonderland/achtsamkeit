package com.elliewonderland.achtsamkeit.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.data.repository.HistoryRepository
import com.elliewonderland.achtsamkeit.model.Entry
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EntryDetailScreen(navController: NavController, entryId: String) {
    val userId = Firebase.auth.currentUser?.uid ?: ""
    val repo  = remember { HistoryRepository() }
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    var entry by remember { mutableStateOf<Entry?>(null) }
    LaunchedEffect(entryId, userId) {
        if (userId.isNotBlank() && entryId.isNotBlank()) {
            entry = runCatching { repo.getEntryById(userId, entryId) }.getOrNull()
        }
    }

    var isEditing        by remember { mutableStateOf(false) }
    var editGuidedAnswer by remember { mutableStateOf("") }
    var editFreeText     by remember { mutableStateOf("") }

    val topBarTitle = entry?.let { e ->
        buildString {
            append(typeLabelFull(e.type))
            if (e.dateStr.isNotBlank()) append("  ·  ${formatDate(e.dateStr)}")
        }
    } ?: "Eintrag"

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eintrag löschen?") },
            text = { Text("Dieser Eintrag wird unwiderruflich gelöscht.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    scope.launch {
                        runCatching { repo.deleteEntry(userId, entryId) }
                        navController.popBackStack()
                    }
                }) {
                    Text("Löschen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Abbrechen")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = AppTheme.colors.ink,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Zurück",
                            tint = AppTheme.colors.ink,
                        )
                    }
                },
                actions = {
                    if (entry != null) {
                        if (isEditing) {
                            IconButton(onClick = { isEditing = false }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Abbrechen", tint = AppTheme.colors.inkSoft)
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    runCatching { repo.updateEntry(userId, entryId, editGuidedAnswer, editFreeText) }
                                    entry = entry?.copy(guidedAnswer = editGuidedAnswer, freeText = editFreeText)
                                    isEditing = false
                                }
                            }) {
                                Icon(Icons.Outlined.Check, contentDescription = "Speichern", tint = AppTheme.colors.accent)
                            }
                        } else {
                            IconButton(onClick = {
                                editGuidedAnswer = entry?.guidedAnswer ?: ""
                                editFreeText     = entry?.freeText ?: ""
                                isEditing = true
                            }) {
                                Icon(Icons.Outlined.Edit, contentDescription = "Bearbeiten", tint = AppTheme.colors.inkSoft)
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Eintrag löschen", tint = AppTheme.colors.inkSoft)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        when {
            entry == null && entryId.isNotBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AppTheme.colors.accent)
                }
            }
            entry != null -> {
                val e = entry!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    // Tags
                    if (e.tags.isNotEmpty()) {
                        DetailCard(title = "Themen") {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                e.tags.forEach { tag ->
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = AppTheme.colors.accent.copy(alpha = 0.15f),
                                            labelColor = AppTheme.colors.ink,
                                        ),
                                    )
                                }
                            }
                        }
                    }

                    // Stimmung & Energie
                    if (e.mood.isNotBlank() || e.energyLevel.isNotBlank()) {
                        DetailCard(title = "Stimmung & Energie") {
                            if (e.mood.isNotBlank()) {
                                DetailRow(label = "Stimmung", value = moodLabel(e.mood))
                            }
                            if (e.energyLevel.isNotBlank()) {
                                DetailRow(label = "Energie", value = energyLabel(e.energyLevel))
                            }
                            if (e.dayRating > 0) {
                                DetailRow(label = "Tagesbewertung", value = ratingLabel(e.dayRating))
                            }
                        }
                    }

                    // Dankbarkeit
                    if (e.gratitudeAreas.isNotEmpty()) {
                        DetailCard(title = "Dankbarkeit") {
                            e.gratitudeAreas.forEach { area ->
                                Text(
                                    text = "· ${gratitudeLabel(area)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppTheme.colors.ink,
                                )
                            }
                        }
                    }

                    // Selbstfürsorge
                    if (e.selfCare.isNotEmpty()) {
                        DetailCard(title = "Selbstfürsorge") {
                            e.selfCare.forEach { item ->
                                Text(
                                    text = "· ${selfCareLabel(item)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppTheme.colors.ink,
                                )
                            }
                        }
                    }

                    // Achtsamkeit
                    if (e.mindfulnessFocus.isNotBlank() || e.mindfulnessPause.isNotBlank()) {
                        DetailCard(title = "Achtsamkeit") {
                            if (e.mindfulnessFocus.isNotBlank()) {
                                DetailRow(label = "Fokus", value = mindfulnessFocusLabel(e.mindfulnessFocus))
                            }
                            if (e.mindfulnessPause.isNotBlank()) {
                                DetailRow(label = "Pause", value = mindfulnessPauseLabel(e.mindfulnessPause))
                            }
                        }
                    }

                    // Rotierende Frage
                    if (e.guidedQuestion.isNotBlank()) {
                        DetailCard(title = e.guidedQuestion) {
                            if (isEditing) {
                                OutlinedTextField(
                                    value         = editGuidedAnswer,
                                    onValueChange = { editGuidedAnswer = it },
                                    modifier      = Modifier.fillMaxWidth(),
                                    minLines      = 3,
                                    textStyle     = MaterialTheme.typography.bodyMedium,
                                    colors        = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor   = AppTheme.colors.accent,
                                        unfocusedBorderColor = AppTheme.colors.hair,
                                        focusedTextColor     = AppTheme.colors.ink,
                                        unfocusedTextColor   = AppTheme.colors.ink,
                                    ),
                                )
                            } else {
                                Text(
                                    text  = e.guidedAnswer.ifBlank { "—" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (e.guidedAnswer.isBlank()) AppTheme.colors.inkSoft else AppTheme.colors.ink,
                                )
                            }
                        }
                    }

                    // Freie Gedanken
                    if (isEditing || e.freeText.isNotBlank()) {
                        DetailCard(title = "Weitere Gedanken") {
                            if (isEditing) {
                                OutlinedTextField(
                                    value         = editFreeText,
                                    onValueChange = { editFreeText = it },
                                    modifier      = Modifier.fillMaxWidth(),
                                    minLines      = 3,
                                    textStyle     = MaterialTheme.typography.bodyMedium,
                                    colors        = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor   = AppTheme.colors.accent,
                                        unfocusedBorderColor = AppTheme.colors.hair,
                                        focusedTextColor     = AppTheme.colors.ink,
                                        unfocusedTextColor   = AppTheme.colors.ink,
                                    ),
                                )
                            } else {
                                Text(
                                    text  = e.freeText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppTheme.colors.ink,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = AppTheme.colors.inkSoft,
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AppTheme.colors.inkSoft,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.ink,
        )
    }
}

private fun typeLabelFull(type: String): String = when (type) {
    "morning"        -> "Morgenroutine"
    "evening"        -> "Abendroutine"
    "weekly_review"  -> "Wochenrückblick"
    "monthly_review" -> "Monatsrückblick"
    else             -> "Eintrag"
}

private fun formatDate(dateStr: String): String = runCatching {
    LocalDate.parse(dateStr).format(DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale("de")))
}.getOrDefault(dateStr)

private fun moodLabel(mood: String): String = when (mood) {
    "joy"     -> "☀️ Freudig"
    "stress"  -> "🌩️ Gestresst"
    "balance" -> "🌿 Ausgeglichen"
    "sadness" -> "🌧️ Traurig"
    else      -> mood
}

private fun energyLabel(energy: String): String = when (energy) {
    "full"   -> "⚡ Voller Energie"
    "medium" -> "🔋 Mittel"
    "low"    -> "🪫 Wenig"
    "empty"  -> "😴 Erschöpft"
    else     -> energy
}

private fun ratingLabel(rating: Int): String = when (rating) {
    1    -> "★☆☆ — Schwieriger Tag"
    3    -> "★★☆ — Okay"
    5    -> "★★★ — Schöner Tag"
    else -> "$rating"
}

private fun gratitudeLabel(area: String): String = when (area) {
    "people"      -> "Menschen in meinem Leben"
    "health"      -> "Meine Gesundheit"
    "moment"      -> "Einen schönen Moment"
    "achievement" -> "Eine Leistung von mir"
    "nature"      -> "Die Natur"
    else          -> area
}

private fun selfCareLabel(item: String): String = when (item) {
    "movement"  -> "Bewegung"
    "nutrition" -> "Gesunde Ernährung"
    "rest"      -> "Ausreichend Ruhe"
    "breathing" -> "Atemübung"
    "outside"   -> "Draußen gewesen"
    "social"    -> "Zeit mit anderen"
    else        -> item
}

private fun mindfulnessFocusLabel(focus: String): String = when (focus) {
    "past"    -> "Vergangenheit"
    "present" -> "Gegenwart"
    "future"  -> "Zukunft"
    else      -> focus
}

private fun mindfulnessPauseLabel(pause: String): String = when (pause) {
    "yes_pure"       -> "Ja, voll präsent"
    "yes_distracted" -> "Ja, aber abgelenkt"
    "no"             -> "Nein"
    else             -> pause
}
