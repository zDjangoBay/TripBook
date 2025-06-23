package com.android.tripbook.reservation.notifications

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.tripbook.reservation.notifications.bridge.NotificationBridge
import com.android.tripbook.reservation.notifications.models.*
import com.android.tripbook.reservation.notifications.utils.NotificationFormatter
import com.tripbook.reservation.notifications.models.InAppNotification

class NotificationManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: NotificationManager? = null

        fun getInstance(context: Context): NotificationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotificationManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val _notifications = MutableLiveData<List<InAppNotification>>()
    val notifications: LiveData<List<InAppNotification>> = _notifications

    private val notificationList = mutableListOf<InAppNotification>()

    private var bridge: NotificationBridge? = null

    fun initializeBridge(notificationBridge: NotificationBridge) {
        this.bridge = notificationBridge
        // Écoute les notifications du service
        bridge?.setNotificationListener { serviceNotification ->
            processIncomingNotification(serviceNotification)
        }
    }
    private fun processIncomingNotification(serviceNotification: ServiceNotification) {
        val inAppNotification = NotificationFormatter.formatNotification(serviceNotification)

        if (inAppNotification.isUrgent || inAppNotification.priority == NotificationPriority.CRITICAL) {
            showUrgentNotification(inAppNotification)
        } else {
            showNotification(inAppNotification)
        }
    }

    fun showNotification(notification: InAppNotification) {
        notificationList.add(0, notification) // Ajouter en première position
        _notifications.postValue(notificationList.toList())

        // Marquer comme vue après 5 secondes si pas d'interaction
        if (!notification.isUrgent) {
            scheduleAutoMarkAsRead(notification.id)
        }
    }

    /**
     * Gère les notifications urgentes (vols annulés, embarquement)
     */
    fun showUrgentNotification(notification: InAppNotification) {
        val urgentNotification = notification.copy(
            isUrgent = true,
            priority = NotificationPriority.CRITICAL
        )

        notificationList.add(0, urgentNotification)
        _notifications.postValue(notificationList.toList())

    }

    fun markAsRead(notificationId: String) {
        val index = notificationList.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            notificationList[index] = notificationList[index].copy(isRead = true)
            _notifications.postValue(notificationList.toList())
        }
    }

    fun dismissNotification(notificationId: String) {
        notificationList.removeAll { it.id == notificationId }
        _notifications.postValue(notificationList.toList())
    }

    fun clearAllNotifications() {
        notificationList.clear()
        _notifications.postValue(emptyList())
    }

    fun getUnreadCount(): Int {
        return notificationList.count { !it.isRead }
    }

    fun getNotificationsByFlight(flightNumber: String): List<InAppNotification> {
        return notificationList.filter { notification ->
            val flightNum = notification.data?.get("flightNumber") as? String
            flightNum == flightNumber
        }
    }

    fun getCriticalNotifications(): List<InAppNotification> {
        return notificationList.filter {
            !it.isRead && (it.priority == NotificationPriority.CRITICAL || it.isUrgent)
        }
    }

    fun cleanExpiredNotifications() {
        val currentTime = System.currentTimeMillis()
        val beforeCount = notificationList.size

        notificationList.removeAll { notification ->
            notification.expiresAt != null && notification.expiresAt < currentTime
        }

        if (notificationList.size != beforeCount) {
            _notifications.postValue(notificationList.toList())
        }
    }
    fun markFlightNotificationsAsRead(flightNumber: String) {
        var hasChanges = false

        notificationList.forEachIndexed { index, notification ->
            val flightNum = notification.data?.get("flightNumber") as? String
            if (flightNum == flightNumber && !notification.isRead) {
                notificationList[index] = notification.copy(isRead = true)
                hasChanges = true
            }
        }

        if (hasChanges) {
            _notifications.postValue(notificationList.toList())
        }
    }

    fun handleNotificationAction(notificationId: String, action: NotificationAction) {
        val notification = notificationList.find { it.id == notificationId }
        notification?.let {

            markAsRead(notificationId)


            bridge?.sendToService("ACTION:${action.name}:${it.data?.get("bookingReference")}")
        }
    }

    fun getNotificationsByBooking(bookingRef: String): List<InAppNotification> {
        return notificationList.filter {
            it.data?.get("bookingReference") == bookingRef
        }
    }

    fun getNotificationStats(): Map<String, Int> {
        return mapOf(
            "total" to notificationList.size,
            "unread" to getUnreadCount(),
            "critical" to getCriticalNotifications().size,
            "urgent" to notificationList.count { it.isUrgent },
            "expired" to notificationList.count {
                it.expiresAt != null && it.expiresAt < System.currentTimeMillis()
            }
        )
    }


    private fun scheduleAutoMarkAsRead(notificationId: String) {
        // Auto-mark as read after 5 seconds (simulate user seeing it)
        Handler(Looper.getMainLooper()).postDelayed({
            markAsRead(notificationId)
        }, 5000)
    }
 fun scheduleFlightReminders(notification: InAppNotification) {
        val flightTime = notification.data?.get("departureTime") as? Long
        if (flightTime != null) {
            val currentTime = System.currentTimeMillis()
            val timeUntilFlight = flightTime - currentTime

            when {
                timeUntilFlight > 24 * 60 * 60 * 1000 -> {

                    scheduleCheckInReminder(notification, flightTime - 24 * 60 * 60 * 1000)
                }
                timeUntilFlight > 2 * 60 * 60 * 1000 -> {

                    scheduleDepartureReminder(notification, flightTime - 2 * 60 * 60 * 1000)
                }
            }
        }
    }

    private fun scheduleCheckInReminder(notification: InAppNotification, reminderTime: Long) {
        val delay = reminderTime - System.currentTimeMillis()
        if (delay > 0) {
            Handler(Looper.getMainLooper()).postDelayed({

                val checkInReminder = InAppNotification(
                    id = "checkin_${notification.id}",
                    title = "⏱️ Rappel enregistrement",
                    message = "Vous pouvez maintenant vous enregistrer pour votre vol",
                    type = NotificationType.CHECK_IN_REMINDER,
                    priority = NotificationPriority.MEDIUM,
                    timestamp = System.currentTimeMillis(),
                    actionButton = "S'enregistrer",
                    actionType = NotificationAction.CHECK_IN_NOW,
                    data = notification.data
                )
                showNotification(checkInReminder)
            }, delay)
        }
    }

    private fun scheduleDepartureReminder(notification: InAppNotification, reminderTime: Long) {
        val delay = reminderTime - System.currentTimeMillis()
        if (delay > 0) {
            Handler(Looper.getMainLooper()).postDelayed({

                val departureReminder = InAppNotification(
                    id = "departure_${notification.id}",
                    title = "🚀 Rappel de départ",
                    message = "Votre vol décolle dans 2 heures",
                    type = NotificationType.BOOKING_REMINDER,
                    priority = NotificationPriority.HIGH,
                    timestamp = System.currentTimeMillis(),
                    actionButton = "Voir détails",
                    actionType = NotificationAction.VIEW_BOARDING_PASS,
                    data = notification.data
                )
                showNotification(departureReminder)
            }, delay)
        }
    }
}