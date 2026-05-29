package com.elliewonderland.achtsamkeit.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val isOnboardingDone: Boolean) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()
    val uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState.value = AuthUiState.Loading
            repo.loginWithEmail(email, password).fold(
                onSuccess = {
                    val uid  = repo.getCurrentUser()!!.uid
                    val done = repo.isOnboardingComplete(uid)
                    uiState.value = AuthUiState.Success(done)
                },
                onFailure = { uiState.value = AuthUiState.Error(it.message ?: "Anmeldung fehlgeschlagen") }
            )
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            uiState.value = AuthUiState.Loading
            repo.registerWithEmail(email, password, name).fold(
                onSuccess = { uiState.value = AuthUiState.Success(false) },
                onFailure = { uiState.value = AuthUiState.Error(it.message ?: "Registrierung fehlgeschlagen") }
            )
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            uiState.value = AuthUiState.Loading
            repo.loginWithGoogle(idToken).fold(
                onSuccess = {
                    val uid  = repo.getCurrentUser()!!.uid
                    val done = repo.isOnboardingComplete(uid)
                    uiState.value = AuthUiState.Success(done)
                },
                onFailure = { uiState.value = AuthUiState.Error(it.message ?: "Google-Anmeldung fehlgeschlagen") }
            )
        }
    }
}
