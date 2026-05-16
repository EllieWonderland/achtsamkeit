package com.elliewonderland.achtsamkeit.ui.heute

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.repository.EntryRepository
import com.elliewonderland.achtsamkeit.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HeuteUiState(
    val hasMorningEntry: Boolean = false,
    val hasEveningEntry: Boolean = false,
    val weeklyUnlocked: Boolean = false,
    val monthlyUnlocked: Boolean = false,
    val isLoading: Boolean = true,
)

class HeuteViewModel : ViewModel() {

    private val repo       = EntryRepository()
    private val reviewRepo = ReviewRepository()

    private val _uiState = MutableStateFlow(HeuteUiState())
    val uiState: StateFlow<HeuteUiState> = _uiState.asStateFlow()

    fun loadTodayStatus(userId: String) {
        viewModelScope.launch {
            val hasMorning      = runCatching { repo.hasEntryToday(userId, "morning") }.onFailure { Log.e("HeuteViewModel", "hasEntryToday morning failed", it) }.getOrDefault(false)
            val hasEvening      = runCatching { repo.hasEntryToday(userId, "evening") }.onFailure { Log.e("HeuteViewModel", "hasEntryToday evening failed", it) }.getOrDefault(false)
            val weeklyUnlocked  = runCatching { reviewRepo.isWeeklyReviewUnlocked(userId) }.onFailure { Log.e("HeuteViewModel", "isWeeklyReviewUnlocked failed", it) }.getOrDefault(false)
            val monthlyUnlocked = reviewRepo.isMonthlyReviewUnlocked()
            _uiState.value = HeuteUiState(
                hasMorningEntry = hasMorning,
                hasEveningEntry = hasEvening,
                weeklyUnlocked  = weeklyUnlocked,
                monthlyUnlocked = monthlyUnlocked,
                isLoading       = false,
            )
        }
    }
}
