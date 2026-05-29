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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.compose.ui.platform.LocalContext
import com.elliewonderland.achtsamkeit.data.local.CardPreferences
import com.elliewonderland.achtsamkeit.data.repository.PremiumRepository
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import android.app.Activity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HeuteScreen(navController: NavController) {
    val vm: HeuteViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cardsConfig by remember(context) { CardPreferences.getHeuteCards(context) }
        .collectAsState(initial = CardPreferences.defaultHeuteCards)

    if (uiState.showFavoriteLimitDialog) {
        AlertDialog(
            onDismissRequest = { vm.dismissFavoriteLimitDialog() },
            title = { Text("Favoriten-Limit erreicht") },
            text = {
                Text(
                    "Als kostenfreie Nutzerin kannst du bis zu 3 Einträge favorisieren. Upgrade auf Premium, um unbegrenzt Lieblingssprüche zu speichern.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.inkSoft,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.dismissFavoriteLimitDialog()
                        scope.launch {
                            val activity = context as? Activity ?: return@launch
                            PremiumRepository.purchase(activity)
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AppTheme.colors.accent),
                ) {
                    Text("Upgrade")
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.dismissFavoriteLimitDialog() }) {
                    Text("Abbrechen")
                }
            },
        )
    }

    val userId    = Firebase.auth.currentUser?.uid ?: ""
    val hour by produceState(initialValue = LocalTime.now().hour) {
        while (true) {
            val now = LocalTime.now()
            delay(((60 - now.second) * 1_000L).coerceAtLeast(1_000L))
            value = LocalTime.now().hour
        }
    }
    val isEvening          = hour >= 17 || hour < 5
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry?.destination?.route, userId) {
        if (navBackStackEntry?.destination?.route == Screen.Heute.route && userId.isNotBlank()) {
            vm.loadTodayStatus(userId)
        }
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
            photoScale     = uiState.photoScale,
            photoOffsetX    = uiState.photoOffsetX,
            photoOffsetY    = uiState.photoOffsetY,
            onProfileClick = { navController.navigate(Screen.Profil.route) },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            cardsConfig.forEach { card ->
                if (card.visible) {
                    when (card.id) {
                        "mood_trend" -> {
                            MoodMonthCard(
                                moodMonth    = uiState.moodMonth,
                                moodTrendPct = uiState.moodTrendPct,
                                today        = today,
                                onClick      = { navController.navigate(Screen.Statistik.route) },
                            )
                        }
                        "quote" -> {
                            QuoteOfDayCard(
                                quote            = uiState.quoteOfDay,
                                isFavorite       = uiState.quoteIsFavorite,
                                onFavoriteToggle = { vm.toggleFavoriteQuote() },
                                onClick          = {},
                            )
                        }
                        "lifehack" -> {
                            LifehackCard(
                                lifehack         = uiState.lifehackOfDay,
                                isFavorite       = uiState.lifehackIsFavorite,
                                onFavoriteToggle = { vm.toggleFavoriteLifehack() },
                            )
                        }
                        "routines" -> {
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
                                            isMorningLocked         -> "Nur bis 17:00 Uhr verfügbar"
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
                                            isEveningLocked         -> "Erst ab 17:00 Uhr verfügbar"
                                            else                    -> "Jetzt starten"
                                        },
                                        isDone   = uiState.hasEveningEntry,
                                        isLocked = isEveningLocked,
                                        onClick  = { navController.navigate(Screen.Entry.createRoute("evening")) },
                                    )
                                }
                            }
                        }
                        "week_strip" -> {
                            if (!uiState.isLoading && uiState.weekDays.isNotEmpty()) {
                                WeekStripCard(
                                    weekDays       = uiState.weekDays,
                                    completedCount = uiState.weekCompletedCount,
                                    maxCount       = uiState.weekMaxCount,
                                    onClick        = { navController.navigate(Screen.Statistik.route) },
                                )
                            }
                        }
                        "reviews" -> {
                            if (!uiState.isLoading) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "RÜCKBLICKE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppTheme.colors.inkSoft,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                                    )
                                    ReviewCard(
                                        title    = "Wochenrückblick",
                                        subtitle = when {
                                            uiState.hasWeeklyReview  -> "Diese Woche verfasst"
                                            uiState.weeklyUnlocked   -> "Jetzt schreiben"
                                            else                     -> "Am Wochenende verfügbar"
                                        },
                                        isDone   = uiState.hasWeeklyReview,
                                        isLocked = !uiState.weeklyUnlocked,
                                        onClick  = { navController.navigate(Screen.WeeklyReview.route) },
                                    )
                                    ReviewCard(
                                        title    = "Monatsrückblick",
                                        subtitle = when {
                                            uiState.hasMonthlyReview -> "Diesen Monat verfasst"
                                            uiState.monthlyUnlocked  -> "Jetzt schreiben"
                                            else                     -> "In der letzten Woche des Monats verfügbar"
                                        },
                                        isDone   = uiState.hasMonthlyReview,
                                        isLocked = !uiState.monthlyUnlocked,
                                        onClick  = { navController.navigate(Screen.MonthlyReview.route) },
                                    )
                                    ReviewCard(
                                        title    = "Jahresrückblick",
                                        subtitle = when {
                                            uiState.hasYearlyReview -> "Dieses Jahr verfasst"
                                            uiState.yearlyUnlocked  -> "Jetzt schreiben"
                                            else                    -> "Im Dezember verfügbar"
                                        },
                                        isDone   = uiState.hasYearlyReview,
                                        isLocked = !uiState.yearlyUnlocked,
                                        onClick  = { navController.navigate(Screen.YearlyReview.route) },
                                    )
                                }
                            }
                        }
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

@Composable
private fun ReviewCard(
    title   : String,
    subtitle: String,
    isDone  : Boolean = false,
    isLocked: Boolean,
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
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier              = Modifier.weight(1f),
            verticalArrangement   = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isLocked && !isDone) colors.inkSoft else colors.ink,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isDone) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal,
                ),
                color = if (isDone) colors.accent else colors.inkSoft,
            )
        }
        Spacer(Modifier.width(8.dp))
        when {
            isDone   -> Icon(Icons.Outlined.Check, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
            isLocked -> Icon(Icons.Outlined.Schedule, contentDescription = null, tint = colors.inkSoft, modifier = Modifier.size(18.dp))
            else     -> Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
        }
    }
}
