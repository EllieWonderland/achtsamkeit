package com.elliewonderland.achtsamkeit.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.components.ShimmerListItem
import com.elliewonderland.achtsamkeit.ui.history.components.EntryListItem
import com.elliewonderland.achtsamkeit.ui.history.components.TagFilterChips
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
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
            e.freeText.contains(text, ignoreCase = true) ||
            e.guidedAnswer.contains(text, ignoreCase = true)
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
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.searchText,
            onValueChange = vm::setSearchText,
            placeholder = { Text("Suchen…", color = AppTheme.colors.inkSoft) },
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
        Spacer(Modifier.height(12.dp))

        TagFilterChips(
            selectedTag = uiState.selectedTag,
            onTagSelected = { tag -> vm.selectTag(userId, tag) },
        )
        Spacer(Modifier.height(8.dp))

        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    repeat(6) { ShimmerListItem() }
                }
            }
            visibleEntries.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📖", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = if (uiState.searchText.isNotBlank() || uiState.selectedTag != null)
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
