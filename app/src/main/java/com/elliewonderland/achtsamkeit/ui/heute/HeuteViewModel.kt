package com.elliewonderland.achtsamkeit.ui.heute

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import com.elliewonderland.achtsamkeit.data.repository.EntryRepository
import com.elliewonderland.achtsamkeit.data.repository.QuoteRepository
import com.elliewonderland.achtsamkeit.data.repository.ReviewRepository
import com.elliewonderland.achtsamkeit.data.repository.StatsRepository
import com.elliewonderland.achtsamkeit.model.Quote
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

data class MoodPoint(val day: Int, val score: Int)

data class WeekDayStatus(
    val dayLabel: String,
    val date: LocalDate,
    val morningDone: Boolean,
    val eveningDone: Boolean,
    val isToday: Boolean,
)

data class HeuteUiState(
    val isLoading: Boolean = true,
    val hasMorningEntry: Boolean = false,
    val hasEveningEntry: Boolean = false,
    val morningCompletedAt: LocalTime? = null,
    val eveningCompletedAt: LocalTime? = null,
    val streak: Int = 0,
    val weeklyUnlocked: Boolean = false,
    val monthlyUnlocked: Boolean = false,
    val moodMonth: List<MoodPoint?> = emptyList(),
    val moodTrendPct: Int? = null,
    val weekDays: List<WeekDayStatus> = emptyList(),
    val weekCompletedCount: Int = 0,
    val weekMaxCount: Int = 14,
    val quoteOfDay: Quote? = null,
    val quoteIsFavorite: Boolean = false,
    val userFirstName: String? = null,
)

class HeuteViewModel(app: Application) : AndroidViewModel(app) {

    private val repo       = EntryRepository()
    private val reviewRepo = ReviewRepository()
    private val statsRepo  = StatsRepository()
    private val authRepo   = AuthRepository()
    private val quoteRepo  = QuoteRepository(QuoteLoader(app))

    private val _uiState = MutableStateFlow(HeuteUiState())
    val uiState: StateFlow<HeuteUiState> = _uiState.asStateFlow()

    fun loadTodayStatus(userId: String) {
        viewModelScope.launch {
            val today      = LocalDate.now()
            val weekStart  = today.with(DayOfWeek.MONDAY)
            val prevMonthDate = today.minusMonths(1)

            val morningD       = async { runCatching { repo.getTodayEntry(userId, "morning") }.onFailure { Log.e("HeuteVM", "morning entry", it) }.getOrNull() }
            val eveningD       = async { runCatching { repo.getTodayEntry(userId, "evening") }.onFailure { Log.e("HeuteVM", "evening entry", it) }.getOrNull() }
            val weeklyD        = async { runCatching { reviewRepo.isWeeklyReviewUnlocked(userId) }.getOrDefault(false) }
            val streakD        = async { runCatching { statsRepo.getCurrentStreak(userId) }.getOrDefault(0) }
            val weekEntriesD   = async { runCatching { repo.getEntriesForWeek(userId, weekStart) }.getOrDefault(emptyList()) }
            val monthEntriesD  = async { runCatching { repo.getEntriesForMonth(userId, today.year, today.monthValue) }.getOrDefault(emptyList()) }
            val prevMonthD     = async { runCatching { repo.getEntriesForMonth(userId, prevMonthDate.year, prevMonthDate.monthValue) }.getOrDefault(emptyList()) }
            val displayNameD   = async { runCatching { authRepo.getUserDisplayName(userId) }.getOrDefault("") }

            val morningEntry = morningD.await()
            val eveningEntry = eveningD.await()

            // Den Spruch vom letzten abgeschlossenen Eintrag des Tages zeigen (Abend hat Vorrang)
            val lastEntry = eveningEntry ?: morningEntry
            val userTags = buildList {
                morningEntry?.let { addAll(repo.deriveTags(it)) }
                eveningEntry?.let { addAll(repo.deriveTags(it)) }
            }.distinct()
            val quote = when {
                lastEntry != null && lastEntry.quoteId.isNotBlank() ->
                    quoteRepo.getQuoteById(lastEntry.quoteId)
                        ?: runCatching { quoteRepo.getOrPickQuoteOfDay(userId, userTags) }.getOrNull()
                else ->
                    runCatching { quoteRepo.getOrPickQuoteOfDay(userId, userTags) }.getOrNull()
            }
            val isFav = if (quote != null) runCatching { quoteRepo.isFavorite(userId, quote.id) }.getOrDefault(false) else false

            val weekEntries   = weekEntriesD.await()
            val monthEntries  = monthEntriesD.await()
            val prevMonthEntries = prevMonthD.await()
            val displayName   = displayNameD.await()

            val daysInMonth = today.lengthOfMonth()
            val moodMonth = buildMoodMonth(monthEntries, daysInMonth)
            val moodTrend = buildMoodTrend(monthEntries, prevMonthEntries, today.dayOfMonth)

            val weekDays = buildWeekDays(weekEntries, weekStart, today)
            val weekCompleted = weekDays.sumOf {
                (if (it.morningDone) 1 else 0) + (if (it.eveningDone) 1 else 0)
            }

            val firstName = displayName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() }

            _uiState.value = HeuteUiState(
                isLoading           = false,
                hasMorningEntry     = morningEntry != null,
                hasEveningEntry     = eveningEntry != null,
                morningCompletedAt  = morningEntry?.let { millisToLocalTime(it.createdAt) },
                eveningCompletedAt  = eveningEntry?.let { millisToLocalTime(it.createdAt) },
                streak              = streakD.await(),
                weeklyUnlocked      = weeklyD.await(),
                monthlyUnlocked     = reviewRepo.isMonthlyReviewUnlocked(),
                moodMonth           = moodMonth,
                moodTrendPct        = moodTrend,
                weekDays            = weekDays,
                weekCompletedCount  = weekCompleted,
                weekMaxCount        = 14,
                quoteOfDay          = quote,
                quoteIsFavorite     = isFav,
                userFirstName       = firstName,
            )
        }
    }

    fun toggleFavoriteQuote() {
        val state = _uiState.value
        val quote = state.quoteOfDay ?: return
        val userId = authRepo.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            runCatching { quoteRepo.toggleFavorite(userId, quote) }
            _uiState.value = state.copy(quoteIsFavorite = !state.quoteIsFavorite)
        }
    }

    private fun buildMoodMonth(entries: List<com.elliewonderland.achtsamkeit.model.Entry>, daysInMonth: Int): List<MoodPoint?> {
        val byDay = entries.groupBy { it.dateStr.takeLast(2).trimStart('0').toIntOrNull() ?: 0 }
        return (1..daysInMonth).map { day ->
            val dayEntries = byDay[day] ?: return@map null
            val avg = dayEntries.map { entryToScore(it) }.average().toInt()
            MoodPoint(day, avg)
        }
    }

    private fun buildMoodTrend(current: List<com.elliewonderland.achtsamkeit.model.Entry>, prev: List<com.elliewonderland.achtsamkeit.model.Entry>, todayDay: Int): Int? {
        if (prev.isEmpty()) return null
        val currentAvg = current.map { entryToScore(it) }.average().takeIf { !it.isNaN() } ?: return null
        val prevAvg    = prev.map { entryToScore(it) }.average().takeIf { it > 0.0 } ?: return null
        return ((currentAvg - prevAvg) / prevAvg * 100).toInt()
    }

    private fun buildWeekDays(
        entries: List<com.elliewonderland.achtsamkeit.model.Entry>,
        weekStart: LocalDate,
        today: LocalDate,
    ): List<WeekDayStatus> {
        return (0..6).map { offset ->
            val date       = weekStart.plusDays(offset.toLong())
            val dateStr    = date.toString()
            val dayEntries = entries.filter { it.dateStr == dateStr }
            WeekDayStatus(
                dayLabel    = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.GERMAN).take(2).replaceFirstChar { it.uppercaseChar() },
                date        = date,
                morningDone = dayEntries.any { it.type == "morning" },
                eveningDone = dayEntries.any { it.type == "evening" },
                isToday     = date == today,
            )
        }
    }

    private fun entryToScore(entry: com.elliewonderland.achtsamkeit.model.Entry): Int {
        val base = when (entry.mood) {
            "joy"     -> 100
            "balance" -> 75
            "sadness" -> 50
            "stress"  -> 25
            else      -> 50
        }
        val modifier = when (entry.energyLevel) {
            "full"  -> 10
            "empty" -> -10
            else    -> 0
        }
        return (base + modifier).coerceIn(5, 100)
    }

    private fun millisToLocalTime(ms: Long): LocalTime =
        Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalTime()
}
