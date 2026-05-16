package com.elliewonderland.achtsamkeit.ui.favorites

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
import com.elliewonderland.achtsamkeit.data.repository.QuoteRepository
import com.elliewonderland.achtsamkeit.model.FavoriteQuote
import com.elliewonderland.achtsamkeit.model.Quote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = QuoteRepository(QuoteLoader(app))

    private val _favorites = MutableStateFlow<List<FavoriteQuote>>(emptyList())
    val favorites: StateFlow<List<FavoriteQuote>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _favorites.value = runCatching { repo.getFavorites(userId) }.onFailure { Log.e("FavoritesViewModel", "getFavorites failed", it) }.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }

    fun unfavorite(userId: String, fav: FavoriteQuote) {
        viewModelScope.launch {
            val quote = Quote(id = fav.id, text = fav.text, author = "", tags = emptyList())
            runCatching { repo.toggleFavorite(userId, quote) }
            _favorites.value = _favorites.value.filter { it.id != fav.id }
        }
    }
}
