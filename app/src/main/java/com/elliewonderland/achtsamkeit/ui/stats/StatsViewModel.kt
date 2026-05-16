package com.elliewonderland.achtsamkeit.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.repository.StatsRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatsUiState(
    val days: Int = 30,
    val streak: Int = 0,
    val freezeAvailableThisMonth: Boolean = false,
    val moodDistribution: Map<String, Int> = emptyMap(),
    val gratitudeDistribution: Map<String, Int> = emptyMap(),
    val energyDistribution: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
)

class StatsViewModel : ViewModel() {

    private val repo = StatsRepository()
    private val _state = MutableStateFlow(StatsUiState())
    val state: StateFlow<StatsUiState> = _state.asStateFlow()

    init { reload() }

    fun setDays(days: Int) {
        _state.update { it.copy(days = days) }
        reload()
    }

    private fun reload() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val days = _state.value.days
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val moodDist     = repo.getMoodDistribution(uid, days)
            val gratDist     = repo.getGratitudeDistribution(uid, days)
            val energyDist   = repo.getEnergyDistribution(uid, days)
            val streak       = repo.getCurrentStreak(uid)
            val freezeAvail  = repo.isStreakFreezeAvailableThisMonth(uid)
            _state.update {
                it.copy(
                    moodDistribution         = moodDist,
                    gratitudeDistribution    = gratDist,
                    energyDistribution       = energyDist,
                    streak                   = streak,
                    freezeAvailableThisMonth = freezeAvail,
                    isLoading                = false,
                )
            }
        }
    }
}
