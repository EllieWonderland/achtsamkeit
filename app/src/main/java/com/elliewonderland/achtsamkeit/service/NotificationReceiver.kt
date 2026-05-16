package com.elliewonderland.achtsamkeit.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.elliewonderland.achtsamkeit.R
import java.time.LocalDate
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title       = intent.getStringExtra(EXTRA_TITLE) ?: "Zeit für dich"
        val body        = intent.getStringExtra(EXTRA_BODY)  ?: "Dein Tagebuch wartet."
        val hour        = intent.getIntExtra(EXTRA_HOUR, -1)
        val minute      = intent.getIntExtra(EXTRA_MINUTE, -1)
        val requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1)
        val type        = intent.getStringExtra(EXTRA_TYPE) ?: ""

        // Nicht anzeigen wenn Eintrag für heute bereits gespeichert wurde
        val today     = LocalDate.now().toString()
        val prefs     = context.getSharedPreferences(NotificationScheduler.PREFS_NAME, Context.MODE_PRIVATE)
        val alreadyDone = prefs.getString("${type}_done_date", "") == today

        if (!alreadyDone) {
            showNotification(context, title, body)
        }

        // Immer für den nächsten Tag neu einplanen
        if (hour >= 0 && minute >= 0 && requestCode >= 0) {
            val nextDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, 1)
            }
            val pi = PendingIntent.getBroadcast(
                context, requestCode,
                NotificationScheduler.buildReceiverIntent(context, hour, minute, requestCode, type, title, body),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextDay.timeInMillis, pi)
        }
    }

    private fun showNotification(context: Context, title: String, body: String) {
        runCatching {
            val notif = NotificationCompat.Builder(context, AchtsameMessagingService.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), notif)
        }
    }

    companion object {
        const val EXTRA_TITLE        = "extra_title"
        const val EXTRA_BODY         = "extra_body"
        const val EXTRA_HOUR         = "extra_hour"
        const val EXTRA_MINUTE       = "extra_minute"
        const val EXTRA_REQUEST_CODE = "extra_request_code"
        const val EXTRA_TYPE         = "extra_type"
    }
}
