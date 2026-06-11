package com.elliewonderland.achtsamkeit.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import com.elliewonderland.achtsamkeit.ui.auth.LoginScreen
import com.elliewonderland.achtsamkeit.ui.auth.RegisterScreen
import com.elliewonderland.achtsamkeit.ui.entry.EntryScreen
import com.elliewonderland.achtsamkeit.ui.today.TodayScreen
import com.elliewonderland.achtsamkeit.ui.history.EntryDetailScreen
import com.elliewonderland.achtsamkeit.ui.history.DiaryScreen
import com.elliewonderland.achtsamkeit.ui.monthly.MonthlyReviewScreen
import com.elliewonderland.achtsamkeit.ui.onboarding.OnboardingScreen
import com.elliewonderland.achtsamkeit.ui.favorites.FavoritesScreen
import com.elliewonderland.achtsamkeit.ui.privacy.PrivacyScreen
import com.elliewonderland.achtsamkeit.ui.imprint.ImprintScreen
import com.elliewonderland.achtsamkeit.ui.profile.LifeProfileScreen
import com.elliewonderland.achtsamkeit.ui.profile.ProfileScreen
import com.elliewonderland.achtsamkeit.ui.quote.QuoteScreen
import com.elliewonderland.achtsamkeit.ui.theme.ThemePickerScreen
import com.elliewonderland.achtsamkeit.ui.settings.NotificationSettingsScreen
import com.elliewonderland.achtsamkeit.ui.settings.CardCustomizationScreen
import com.elliewonderland.achtsamkeit.ui.stats.StatisticsScreen
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.elliewonderland.achtsamkeit.ui.weekly.WeeklyReviewScreen
import com.elliewonderland.achtsamkeit.ui.yearly.YearlyReviewScreen

@Composable
fun AppNavHost(choice: ThemeChoice) {
    val navController = rememberNavController()
    val authRepo      = remember { AuthRepository() }
    val startDest     = if (authRepo.getCurrentUser() != null) Screen.Today.route
                        else Screen.Login.route

    NavHost(
        navController    = navController,
        startDestination = startDest,
    ) {
        composable(Screen.Login.route)         { LoginScreen(navController) }
        composable(Screen.Register.route)      { RegisterScreen(navController) }
        composable(Screen.Onboarding.route)    { OnboardingScreen(navController) }
        
        composable(
            route = "main_tabs?tab={tab}&scrollToDate={scrollToDate}",
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "today"
                },
                navArgument("scrollToDate") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { back ->
            val tab = back.arguments?.getString("tab") ?: "today"
            val scrollToDate = back.arguments?.getString("scrollToDate")
            MainTabContainerScreen(
                navController = navController,
                choice = choice,
                initialTab = tab,
                scrollToDate = scrollToDate,
            )
        }
            composable(Screen.ThemePicker.route)   { ThemePickerScreen(choice, navController) }
            composable(Screen.NotifSettings.route) { NotificationSettingsScreen(navController) }
            composable(Screen.Favorites.route)     { FavoritesScreen(navController) }
            composable(Screen.WeeklyReview.route)  { WeeklyReviewScreen(navController) }
            composable(Screen.MonthlyReview.route) { MonthlyReviewScreen(navController) }
            composable(Screen.YearlyReview.route)  { YearlyReviewScreen(navController) }
            composable(Screen.Imprint.route)     { ImprintScreen(navController) }
            composable(Screen.Privacy.route)   { PrivacyScreen(navController) }
            composable(Screen.LifeProfile.route)   { LifeProfileScreen(navController) }
            composable(Screen.CardCustomization.route) { CardCustomizationScreen(navController) }
            composable(
                route     = Screen.Entry.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType }),
            ) { back ->
                EntryScreen(navController, back.arguments?.getString("type") ?: "morning")
            }
            composable(
                route     = Screen.Quote.route,
                arguments = listOf(navArgument("entryId") { type = NavType.StringType }),
            ) { back ->
                QuoteScreen(navController, back.arguments?.getString("entryId") ?: "")
            }
            composable(
                route     = Screen.EntryDetail.route,
                arguments = listOf(navArgument("entryId") { type = NavType.StringType }),
            ) { back ->
                EntryDetailScreen(navController, back.arguments?.getString("entryId") ?: "")
            }
        }
    }
