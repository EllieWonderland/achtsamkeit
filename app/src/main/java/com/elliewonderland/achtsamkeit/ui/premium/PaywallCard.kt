package com.elliewonderland.achtsamkeit.ui.premium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@Composable
fun PaywallCard(
    description: String,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
    ) {
        Column(
            modifier            = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector        = Icons.Outlined.Lock,
                contentDescription = null,
                tint               = AppTheme.colors.accent,
                modifier           = Modifier.size(36.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Premium-Feature",
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.ink,
            )
            Text(
                description,
                style     = MaterialTheme.typography.bodySmall,
                color     = AppTheme.colors.inkSoft,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick  = onUpgrade,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent),
            ) {
                Text(
                    "Premium freischalten",
                    style = MaterialTheme.typography.labelLarge,
                    color = AppTheme.colors.onAccent,
                )
            }
        }
    }
}
