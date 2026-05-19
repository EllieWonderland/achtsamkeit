package com.elliewonderland.achtsamkeit.ui.datenschutz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatenschutzScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datenschutzerklärung") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DatenschutzSection(
                title = "1. Verantwortliche Stelle",
                body  = "Jana Fisenko Digital Solutions\nAkazienstr. 8\n27793 Wildeshausen\n\nKontakt: janafisenko@hotmail.com",
            )

            DatenschutzSection(
                title = "2. Erhobene Daten",
                body  = "Die App erhebt folgende personenbezogene Daten:\n" +
                        "• E-Mail-Adresse (für Konto-Erstellung und Login)\n" +
                        "• Profilbild (optional, auf Wunsch hochgeladen)\n" +
                        "• Tagebucheinträge, Stimmungsdaten und Routinen (gespeichert in deinem Konto)",
            )

            DatenschutzSection(
                title = "3. Zweck der Datenverarbeitung",
                body  = "Deine Daten werden ausschließlich zur Bereitstellung der App-Funktionen genutzt:\n" +
                        "• Authentifizierung über Firebase Auth\n" +
                        "• Speicherung deiner Einträge in deiner persönlichen Cloud (Firebase Firestore)\n" +
                        "• Versand von Push-Benachrichtigungen über Firebase Cloud Messaging",
            )

            DatenschutzSection(
                title = "4. Drittanbieter",
                body  = "Die App nutzt Dienste von Google Firebase (Firebase Auth, Firestore, Cloud Messaging) " +
                        "sowie RevenueCat für die Verwaltung von In-App-Käufen. " +
                        "Beide Dienste verarbeiten Daten gemäß ihren eigenen Datenschutzrichtlinien.",
            )

            DatenschutzSection(
                title = "5. Datenlöschung",
                body  = "Du kannst dein Konto und alle gespeicherten Daten jederzeit über " +
                        "Profil → Konto löschen unwiderruflich entfernen.",
            )

            DatenschutzSection(
                title = "6. Deine Rechte",
                body  = "Du hast das Recht auf Auskunft, Berichtigung, Löschung und Einschränkung " +
                        "der Verarbeitung deiner personenbezogenen Daten (Art. 15–18 DSGVO). " +
                        "Wende dich dazu an: janafisenko@hotmail.com",
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = AppTheme.colors.hair)
            Spacer(Modifier.height(4.dp))
            Text(
                "Stand: Mai 2026",
                style = MaterialTheme.typography.labelSmall,
                color = AppTheme.colors.inkSoft,
            )
        }
    }
}

@Composable
private fun DatenschutzSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = AppTheme.colors.ink)
        Text(body,  style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.inkSoft)
    }
}
