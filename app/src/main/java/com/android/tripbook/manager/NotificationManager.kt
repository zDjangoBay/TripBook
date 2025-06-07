package com.android.tripbook.manager

import android.content.Context
import com.android.tripbook.model.*
import com.android.tripbook.service.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

/**
 * Central manager for handling all notification-related operations
 */
class NotificationManager(private val context: Context) {
    
    private val notificationScheduler = NotificationScheduler(context)
    
    // State flows for reactive UI updates
    private val _notifications = MutableStateFlow<List<TripNotification>>(emptyList())
    val notifications: StateFlow<List<TripNotification>> = _notifications.asStateFlow()
    
    private val _preferences = MutableStateFlow(getDefaultPreferences())
    val preferences: StateFlow<NotificationPreferences> = _preferences.asStateFlow()
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    // In-memory storage (in a real app, this would be a database)
    private val notificationStorage = mutableListOf<TripNotification>()
    
    /**
     * Initialize the notification manager
     */
    fun initialize() {
        loadNotifications()
        loadPreferences()
        updateUnreadCount()
    }
    
    /**
     * Schedule notifications for a new or updated trip
     */
    fun scheduleNotificationsForTrip(trip: Trip) {
        notificationScheduler.scheduleNotificationsForTrip(trip, _preferences.value)
    }
    
    /**
     * Cancel notifications for a trip (e.g., when trip is cancelled)
     */
    fun cancelNotificationsForTrip(tripId: String) {
        notificationScheduler.cancelNotificationsForTrip(tripId)
        
        // Remove from local storage
        notificationStorage.removeAll { it.tripId == tripId }
        _notifications.value = notificationStorage.toList()
        updateUnreadCount()
    }
    
    /**
     * Update notification preferences
     */
    fun updatePreferences(newPreferences: NotificationPreferences, trips: List<Trip>) {
        _preferences.value = newPreferences
        savePreferences(newPreferences)
        
        // Reschedule all notifications with new preferences
        notificationScheduler.updateNotificationPreferences(newPreferences, trips)
    }
    
    /**
     * Mark a notification as read
     */
    fun markAsRead(notificationId: String) {
        val index = notificationStorage.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            notificationStorage[index] = notificationStorage[index].copy(isRead = true)
            _notifications.value = notificationStorage.toList()
            updateUnreadCount()
        }
    }
    
    /**
     * Mark all notifications as read
     */
    fun markAllAsRead() {
        notificationStorage.forEachIndexed { index, notification ->
            notificationStorage[index] = notification.copy(isRead = true)
        }
        _notifications.value = notificationStorage.toList()
        updateUnreadCount()
    }
    
    /**
     * Add a new notification (called when a notification is triggered)
     */
    fun addNotification(notification: TripNotification) {
        notificationStorage.add(0, notification) // Add to beginning
        _notifications.value = notificationStorage.toList()
        updateUnreadCount()
        
        // Keep only last 50 notifications to prevent memory issues
        if (notificationStorage.size > 50) {
            notificationStorage.removeAt(notificationStorage.size - 1)
        }
    }
    
    /**
     * Remove a notification
     */
    fun removeNotification(notificationId: String) {
        notificationStorage.removeAll { it.id == notificationId }
        _notifications.value = notificationStorage.toList()
        updateUnreadCount()
    }
    
    /**
     * Clear all notifications
     */
    fun clearAllNotifications() {
        notificationStorage.clear()
        _notifications.value = emptyList()
        updateUnreadCount()
    }
    
    /**
     * Get notifications for a specific trip
     */
    fun getNotificationsForTrip(tripId: String): List<TripNotification> {
        return notificationStorage.filter { it.tripId == tripId }
    }
    
    /**
     * Get notifications by type
     */
    fun getNotificationsByType(type: NotificationType): List<TripNotification> {
        return notificationStorage.filter { it.type == type }
    }
    
    /**
     * Schedule an immediate test notification
     */
    fun scheduleTestNotification(trip: Trip, type: NotificationType) {
        notificationScheduler.scheduleImmediateNotification(trip, type)
    }
    
    /**
     * Check if notifications are enabled for a specific type
     */
    fun isNotificationTypeEnabled(type: NotificationType): Boolean {
        val prefs = _preferences.value
        return prefs.enablePushNotifications && when (type) {
            NotificationType.TRIP_STARTING_SOON,
            NotificationType.TRIP_STARTING_TODAY -> prefs.enableTripStartReminders
            NotificationType.ADD_ACTIVITIES -> prefs.enableActivityReminders
            NotificationType.BUDGET_REMINDER -> prefs.enableBudgetReminders
            NotificationType.PACKING_REMINDER -> prefs.enablePackingReminders
            NotificationType.DOCUMENT_REMINDER -> prefs.enableDocumentReminders
            else -> true
        }
    }
    
    /**
     * Get upcoming notifications for a trip
     */
    fun getUpcomingNotifications(tripId: String): List<TripNotification> {
        val now = LocalDateTime.now()
        return notificationStorage.filter { 
            it.tripId == tripId && 
            it.scheduledTime.isAfter(now) && 
            !it.isSent 
        }.sortedBy { it.scheduledTime }
    }
    
    /**
     * Update unread count
     */
    private fun updateUnreadCount() {
        _unreadCount.value = notificationStorage.count { !it.isRead }
    }
    
    /**
     * Load notifications from storage (placeholder - would be from database)
     */
    private fun loadNotifications() {
        // In a real app, load from database
        // For now, start with empty list
        _notifications.value = notificationStorage.toList()
    }
    
    /**
     * Load preferences from storage (placeholder - would be from SharedPreferences/database)
     */
    private fun loadPreferences() {
        // In a real app, load from SharedPreferences or database
        // For now, use default preferences
        _preferences.value = getDefaultPreferences()
    }
    
    /**
     * Save preferences to storage (placeholder)
     */
    private fun savePreferences(preferences: NotificationPreferences) {
        // In a real app, save to SharedPreferences or database
        // For now, just keep in memory
    }
    
    /**
     * Get default notification preferences
     */
    private fun getDefaultPreferences(): NotificationPreferences {
        return NotificationPreferences(
            userId = "default_user",
            enablePushNotifications = true,
            enableTripStartReminders = true,
            enableActivityReminders = true,
            enableBudgetReminders = true,
            enablePackingReminders = true,
            enableDocumentReminders = true,
            tripStartReminderDays = listOf(7, 3, 1),
            activityReminderDays = 2,
            quietHoursStart = 22,
            quietHoursEnd = 8
        )
    }
    
    /**
     * Create sample notifications for testing
     */
    fun createSampleNotifications(trips: List<Trip>) {
        if (trips.isNotEmpty()) {
            val trip = trips.first()
            val sampleNotifications = listOf(
                TripNotification(
                    id = "sample_1",
                    tripId = trip.id,
                    type = NotificationType.TRIP_STARTING_SOON,
                    title = "Trip Starting Soon! 🌍",
                    message = "Your trip to ${trip.destination} starts in 3 days! Get ready for an amazing adventure.",
                    scheduledTime = LocalDateTime.now().minusHours(2),
                    priority = NotificationPriority.HIGH,
                    isRead = false,
                    isSent = true
                ),
                TripNotification(
                    id = "sample_2",
                    tripId = trip.id,
                    type = NotificationType.ADD_ACTIVITIES,
                    title = "Plan Your Adventure 📝",
                    message = "Add activities to your ${trip.name} itinerary to make the most of your trip!",
                    scheduledTime = LocalDateTime.now().minusDays(1),
                    priority = NotificationPriority.NORMAL,
                    isRead = true,
                    isSent = true
                ),
                TripNotification(
                    id = "sample_3",
                    tripId = trip.id,
                    type = NotificationType.PACKING_REMINDER,
                    title = "Time to Pack! 🧳",
                    message = "Start packing for your trip to ${trip.destination}. Don't forget the essentials!",
                    scheduledTime = LocalDateTime.now().minusHours(6),
                    priority = NotificationPriority.HIGH,
                    isRead = false,
                    isSent = true
                )
            )
            
            notificationStorage.addAll(sampleNotifications)
            _notifications.value = notificationStorage.toList()
            updateUnreadCount()
        }
    }
}
