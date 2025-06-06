package com.android.tripbook.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.*
import com.android.tripbook.notifications.TripNotificationService
import com.android.tripbook.notifications.NotificationTester
import com.android.tripbook.repository.SupabaseTripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * ViewModel for managing notification frontend state and operations
 */
class NotificationViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "NotificationViewModel"
    }

    private val notificationService = TripNotificationService.getInstance(application)
    private val tripRepository = SupabaseTripRepository.getInstance()

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        loadScheduledNotifications()
        loadPreferences()
    }

    /**
     * Load notification history (for now using sample data)
     */
    fun loadNotifications() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // For now, use sample data. In a real app, this would load from a database
                val notifications = NotificationSampleData.getSampleNotifications()
                val stats = calculateStats(notifications)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    notifications = notifications,
                    stats = stats,
                    error = null
                )
                
                Log.d(TAG, "Loaded ${notifications.size} notifications")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading notifications", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load notifications: ${e.message}"
                )
            }
        }
    }

    /**
     * Load scheduled notifications
     */
    fun loadScheduledNotifications() {
        viewModelScope.launch {
            try {
                // For now, use sample data. In a real app, this would query WorkManager
                val scheduledNotifications = NotificationSampleData.getSampleScheduledNotifications()
                
                _uiState.value = _uiState.value.copy(
                    scheduledNotifications = scheduledNotifications
                )
                
                Log.d(TAG, "Loaded ${scheduledNotifications.size} scheduled notifications")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading scheduled notifications", e)
            }
        }
    }

    /**
     * Load user preferences
     */
    fun loadPreferences() {
        viewModelScope.launch {
            try {
                // For now, use default preferences. In a real app, this would load from SharedPreferences
                val preferences = NotificationPreferences()
                
                _uiState.value = _uiState.value.copy(preferences = preferences)
                
                Log.d(TAG, "Loaded notification preferences")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading preferences", e)
            }
        }
    }

    /**
     * Mark notification as read
     */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val currentNotifications = _uiState.value.notifications
                val updatedNotifications = currentNotifications.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(isRead = true)
                    } else {
                        notification
                    }
                }
                
                val stats = calculateStats(updatedNotifications)
                
                _uiState.value = _uiState.value.copy(
                    notifications = updatedNotifications,
                    stats = stats
                )
                
                Log.d(TAG, "Marked notification $notificationId as read")
            } catch (e: Exception) {
                Log.e(TAG, "Error marking notification as read", e)
            }
        }
    }

    /**
     * Mark all notifications as read
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val currentNotifications = _uiState.value.notifications
                val updatedNotifications = currentNotifications.map { it.copy(isRead = true) }
                val stats = calculateStats(updatedNotifications)
                
                _uiState.value = _uiState.value.copy(
                    notifications = updatedNotifications,
                    stats = stats
                )
                
                Log.d(TAG, "Marked all notifications as read")
            } catch (e: Exception) {
                Log.e(TAG, "Error marking all notifications as read", e)
            }
        }
    }

    /**
     * Delete notification
     */
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                val currentNotifications = _uiState.value.notifications
                val updatedNotifications = currentNotifications.filter { it.id != notificationId }
                val stats = calculateStats(updatedNotifications)
                
                _uiState.value = _uiState.value.copy(
                    notifications = updatedNotifications,
                    stats = stats
                )
                
                Log.d(TAG, "Deleted notification $notificationId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting notification", e)
            }
        }
    }

    /**
     * Clear all notifications
     */
    fun clearAllNotifications() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    notifications = emptyList(),
                    stats = NotificationStats()
                )
                
                Log.d(TAG, "Cleared all notifications")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing notifications", e)
            }
        }
    }

    /**
     * Update notification filter
     */
    fun setFilter(filter: NotificationFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        Log.d(TAG, "Set notification filter to $filter")
    }

    /**
     * Toggle settings visibility
     */
    fun toggleSettings() {
        _uiState.value = _uiState.value.copy(
            showSettings = !_uiState.value.showSettings
        )
    }

    /**
     * Toggle scheduled notifications visibility
     */
    fun toggleScheduled() {
        _uiState.value = _uiState.value.copy(
            showScheduled = !_uiState.value.showScheduled
        )
    }

    /**
     * Update notification preferences
     */
    fun updatePreferences(preferences: NotificationPreferences) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(preferences = preferences)
                
                // In a real app, save to SharedPreferences here
                Log.d(TAG, "Updated notification preferences")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating preferences", e)
            }
        }
    }

    /**
     * Send test notification
     */
    fun sendTestNotification() {
        notificationService.sendTestNotification()
        
        // Add test notification to the list
        val testNotification = NotificationItem(
            id = "test_${System.currentTimeMillis()}",
            title = "🧪 Test Notification",
            message = "TripBook notifications are working correctly!",
            type = NotificationItemType.TEST,
            timestamp = LocalDateTime.now()
        )
        
        val currentNotifications = _uiState.value.notifications
        val updatedNotifications = listOf(testNotification) + currentNotifications
        val stats = calculateStats(updatedNotifications)
        
        _uiState.value = _uiState.value.copy(
            notifications = updatedNotifications,
            stats = stats
        )
        
        Log.d(TAG, "Sent test notification")
    }

    /**
     * Run comprehensive notification tests
     */
    fun runNotificationTests() {
        NotificationTester.runNotificationTests(getApplication())
        Log.d(TAG, "Running comprehensive notification tests")
    }

    /**
     * Handle notification action
     */
    fun handleNotificationAction(notification: NotificationItem): String? {
        return when (notification.actionData?.actionType) {
            NotificationActionType.VIEW_TRIP -> {
                Log.d(TAG, "Handling VIEW_TRIP action for trip ${notification.tripId}")
                "TripDetails" // Return screen to navigate to
            }
            NotificationActionType.VIEW_ITINERARY -> {
                Log.d(TAG, "Handling VIEW_ITINERARY action")
                "TripDetails" // Return screen to navigate to
            }
            NotificationActionType.EDIT_TRIP -> {
                Log.d(TAG, "Handling EDIT_TRIP action")
                "EditTrip" // Return screen to navigate to
            }
            else -> {
                Log.d(TAG, "No action defined for notification ${notification.id}")
                null
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Calculate notification statistics
     */
    private fun calculateStats(notifications: List<NotificationItem>): NotificationStats {
        val unreadCount = notifications.count { !it.isRead }
        val todayCount = notifications.count { it.isToday() }
        val thisWeekCount = notifications.count { it.isThisWeek() }
        
        return NotificationStats(
            totalNotifications = notifications.size,
            unreadCount = unreadCount,
            todayCount = todayCount,
            thisWeekCount = thisWeekCount,
            upcomingScheduled = _uiState.value.scheduledNotifications.size
        )
    }

    /**
     * Get filtered notifications based on current filter
     */
    fun getFilteredNotifications(): List<NotificationItem> {
        return _uiState.value.notifications.filterByType(_uiState.value.selectedFilter)
    }
}
