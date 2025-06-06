package com.android.tripbook.model

import java.time.LocalDateTime

/**
 * Data models for the notification frontend system
 */

/**
 * Represents a notification in the frontend
 */
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationItemType,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val tripId: String? = null,
    val tripName: String? = null,
    val actionData: NotificationActionData? = null
)

/**
 * Types of notifications for frontend display
 */
enum class NotificationItemType(val displayName: String, val icon: String) {
    TRIP_STARTING("Trip Starting", "🎒"),
    TRIP_ENDING("Trip Ending", "🏁"),
    ITINERARY_REMINDER("Activity Reminder", "📅"),
    TRIP_UPDATE("Trip Update", "✏️"),
    SYSTEM("System", "⚙️"),
    TEST("Test", "🧪")
}

/**
 * Action data for notifications that can be acted upon
 */
data class NotificationActionData(
    val actionType: NotificationActionType,
    val targetId: String,
    val additionalData: Map<String, String> = emptyMap()
)

/**
 * Types of actions that can be performed from notifications
 */
enum class NotificationActionType {
    VIEW_TRIP,
    VIEW_ITINERARY,
    EDIT_TRIP,
    OPEN_ACTIVITY,
    NONE
}

/**
 * Notification preferences for users
 */
data class NotificationPreferences(
    val tripStartNotifications: Boolean = true,
    val tripEndNotifications: Boolean = true,
    val itineraryReminders: Boolean = true,
    val tripStartAdvanceDays: Int = 3, // How many days before trip to notify
    val itineraryAdvanceHours: Int = 2, // How many hours before activity to notify
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "08:00",
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

/**
 * Notification statistics for display
 */
data class NotificationStats(
    val totalNotifications: Int = 0,
    val unreadCount: Int = 0,
    val todayCount: Int = 0,
    val thisWeekCount: Int = 0,
    val upcomingScheduled: Int = 0
)

/**
 * Scheduled notification info for display
 */
data class ScheduledNotificationInfo(
    val id: String,
    val title: String,
    val message: String,
    val scheduledTime: LocalDateTime,
    val type: NotificationItemType,
    val tripId: String? = null,
    val tripName: String? = null,
    val isActive: Boolean = true
)

/**
 * UI state for notification screen
 */
data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationItem> = emptyList(),
    val scheduledNotifications: List<ScheduledNotificationInfo> = emptyList(),
    val preferences: NotificationPreferences = NotificationPreferences(),
    val stats: NotificationStats = NotificationStats(),
    val error: String? = null,
    val selectedFilter: NotificationFilter = NotificationFilter.ALL,
    val showSettings: Boolean = false,
    val showScheduled: Boolean = false
)

/**
 * Filter options for notifications
 */
enum class NotificationFilter(val displayName: String) {
    ALL("All"),
    UNREAD("Unread"),
    TRIP_RELATED("Trip Related"),
    TODAY("Today"),
    THIS_WEEK("This Week")
}

/**
 * Extension functions for convenience
 */
fun NotificationItem.isToday(): Boolean {
    val today = LocalDateTime.now().toLocalDate()
    return timestamp.toLocalDate() == today
}

fun NotificationItem.isThisWeek(): Boolean {
    val now = LocalDateTime.now()
    val weekStart = now.minusDays(now.dayOfWeek.value.toLong() - 1)
    return timestamp.isAfter(weekStart)
}

fun List<NotificationItem>.filterByType(filter: NotificationFilter): List<NotificationItem> {
    return when (filter) {
        NotificationFilter.ALL -> this
        NotificationFilter.UNREAD -> this.filter { !it.isRead }
        NotificationFilter.TRIP_RELATED -> this.filter { it.tripId != null }
        NotificationFilter.TODAY -> this.filter { it.isToday() }
        NotificationFilter.THIS_WEEK -> this.filter { it.isThisWeek() }
    }
}

/**
 * Sample data for testing
 */
object NotificationSampleData {
    fun getSampleNotifications(): List<NotificationItem> {
        val now = LocalDateTime.now()
        return listOf(
            NotificationItem(
                id = "1",
                title = "🎒 Trip starting tomorrow!",
                message = "Get ready for your Paris Adventure trip!",
                type = NotificationItemType.TRIP_STARTING,
                timestamp = now.minusHours(2),
                tripId = "trip_1",
                tripName = "Paris Adventure",
                actionData = NotificationActionData(NotificationActionType.VIEW_TRIP, "trip_1")
            ),
            NotificationItem(
                id = "2",
                title = "📅 Activity reminder",
                message = "Eiffel Tower visit in 2 hours",
                type = NotificationItemType.ITINERARY_REMINDER,
                timestamp = now.minusHours(1),
                tripId = "trip_1",
                tripName = "Paris Adventure",
                actionData = NotificationActionData(NotificationActionType.VIEW_ITINERARY, "activity_1")
            ),
            NotificationItem(
                id = "3",
                title = "🏁 Last day of trip!",
                message = "Make the most of your remaining time in Tokyo",
                type = NotificationItemType.TRIP_ENDING,
                timestamp = now.minusDays(1),
                isRead = true,
                tripId = "trip_2",
                tripName = "Tokyo Explorer"
            ),
            NotificationItem(
                id = "4",
                title = "🧪 Test Notification",
                message = "TripBook notifications are working correctly!",
                type = NotificationItemType.TEST,
                timestamp = now.minusMinutes(30)
            )
        )
    }
    
    fun getSampleScheduledNotifications(): List<ScheduledNotificationInfo> {
        val now = LocalDateTime.now()
        return listOf(
            ScheduledNotificationInfo(
                id = "sched_1",
                title = "Trip starting in 3 days",
                message = "Beach Vacation starts soon!",
                scheduledTime = now.plusDays(3).withHour(9).withMinute(0),
                type = NotificationItemType.TRIP_STARTING,
                tripId = "trip_3",
                tripName = "Beach Vacation"
            ),
            ScheduledNotificationInfo(
                id = "sched_2",
                title = "Activity reminder",
                message = "Sunset beach walk in 2 hours",
                scheduledTime = now.plusHours(2),
                type = NotificationItemType.ITINERARY_REMINDER,
                tripId = "trip_3",
                tripName = "Beach Vacation"
            )
        )
    }
}
