package com.android.tripbook.data.services

import com.android.tripbook.data.models.Notification
import com.android.tripbook.data.models.NotificationType
import com.android.tripbook.data.mock.MockNotificationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class NotificationDispatcher {
    private val _notifications = MutableStateFlow(MockNotificationData.getNotifications())
    val notifications: StateFlow<List<Notification>> = _notifications

    fun addNotification(notification: Notification) {
        MockNotificationData.addNotification(notification)
        _notifications.value = MockNotificationData.getNotifications()
    }

    fun markAsRead(notificationId: String) {
        MockNotificationData.markAsRead(notificationId)
        _notifications.value = MockNotificationData.getNotifications()
    }

    fun sendNotification(type: NotificationType, message: String) {
        val notification = Notification(
            id = System.currentTimeMillis().toString(),
            type = type,
            message = message,
            timestamp = Date(),
            isRead = false
        )
        addNotification(notification)
    }
}
