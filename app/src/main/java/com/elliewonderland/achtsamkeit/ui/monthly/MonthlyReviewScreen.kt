package com.elliewonderland.achtsamkeit.ui.monthly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@Composable
fun MonthlyReviewScreen(navController: NavController) {
    Column(
        modifier            = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Monatsrückblick",
            style = MaterialTheme.typography.headlineMedium,
            color = AppTheme.colors.ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "wird in Phase 8 implementiert",
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.inkSoft,
        )
    }
}
