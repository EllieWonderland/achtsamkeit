package com.elliewonderland.achtsamkeit.ui.monthly

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.data.repository.PremiumRepository
import com.elliewonderland.achtsamkeit.data.repository.ReviewRepository
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.entry.components.SectionCard
import com.elliewonderland.achtsamkeit.ui.premium.PaywallCard
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

private val MONTHLY_QUESTIONS = listOf(
    "Was war mein größtes Wachstum oder meine wichtigste Erkenntnis in diesem Monat?",
    "Welche Muster oder Gewohnheiten habe ich bei mir beobachtet?",
    "Was hat mir in diesem Monat die meiste Energie gegeben — und was hat sie geraubt?",
    "Welcher Moment aus diesem Monat wird mir am längsten in Erinnerung bleiben?",
    "Was möchte ich im nächsten Monat loslassen oder hinter mir lassen?",
    "Welche eine Veränderung würde mein Leben im nächsten Monat am meisten bereichern?",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyReviewScreen(navController: NavController) {
    val scope    = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val repo     = remember { ReviewRepository() }
    val uid      = Firebase.auth.currentUser?.uid ?: ""
    val context  = LocalContext.current

    var isPremium         by remember { mutableStateOf(false) }
    var isCheckingPremium by remember { mutableStateOf(true) }
    var answers           by remember { mutableStateOf(List(MONTHLY_QUESTIONS.size) { "" }) }
    var isSaving          by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isPremium = PremiumRepository.isPremium()
        isCheckingPremium = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Monatsrückblick",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppTheme.colors.ink,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Zurück",
                            tint = AppTheme.colors.ink,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        when {
            isCheckingPremium -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AppTheme.colors.accent)
                }
            }
            !isPremium -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PaywallCard(
                        description = "Der Monatsrückblick ist ein Premium-Feature. Upgrade, um jeden Monat tiefer in deine Entwicklung einzutauchen.",
                        onUpgrade   = {
                            scope.launch {
                                val activity = context as? Activity ?: return@launch
                                val success = PremiumRepository.purchase(activity)
                                if (success) isPremium = true
                            }
                        },
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        "Ein ganzer Monat liegt hinter dir. Nimm dir Zeit für diese tiefere Reflexion.",
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = AppTheme.colors.inkSoft,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    )

                    MONTHLY_QUESTIONS.forEachIndexed { index, question ->
                        SectionCard(title = question) {
                            OutlinedTextField(
                                value         = answers[index],
                                onValueChange = { new ->
                                    answers = answers.toMutableList().also { it[index] = new }
                                },
                                modifier      = Modifier.fillMaxWidth(),
                                placeholder   = { Text("Deine Gedanken…", color = AppTheme.colors.inkSoft) },
                                minLines      = 3,
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = AppTheme.colors.accent,
                                    unfocusedBorderColor = AppTheme.colors.hair,
                                    focusedTextColor     = AppTheme.colors.ink,
                                    unfocusedTextColor   = AppTheme.colors.ink,
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (uid.isBlank()) return@Button
                            isSaving = true
                            scope.launch {
                                val pairs = MONTHLY_QUESTIONS.zip(answers)
                                runCatching { repo.saveReview(uid, "monthly_review", pairs) }
                                    .onSuccess {
                                        snackbar.showSnackbar("Monatsrückblick gespeichert!")
                                        navController.navigate(Screen.Tagebuch.route) {
                                            popUpTo(Screen.Heute.route) { inclusive = false }
                                        }
                                    }
                                    .onFailure {
                                        snackbar.showSnackbar("Fehler beim Speichern.")
                                    }
                                isSaving = false
                            }
                        },
                        enabled  = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent),
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (isSaving) "Wird gespeichert…" else "Rückblick speichern",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}
