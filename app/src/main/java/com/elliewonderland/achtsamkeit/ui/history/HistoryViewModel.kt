package com.elliewonderland.achtsamkeit.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.repository.HistoryRepository
import com.elliewonderland.achtsamkeit.model.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HistoryTab {
    TAG, WOCHE, MONAT, JAHR
}

data class HistoryUiState(
    val entries: List<Entry> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTag: String? = null,
    val searchText: String = "",
    val selectedTab: HistoryTab = HistoryTab.TAG,
)

class HistoryViewModel : ViewModel() {

    private val repo = HistoryRepository()

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    fun load(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val entries = runCatching { repo.getEntries(userId) }.onFailure { Log.e("HistoryViewModel", "getEntries failed", it) }.getOrDefault(emptyList())
            _uiState.update { it.copy(entries = entries, isLoading = false) }
        }
    }

    fun selectTag(userId: String, tag: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedTag = tag, isLoading = true) }
            val entries = runCatching {
                if (tag == null) repo.getEntries(userId) else repo.getEntriesByTag(userId, tag)
            }.onFailure { Log.e("HistoryViewModel", "getEntriesByTag failed (tag=$tag)", it) }.getOrDefault(emptyList())
            _uiState.update { it.copy(entries = entries, isLoading = false) }
        }
    }

    fun selectTab(tab: HistoryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }
}
