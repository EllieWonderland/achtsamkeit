package com.elliewonderland.achtsamkeit.ui.heute

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalTime

@Composable
fun HeuteScreen(navController: NavController) {
    val vm: HeuteViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()

    val userId = Firebase.auth.currentUser?.uid ?: ""
    val isEvening = LocalTime.now().hour >= 17

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) vm.loadTodayStatus(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            "Heute",
            style = MaterialTheme.typography.displaySmall,
            color = AppTheme.colors.ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Wie war dein Tag?",
            style = MaterialTheme.typography.bodyLarge,
            color = AppTheme.colors.inkSoft,
        )
        Spacer(Modifier.height(40.dp))

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }
            uiState.hasMorningEntry && uiState.hasEveningEntry -> {
                Text(
                    "Heute abgeschlossen ✓",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppTheme.colors.accent,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Schön, dass du dir Zeit für dich genommen hast.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.inkSoft,
                )
            }
            else -> {
                if (!uiState.hasMorningEntry) {
                    Button(
                        onClick  = { navController.navigate(Screen.Entry.createRoute("morning")) },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent),
                    ) {
                        Text(
                            "Morgen starten",
                            style = MaterialTheme.typography.labelLarge,
                            color = AppTheme.colors.onAccent,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                if (isEvening && !uiState.hasEveningEntry) {
                    OutlinedButton(
                        onClick  = { navController.navigate(Screen.Entry.createRoute("evening")) },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.accent),
                    ) {
                        Text("Abend starten", style = MaterialTheme.typography.labelLarge)
                    }
                } else if (!isEvening && uiState.hasMorningEntry && !uiState.hasEveningEntry) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Abend-Eintrag ab 17 Uhr verfügbar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.inkSoft,
                    )
                }
            }
        }

        if (!uiState.isLoading && (uiState.weeklyUnlocked || uiState.monthlyUnlocked)) {
            Spacer(Modifier.height(32.dp))
            Divider(color = AppTheme.colors.hair)
            Spacer(Modifier.height(24.dp))

            Text(
                "Rückblicke",
                style = MaterialTheme.typography.titleSmall,
                color = AppTheme.colors.inkSoft,
            )
            Spacer(Modifier.height(12.dp))

            if (uiState.weeklyUnlocked) {
                OutlinedButton(
                    onClick  = { navController.navigate(Screen.WeeklyReview.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.accent),
                ) {
                    Text("Wochenrückblick", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(8.dp))
            }

            if (uiState.monthlyUnlocked) {
                OutlinedButton(
                    onClick  = { navController.navigate(Screen.MonthlyReview.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.accent),
                ) {
                    Text("MonatsRückblick", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}
