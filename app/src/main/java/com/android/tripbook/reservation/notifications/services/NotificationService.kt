package com.android.tripbook.reservation.notifications.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.tripbook.reservation.notifications.models.ServiceNotification

class NotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Logique du service de notifications
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Nettoyage des ressources
    }
}