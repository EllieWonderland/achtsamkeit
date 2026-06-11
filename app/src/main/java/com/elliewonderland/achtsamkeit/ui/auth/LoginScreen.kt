package com.elliewonderland.achtsamkeit.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.R
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var email         by remember { mutableStateOf("") }
    var password      by remember { mutableStateOf("") }
    var showPassword  by remember { mutableStateOf(false) }
    val snackbarState = remember { SnackbarHostState() }
    val context           = LocalContext.current
    val coroutineScope    = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.Success -> {
                val dest = if (s.isOnboardingDone) "today" else "onboarding"
                navController.navigate(dest) { popUpTo("login") { inclusive = true } }
            }
            is AuthUiState.Error -> snackbarState.showSnackbar(s.message)
            else -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Willkommen zurück",
                style = MaterialTheme.typography.headlineMedium,
                color = AppTheme.colors.ink,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Melde dich an, um weiterzumachen",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.inkSoft,
            )
            Spacer(Modifier.height(32.dp))

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
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { vm.login(email, password) },
                enabled = uiState !is AuthUiState.Loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Anmelden")
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick  = {
                    coroutineScope.launch {
                        try {
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(context.getString(R.string.google_web_client_id))
                                .build()
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()
                            val result     = credentialManager.getCredential(context, request)
                            val credential = result.credential
                            if (credential is CustomCredential &&
                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                            ) {
                                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                vm.loginWithGoogle(tokenCredential.idToken)
                            } else {
                                snackbarState.showSnackbar("Google-Anmeldung fehlgeschlagen")
                            }
                        } catch (e: GetCredentialException) {
                            snackbarState.showSnackbar(e.message ?: "Google-Anmeldung fehlgeschlagen")
                        }
                    }
                },
                enabled  = uiState !is AuthUiState.Loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Mit Google anmelden")
            }
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = { navController.navigate("register") }) {
                Text("Noch kein Konto? Registrieren")
            }
        }
    }
}
