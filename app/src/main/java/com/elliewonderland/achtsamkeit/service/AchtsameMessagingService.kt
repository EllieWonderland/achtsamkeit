package com.elliewonderland.achtsamkeit.service

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.elliewonderland.achtsamkeit.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AchtsameMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: "Zeit für dich"
        val body  = message.notification?.body  ?: "Dein Eintrag wartet."

        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notif)
    }

    override fun onNewToken(token: String) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid)
            .update("fcm_token", token)
    }

    companion object {
        const val CHANNEL_ID = "achtsam_reminder"
    }
}
