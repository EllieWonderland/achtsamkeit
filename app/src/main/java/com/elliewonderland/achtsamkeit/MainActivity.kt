package com.elliewonderland.achtsamkeit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationChannelCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elliewonderland.achtsamkeit.service.AchtsameMessagingService
import com.elliewonderland.achtsamkeit.ui.navigation.AppNavHost
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.Palette
import com.elliewonderland.achtsamkeit.ui.theme.ThemeChoice
import com.elliewonderland.achtsamkeit.ui.theme.ThemePreferences
import com.elliewonderland.achtsamkeit.ui.theme.Variant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        val initial = ThemeChoice(Variant.HAIN, Palette.SALBEI)
        setContent {
            val choice by ThemePreferences.flow(this).collectAsStateWithLifecycle(initial)
            AppTheme(variant = choice.variant, palette = choice.palette) {
                AppNavHost(choice)
            }
        }
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
