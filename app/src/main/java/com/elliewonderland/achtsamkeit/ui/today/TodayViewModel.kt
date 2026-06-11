package com.elliewonderland.achtsamkeit.ui.today

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elliewonderland.achtsamkeit.data.local.QuoteLoader
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import com.elliewonderland.achtsamkeit.data.repository.EntryRepository
import com.elliewonderland.achtsamkeit.data.local.LifehackLoader
import com.elliewonderland.achtsamkeit.data.repository.LifehackRepository
import com.elliewonderland.achtsamkeit.data.repository.QuoteRepository
import com.elliewonderland.achtsamkeit.data.repository.ReviewRepository
import com.elliewonderland.achtsamkeit.model.EnergyKey
import com.elliewonderland.achtsamkeit.model.Lifehack
import com.elliewonderland.achtsamkeit.model.MoodKey
import com.elliewonderland.achtsamkeit.model.Quote
import com.elliewonderland.achtsamkeit.data.repository.PremiumRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

data class TodayUiState(
    val isLoading: Boolean = true,
    val hasMorningEntry: Boolean = false,
    val hasEveningEntry: Boolean = false,
    val morningCompletedAt: LocalTime? = null,
    val eveningCompletedAt: LocalTime? = null,
    val weeklyUnlocked: Boolean = false,
    val monthlyUnlocked: Boolean = false,
    val yearlyUnlocked: Boolean = false,
    val hasWeeklyReview: Boolean = false,
    val hasMonthlyReview: Boolean = false,
    val hasYearlyReview: Boolean = false,
    val moodMonth: List<MoodPoint?> = emptyList(),
    val moodTrendPct: Int? = null,
    val weekDays: List<WeekDayStatus> = emptyList(),
    val weekCompletedCount: Int = 0,
    val weekMaxCount: Int = 14,
    val quoteOfDay: Quote? = null,
    val quoteIsFavorite: Boolean = false,
    val lifehackOfDay: Lifehack? = null,
    val lifehackIsFavorite: Boolean = false,
    val userFirstName: String? = null,
    val photoUrl: String? = null,
    val photoScale: Float = 1.0f,
    val photoOffsetX: Float = 0.0f,
    val photoOffsetY: Float = 0.0f,
    val showFavoriteLimitDialog: Boolean = false,
    val weekOffsetData: Map<Int, List<WeekDayStatus>> = emptyMap(),
)

class TodayViewModel(app: Application) : AndroidViewModel(app) {

    private val repo       = EntryRepository()
    private val reviewRepo = ReviewRepository()
    private val authRepo   = AuthRepository()
    private val quoteRepo  = QuoteRepository(QuoteLoader(app))
    private val lifehackRepo = LifehackRepository(LifehackLoader(app))

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    fun loadTodayStatus(userId: String) {
        viewModelScope.launch {
            val today      = LocalDate.now()
            val weekStart  = today.with(DayOfWeek.MONDAY)
            val prevMonthDate = today.minusMonths(1)

            val morningD       = async { runCatching { repo.getTodayEntry(userId, "morning") }.onFailure { Log.e("HeuteVM", "morning entry", it) }.getOrNull() }
            val eveningD       = async { runCatching { repo.getTodayEntry(userId, "evening") }.onFailure { Log.e("HeuteVM", "evening entry", it) }.getOrNull() }
            val weekEntriesD   = async { runCatching { repo.getEntriesForWeek(userId, weekStart) }.getOrDefault(emptyList()) }
            val monthEntriesD  = async { runCatching { repo.getEntriesForMonth(userId, today.year, today.monthValue) }.getOrDefault(emptyList()) }
            val prevMonthD     = async { runCatching { repo.getEntriesForMonth(userId, prevMonthDate.year, prevMonthDate.monthValue) }.getOrDefault(emptyList()) }
            val displayNameD   = async { runCatching { authRepo.getUserDisplayName(userId) }.getOrDefault("") }
            val photoUrlD      = async { runCatching { authRepo.getUserPhotoUrl(userId) }.getOrDefault("") }
            val cropParamsD    = async { runCatching { authRepo.getPhotoCropParams(userId) }.getOrDefault(Triple(1.0f, 0.0f, 0.0f)) }
            val lifehackD      = async { runCatching { lifehackRepo.getOrPickLifehackOfDay(userId) }.getOrNull() }

            val morningEntry = morningD.await()
            val eveningEntry = eveningD.await()
            val weekEntries  = weekEntriesD.await()

            // Show the quote from the last completed entry of the day (evening takes precedence)
            val lastEntry = eveningEntry ?: morningEntry
            val userTags = buildList {
                morningEntry?.let { addAll(repo.deriveTags(it)) }
                eveningEntry?.let { addAll(repo.deriveTags(it)) }
                weekEntries.forEach { addAll(repo.deriveTags(it)) }
            }.distinct()
            val quote = when {
                lastEntry != null && lastEntry.quoteId.isNotBlank() ->
                    quoteRepo.getQuoteById(lastEntry.quoteId)
                        ?: runCatching { quoteRepo.getOrPickQuoteOfDay(userId, userTags) }.getOrNull()
                else ->
                    runCatching { quoteRepo.getOrPickQuoteOfDay(userId, userTags) }.getOrNull()
            }
            val isFav = if (quote != null) runCatching { quoteRepo.isFavorite(userId, quote.id) }.getOrDefault(false) else false
            val lifehack = lifehackD.await()
            val isHackFav = if (lifehack != null) runCatching { quoteRepo.isFavorite(userId, lifehack.id) }.getOrDefault(false) else false
            val monthEntries  = monthEntriesD.await()
            val prevMonthEntries = prevMonthD.await()
            val displayName   = displayNameD.await()
            val cropParams    = cropParamsD.await()

            val daysInMonth = today.lengthOfMonth()
            val moodMonth = buildMoodMonth(monthEntries, daysInMonth)
            val moodTrend = buildMoodTrend(monthEntries, prevMonthEntries, today.dayOfMonth)

            val weekDays = buildWeekDays(weekEntries, weekStart, today)
            val weekCompleted = weekDays.sumOf {
                (if (it.morningDone) 1 else 0) + (if (it.eveningDone) 1 else 0)
            }

            val firstName = displayName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() }
            val photoUrl  = photoUrlD.await().takeIf { it.isNotBlank() }

            _uiState.value = TodayUiState(
                isLoading           = false,
                hasMorningEntry     = morningEntry != null,
                hasEveningEntry     = eveningEntry != null,
                morningCompletedAt  = morningEntry?.let { millisToLocalTime(it.createdAt) },
                eveningCompletedAt  = eveningEntry?.let { millisToLocalTime(it.createdAt) },
                weeklyUnlocked      = reviewRepo.isWeeklyReviewUnlocked(),
                monthlyUnlocked     = reviewRepo.isMonthlyReviewUnlocked(),
                yearlyUnlocked      = reviewRepo.isYearlyReviewUnlocked(),
                hasWeeklyReview     = weekEntries.any { it.type == "weekly_review" },
                hasMonthlyReview    = monthEntries.any { it.type == "monthly_review" },
                hasYearlyReview     = monthEntries.any { it.type == "yearly_review" },
                moodMonth           = moodMonth,
                moodTrendPct        = moodTrend,
                weekDays            = weekDays,
                weekCompletedCount  = weekCompleted,
                weekMaxCount        = 14,
                quoteOfDay          = quote,
                quoteIsFavorite     = isFav,
                lifehackOfDay       = lifehack,
                lifehackIsFavorite  = isHackFav,
                userFirstName       = firstName,
                photoUrl            = photoUrl,
                photoScale          = cropParams.first,
                photoOffsetX         = cropParams.second,
                photoOffsetY         = cropParams.third,
                showFavoriteLimitDialog = false,
                weekOffsetData      = mapOf(0 to weekDays),
            )
        }
    }

    fun toggleFavoriteQuote() {
        val state = _uiState.value
        val quote = state.quoteOfDay ?: return
        val userId = authRepo.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            if (!state.quoteIsFavorite) {
                val isPremium = PremiumRepository.isPremium()
                if (!isPremium) {
                    val count = quoteRepo.getFavoritesCount(userId)
                    if (count >= 3) {
                        _uiState.update { it.copy(showFavoriteLimitDialog = true) }
                        return@launch
                    }
                }
            }
            runCatching { quoteRepo.toggleFavorite(userId, quote) }
            _uiState.update { it.copy(quoteIsFavorite = !state.quoteIsFavorite) }
        }
    }

    fun dislikeQuote() {
        val state = _uiState.value
        val quote = state.quoteOfDay ?: return
        val userId = authRepo.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            // 1. Mark as disliked in Firestore
            runCatching { quoteRepo.dislikeQuote(userId, quote.id) }
            
            // 2. Load today's entry (evening or morning)
            val today = LocalDate.now()
            val morningEntry = runCatching { repo.getTodayEntry(userId, "morning") }.getOrNull()
            val eveningEntry = runCatching { repo.getTodayEntry(userId, "evening") }.getOrNull()
            val lastEntry = eveningEntry ?: morningEntry
            
            // 3. Derive user tags and pick new quote
            val weekStart = today.with(DayOfWeek.MONDAY)
            val weekEntries = runCatching { repo.getEntriesForWeek(userId, weekStart) }.getOrDefault(emptyList())
            val userTags = buildList {
                morningEntry?.let { addAll(repo.deriveTags(it)) }
                eveningEntry?.let { addAll(repo.deriveTags(it)) }
                weekEntries.forEach { addAll(repo.deriveTags(it)) }
            }.distinct()
            
            val newQuote = runCatching { quoteRepo.pickQuote(userId, userTags) }.getOrNull()
            if (newQuote != null) {
                // Update today's entry's quoteId in Firestore if it matched the old one
                if (lastEntry != null && lastEntry.quoteId == quote.id) {
                    runCatching { repo.updateEntryQuoteId(userId, lastEntry.id, newQuote.id) }
                }
                
                // Update general quote of the day in user's profile
                runCatching {
                    Firebase.firestore.collection("users").document(userId).update(mapOf(
                        "quote_of_day_id" to newQuote.id,
                        "quote_of_day_date" to today.toString()
                    )).await()
                }
                
                val isFav = runCatching { quoteRepo.isFavorite(userId, newQuote.id) }.getOrDefault(false)
                
                _uiState.update { it.copy(
                    quoteOfDay = newQuote,
                    quoteIsFavorite = isFav
                ) }
            }
        }
    }

    fun dislikeLifehack() {
        val state = _uiState.value
        val hack = state.lifehackOfDay ?: return
        val userId = authRepo.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            // 1. Mark as disliked in Firestore
            runCatching { lifehackRepo.dislikeLifehack(userId, hack.id) }
            
            // 2. Pick a new lifehack (which automatically excludes disliked ones)
            val newHack = runCatching { lifehackRepo.pickLifehack(userId) }.getOrNull()
            if (newHack != null) {
                // Update general lifehack of the day in user's profile
                runCatching {
                    val today = LocalDate.now().toString()
                    Firebase.firestore.collection("users").document(userId).update(mapOf(
                        "lifehack_of_day_id" to newHack.id,
                        "lifehack_of_day_date" to today
                    )).await()
                }
                
                val isFav = runCatching { quoteRepo.isFavorite(userId, newHack.id) }.getOrDefault(false)
                
                _uiState.update { it.copy(
                    lifehackOfDay = newHack,
                    lifehackIsFavorite = isFav
                ) }
            }
        }
    }

    fun toggleFavoriteLifehack() {
        val state = _uiState.value
        val hack = state.lifehackOfDay ?: return
        val userId = authRepo.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            if (!state.lifehackIsFavorite) {
                val isPremium = PremiumRepository.isPremium()
                if (!isPremium) {
                    val count = quoteRepo.getFavoritesCount(userId)
                    if (count >= 3) {
                        _uiState.update { it.copy(showFavoriteLimitDialog = true) }
                        return@launch
                    }
                }
            }
            runCatching { quoteRepo.toggleFavoriteLifehack(userId, hack) }
            _uiState.update { it.copy(lifehackIsFavorite = !state.lifehackIsFavorite) }
        }
    }

    fun dismissFavoriteLimitDialog() {
        _uiState.update { it.copy(showFavoriteLimitDialog = false) }
    }

    fun loadWeekOffset(userId: String, offset: Int) {
        if (_uiState.value.weekOffsetData.containsKey(offset)) return
        viewModelScope.launch {
            val today = LocalDate.now()
            val weekStart = today.plusWeeks(offset.toLong()).with(DayOfWeek.MONDAY)
            val weekEntries = runCatching { repo.getEntriesForWeek(userId, weekStart) }.getOrDefault(emptyList())
            val weekDays = buildWeekDays(weekEntries, weekStart, today)
            _uiState.update { state ->
                state.copy(
                    weekOffsetData = state.weekOffsetData + (offset to weekDays)
                )
            }
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
            // Morning moods
            MoodKey.EXCITEMENT   -> 90
            MoodKey.PEACE        -> 75
            MoodKey.TIREDNESS    -> 40
            MoodKey.ANXIETY      -> 30
            MoodKey.MELANCHOLY   -> 20
            // Evening moods
            MoodKey.SATISFACTION -> 90
            MoodKey.RELIEF       -> 75
            MoodKey.EXHAUSTION   -> 40
            MoodKey.OVERWHELMED  -> 25
            MoodKey.LONELINESS   -> 20
            else                 -> 50
        }
        val modifier = when (entry.energyLevel) {
            EnergyKey.FULL            -> 10
            EnergyKey.SATISFIED_TIRED -> 5
            EnergyKey.WIRED           -> 0
            EnergyKey.MEDIUM          -> 0
            EnergyKey.LOW             -> -5
            EnergyKey.EMPTY           -> -10
            else                      -> 0
        }
        return (base + modifier).coerceIn(5, 100)
    }

    private fun millisToLocalTime(ms: Long): LocalTime =
        Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalTime()
}
