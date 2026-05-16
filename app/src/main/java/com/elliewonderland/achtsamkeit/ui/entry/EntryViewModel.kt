package com.elliewonderland.achtsamkeit.ui.entry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.repository.EntryRepository
import com.elliewonderland.achtsamkeit.model.Entry
import com.elliewonderland.achtsamkeit.model.GuidedQuestions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate

data class EntryFormState(
    val energyLevel: String = "",
    val mood: String = "",
    val gratitudeAreas: List<String> = emptyList(),
    val dayRating: Int = 0,
    val selfCare: List<String> = emptyList(),
    val mindfulnessFocus: String = "",
    val mindfulnessPause: String = "",
    val guidedQuestion: String = "",
    val guidedAnswer: String = "",
    val freeText: String = "",
)

sealed class EntrySaveState {
    object Idle    : EntrySaveState()
    object Saving  : EntrySaveState()
    data class Saved(val entryId: String) : EntrySaveState()
    data class Error(val message: String) : EntrySaveState()
}

class EntryViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = EntryRepository()

    private val _form = MutableStateFlow(EntryFormState())
    val form: StateFlow<EntryFormState> = _form.asStateFlow()

    private val _saveState = MutableStateFlow<EntrySaveState>(EntrySaveState.Idle)
    val saveState: StateFlow<EntrySaveState> = _saveState.asStateFlow()

    fun initType(type: String) {
        viewModelScope.launch {
            val raw = withContext(Dispatchers.IO) {
                getApplication<Application>().assets.open("guided_questions.json")
                    .bufferedReader().readText()
            }
            val questions = Json.decodeFromString<GuidedQuestions>(raw)
            val list      = if (type == "morning") questions.morning else questions.evening
            val index     = LocalDate.now().dayOfYear % list.size
            _form.update { it.copy(guidedQuestion = list[index]) }
        }
    }

    fun updateEnergyLevel(value: String)      = _form.update { it.copy(energyLevel = value) }
    fun updateMood(value: String)              = _form.update { it.copy(mood = value) }
    fun updateDayRating(value: Int)            = _form.update { it.copy(dayRating = value) }
    fun updateMindfulnessFocus(value: String)  = _form.update { it.copy(mindfulnessFocus = value) }
    fun updateMindfulnessPause(value: String)  = _form.update { it.copy(mindfulnessPause = value) }
    fun updateGuidedAnswer(value: String)      = _form.update { it.copy(guidedAnswer = value) }
    fun updateFreeText(value: String)          = _form.update { it.copy(freeText = value) }

    fun toggleGratitudeArea(value: String) = _form.update { f ->
        val next = f.gratitudeAreas.toMutableList()
        if (value in next) next.remove(value) else next.add(value)
        f.copy(gratitudeAreas = next)
    }

    fun toggleSelfCare(value: String) = _form.update { f ->
        val next = f.selfCare.toMutableList()
        if (value in next) next.remove(value) else next.add(value)
        f.copy(selfCare = next)
    }

    private val _showValidationDialog = MutableStateFlow(false)
    val showValidationDialog: StateFlow<Boolean> = _showValidationDialog.asStateFlow()

    fun saveEntry(userId: String, type: String) {
        val f = _form.value
        if (f.mood.isBlank() || f.energyLevel.isBlank()) {
            _showValidationDialog.value = true
            return
        }
        doSave(userId, type, f)
    }

    fun dismissValidationDialog() {
        _showValidationDialog.value = false
    }

    fun saveEntryAnyway(userId: String, type: String) {
        _showValidationDialog.value = false
        doSave(userId, type, _form.value)
    }

    private fun doSave(userId: String, type: String, f: EntryFormState) {
        viewModelScope.launch {
            _saveState.value = EntrySaveState.Saving
            runCatching {
                repo.saveEntry(
                    userId,
                    Entry(
                        type             = type,
                        energyLevel      = f.energyLevel,
                        mood             = f.mood,
                        gratitudeAreas   = f.gratitudeAreas,
                        dayRating        = f.dayRating,
                        selfCare         = f.selfCare,
                        mindfulnessFocus = f.mindfulnessFocus,
                        mindfulnessPause = f.mindfulnessPause,
                        guidedQuestion   = f.guidedQuestion,
                        guidedAnswer     = f.guidedAnswer,
                        freeText         = f.freeText,
                    )
                )
            }.fold(
                onSuccess = { id -> _saveState.value = EntrySaveState.Saved(id) },
                onFailure = { e  -> _saveState.value = EntrySaveState.Error(e.message ?: "Fehler beim Speichern") },
            )
        }
    }
}
