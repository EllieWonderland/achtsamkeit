package com.elliewonderland.achtsamkeit.ui.heute

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HeuteScreen(navController: NavController) {
    val vm: HeuteViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()

    val userId = Firebase.auth.currentUser?.uid ?: ""
    val hour = LocalTime.now().hour
    val isEvening = hour >= 17 || hour < 4

    val greeting = when {
        hour < 4  -> "Guten Abend"
        hour < 12 -> "Guten Morgen"
        hour < 17 -> "Guten Tag"
        else      -> "Guten Abend"
    }

    val dateText = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, d. MMMM", Locale.GERMAN)
        )
    }

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) vm.loadTodayStatus(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Begrüßung
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text  = greeting,
                style = MaterialTheme.typography.displaySmall,
                color = AppTheme.colors.ink,
            )
            Text(
                text  = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.inkSoft,
            )
        }

        // Streak-Zeile
        if (!uiState.isLoading && uiState.streak > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.colors.surfaceAlt)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("🔥", style = MaterialTheme.typography.titleMedium)
                Text(
                    text  = if (uiState.streak == 1) "1 Tag in Folge" else "${uiState.streak} Tage in Folge",
                    style = MaterialTheme.typography.titleSmall,
                    color = AppTheme.colors.ink,
                )
            }
        }

        // Eintrags-Bereich
        when {
            uiState.isLoading -> {
                Box(
                    modifier        = Modifier.fillMaxWidth().height(160.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AppTheme.colors.accent)
                }
            }

            uiState.hasMorningEntry && uiState.hasEveningEntry -> {
                EntryDoneCard()
            }

            else -> {
                RoutineCard(
                    emoji       = "☀️",
                    title       = "Morgenroutine",
                    subtitle    = if (uiState.hasMorningEntry) "Erledigt" else "Jetzt starten",
                    isDone      = uiState.hasMorningEntry,
                    isLocked    = false,
                    onClick     = { navController.navigate(Screen.Entry.createRoute("morning")) },
                )

                // Abendroutine erst ab 17 Uhr einblenden
                if (isEvening || uiState.hasEveningEntry) {
                    RoutineCard(
                        emoji       = "🌙",
                        title       = "Abendroutine",
                        subtitle    = if (uiState.hasEveningEntry) "Erledigt" else "Jetzt starten",
                        isDone      = uiState.hasEveningEntry,
                        isLocked    = false,
                        onClick     = { navController.navigate(Screen.Entry.createRoute("evening")) },
                    )
                }
            }
        }

        // Rückblicke
        if (!uiState.isLoading && (uiState.weeklyUnlocked || uiState.monthlyUnlocked)) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "RÜCKBLICKE",
                style = MaterialTheme.typography.labelSmall,
                color = AppTheme.colors.inkSoft,
            )

            if (uiState.weeklyUnlocked) {
                OutlinedButton(
                    onClick  = { navController.navigate(Screen.WeeklyReview.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.accent),
                ) {
                    Text("Wochenrückblick", style = MaterialTheme.typography.labelLarge)
                }
            }
            if (uiState.monthlyUnlocked) {
                OutlinedButton(
                    onClick  = { navController.navigate(Screen.MonthlyReview.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.accent),
                ) {
                    Text("Monatsrückblick", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun EntryDoneCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        shape    = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier          = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("✨", style = MaterialTheme.typography.headlineMedium)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text  = "Heute abgeschlossen",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppTheme.colors.accent,
                )
                Text(
                    text  = "Schön, dass du dir Zeit für dich genommen hast.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.inkSoft,
                )
            }
        }
    }
}

@Composable
private fun RoutineCard(
    emoji    : String,
    title    : String,
    subtitle : String,
    isDone   : Boolean,
    isLocked : Boolean,
    onClick  : () -> Unit,
) {
    val colors = AppTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isDone && !isLocked, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) colors.surfaceAlt else colors.surface,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier              = Modifier.padding(20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier              = Modifier.weight(1f),
            ) {
                Text(emoji, style = MaterialTheme.typography.headlineMedium)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text  = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.ink,
                    )
                    Text(
                        text  = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDone) colors.accent else colors.inkSoft,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            when {
                isDone   -> Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = colors.accent, modifier = Modifier.size(22.dp))
                isLocked -> Icon(Icons.Outlined.Lock,        contentDescription = null, tint = colors.inkSoft, modifier = Modifier.size(20.dp))
                else     -> Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
            }
        }
    }
}
