package com.elliewonderland.achtsamkeit.ui.quote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
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
    data class Ready(val quote: Quote, val isFavorite: Boolean) : QuoteUiState()
    data class Error(val message: String) : QuoteUiState()
}

class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = QuoteRepository(QuoteLoader(application))

    private val _uiState = MutableStateFlow<QuoteUiState>(QuoteUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(userId: String, entryId: String) {
        viewModelScope.launch {
            runCatching {
                val snap = Firebase.firestore
                    .collection("users").document(userId)
                    .collection("entries").document(entryId)
                    .get().await()
                @Suppress("UNCHECKED_CAST")
                val tags = (snap.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                val quote = repo.pickQuote(userId, tags)
                val isFav = repo.isFavorite(userId, quote.id)
                _uiState.value = QuoteUiState.Ready(quote, isFav)
            }.onFailure {
                _uiState.value = QuoteUiState.Error(it.message ?: "Fehler beim Laden")
            }
        }
    }

    fun toggleFavorite(userId: String) {
        val current = _uiState.value as? QuoteUiState.Ready ?: return
        viewModelScope.launch {
            runCatching {
                repo.toggleFavorite(userId, current.quote)
                val isFav = repo.isFavorite(userId, current.quote.id)
                _uiState.value = current.copy(isFavorite = isFav)
            }
        }
    }
}
