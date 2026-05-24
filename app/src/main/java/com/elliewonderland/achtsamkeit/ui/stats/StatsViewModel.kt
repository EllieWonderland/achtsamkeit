package com.elliewonderland.achtsamkeit.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.repository.StatsRepository
import com.elliewonderland.achtsamkeit.model.Entry
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatsUiState(
    val days: Int = 30,
    val entries: List<Entry> = emptyList(),
    val moodDistribution: Map<String, Int> = emptyMap(),
    val gratitudeDistribution: Map<String, Int> = emptyMap(),
    val energyDistribution: Map<String, Int> = emptyMap(),
    val selfCareDistribution: Map<String, Int> = emptyMap(),
    val focusDistribution: Map<String, Int> = emptyMap(),
    val pauseDistribution: Map<String, Int> = emptyMap(),
    val avgDayRating: Double = 0.0,
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

    fun reload() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val days = _state.value.days
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val entries = repo.getEntries(uid, days)
            
            val moodDist = entries.groupingBy { it.mood }.eachCount().filter { it.key.isNotEmpty() }
            
            val gratDist = mutableMapOf<String, Int>()
            entries.forEach { entry ->
                entry.gratitudeAreas.forEach { area ->
                    gratDist[area] = (gratDist[area] ?: 0) + 1
                }
            }
            
            val energyDist = entries.groupingBy { it.energyLevel }.eachCount().filter { it.key.isNotEmpty() }
            
            val selfCareDist = mutableMapOf<String, Int>()
            entries.forEach { entry ->
                entry.selfCare.forEach { action ->
                    selfCareDist[action] = (selfCareDist[action] ?: 0) + 1
                }
            }
            
            val focusDist = entries.map { it.mindfulnessFocus }.filter { it.isNotEmpty() }.groupingBy { it }.eachCount()
            val pauseDist = entries.map { it.mindfulnessPause }.filter { it.isNotEmpty() }.groupingBy { it }.eachCount()
            
            val ratings = entries.filter { it.dayRating > 0 }.map { it.dayRating }
            val avgRating = if (ratings.isEmpty()) 0.0 else ratings.average()

            _state.update {
                it.copy(
                    entries               = entries,
                    moodDistribution      = moodDist,
                    gratitudeDistribution = gratDist,
                    energyDistribution    = energyDist,
                    selfCareDistribution  = selfCareDist,
                    focusDistribution     = focusDist,
                    pauseDistribution     = pauseDist,
                    avgDayRating          = avgRating,
                    isLoading             = false,
                )
            }
        }
    }
}

