package com.elliewonderland.achtsamkeit.ui.settings

import android.Manifest
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import com.elliewonderland.achtsamkeit.data.repository.NotificationRepository
import com.elliewonderland.achtsamkeit.service.NotificationScheduler
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(navController: NavController) {
    val context       = LocalContext.current
    val scope         = rememberCoroutineScope()
    val snackbar      = remember { SnackbarHostState() }

    val authRepo  = remember { AuthRepository() }
    val notifRepo = remember { NotificationRepository() }
    val uid = authRepo.getCurrentUser()?.uid

    var morningTime by remember { mutableStateOf("08:00") }
    var eveningTime by remember { mutableStateOf("21:00") }
    var permissionGranted by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val alarmManager = remember { context.getSystemService(AlarmManager::class.java) }
    var canScheduleExact by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                canScheduleExact = alarmManager.canScheduleExactAlarms()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (!granted) {
            scope.launch { snackbar.showSnackbar("Bitte erlaube Benachrichtigungen in den Systemeinstellungen.") }
        }
    }

    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        val (m, e) = notifRepo.getNotificationTimes(uid)
        morningTime = m
        eveningTime = e

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissionGranted = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Benachrichtigungen",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                "Leg fest, wann du täglich an dein Tagebuch erinnert werden möchtest.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.inkSoft,
            )

            TimePickerRow(
                label    = "Morgen-Erinnerung",
                time     = morningTime,
                onPicked = { morningTime = it },
                context  = context,
            )

            TimePickerRow(
                label    = "Abend-Erinnerung",
                time     = eveningTime,
                onPicked = { eveningTime = it },
                context  = context,
            )

            Spacer(Modifier.height(8.dp))

            if (!canScheduleExact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Für pünktliche Erinnerungen benötigt die App die Erlaubnis für exakte Alarme.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                    OutlinedButton(
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Exakte Alarme erlauben", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Button(
                onClick = {
                    if (uid == null) return@Button
                    if (!permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        return@Button
                    }
                    isSaving = true
                    scope.launch {
                        notifRepo.saveNotificationTimes(uid, morningTime, eveningTime)
                        NotificationScheduler.scheduleAlarms(context, morningTime, eveningTime)
                        isSaving = false
                        snackbar.showSnackbar("Erinnerungszeiten gespeichert!")
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.accent,
                ),
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isSaving) "Wird gespeichert…" else "Speichern",
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            if (!permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Text(
                    "Benachrichtigungen sind noch nicht erlaubt. Tippe auf Speichern, um die Berechtigung anzufragen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun TimePickerRow(
    label: String,
    time: String,
    onPicked: (String) -> Unit,
    context: android.content.Context,
) {
    val parts  = time.split(":")
    val hour   = parts.getOrNull(0)?.toIntOrNull() ?: 8
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    Row(
        modifier            = Modifier.fillMaxWidth(),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = AppTheme.colors.ink)
            Text(time, style = MaterialTheme.typography.displaySmall, color = AppTheme.colors.accent)
        }
        OutlinedButton(
            onClick = {
                TimePickerDialog(context, { _, h, m ->
                    onPicked("%02d:%02d".format(h, m))
                }, hour, minute, true).show()
            }
        ) {
            Text("Ändern", style = MaterialTheme.typography.labelMedium)
        }
    }
}
