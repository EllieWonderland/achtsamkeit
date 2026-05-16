package com.elliewonderland.achtsamkeit.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.model.FavoriteQuote
import com.elliewonderland.achtsamkeit.ui.components.ShimmerListItem
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    val vm: FavoritesViewModel = viewModel()
    val favorites by vm.favorites.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val userId = Firebase.auth.currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) vm.load(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorisierte Sprüche", color = AppTheme.colors.ink) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = AppTheme.colors.ink,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                isLoading -> Column(modifier = Modifier.fillMaxSize()) {
                    repeat(5) { ShimmerListItem() }
                }
                favorites.isEmpty() -> {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Noch keine Favoriten",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppTheme.colors.ink,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tippe auf das Herz im Spruch-Screen, um Sprüche zu speichern.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.colors.inkSoft,
                        )
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(favorites, key = { it.id }) { fav ->
                        FavoriteItem(
                            quote = fav,
                            onUnfavorite = { vm.unfavorite(userId, fav) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteItem(quote: FavoriteQuote, onUnfavorite: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = quote.text,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.ink,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onUnfavorite,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Aus Favoriten entfernen",
                    tint = AppTheme.colors.accent,
                )
            }
        }
    }
}
