package com.android.tripbook.reservation.notifications.bridge

import com.android.tripbook.reservation.notifications.models.ServiceNotification

interface NotificationBridge {
    fun setNotificationListener(listener: (ServiceNotification) -> Unit)
    fun sendToService(message: String)
    fun disconnect()
}