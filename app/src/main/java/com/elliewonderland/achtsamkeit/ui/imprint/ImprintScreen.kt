package com.elliewonderland.achtsamkeit.ui.imprint

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
fun ImprintScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impressum") },
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                "CapiVision ist ein Projekt von:",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.inkSoft,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Jana Fisenko Digital Solutions",
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.ink,
            )
            Text("Akazienstr. 8", style = MaterialTheme.typography.bodyLarge, color = AppTheme.colors.ink)
            Text("27793 Wildeshausen", style = MaterialTheme.typography.bodyLarge, color = AppTheme.colors.ink)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = AppTheme.colors.hair)
            Spacer(Modifier.height(16.dp))

            ImprintEntry("Kontakt", "janafisenko@hotmail.com")
            Spacer(Modifier.height(8.dp))
            ImprintEntry("Steuernummer", "123 456 789 0")
        }
    }
}

@Composable
private fun ImprintEntry(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = AppTheme.colors.inkSoft)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = AppTheme.colors.ink)
    }
}
