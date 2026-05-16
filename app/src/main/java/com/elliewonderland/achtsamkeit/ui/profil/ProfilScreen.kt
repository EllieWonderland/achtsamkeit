package com.elliewonderland.achtsamkeit.ui.profil

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private const val DATENSCHUTZ_URL = "https://elliewonderland.de/datenschutz"

@Composable
fun ProfilScreen(navController: NavController, choice: ThemeChoice) {
    val vm: ProfilViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()
    val navigateToLogin by vm.navigateToLogin.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val userId = Firebase.auth.currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) vm.loadProfile(userId)
    }

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            vm.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))

            // Avatar mit erstem Buchstaben des Namens
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(AppTheme.colors.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppTheme.colors.onAccent,
                )
            }

            Spacer(Modifier.height(16.dp))

            if (uiState.displayName.isNotBlank()) {
                Text(
                    uiState.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = AppTheme.colors.ink,
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                uiState.email,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.inkSoft,
            )

            Spacer(Modifier.height(40.dp))

            ProfilButton("Aussehen anpassen") { navController.navigate(Screen.ThemePicker.route) }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Benachrichtigungen") { navController.navigate(Screen.NotifSettings.route) }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Favorisierte Sprüche") { navController.navigate(Screen.Favorites.route) }

            Spacer(Modifier.height(32.dp))
            HorizontalDivider(color = AppTheme.colors.hair)
            Spacer(Modifier.height(32.dp))

            ProfilButton("Datenschutzerklärung") {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DATENSCHUTZ_URL)))
            }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Meine Daten exportieren") { vm.exportData(userId, context) }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Abmelden") { vm.logout() }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { vm.showDeleteDialog() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text("Konto löschen", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(40.dp))
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { vm.hideDeleteDialog() },
            title = {
                Text("Konto wirklich löschen?", style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Text(
                    "Alle deine Einträge, Favoriten und Daten werden unwiderruflich gelöscht.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.inkSoft,
                    textAlign = TextAlign.Start,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.deleteAccount(userId) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Ja, Konto löschen")
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.hideDeleteDialog() }) {
                    Text("Abbrechen")
                }
            },
        )
    }
}

@Composable
private fun ProfilButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.ink),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}
