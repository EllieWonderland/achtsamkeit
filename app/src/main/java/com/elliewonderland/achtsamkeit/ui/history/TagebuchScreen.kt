package com.elliewonderland.achtsamkeit.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.model.FavoriteQuote
import com.elliewonderland.achtsamkeit.ui.components.ShimmerListItem
import com.elliewonderland.achtsamkeit.ui.history.components.EntryListItem
import com.elliewonderland.achtsamkeit.ui.history.components.formatDate
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun TagebuchScreen(navController: NavController) {
    val vm: HistoryViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()
    val userId = Firebase.auth.currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) vm.load(userId)
    }

    val visibleEntries = remember(uiState.entries, uiState.searchText) {
        val text = uiState.searchText.trim()
        if (text.isEmpty()) uiState.entries
        else uiState.entries.filter { e ->
            val formattedDate = formatDate(e.dateStr)
            e.freeText.contains(text, ignoreCase = true) ||
            e.guidedAnswer.contains(text, ignoreCase = true) ||
            formattedDate.contains(text, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Mein Tagebuch",
            style = MaterialTheme.typography.headlineSmall,
            color = AppTheme.colors.ink,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.searchText,
            onValueChange = vm::setSearchText,
            placeholder = { Text("Suchen nach Text, Datum, Wochentag…", color = AppTheme.colors.inkSoft) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = AppTheme.colors.inkSoft,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppTheme.colors.accent,
                unfocusedBorderColor = AppTheme.colors.hair,
                cursorColor          = AppTheme.colors.accent,
                focusedTextColor     = AppTheme.colors.ink,
                unfocusedTextColor   = AppTheme.colors.ink,
            ),
            shape = MaterialTheme.shapes.medium,
        )
        Spacer(Modifier.height(16.dp))

        // Elegantes Favoriten-Karussell (nur wenn Favoriten vorhanden sind und nicht gesucht wird)
        if (uiState.favorites.isNotEmpty() && uiState.searchText.isEmpty()) {
            FavoritesCarousel(
                favorites = uiState.favorites,
                onUnfavorite = { fav -> vm.toggleFavorite(userId, fav) },
            )
            Spacer(Modifier.height(16.dp))
        }

        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    repeat(6) { ShimmerListItem() }
                }
            }
            visibleEntries.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = if (uiState.searchText.isNotBlank())
                                "Keine Einträge gefunden."
                            else
                                "Noch keine Einträge vorhanden.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppTheme.colors.inkSoft,
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(visibleEntries, key = { it.id }) { entry ->
                        EntryListItem(
                            entry = entry,
                            onClick = { navController.navigate(Screen.EntryDetail.createRoute(entry.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesCarousel(
    favorites: List<FavoriteQuote>,
    onUnfavorite: (FavoriteQuote) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (favorites.isEmpty()) return

    val colors = AppTheme.colors
    val pagerState = rememberPagerState(pageCount = { favorites.size })

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "FAVORISIERTE SPRÜCHE",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            Text(
                text = "${pagerState.currentPage + 1}/${favorites.size}",
                style = MaterialTheme.typography.labelSmall,
                color = colors.accent,
            )
        }
        Spacer(Modifier.height(8.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val fav = favorites[page]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, colors.hair, RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                colors.surface,
                                colors.accent2.copy(alpha = 0.15f),
                            )
                        )
                    )
            ) {
                // Subtle Glow Orb top-right
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-30).dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    colors.accent3.copy(alpha = 0.40f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = fav.text,
                        style = SerifItalic.copy(fontSize = 16.sp),
                        color = colors.ink,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(12.dp))
                    IconButton(
                        onClick = { onUnfavorite(fav) },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Aus Favoriten entfernen",
                            tint = colors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (favorites.size > 1) {
            Spacer(Modifier.height(8.dp))
            // Subtle dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(favorites.size) { index ->
                    val isSelected = pagerState.currentPage == index
                      Box(
                          modifier = Modifier
                              .size(if (isSelected) 6.dp else 4.dp)
                              .clip(CircleShape)
                              .background(if (isSelected) colors.accent else colors.inkSoft.copy(alpha = 0.4f))
                      )
                }
            }
        }
    }
}
