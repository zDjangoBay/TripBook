package com.android.tripbook.notifications

import android.content.Context
import android.util.Log
import com.android.tripbook.model.Trip
import com.android.tripbook.model.ItineraryItem

/**
 * Service that manages trip-related notifications
 * This is the main interface for scheduling and managing notifications
 */
class TripNotificationService(private val context: Context) {
    
    companion object {
        private const val TAG = "TripNotificationService"
        
        @Volatile
        private var INSTANCE: TripNotificationService? = null
        
        fun getInstance(context: Context): TripNotificationService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TripNotificationService(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Schedule all notifications when a new trip is created
     */
    fun onTripCreated(trip: Trip) {
        Log.d(TAG, "Trip created: ${trip.name}, scheduling notifications")
        NotificationScheduler.scheduleNotificationsForTrip(trip)
    }
    
    /**
     * Update notifications when a trip is modified
     */
    fun onTripUpdated(trip: Trip) {
        Log.d(TAG, "Trip updated: ${trip.name}, rescheduling notifications")
        
        // Cancel existing notifications for this trip
        NotificationScheduler.cancelNotificationsForTrip(trip.id)
        
        // Schedule new notifications with updated data
        NotificationScheduler.scheduleNotificationsForTrip(trip)
    }
    
    /**
     * Cancel all notifications when a trip is deleted
     */
    fun onTripDeleted(tripId: String) {
        Log.d(TAG, "Trip deleted: $tripId, cancelling notifications")
        NotificationScheduler.cancelNotificationsForTrip(tripId)
    }
    
    /**
     * Schedule notifications when a new itinerary item is added
     */
    fun onItineraryItemAdded(trip: Trip, item: ItineraryItem) {
        Log.d(TAG, "Itinerary item added: ${item.title}, updating notifications")
        
        // Reschedule all notifications for the trip to include the new item
        onTripUpdated(trip)
    }
    
    /**
     * Update notifications when an itinerary item is modified
     */
    fun onItineraryItemUpdated(trip: Trip, item: ItineraryItem) {
        Log.d(TAG, "Itinerary item updated: ${item.title}, updating notifications")
        
        // Reschedule all notifications for the trip
        onTripUpdated(trip)
    }
    
    /**
     * Remove notifications when an itinerary item is deleted
     */
    fun onItineraryItemDeleted(trip: Trip, itemId: String) {
        Log.d(TAG, "Itinerary item deleted: $itemId, updating notifications")
        
        // Reschedule all notifications for the trip
        onTripUpdated(trip)
    }
    
    /**
     * Send immediate test notification
     */
    fun sendTestNotification() {
        Log.d(TAG, "Sending test notification")
        
        NotificationHelper.showNotification(
            context = context,
            notificationId = 9999,
            title = "🧪 Test Notification",
            message = "TripBook notifications are working correctly!",
            type = NotificationHelper.NotificationType.TRIP_STARTING_SOON
        )
    }
    
    /**
     * Get notification system status for debugging
     */
    fun getNotificationStatus(): NotificationStatus {
        return NotificationStatus(
            isInitialized = true,
            schedulerInfo = NotificationScheduler.getScheduledNotificationsInfo(),
            testNotificationSent = false
        )
    }
}

/**
 * Data class for notification system status
 */
data class NotificationStatus(
    val isInitialized: Boolean,
    val schedulerInfo: String,
    val testNotificationSent: Boolean
)
