package com.elliewonderland.achtsamkeit.ui.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
import com.elliewonderland.achtsamkeit.data.repository.HistoryRepository
import com.elliewonderland.achtsamkeit.data.repository.QuoteRepository
import com.elliewonderland.achtsamkeit.model.Entry
import com.elliewonderland.achtsamkeit.model.FavoriteQuote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val entries: List<Entry> = emptyList(),
    val favorites: List<FavoriteQuote> = emptyList(),
    val isLoading: Boolean = false,
    val searchText: String = "",
)

class HistoryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = HistoryRepository()
    private val quoteRepo = QuoteRepository(QuoteLoader(app))

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    fun load(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val entries = runCatching { repo.getEntries(userId) }
                .onFailure { Log.e("HistoryViewModel", "getEntries failed", it) }
                .getOrDefault(emptyList())
            val favorites = runCatching { quoteRepo.getFavorites(userId) }
                .onFailure { Log.e("HistoryViewModel", "getFavorites failed", it) }
                .getOrDefault(emptyList())
            _uiState.update { it.copy(entries = entries, favorites = favorites, isLoading = false) }
        }
    }

    fun toggleFavorite(userId: String, fav: FavoriteQuote) {
        viewModelScope.launch {
            val quote = com.elliewonderland.achtsamkeit.model.Quote(id = fav.id, text = fav.text, author = "", tags = emptyList())
            runCatching { quoteRepo.toggleFavorite(userId, quote) }
            _uiState.update { it.copy(favorites = it.favorites.filter { f -> f.id != fav.id }) }
        }
    }

    fun setSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }
}
