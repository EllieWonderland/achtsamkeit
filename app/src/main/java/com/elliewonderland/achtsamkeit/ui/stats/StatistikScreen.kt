package com.elliewonderland.achtsamkeit.ui.stats

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.data.repository.PremiumRepository
import com.elliewonderland.achtsamkeit.ui.components.ShimmerCard
import com.elliewonderland.achtsamkeit.ui.premium.PaywallCard
import com.elliewonderland.achtsamkeit.ui.stats.components.GratitudePieChart
import com.elliewonderland.achtsamkeit.ui.stats.components.MoodBarChart
import com.elliewonderland.achtsamkeit.ui.stats.components.StreakCard
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun StatistikScreen(
    navController: NavController,
    vm: StatsViewModel = viewModel(),
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isPremium by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isPremium = PremiumRepository.isPremium() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text  = "Statistiken",
            style = MaterialTheme.typography.headlineMedium,
            color = AppTheme.colors.ink,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(7, 30, 90).forEach { days ->
                FilterChip(
                    selected = state.days == days,
                    onClick  = { vm.setDays(days) },
                    label    = { Text("$days Tage") },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppTheme.colors.accent,
                        selectedLabelColor     = AppTheme.colors.onAccent,
                    ),
                )
            }
        }

        if (state.isLoading) {
            ShimmerCard(height = 80.dp)
            ShimmerCard(height = 200.dp)
            ShimmerCard(height = 200.dp)
        } else {
            StreakCard(streak = state.streak)

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
                StatCard(title = "Stimmungsverteilung") {
                    MoodBarChart(distribution = state.moodDistribution)
                }

                StatCard(title = "Dankbarkeits-Momente") {
                    GratitudePieChart(distribution = state.gratitudeDistribution)
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = title,
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.ink,
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
