package com.elliewonderland.achtsamkeit.ui.profil

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LifeProfileViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    val profileMap = mutableStateMapOf(
        "arbeit" to false,
        "mama" to false,
        "alleinerziehend" to false,
        "care_arbeit" to false,
        "oma" to false,
        "scheidung" to false,
        "studium" to false,
    )

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = repo.getUserProfile(userId)
            profileMap.keys.forEach { key ->
                profileMap[key] = profile[key] ?: false
            }
            _isLoading.value = false
        }
    }

    fun toggleKey(key: String) {
        profileMap[key] = !(profileMap[key] ?: false)
    }

    fun saveProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repo.saveUserProfile(userId, profileMap.toMap())
            _saveSuccess.value = true
            _isLoading.value = false
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}
