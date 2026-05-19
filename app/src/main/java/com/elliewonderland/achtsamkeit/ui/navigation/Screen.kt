package com.elliewonderland.achtsamkeit.ui.navigation

sealed class Screen(val route: String) {
    object Login         : Screen("login")
    object Register      : Screen("register")
    object Onboarding    : Screen("onboarding")
    object Heute         : Screen("heute")
    object Tagebuch      : Screen("tagebuch")
    object Statistik     : Screen("statistik")
    object Profil        : Screen("profil")
    object ThemePicker   : Screen("theme_picker")
    object NotifSettings : Screen("notif_settings")
    object Favorites     : Screen("favorites")
    object WeeklyReview  : Screen("weekly_review")
    object MonthlyReview : Screen("monthly_review")
    object YearlyReview  : Screen("yearly_review")
    object Quote         : Screen("quote/{entryId}") {
        fun createRoute(entryId: String) = "quote/$entryId"
    }
    object Entry         : Screen("entry/{type}") {
        fun createRoute(type: String) = "entry/$type"
    }
    object EntryDetail   : Screen("entry_detail/{entryId}") {
        fun createRoute(entryId: String) = "entry_detail/$entryId"
    }
    object Impressum     : Screen("impressum")
}
