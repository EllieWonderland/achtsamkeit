package com.elliewonderland.achtsamkeit.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs   = context.getSharedPreferences(NotificationScheduler.PREFS_NAME, Context.MODE_PRIVATE)
        val morning = prefs.getString("morning_time", null) ?: return
        val evening = prefs.getString("evening_time", null) ?: return
        NotificationScheduler.scheduleAlarms(context, morning, evening)
    }
}
