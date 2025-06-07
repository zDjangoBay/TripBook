package com.android.tripbook.reservation.notifications.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Traitement des actions de notification
        val action = intent.action
        when (action) {
            "com.tripbook.reservation.NOTIFICATION_ACTION" -> {
                // Traiter l'action de notification
            }
        }
    }
}