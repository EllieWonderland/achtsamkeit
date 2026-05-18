package com.elliewonderland.achtsamkeit.ui.profil

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.export.ExcelExporter
import com.elliewonderland.achtsamkeit.data.export.PdfExporter
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

enum class ExportFormat { JSON, PDF, EXCEL }

data class ProfilUiState(
    val displayName: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showResetDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val isEditingName: Boolean = false,
    val nameInput: String = "",
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
            val name = runCatching { authRepo.getUserDisplayName(userId) }
                .onFailure { Log.e("ProfilViewModel", "getUserDisplayName failed", it) }
                .getOrDefault("")
            _uiState.value = _uiState.value.copy(
                displayName = name,
                nameInput   = name,
                email       = authRepo.getUserEmail(),
            )
        }
    }

    // ── Name bearbeiten ──────────────────────────────────────────────────────

    fun startEditName() {
        _uiState.value = _uiState.value.copy(isEditingName = true)
    }

    fun onNameInput(value: String) {
        _uiState.value = _uiState.value.copy(nameInput = value)
    }

    fun cancelEditName() {
        _uiState.value = _uiState.value.copy(
            isEditingName = false,
            nameInput     = _uiState.value.displayName,
        )
    }

    fun saveDisplayName(userId: String) {
        val name = _uiState.value.nameInput.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching { authRepo.updateDisplayName(userId, name) }.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading     = false,
                        isEditingName = false,
                        displayName   = name,
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading    = false,
                        errorMessage = e.message ?: "Name konnte nicht gespeichert werden.",
                    )
                },
            )
        }
    }

    // ── Dialoge ──────────────────────────────────────────────────────────────

    fun showDeleteDialog()  { _uiState.value = _uiState.value.copy(showDeleteDialog = true) }
    fun hideDeleteDialog()  { _uiState.value = _uiState.value.copy(showDeleteDialog = false) }
    fun showResetDialog()   { _uiState.value = _uiState.value.copy(showResetDialog = true) }
    fun hideResetDialog()   { _uiState.value = _uiState.value.copy(showResetDialog = false) }
    fun showExportDialog()  { _uiState.value = _uiState.value.copy(showExportDialog = true) }
    fun hideExportDialog()  { _uiState.value = _uiState.value.copy(showExportDialog = false) }
    fun clearError()        { _uiState.value = _uiState.value.copy(errorMessage = null) }

    // ── Daten zurücksetzen ───────────────────────────────────────────────────

    fun resetAllData(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showResetDialog = false)
            runCatching { authRepo.resetAllData(userId) }.fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false) },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading    = false,
                        errorMessage = e.message ?: "Fehler beim Zurücksetzen.",
                    )
                },
            )
        }
    }

    // ── Abmelden / Löschen ───────────────────────────────────────────────────

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

    // ── Export ───────────────────────────────────────────────────────────────

    fun exportData(userId: String, format: ExportFormat, context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showExportDialog = false)
            runCatching {
                val entries   = historyRepo.getEntries(userId, limit = 10_000)
                val favorites = quoteRepo.getFavorites(userId)
                val name      = _uiState.value.displayName

                val (file, mimeType) = withContext(Dispatchers.IO) {
                    val dir = File(context.cacheDir, "export").also { it.mkdirs() }
                    when (format) {
                        ExportFormat.JSON -> {
                            val json = Json { prettyPrint = true }
                            val content = buildString {
                                append("{")
                                append("\"entries\":"); append(json.encodeToString(entries))
                                append(",\"favorites\":"); append(json.encodeToString(favorites))
                                append("}")
                            }
                            val f = File(dir, "achtsamkeit_daten.json")
                            f.writeText(content)
                            Pair(f, "application/json")
                        }
                        ExportFormat.PDF -> {
                            val f = File(dir, "achtsamkeit_daten.pdf")
                            PdfExporter.write(entries, name, f)
                            Pair(f, "application/pdf")
                        }
                        ExportFormat.EXCEL -> {
                            val f = File(dir, "achtsamkeit_daten.xlsx")
                            ExcelExporter.write(entries, f)
                            Pair(f, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        }
                    }
                }

                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file) to mimeType
            }.fold(
                onSuccess = { (uri, mimeType) ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = mimeType
                        putExtra(Intent.EXTRA_SUBJECT, "Meine Achtsamkeit-Daten")
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Daten exportieren"))
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading    = false,
                        errorMessage = e.message ?: "Fehler beim Exportieren.",
                    )
                },
            )
        }
    }
}
