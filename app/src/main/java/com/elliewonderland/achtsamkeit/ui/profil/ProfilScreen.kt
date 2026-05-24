package com.elliewonderland.achtsamkeit.ui.profil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ProfilScreen(navController: NavController, choice: ThemeChoice) {
    val vm: ProfilViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()
    val navigateToLogin by vm.navigateToLogin.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val userId = Firebase.auth.currentUser?.uid ?: ""

    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCropDialog by remember { mutableStateOf(false) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { 
            pendingImageUri = it
            showCropDialog = true
        } },
    )

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) vm.loadProfile(userId)
    }

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            vm.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable {
                        if (uiState.photoUrl.isNotBlank()) {
                            showPhotoOptionsDialog = true
                        } else {
                            photoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    },
                contentAlignment = Alignment.BottomEnd,
            ) {
                if (uiState.photoUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model              = uiState.photoUrl,
                            contentDescription = "Profilbild",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX       = uiState.photoScale,
                                    scaleY       = uiState.photoScale,
                                    translationX = uiState.photoOffsetX,
                                    translationY = uiState.photoOffsetY
                                ),
                        )
                    }
                } else {
                    Image(
                        painter            = painterResource(id = com.elliewonderland.achtsamkeit.R.drawable.logo_round),
                        contentDescription = "App-Logo",
                        modifier           = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
                Box(
                    modifier         = Modifier
                        .size(26.dp)
                        .background(AppTheme.colors.accent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = null,
                        tint               = AppTheme.colors.onAccent,
                        modifier           = Modifier.size(15.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (uiState.isEditingName) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value         = uiState.nameInput,
                        onValueChange = { vm.onNameInput(it) },
                        label         = { Text("Name") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { vm.saveDisplayName(userId) }) {
                        Icon(Icons.Outlined.Check, contentDescription = "Speichern", tint = AppTheme.colors.accent)
                    }
                    IconButton(onClick = { vm.cancelEditName() }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Abbrechen", tint = AppTheme.colors.inkSoft)
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (uiState.displayName.isNotBlank()) {
                        Text(
                            uiState.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            color = AppTheme.colors.ink,
                        )
                        Spacer(Modifier.width(4.dp))
                        IconButton(onClick = { vm.startEditName() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Namen bearbeiten",
                                tint = AppTheme.colors.inkSoft, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        TextButton(onClick = { vm.startEditName() }) {
                            Text("Namen eingeben", color = AppTheme.colors.accent)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            Text(
                uiState.email,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.inkSoft,
            )

            Spacer(Modifier.height(40.dp))

            ProfilButton("Aussehen anpassen") { navController.navigate(Screen.ThemePicker.route) }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Benachrichtigungen") { navController.navigate(Screen.NotifSettings.route) }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Favorisierte Sprüche") { navController.navigate(Screen.Favorites.route) }

            Spacer(Modifier.height(32.dp))
            HorizontalDivider(color = AppTheme.colors.hair)
            Spacer(Modifier.height(32.dp))

            ProfilButton("Impressum") { navController.navigate(Screen.Impressum.route) }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Datenschutzerklärung") { navController.navigate(Screen.Datenschutz.route) }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Meine Daten exportieren") { vm.showExportDialog() }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Alle Einträge zurücksetzen") { vm.showResetDialog() }
            Spacer(Modifier.height(12.dp))
            ProfilButton("Abmelden") { vm.logout() }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick  = { vm.showDeleteDialog() },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Konto löschen", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(40.dp))
        }

        if (uiState.isLoading) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter),
        ) { data -> Snackbar(snackbarData = data) }
    }

    // ── Konto löschen ────────────────────────────────────────────────────────
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { vm.hideDeleteDialog() },
            title = { Text("Konto wirklich löschen?", style = MaterialTheme.typography.titleMedium) },
            text  = {
                Text(
                    "Alle deine Einträge, Favoriten und Daten werden unwiderruflich gelöscht.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = AppTheme.colors.inkSoft,
                    textAlign = TextAlign.Start,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.deleteAccount(userId) },
                    colors  = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Ja, Konto löschen") }
            },
            dismissButton = {
                TextButton(onClick = { vm.hideDeleteDialog() }) { Text("Abbrechen") }
            },
        )
    }

    // ── Daten zurücksetzen ───────────────────────────────────────────────────
    if (uiState.showResetDialog) {
        AlertDialog(
            onDismissRequest = { vm.hideResetDialog() },
            title = { Text("Alle Daten zurücksetzen?", style = MaterialTheme.typography.titleMedium) },
            text  = {
                Text(
                    "Alle Einträge, Statistiken und Spruch-Favoriten werden gelöscht. Dein Konto bleibt erhalten.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = AppTheme.colors.inkSoft,
                    textAlign = TextAlign.Start,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.resetAllData(userId) },
                    colors  = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Ja, zurücksetzen") }
            },
            dismissButton = {
                TextButton(onClick = { vm.hideResetDialog() }) { Text("Abbrechen") }
            },
        )
    }

    // ── Export-Format wählen ─────────────────────────────────────────────────
    if (uiState.showExportDialog) {
        AlertDialog(
            onDismissRequest = { vm.hideExportDialog() },
            title = { Text("Format wählen", style = MaterialTheme.typography.titleMedium) },
            text  = {
                Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    ExportFormatButton("JSON (.json)") { vm.exportData(userId, ExportFormat.JSON, context) }
                    ExportFormatButton("PDF (.pdf)")   { vm.exportData(userId, ExportFormat.PDF,  context) }
                    ExportFormatButton("Excel (.xlsx)") { vm.exportData(userId, ExportFormat.EXCEL, context) }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { vm.hideExportDialog() }) { Text("Abbrechen") }
            },
        )
    }

    // ── Photo Options Dialog ────────────────────────────────────────────────
    if (showPhotoOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoOptionsDialog = false },
            title = { Text("Profilbild anpassen", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            showPhotoOptionsDialog = false
                            photoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.ink)
                    ) {
                        Text("Neues Foto auswählen")
                    }
                    OutlinedButton(
                        onClick = {
                            showPhotoOptionsDialog = false
                            pendingImageUri = null // adjusting current photo
                            showCropDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.ink)
                    ) {
                        Text("Foto-Ausschnitt anpassen")
                    }
                    OutlinedButton(
                        onClick = {
                            showPhotoOptionsDialog = false
                            vm.deleteProfilePhoto(userId)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Foto löschen")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoOptionsDialog = false }) { Text("Abbrechen") }
            }
        )
    }

    // ── Crop & Adjustment Dialog ────────────────────────────────────────────
    if (showCropDialog) {
        val cropImageModel = pendingImageUri ?: uiState.photoUrl
        
        var scale by remember { mutableStateOf(if (pendingImageUri == null) uiState.photoScale else 1.0f) }
        var offset by remember { mutableStateOf(if (pendingImageUri == null) Offset(uiState.photoOffsetX, uiState.photoOffsetY) else Offset.Zero) }

        AlertDialog(
            onDismissRequest = { showCropDialog = false },
            title = { Text("Ausschnitt anpassen", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Bewege das Bild mit einem Finger und zoome es mit zwei Fingern oder dem Regler.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.inkSoft,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.surfaceAlt)
                            .border(2.dp, AppTheme.colors.accent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = cropImageModel,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        scale = (scale * zoom).coerceIn(1.0f, 4.0f)
                                        offset = offset + pan
                                    }
                                }
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                        )
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text("Zoom", style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.inkSoft)
                    Slider(
                        value = scale,
                        onValueChange = { scale = it },
                        valueRange = 1.0f..4.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = AppTheme.colors.accent,
                            activeTrackColor = AppTheme.colors.accent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCropDialog = false
                        if (pendingImageUri != null) {
                            vm.uploadProfilePhoto(userId, pendingImageUri!!, context, scale, offset.x, offset.y)
                        } else {
                            vm.savePhotoCropParams(userId, scale, offset.x, offset.y)
                        }
                    }
                ) {
                    Text("Speichern", color = AppTheme.colors.accent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCropDialog = false }) {
                    Text("Abbrechen", color = AppTheme.colors.inkSoft)
                }
            }
        )
    }
}

@Composable
private fun ProfilButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.ink),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ExportFormatButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.accent),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}
