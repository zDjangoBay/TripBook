package com.android.tripbook.data.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

/**
 * Fake notification dispatcher for simulating in-app notifications
 */
class FakeNotificationDispatcher {
    
    data class Notification(
        val id: String,
        val title: String,
        val message: String,
        val type: NotificationType,
        val timestamp: LocalDateTime,
        val isRead: Boolean = false
    )
    
    enum class NotificationType {
        PAYMENT_SUCCESS,
        PAYMENT_FAILED,
        TRIP_REMINDER,
        BOOKING_CONFIRMED,
        BOOKING_CANCELLED,
        GENERAL
    }
    
    private val _notifications = MutableStateFlow<List<Notification>>(
        // Add some initial dummy notifications
        listOf(
            Notification(
                id = "notif_1",
                title = "Welcome to TripBook!",
                message = "Start planning your next adventure with us.",
                type = NotificationType.GENERAL,
                timestamp = LocalDateTime.now().minusHours(2)
            ),
            Notification(
                id = "notif_2",
                title = "Trip Reminder",
                message = "Your Safari Adventure in Kenya is coming up in 5 days!",
                type = NotificationType.TRIP_REMINDER,
                timestamp = LocalDateTime.now().minusHours(1)
            )
        )
    )
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    
    fun sendPaymentSuccessNotification(reservationId: String, amount: Double) {
        val notification = Notification(
            id = "notif_payment_${System.currentTimeMillis()}",
            title = "Payment Successful!",
            message = "Your payment of $${String.format("%.2f", amount)} has been processed successfully.",
            type = NotificationType.PAYMENT_SUCCESS,
            timestamp = LocalDateTime.now()
        )
        addNotification(notification)
    }
    
    fun sendPaymentFailedNotification(reservationId: String) {
        val notification = Notification(
            id = "notif_payment_fail_${System.currentTimeMillis()}",
            title = "Payment Failed",
            message = "We couldn't process your payment. Please try again.",
            type = NotificationType.PAYMENT_FAILED,
            timestamp = LocalDateTime.now()
        )
        addNotification(notification)
    }
    
    fun sendBookingConfirmedNotification(tripTitle: String) {
        val notification = Notification(
            id = "notif_booking_${System.currentTimeMillis()}",
            title = "Booking Confirmed!",
            message = "Your booking for '$tripTitle' has been confirmed.",
            type = NotificationType.BOOKING_CONFIRMED,
            timestamp = LocalDateTime.now()
        )
        addNotification(notification)
    }
    
    fun sendTripReminderNotification(tripTitle: String, daysUntilTrip: Int) {
        val notification = Notification(
            id = "notif_reminder_${System.currentTimeMillis()}",
            title = "Trip Reminder",
            message = "Your trip '$tripTitle' is coming up in $daysUntilTrip days!",
            type = NotificationType.TRIP_REMINDER,
            timestamp = LocalDateTime.now()
        )
        addNotification(notification)
    }
    
    fun sendBookingCancelledNotification(tripTitle: String) {
        val notification = Notification(
            id = "notif_cancel_${System.currentTimeMillis()}",
            title = "Booking Cancelled",
            message = "Your booking for '$tripTitle' has been cancelled.",
            type = NotificationType.BOOKING_CANCELLED,
            timestamp = LocalDateTime.now()
        )
        addNotification(notification)
    }
    
    private fun addNotification(notification: Notification) {
        _notifications.value = listOf(notification) + _notifications.value
    }
    
    fun markAsRead(notificationId: String) {
        _notifications.value = _notifications.value.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
    }
    
    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
    
    fun getUnreadCount(): Int {
        return _notifications.value.count { !it.isRead }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: FakeNotificationDispatcher? = null
        
        fun getInstance(): FakeNotificationDispatcher {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FakeNotificationDispatcher().also { INSTANCE = it }
            }
        }
    }
}
