package com.elliewonderland.achtsamkeit.ui.quote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
import com.elliewonderland.achtsamkeit.data.repository.EntryRepository
import com.elliewonderland.achtsamkeit.data.repository.PremiumRepository
import com.elliewonderland.achtsamkeit.data.repository.QuoteRepository
import com.elliewonderland.achtsamkeit.model.Quote
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class QuoteUiState {
    object Loading : QuoteUiState()
    data class Ready(
        val quote: Quote,
        val isFavorite: Boolean,
        val showFavoriteLimitDialog: Boolean = false,
    ) : QuoteUiState()
    data class Error(val message: String) : QuoteUiState()
}

class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repo      = QuoteRepository(QuoteLoader(application))
    private val entryRepo = EntryRepository()

    private val _uiState = MutableStateFlow<QuoteUiState>(QuoteUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(userId: String, entryId: String) {
        viewModelScope.launch {
            runCatching {
                val snap = Firebase.firestore
                    .collection("users").document(userId)
                    .collection("entries").document(entryId)
                    .get().await()

                // Falls für diesen Eintrag schon ein Spruch ausgewählt wurde, denselben zeigen
                val savedQuoteId = snap.getString("quote_id")
                val quote = if (!savedQuoteId.isNullOrBlank()) {
                    repo.getQuoteById(savedQuoteId) ?: pickAndSave(userId, entryId, snap)
                } else {
                    pickAndSave(userId, entryId, snap)
                }

                val isFav = repo.isFavorite(userId, quote.id)
                _uiState.value = QuoteUiState.Ready(quote, isFav)
            }.onFailure {
                _uiState.value = QuoteUiState.Error(it.message ?: "Fehler beim Laden")
            }
        }
    }

    private suspend fun pickAndSave(
        userId: String,
        entryId: String,
        snap: com.google.firebase.firestore.DocumentSnapshot,
    ): Quote {
        @Suppress("UNCHECKED_CAST")
        val tags  = (snap.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        val quote = repo.pickQuote(userId, tags)
        runCatching { entryRepo.updateEntryQuoteId(userId, entryId, quote.id) }
        return quote
    }

    fun toggleFavorite(userId: String) {
        val current = _uiState.value as? QuoteUiState.Ready ?: return
        viewModelScope.launch {
            runCatching {
                if (!current.isFavorite) {
                    val isPremium = PremiumRepository.isPremium()
                    if (!isPremium) {
                        val count = repo.getFavoritesCount(userId)
                        if (count >= 3) {
                            _uiState.value = current.copy(showFavoriteLimitDialog = true)
                            return@launch
                        }
                    }
                }
                repo.toggleFavorite(userId, current.quote)
                val isFav = repo.isFavorite(userId, current.quote.id)
                _uiState.value = current.copy(isFavorite = isFav)
            }
        }
    }

    fun dismissFavoriteLimitDialog() {
        val current = _uiState.value as? QuoteUiState.Ready ?: return
        _uiState.value = current.copy(showFavoriteLimitDialog = false)
    }
}
