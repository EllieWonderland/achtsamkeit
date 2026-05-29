package com.elliewonderland.achtsamkeit.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.ui.heute.HeuteScreen
import com.elliewonderland.achtsamkeit.ui.history.TagebuchScreen
import com.elliewonderland.achtsamkeit.ui.profil.ProfilScreen
import com.elliewonderland.achtsamkeit.ui.stats.StatistikScreen
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun MainTabContainerScreen(
    navController: NavController,
    choice: ThemeChoice,
    initialTab: String = "heute",
    scrollToDate: String? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val userId = remember { Firebase.auth.currentUser?.uid ?: "" }

    val pagerState = rememberPagerState(
        initialPage = when (initialTab) {
            "heute" -> 0
            "tagebuch" -> 1
            "statistik" -> 2
            "profil" -> 3
            else -> 0
        },
        pageCount = { 4 }
    )

    // Sync external arguments with the active page index
    LaunchedEffect(initialTab) {
        val targetPage = when (initialTab) {
            "heute" -> 0
            "tagebuch" -> 1
            "statistik" -> 2
            "profil" -> 3
            else -> 0
        }
        if (pagerState.currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    // Chronological navigation stack for custom back button behavior
    var isBackNavigating by remember { mutableStateOf(false) }
    val tabHistory = remember { mutableStateListOf<Int>() }

    // Seed history with initial page
    LaunchedEffect(Unit) {
        tabHistory.add(pagerState.currentPage)
    }

    LaunchedEffect(pagerState.currentPage) {
        val page = pagerState.currentPage
        if (isBackNavigating) {
            isBackNavigating = false
        } else {
            if (tabHistory.isEmpty() || tabHistory.last() != page) {
                // Remove existing occurrences to prevent infinite back-and-forth loops
                tabHistory.removeAll { it == page }
                tabHistory.add(page)
                
                // Keep history capped at 10 items to save memory
                if (tabHistory.size > 10) {
                    tabHistory.removeAt(0)
                }
            }
        }
    }

    // Intercept back gesture to navigate chronologically through tabs
    BackHandler(enabled = tabHistory.size > 1) {
        isBackNavigating = true
        tabHistory.removeLast() // remove current
        val prevPage = tabHistory.last()
        coroutineScope.launch {
            pagerState.animateScrollToPage(prevPage)
        }
    }

    Scaffold(
        bottomBar = {
            val currentRoute = when (pagerState.currentPage) {
                0 -> Screen.Heute.route
                1 -> Screen.Tagebuch.route
                2 -> Screen.Statistik.route
                3 -> Screen.Profil.route
                else -> Screen.Heute.route
            }
            BottomNavBar(
                navController = navController,
                currentRoute = currentRoute,
                onTabClick = { screen ->
                    val targetPage = when (screen) {
                        Screen.Heute -> 0
                        Screen.Tagebuch -> 1
                        Screen.Statistik -> 2
                        Screen.Profil -> 3
                        else -> 0
                    }
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(targetPage)
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) { page ->
            when (page) {
                0 -> HeuteScreen(navController = navController, isActive = (pagerState.currentPage == 0))
                1 -> TagebuchScreen(navController = navController, scrollToDate = scrollToDate, isActive = (pagerState.currentPage == 1))
                2 -> StatistikScreen(navController = navController, isActive = (pagerState.currentPage == 2))
                3 -> ProfilScreen(navController = navController, choice = choice)
            }
        }
    }
}
