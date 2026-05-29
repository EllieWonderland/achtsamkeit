package com.elliewonderland.achtsamkeit.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    fun complete(profile: Map<String, Boolean>) {
        viewModelScope.launch {
            val uid = repo.getCurrentUser()?.uid ?: return@launch
            repo.saveUserProfile(uid, profile)
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
        title = "Nur für dich & sicher",
        body  = "Deine Einträge sind privat und sicher auf EU-Servern in Deutschland gespeichert. Du kannst deine Daten jederzeit exportieren oder löschen.",
    ),
    OnboardingPage(
        emoji = "🎯",
        title = "Dein Lebensprofil",
        body  = "Damit deine täglichen Lifehacks perfekt zu deinem Alltag passen, kannst du freiwillig ein paar Angaben machen. Die Antworten werden streng vertraulich behandelt.",
    ),
)

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun OnboardingScreen(navController: NavController, vm: OnboardingViewModel = viewModel()) {
    val scope       = rememberCoroutineScope()
    val pagerState  = rememberPagerState(pageCount = { pages.size })
    val uriHandler  = LocalUriHandler.current
    var consentGiven       by remember { mutableStateOf(false) }
    var showNotifRationale by remember { mutableStateOf(false) }
    
    val isPage3Consent = pagerState.currentPage == 2
    val isLastPage      = pagerState.currentPage == pages.lastIndex

    // Store state for the 7 profile questions
    val profileMap = remember {
        mutableStateMapOf(
            "arbeit" to false,
            "mama" to false,
            "alleinerziehend" to false,
            "care_arbeit" to false,
            "oma" to false,
            "scheidung" to false,
            "studium" to false,
        )
    }

    val navigateToHeute = {
        navController.navigate("heute") { popUpTo("onboarding") { inclusive = true } }
    }
    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> navigateToHeute() }

    val handleOnboardingComplete = { withProfile: Boolean ->
        val finalProfile = if (withProfile) profileMap.toMap() else emptyMap()
        vm.complete(finalProfile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            showNotifRationale = true
        } else {
            navigateToHeute()
        }
    }

    if (showNotifRationale) {
        AlertDialog(
            onDismissRequest = { showNotifRationale = false; navigateToHeute() },
            title = { Text("Erinnerungen aktivieren?") },
            text  = {
                Text(
                    "Damit du deine täglichen Routinen nicht vergisst, kannst du Erinnerungen " +
                    "aktivieren.\n\nDu kannst Benachrichtigungen jederzeit unter " +
                    "Profil → Benachrichtigungen anpassen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.ink,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showNotifRationale = false
                    notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) { Text("Aktivieren") }
            },
            dismissButton = {
                TextButton(onClick = { showNotifRationale = false; navigateToHeute() }) {
                    Text("Vielleicht später")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = !isPage3Consent || consentGiven, // Lock navigation to page 4 until consent is given
        ) { pageIndex ->
            val page = pages[pageIndex]
            
            if (pageIndex == 3) {
                // Page 4: Profile customization scrollable list
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(page.emoji, style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        page.title,
                        style     = MaterialTheme.typography.headlineSmall,
                        color     = AppTheme.colors.ink,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        page.body,
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = AppTheme.colors.inkSoft,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))

                    // Checklist
                    val checklistItems = listOf(
                        Triple("arbeit", "Berufsleben & Karriere", "Befindest du dich aktuell in einem fordernden Berufsleben oder einer aktiven Karrierephase?"),
                        Triple("mama", "Elternschaft & Familie", "Begleitest du Kinder auf ihrem Lebensweg und managest das Familienleben?"),
                        Triple("alleinerziehend", "Haushalt & Erziehung allein", "Meisterst du deinen Haushalt und die Kindererziehung vorwiegend allein auf deinen Schultern?"),
                        Triple("care_arbeit", "Unterstützung von Angehörigen", "Kümmerst du dich nebenbei liebevoll um Angehörige oder unterstützt Familienmitglieder?"),
                        Triple("oma", "Generation Großeltern", "Gehörst du zur Generation der Großeltern und genießt die gemeinsame Zeit mit Enkelkindern?"),
                        Triple("scheidung", "Neuorientierung & Neuanfang", "Gehst du gerade durch eine Phase der Neuorientierung oder des partnerschaftlichen Neuanfangs?"),
                        Triple("studium", "Studium & Ausbildung", "Bist du aktuell im Studium, in der Ausbildung oder lernst intensiv für Prüfungen?"),
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        checklistItems.forEach { (key, label, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppTheme.colors.surface)
                                    .clickable { profileMap[key] = !(profileMap[key] ?: false) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = profileMap[key] ?: false,
                                    onCheckedChange = { profileMap[key] = it },
                                    colors = CheckboxDefaults.colors(checkedColor = AppTheme.colors.accent)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = AppTheme.colors.ink
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = AppTheme.colors.inkSoft
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    
                    // Secure Note
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppTheme.colors.accent.copy(alpha = 0.06f))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛡️ Freiwillig & Sicher: Deine Angaben werden verschlüsselt und sicher auf EU-Servern in Deutschland gespeichert. Es findet kein Tracking oder Weitergabe statt.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = AppTheme.colors.inkSoft,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            } else {
                // Pages 1-3
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

        Spacer(Modifier.height(20.dp))

        if (isPage3Consent) {
            // Privacy consent on page 3
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.fillMaxWidth(),
                ) {
                    Checkbox(
                        checked         = consentGiven,
                        onCheckedChange = { consentGiven = it },
                        colors = CheckboxDefaults.colors(checkedColor = AppTheme.colors.accent)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Ich habe die ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.ink,
                            )
                            Text(
                                "Datenschutzerklärung",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = AppTheme.colors.accent,
                                modifier = Modifier.clickable { uriHandler.openUri("https://elliewonderland.de/datenschutz") }
                            )
                        }
                        Text(
                            "gelesen und stimme ihr uneingeschränkt zu.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.colors.ink,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(3)
                        }
                    },
                    enabled  = consentGiven,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent)
                ) {
                    Text("Zustimmen & Weiter", color = AppTheme.colors.onAccent)
                }
            }
        } else if (isLastPage) {
            // Page 4: Profile Complete & Skip Buttons
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { handleOnboardingComplete(true) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent)
                ) {
                    Text("Impulse personalisieren & starten", color = AppTheme.colors.onAccent)
                }
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { handleOnboardingComplete(false) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Überspringen & starten", color = AppTheme.colors.inkSoft)
                }
            }
        } else {
            // Pages 1-2: "Weiter" button
            Button(
                onClick  = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent)
            ) {
                Text("Weiter", color = AppTheme.colors.onAccent)
            }
        }
    }
}
