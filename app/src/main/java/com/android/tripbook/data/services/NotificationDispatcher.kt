package com.android.tripbook.data.services

import com.android.tripbook.data.models.Notification
import com.android.tripbook.data.mock.MockNotificationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
}
