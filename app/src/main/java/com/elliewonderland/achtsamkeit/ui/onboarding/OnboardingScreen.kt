package com.elliewonderland.achtsamkeit.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// ─── ViewModel ───────────────────────────────────────────────────────────────

class OnboardingViewModel : ViewModel() {
    private val repo     = AuthRepository()
    val isComplete       = MutableStateFlow(false)

    fun complete() {
        viewModelScope.launch {
            val uid = repo.getCurrentUser()?.uid ?: return@launch
            repo.completeOnboarding(uid)
            isComplete.value = true
        }
    }
}

// ─── Data ────────────────────────────────────────────────────────────────────

private data class OnboardingPage(val title: String, val body: String, val emoji: String)

private val pages = listOf(
    OnboardingPage(
        emoji = "🌿",
        title = "Achtsamkeit in 3 Minuten",
        body  = "Dein persönliches Tagebuch für tägliche Selbstreflexion — morgens & abends. Kein langer Text, kein Druck.",
    ),
    OnboardingPage(
        emoji = "✨",
        title = "Einfach & schnell",
        body  = "Ein paar Klicks reichen für einen vollständigen Eintrag. Auch 3 Checkboxen ohne Text zählen als Erfolg.",
    ),
    OnboardingPage(
        emoji = "🔒",
        title = "Nur für dich",
        body  = "Deine Einträge sind privat und sicher auf EU-Servern gespeichert. Du kannst deine Daten jederzeit exportieren oder löschen.",
    ),
)

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun OnboardingScreen(navController: NavController, vm: OnboardingViewModel = viewModel()) {
    val scope       = rememberCoroutineScope()
    val pagerState  = rememberPagerState(pageCount = { pages.size })
    val uriHandler  = LocalUriHandler.current
    var consentGiven by remember { mutableStateOf(false) }
    val isLastPage   = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.weight(1f),
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(page.emoji, style = MaterialTheme.typography.displayMedium)
                Spacer(Modifier.height(24.dp))
                Text(
                    page.title,
                    style     = MaterialTheme.typography.headlineSmall,
                    color     = AppTheme.colors.ink,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    page.body,
                    style     = MaterialTheme.typography.bodyLarge,
                    color     = AppTheme.colors.inkSoft,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Pager dots
        Row(horizontalArrangement = Arrangement.Center) {
            pages.indices.forEach { i ->
                val isActive = pagerState.currentPage == i
                Box(
                    Modifier
                        .size(if (isActive) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isActive) AppTheme.colors.accent else AppTheme.colors.inkSoft)
                )
                if (i < pages.lastIndex) Spacer(Modifier.width(6.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        if (isLastPage) {
            // Privacy consent
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth(),
            ) {
                Checkbox(
                    checked         = consentGiven,
                    onCheckedChange = { consentGiven = it },
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "Ich habe die",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.ink,
                    )
                    TextButton(
                        onClick      = { uriHandler.openUri("https://elliewonderland.de/datenschutz") },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    ) {
                        Text(
                            "Datenschutzerklärung",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.colors.accent,
                        )
                    }
                    Text(
                        "gelesen und stimme zu.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.ink,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick  = {
                    vm.complete()
                    navController.navigate("heute") { popUpTo("onboarding") { inclusive = true } }
                },
                enabled  = consentGiven,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Ich stimme zu und möchte loslegen")
            }
        } else {
            Button(
                onClick  = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Weiter")
            }
        }
    }
}
