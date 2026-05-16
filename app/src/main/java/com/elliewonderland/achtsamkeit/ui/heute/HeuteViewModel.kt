package com.elliewonderland.achtsamkeit.ui.heute

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
            val hasMorning      = runCatching { repo.hasEntryToday(userId, "morning") }.getOrDefault(false)
            val hasEvening      = runCatching { repo.hasEntryToday(userId, "evening") }.getOrDefault(false)
            val weeklyUnlocked  = runCatching { reviewRepo.isWeeklyReviewUnlocked(userId) }.getOrDefault(false)
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
