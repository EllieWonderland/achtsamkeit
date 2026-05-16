package com.elliewonderland.achtsamkeit.ui.quote

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun QuoteScreen(navController: NavController, entryId: String) {
    val vm: QuoteViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    val userId = Firebase.auth.currentUser?.uid ?: ""

    LaunchedEffect(entryId) {
        if (userId.isNotBlank() && entryId.isNotBlank()) vm.load(userId, entryId)
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val s = uiState) {
            is QuoteUiState.Loading -> {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }
            is QuoteUiState.Error -> {
                Text(
                    s.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.inkSoft,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate(Screen.Heute.route) },
                    colors  = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent),
                ) {
                    Text("Weiter", color = AppTheme.colors.onAccent)
                }
            }
            is QuoteUiState.Ready -> {
                Text(
                    text      = "\u201E${s.quote.text}\u201C",
                    style     = MaterialTheme.typography.headlineSmall,
                    color     = AppTheme.colors.ink,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text  = "– ${s.quote.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.inkSoft,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(32.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { vm.toggleFavorite(userId) }) {
                        Icon(
                            imageVector = if (s.isFavorite) Icons.Filled.Favorite
                                          else Icons.Filled.FavoriteBorder,
                            contentDescription = if (s.isFavorite) "Aus Favoriten entfernen"
                                                 else "Zu Favoriten hinzufügen",
                            tint     = AppTheme.colors.accent,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    IconButton(onClick = {
                        val shareText = "\u201E${s.quote.text}\u201C – ${s.quote.author}"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                    }) {
                        Icon(
                            imageVector        = Icons.Outlined.Share,
                            contentDescription = "Spruch teilen",
                            tint               = AppTheme.colors.inkSoft,
                            modifier           = Modifier.size(28.dp),
                        )
                    }
                }
                Spacer(Modifier.height(48.dp))
                Button(
                    onClick  = { navController.navigate(Screen.Heute.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent),
                ) {
                    Text(
                        "Weiter",
                        style = MaterialTheme.typography.labelLarge,
                        color = AppTheme.colors.onAccent,
                    )
                }
            }
        }
    }
}
