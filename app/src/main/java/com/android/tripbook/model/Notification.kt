package com.android.tripbook.model

import java.time.LocalDateTime

/**
 * Represents different types of trip milestone notifications
 */
enum class NotificationType {
    TRIP_STARTING_SOON,      // "Your trip starts in X days!"
    TRIP_STARTING_TODAY,     // "Your trip starts today!"
    ADD_ACTIVITIES,          // "Add activities to your itinerary"
    TRIP_ENDING_SOON,        // "Your trip ends in X days"
    TRIP_COMPLETED,          // "How was your trip? Rate your experience"
    BUDGET_REMINDER,         // "Don't forget to track your expenses"
    PACKING_REMINDER,        // "Start packing for your trip"
    DOCUMENT_REMINDER        // "Check your travel documents"
}

/**
 * Represents the priority level of notifications
 */
enum class NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

/**
 * Data class representing a trip milestone notification
 */
data class TripNotification(
    val id: String,
    val tripId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val scheduledTime: LocalDateTime,
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val isRead: Boolean = false,
    val isSent: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val actionUrl: String? = null,  // Deep link to specific screen
    val metadata: Map<String, String> = emptyMap()  // Additional data
)

/**
 * Data class for notification preferences
 */
data class NotificationPreferences(
    val userId: String,
    val enablePushNotifications: Boolean = true,
    val enableTripStartReminders: Boolean = true,
    val enableActivityReminders: Boolean = true,
    val enableBudgetReminders: Boolean = true,
    val enablePackingReminders: Boolean = true,
    val enableDocumentReminders: Boolean = true,
    val tripStartReminderDays: List<Int> = listOf(7, 3, 1), // Days before trip
    val activityReminderDays: Int = 2, // Days after trip creation
    val quietHoursStart: Int = 22, // 10 PM
    val quietHoursEnd: Int = 8,    // 8 AM
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Data class representing a milestone event
 */
data class TripMilestone(
    val id: String,
    val tripId: String,
    val type: NotificationType,
    val triggerDate: LocalDateTime,
    val isTriggered: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
