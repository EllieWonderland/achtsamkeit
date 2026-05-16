package com.elliewonderland.achtsamkeit.ui.stats

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.stats.components.GratitudePieChart
import com.elliewonderland.achtsamkeit.ui.stats.components.MoodBarChart
import com.elliewonderland.achtsamkeit.ui.stats.components.StreakCard
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@Composable
fun StatistikScreen(
    navController: NavController,
    vm: StatsViewModel = viewModel(),
) {
    val state by vm.state.collectAsState()

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
            Box(
                modifier        = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }
        } else {
            StreakCard(streak = state.streak)

            StatCard(title = "Stimmungsverteilung") {
                MoodBarChart(distribution = state.moodDistribution)
            }

            StatCard(title = "Dankbarkeits-Momente") {
                GratitudePieChart(distribution = state.gratitudeDistribution)
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
