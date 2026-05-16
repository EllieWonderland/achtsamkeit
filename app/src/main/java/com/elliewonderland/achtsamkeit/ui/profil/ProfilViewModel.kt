package com.elliewonderland.achtsamkeit.ui.profil

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import com.elliewonderland.achtsamkeit.data.repository.HistoryRepository
import com.elliewonderland.achtsamkeit.data.repository.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

data class ProfilUiState(
    val displayName: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val errorMessage: String? = null,
)

class ProfilViewModel(app: Application) : AndroidViewModel(app) {

    private val authRepo    = AuthRepository()
    private val historyRepo = HistoryRepository()
    private val quoteRepo   = QuoteRepository(QuoteLoader(app))

    private val _uiState = MutableStateFlow(ProfilUiState())
    val uiState: StateFlow<ProfilUiState> = _uiState.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            val name = runCatching { authRepo.getUserDisplayName(userId) }.onFailure { Log.e("ProfilViewModel", "getUserDisplayName failed", it) }.getOrDefault("")
            _uiState.value = _uiState.value.copy(
                displayName = name,
                email       = authRepo.getUserEmail(),
            )
        }
    }

    fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun logout() {
        authRepo.logout()
        _navigateToLogin.value = true
    }

    fun deleteAccount(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showDeleteDialog = false)
            runCatching { authRepo.deleteAccount(userId) }.fold(
                onSuccess = { _navigateToLogin.value = true },
                onFailure = { e ->
                    val msg = if (e.message?.contains("requires-recent-login") == true)
                        "Bitte melde dich ab und erneut an, bevor du dein Konto löschst."
                    else
                        e.message ?: "Fehler beim Löschen des Kontos."
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = msg)
                },
            )
        }
    }

    fun exportData(userId: String, context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching {
                val entries   = historyRepo.getEntries(userId, limit = 10_000)
                val favorites = quoteRepo.getFavorites(userId)
                val json = Json { prettyPrint = true }
                val jsonString = buildString {
                    append("{")
                    append("\"entries\":")
                    append(json.encodeToString(entries))
                    append(",\"favorites\":")
                    append(json.encodeToString(favorites))
                    append("}")
                }
                withContext(Dispatchers.IO) {
                    val exportDir = File(context.cacheDir, "export").also { it.mkdirs() }
                    val file = File(exportDir, "achtsamkeit_daten.json")
                    file.writeText(jsonString)
                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                }
            }.fold(
                onSuccess = { uri ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_SUBJECT, "Meine Achtsamkeit-Daten")
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Daten exportieren"))
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Fehler beim Exportieren.",
                    )
                },
            )
        }
    }
}
