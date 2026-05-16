package com.elliewonderland.achtsamkeit.ui.entry

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.entry.components.EnergySection
import com.elliewonderland.achtsamkeit.ui.entry.components.FreeTextSection
import com.elliewonderland.achtsamkeit.ui.entry.components.GratitudeSection
import com.elliewonderland.achtsamkeit.ui.entry.components.GuidedQuestionSection
import com.elliewonderland.achtsamkeit.ui.entry.components.MindfulnessSection
import com.elliewonderland.achtsamkeit.ui.entry.components.MoodSection
import com.elliewonderland.achtsamkeit.ui.entry.components.RatingSection
import com.elliewonderland.achtsamkeit.ui.entry.components.SelfCareSection
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalTime

@Composable
fun EntryScreen(navController: NavController, type: String) {
    val vm: EntryViewModel = viewModel()
    val form by vm.form.collectAsState()
    val saveState by vm.saveState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val userId = Firebase.auth.currentUser?.uid ?: ""
    val displayName = Firebase.auth.currentUser?.displayName?.let { it.ifBlank { null } }

    val greeting = when (type) {
        "morning" -> "Guten Morgen${displayName?.let { ", $it" } ?: ""}!"
        else      -> "Guten Abend${displayName?.let { ", $it" } ?: ""}!"
    }
    val subtitle = when (type) {
        "morning" -> "Deine Morgenroutine — nimm dir 3 Minuten nur für dich."
        else      -> "Deine Abendroutine — lass den Tag sanft ausklingen."
    }

    LaunchedEffect(type) { vm.initType(type) }

    LaunchedEffect(saveState) {
        when (val s = saveState) {
            is EntrySaveState.Saved  -> navController.navigate(Screen.Quote.createRoute(s.entryId)) {
                popUpTo(Screen.Heute.route)
            }
            is EntrySaveState.Error  -> snackbarHostState.showSnackbar(s.message)
            else -> Unit
        }
    }

    androidx.compose.material3.Scaffold(
        containerColor = AppTheme.colors.background,
        snackbarHost   = { SnackbarHost(snackbarHostState) { Snackbar(it) } },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text     = greeting,
                    style    = MaterialTheme.typography.headlineMedium,
                    color    = AppTheme.colors.ink,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = subtitle,
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = AppTheme.colors.inkSoft,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(Modifier.height(16.dp))
            }
            item { EnergySection(type = type, selected = form.energyLevel, onSelect = vm::updateEnergyLevel) }
            item { MoodSection(type = type, selected = form.mood, onSelect = vm::updateMood) }
            item { GratitudeSection(type = type, selected = form.gratitudeAreas, onToggle = vm::toggleGratitudeArea) }
            if (type == "evening") {
                item { RatingSection(selected = form.dayRating, onSelect = vm::updateDayRating) }
            }
            item { SelfCareSection(type = type, selected = form.selfCare, onToggle = vm::toggleSelfCare) }
            item {
                MindfulnessSection(
                    type          = type,
                    selectedFocus = form.mindfulnessFocus,
                    selectedPause = form.mindfulnessPause,
                    onFocusSelect = vm::updateMindfulnessFocus,
                    onPauseSelect = vm::updateMindfulnessPause,
                )
            }
            item {
                if (form.guidedQuestion.isNotBlank()) {
                    GuidedQuestionSection(
                        question      = form.guidedQuestion,
                        answer        = form.guidedAnswer,
                        onAnswerChange = vm::updateGuidedAnswer,
                    )
                }
            }
            item { FreeTextSection(value = form.freeText, onValueChange = vm::updateFreeText) }
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick  = { if (userId.isNotBlank()) vm.saveEntry(userId, type) },
                    enabled  = saveState !is EntrySaveState.Saving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent),
                ) {
                    if (saveState is EntrySaveState.Saving) {
                        CircularProgressIndicator(
                            color     = AppTheme.colors.onAccent,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            "Fertig",
                            style = MaterialTheme.typography.labelLarge,
                            color = AppTheme.colors.onAccent,
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
