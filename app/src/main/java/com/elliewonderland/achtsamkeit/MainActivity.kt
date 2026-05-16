package com.elliewonderland.achtsamkeit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elliewonderland.achtsamkeit.data.repository.AuthRepository
import com.elliewonderland.achtsamkeit.ui.auth.LoginScreen
import com.elliewonderland.achtsamkeit.ui.auth.RegisterScreen
import com.elliewonderland.achtsamkeit.ui.onboarding.OnboardingScreen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.Palette
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.elliewonderland.achtsamkeit.ui.theme.ThemePreferences
import com.elliewonderland.achtsamkeit.ui.theme.Variant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initial = ThemeChoice(Variant.HAIN, Palette.SALBEI)
        setContent {
            val choice by ThemePreferences.flow(this).collectAsStateWithLifecycle(initial)
            AppTheme(variant = choice.variant, palette = choice.palette) {
                AppNavHost(choice)
            }
        }
    }
}

@Composable
fun AppNavHost(choice: ThemeChoice) {
    val navController = rememberNavController()
    val authRepo      = remember { AuthRepository() }
    val startDest     = if (authRepo.getCurrentUser() != null) "heute" else "login"

    NavHost(navController = navController, startDestination = startDest) {
        composable("login")      { LoginScreen(navController) }
        composable("register")   { RegisterScreen(navController) }
        composable("onboarding") { OnboardingScreen(navController) }
        // Platzhalter — wird in Phase 4 + 5 durch den echten Screen ersetzt
        composable("heute")      { HeuteScreenPlaceholder() }
    }
}

@Composable
private fun HeuteScreenPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Heute — wird in Phase 5 implementiert")
    }
}
