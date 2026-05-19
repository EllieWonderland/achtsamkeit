package com.elliewonderland.achtsamkeit.ui.heute

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
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

    val userId    = Firebase.auth.currentUser?.uid ?: ""
    val hour               = LocalTime.now().hour
    val isEvening          = hour >= 15 || hour < 3
    val isMorningLocked    = isEvening && !uiState.hasMorningEntry
    val isEveningLocked    = !isEvening && !uiState.hasEveningEntry
    val today     = remember { LocalDate.now() }

    val greeting = when {
        hour < 4  -> "Guten Abend"
        hour < 12 -> "Guten Morgen"
        hour < 17 -> "Guten Tag"
        else      -> "Guten Abend"
    }

    val dateText = remember {
        today.format(DateTimeFormatter.ofPattern("EEEE · d. MMMM", Locale.GERMAN))
    }

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) vm.loadTodayStatus(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .verticalScroll(rememberScrollState()),
    ) {
        HeroHeader(
            greeting       = greeting,
            firstName      = uiState.userFirstName,
            dateText       = dateText,
            photoUrl       = uiState.photoUrl,
            onProfileClick = { navController.navigate(Screen.Profil.route) },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Spacer(Modifier.height(0.dp))

            if (!uiState.isLoading && uiState.streak > 0) {
                StreakPill(uiState.streak)
            }

            MoodMonthCard(
                moodMonth    = uiState.moodMonth,
                moodTrendPct = uiState.moodTrendPct,
                today        = today,
                onClick      = { navController.navigate(Screen.Statistik.route) },
            )

            QuoteOfDayCard(
                quote            = uiState.quoteOfDay,
                isFavorite       = uiState.quoteIsFavorite,
                onFavoriteToggle = { vm.toggleFavoriteQuote() },
                onClick          = {},
            )

            if (uiState.isLoading) {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AppTheme.colors.accent)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    RoutineCard(
                        emoji    = "☀️",
                        title    = "Morgenroutine",
                        subtitle = when {
                            uiState.hasMorningEntry -> uiState.morningCompletedAt
                                ?.let { "Erledigt um %02d:%02d".format(it.hour, it.minute) } ?: "Erledigt"
                            isMorningLocked         -> "Nur bis 15:00 Uhr verfügbar"
                            else                    -> "Jetzt starten · 3 Minuten"
                        },
                        isDone   = uiState.hasMorningEntry,
                        isLocked = isMorningLocked,
                        onClick  = { navController.navigate(Screen.Entry.createRoute("morning")) },
                    )
                    RoutineCard(
                        emoji    = "🌙",
                        title    = "Abendroutine",
                        subtitle = when {
                            uiState.hasEveningEntry -> uiState.eveningCompletedAt
                                ?.let { "Erledigt um %02d:%02d".format(it.hour, it.minute) } ?: "Erledigt"
                            isEveningLocked         -> "Erst ab 15:00 Uhr verfügbar"
                            else                    -> "Jetzt starten"
                        },
                        isDone   = uiState.hasEveningEntry,
                        isLocked = isEveningLocked,
                        onClick  = { navController.navigate(Screen.Entry.createRoute("evening")) },
                    )
                }
            }

            if (!uiState.isLoading && uiState.weekDays.isNotEmpty()) {
                WeekStripCard(
                    weekDays       = uiState.weekDays,
                    completedCount = uiState.weekCompletedCount,
                    maxCount       = uiState.weekMaxCount,
                    onClick        = { navController.navigate(Screen.Statistik.route) },
                )
            }

            if (!uiState.isLoading && (uiState.weeklyUnlocked || uiState.monthlyUnlocked)) {
                Text(
                    "RÜCKBLICKE",
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
}

@Composable
private fun RoutineCard(
    emoji   : String,
    title   : String,
    subtitle: String,
    isDone  : Boolean,
    isLocked: Boolean = false,
    onClick : () -> Unit,
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, colors.hair, RoundedCornerShape(18.dp))
            .background(if (isDone || isLocked) colors.surfaceAlt else colors.surface)
            .clickable(enabled = !isDone && !isLocked, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier              = Modifier.weight(1f),
        ) {
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDone || isLocked) colors.surface else colors.background),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, style = MaterialTheme.typography.titleLarge)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isLocked) colors.inkSoft else colors.ink,
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
                    ),
                    color = if (isDone) colors.accent else colors.inkSoft,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        when {
            isDone   -> Icon(Icons.Outlined.Check, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
            isLocked -> Icon(Icons.Outlined.Schedule, contentDescription = null, tint = colors.inkSoft, modifier = Modifier.size(20.dp))
            else     -> Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
        }
    }
}
