package com.elliewonderland.achtsamkeit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elliewonderland.achtsamkeit.service.AchtsameMessagingService
import com.elliewonderland.achtsamkeit.service.NotificationScheduler
import com.elliewonderland.achtsamkeit.ui.navigation.AppNavHost
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.Palette
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.elliewonderland.achtsamkeit.ui.theme.ThemePreferences
import com.elliewonderland.achtsamkeit.ui.theme.Variant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        rescheduleAlarmsIfNeeded()
        val initial = ThemeChoice(Variant.HAIN, Palette.SALBEI)
        setContent {
            val choice by ThemePreferences.flow(this).collectAsStateWithLifecycle(initial)
            AppTheme(variant = choice.variant, palette = choice.palette) {
                AppNavHost(choice)
            }
        }
    }

    private fun rescheduleAlarmsIfNeeded() {
        val prefs   = getSharedPreferences(NotificationScheduler.PREFS_NAME, MODE_PRIVATE)
        val morning = prefs.getString("morning_time", null) ?: return
        val evening = prefs.getString("evening_time", null) ?: return
        NotificationScheduler.scheduleAlarms(this, morning, evening)
    }

    private fun createNotificationChannel() {
        NotificationManagerCompat.from(this).createNotificationChannel(
            NotificationChannelCompat
                .Builder(AchtsameMessagingService.CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName("Tägliche Erinnerungen")
                .setDescription("Morgen- und Abend-Erinnerungen für dein Tagebuch")
                .build()
        )
    }
}
