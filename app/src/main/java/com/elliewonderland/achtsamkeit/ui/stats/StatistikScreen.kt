package com.elliewonderland.achtsamkeit.ui.stats

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.data.local.MindfulImpulseLoader
import com.elliewonderland.achtsamkeit.data.repository.PremiumRepository
import com.elliewonderland.achtsamkeit.ui.components.ShimmerCard
import com.elliewonderland.achtsamkeit.ui.premium.PaywallCard
import com.elliewonderland.achtsamkeit.data.local.CardPreferences
import com.elliewonderland.achtsamkeit.ui.stats.components.EnergyBarChart
import com.elliewonderland.achtsamkeit.ui.stats.components.GratitudePieChart
import com.elliewonderland.achtsamkeit.ui.stats.components.MindfulnessFocusChart
import com.elliewonderland.achtsamkeit.ui.stats.components.MoodBarChart
import com.elliewonderland.achtsamkeit.ui.stats.components.SelfCarePillarsList
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.HandwrittenStyle
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun StatistikScreen(
    navController: NavController,
    vm: StatsViewModel = viewModel(),
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val statsCardsConfig by remember(context) { CardPreferences.getStatsCards(context) }
        .collectAsState(initial = CardPreferences.defaultStatsCards)

    var isPremium by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        vm.reload()
        isPremium = PremiumRepository.isPremium()
    }

    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text  = "Statistiken",
            style = MaterialTheme.typography.headlineMedium,
            color = colors.ink,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(7, 30, 90).forEach { days ->
                FilterChip(
                    selected = state.days == days,
                    onClick  = { vm.setDays(days) },
                    label    = { Text("$days Tage") },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.accent,
                        selectedLabelColor     = colors.onAccent,
                        containerColor         = colors.surface,
                        labelColor             = colors.inkSoft,
                    ),
                    border = BorderStroke(1.dp, if (state.days == days) colors.accent else colors.hair)
                )
            }
        }

        if (state.isLoading) {
            ShimmerCard(height = 120.dp)
            ShimmerCard(height = 200.dp)
            ShimmerCard(height = 200.dp)
        } else {
            if (!isPremium && state.days > 30) {
                PaywallCard(
                    description = "Statistiken über 30 Tage hinaus sind ein Premium-Feature. Upgrade, um Muster über bis zu 90 Tage zu entdecken.",
                    onUpgrade   = {
                        scope.launch {
                            val activity = context as? Activity ?: return@launch
                            val success = PremiumRepository.purchase(activity)
                            if (success) isPremium = true
                        }
                    },
                )
            } else {
                statsCardsConfig.forEach { card ->
                    if (card.visible) {
                        when (card.id) {
                            "kompass" -> AchtsamkeitsKompassCard(state = state)
                            "mood_dist" -> {
                                StatCard(title = "Stimmungsverteilung") {
                                    MoodBarChart(distribution = state.moodDistribution)
                                }
                            }
                            "energy" -> {
                                StatCard(title = "Energielevel") {
                                    EnergyBarChart(distribution = state.energyDistribution)
                                }
                            }
                            "focus" -> {
                                StatCard(title = "Achtsamkeits-Fokus") {
                                    MindfulnessFocusChart(distribution = state.focusDistribution)
                                }
                            }
                            "gratitude" -> {
                                StatCard(title = "Dankbarkeits-Momente") {
                                    GratitudePieChart(distribution = state.gratitudeDistribution)
                                }
                            }
                            "self_care" -> {
                                StatCard(title = "Selbstfürsorge-Säulen") {
                                    SelfCarePillarsList(distribution = state.selfCareDistribution)
                                }
                            }
                            "impulse" -> {
                                MitfuehlenderImpulsCard(avgRating = state.avgDayRating)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchtsamkeitsKompassCard(state: StatsUiState) {
    val colors = AppTheme.colors
    
    // Calculate mindfulness pause percentage
    val pauseYesCount = state.pauseDistribution.filterKeys { it == "yes_pure" || it == "yes_distracted" }.values.sum()
    val pausePct = if (state.entries.isEmpty()) 0 else (pauseYesCount * 100) / state.entries.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = colors.accent.copy(alpha = 0.08f).compositeOver(colors.surface)
        ),
        border   = BorderStroke(1.dp, colors.accent.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text  = "Dein Achtsamkeits-Kompass",
                style = MaterialTheme.typography.titleMedium,
                color = colors.ink,
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Rating Average
                Column(modifier = Modifier.weight(1.1f)) {
                    Text(
                        text  = "Tagesbewertung",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.inkSoft,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text  = String.format(Locale.getDefault(), "%.1f", state.avgDayRating),
                            style = MaterialTheme.typography.displayMedium,
                            color = colors.ink,
                            lineHeight = 36.sp
                        )
                        Text(
                            text  = " / 5.0",
                            style = SerifItalic.copy(fontSize = 16.sp),
                            color = colors.inkSoft,
                            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    // Visual Stars (★ / ☆)
                    val filledStars = state.avgDayRating.toInt().coerceIn(0, 5)
                    val starString = "★".repeat(filledStars) + "☆".repeat(5 - filledStars)
                    Text(
                        text  = starString,
                        style = TextStyle(fontSize = 18.sp, color = colors.accent),
                    )
                }

                // Divider Line
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp)
                        .background(colors.hair)
                )

                Spacer(Modifier.width(16.dp))

                // Right Column: Action stats
                Column(modifier = Modifier.weight(0.9f)) {
                    Text(
                        text  = "Aktivität & Pausen",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.inkSoft,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = "• ${state.entries.size} Tagebücher",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.ink,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = "• $pausePct% stille Pausen",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.ink,
                    )
                }
            }
        }
    }
}

@Composable
private fun MitfuehlenderImpulsCard(avgRating: Double) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val loader = remember(context) { MindfulImpulseLoader(context) }
    val tipText = remember(avgRating, loader) { loader.getImpulseForRating(avgRating) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = colors.surface),
        border   = BorderStroke(1.dp, colors.hair),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text  = "Mitfühlender Impuls",
                style = MaterialTheme.typography.titleSmall,
                color = colors.accent,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = tipText,
                style = HandwrittenStyle.copy(fontSize = 22.sp, lineHeight = 28.sp),
                color = colors.ink,
            )
        }
    }
}

@Composable
private fun StatCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        border   = BorderStroke(1.dp, AppTheme.colors.hair),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text  = title,
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.ink,
            )
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

// Helper extension to blend colors
private fun Color.compositeOver(bg: Color): Color {
    val a = alpha
    return Color(
        red   = red   * a + bg.red   * (1 - a),
        green = green * a + bg.green * (1 - a),
        blue  = blue  * a + bg.blue  * (1 - a),
        alpha = 1f,
    )
}

