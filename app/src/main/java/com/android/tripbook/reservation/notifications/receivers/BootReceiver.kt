package com.android.tripbook.reservation.notifications.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_BOOT_COMPLETED) {
            // Redémarrer le service de notifications
            val serviceIntent = Intent(context,
                com.tripbook.reservation.notifications.services.NotificationService::class.java)
            context.startService(serviceIntent)
        }
    }
}