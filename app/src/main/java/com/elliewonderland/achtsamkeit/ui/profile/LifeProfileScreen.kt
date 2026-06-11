package com.elliewonderland.achtsamkeit.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeProfileScreen(navController: NavController, vm: LifeProfileViewModel = viewModel()) {
    val userId = Firebase.auth.currentUser?.uid ?: ""
    val isLoading by vm.isLoading.collectAsState()
    val saveSuccess by vm.saveSuccess.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            vm.loadProfile(userId)
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            vm.resetSaveSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Impuls-Personalisierung",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppTheme.colors.ink,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Dein Lebensprofil",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppTheme.colors.ink,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Indem du freiwillig deine Lebenssituation auswählst, können wir die täglichen Lifehacks perfekt auf dich abstimmen. Unzutreffende Themen werden komplett ausgeblendet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.inkSoft,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                // Checklist Items
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
                                .clickable { vm.toggleKey(key) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = vm.profileMap[key] ?: false,
                                onCheckedChange = { vm.toggleKey(key) },
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

                // Privacy Reassurance Card
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

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { if (userId.isNotBlank()) vm.saveProfile(userId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = AppTheme.colors.onAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Einstellungen speichern", color = AppTheme.colors.onAccent)
                }

                Spacer(Modifier.height(40.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppTheme.colors.accent)
                }
            }
        }
    }
}
