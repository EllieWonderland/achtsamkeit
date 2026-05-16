package com.elliewonderland.achtsamkeit.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@Composable
fun RegisterScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var name            by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword    by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val snackbarState   = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.Success -> navController.navigate("onboarding") { popUpTo("login") { inclusive = true } }
            is AuthUiState.Error   -> snackbarState.showSnackbar(s.message)
            else -> {}
        }
    }

    fun validate(): Boolean {
        validationError = when {
            name.isBlank()              -> "Bitte gib deinen Namen ein."
            !email.contains("@")        -> "Bitte gib eine gültige E-Mail-Adresse ein."
            password.length < 6         -> "Das Passwort muss mindestens 6 Zeichen lang sein."
            password != confirmPassword -> "Die Passwörter stimmen nicht überein."
            else                        -> null
        }
        return validationError == null
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Konto erstellen",
                style = MaterialTheme.typography.headlineMedium,
                color = AppTheme.colors.ink,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Erstelle dein persönliches Tagebuch",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.inkSoft,
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Dein Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-Mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Passwort") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(
                            if (showPassword) "verbergen" else "zeigen",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Passwort wiederholen") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            validationError?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { if (validate()) vm.register(email, password, name) },
                enabled = uiState !is AuthUiState.Loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Registrieren")
                }
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Bereits ein Konto? Anmelden")
            }
        }
    }
}
