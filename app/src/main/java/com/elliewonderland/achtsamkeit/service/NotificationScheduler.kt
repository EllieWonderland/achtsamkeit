package com.elliewonderland.achtsamkeit.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    const val REQUEST_MORNING = 1001
    const val REQUEST_EVENING = 1002
    const val PREFS_NAME = "notification_prefs"

    fun scheduleAlarms(context: Context, morningTime: String, eveningTime: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString("morning_time", morningTime)
            .putString("evening_time", eveningTime)
            .apply()

        scheduleAlarm(context, morningTime, REQUEST_MORNING, "morning",
            "Morgenroutine", "Nimm dir 3 Minuten nur für dich.")
        scheduleAlarm(context, eveningTime, REQUEST_EVENING, "evening",
            "Abendroutine", "Wie war dein Tag heute?")
    }

    private fun scheduleAlarm(
        context: Context, timeStr: String, requestCode: Int,
        type: String, title: String, body: String,
    ) {
        val parts  = timeStr.split(":")
        val hour   = parts.getOrNull(0)?.toIntOrNull() ?: return
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return

        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }

        val pi = pendingIntent(context, hour, minute, requestCode, type, title, body,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.timeInMillis, pi)
    }

    fun buildReceiverIntent(
        context: Context, hour: Int, minute: Int, requestCode: Int,
        type: String, title: String, body: String,
    ): Intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra(NotificationReceiver.EXTRA_TITLE, title)
        putExtra(NotificationReceiver.EXTRA_BODY, body)
        putExtra(NotificationReceiver.EXTRA_HOUR, hour)
        putExtra(NotificationReceiver.EXTRA_MINUTE, minute)
        putExtra(NotificationReceiver.EXTRA_REQUEST_CODE, requestCode)
        putExtra(NotificationReceiver.EXTRA_TYPE, type)
    }

    private fun pendingIntent(
        context: Context, hour: Int, minute: Int, requestCode: Int,
        type: String, title: String, body: String, flags: Int,
    ) = PendingIntent.getBroadcast(
        context, requestCode,
        buildReceiverIntent(context, hour, minute, requestCode, type, title, body),
        flags,
    )
}
