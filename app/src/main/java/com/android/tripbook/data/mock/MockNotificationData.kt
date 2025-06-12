package com.android.tripbook.data.mock

import com.android.tripbook.data.models.Notification
import com.android.tripbook.data.models.NotificationType
import java.util.Date

object MockNotificationData {
    private val notifications = mutableListOf(
        Notification(
            id = "1",
            type = NotificationType.PAYMENT_SUCCESS,
            message = "Your payment was successful!",
            timestamp = Date(),
            isRead = false
        ),
        Notification(
            id = "2",
            type = NotificationType.BOOKING_CONFIRMED,
            message = "Your booking has been confirmed!",
            timestamp = Date(),
            isRead = false
        ),
        Notification(
            id = "3",
            type = NotificationType.GENERAL,
            message = "Welcome to TripBook!",
            timestamp = Date(),
            isRead = true
        )
    )

    fun getNotifications(): List<Notification> = notifications

    fun markAsRead(notificationId: String) {
        notifications.find { it.id == notificationId }?.isRead = true
    }

    fun addNotification(notification: Notification) {
        notifications.add(notification)
    }
}
