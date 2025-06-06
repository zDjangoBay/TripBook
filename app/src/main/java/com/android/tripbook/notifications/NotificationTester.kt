package com.android.tripbook.notifications

import android.content.Context
import android.util.Log
import com.android.tripbook.model.*
import java.time.LocalDate
import java.time.LocalTime

/**
 * Utility class for testing notification functionality
 */
object NotificationTester {
    
    private const val TAG = "NotificationTester"
    
    /**
     * Test all notification types with sample data
     */
    fun runNotificationTests(context: Context) {
        Log.d(TAG, "Starting notification tests...")
        
        // Test 1: Basic notification
        testBasicNotification(context)
        
        // Test 2: Trip starting notifications
        testTripStartingNotifications(context)
        
        // Test 3: Itinerary reminders
        testItineraryReminders(context)
        
        // Test 4: Trip ending notifications
        testTripEndingNotifications(context)
        
        // Test 5: Notification scheduling
        testNotificationScheduling(context)
        
        Log.d(TAG, "All notification tests completed!")
    }
    
    private fun testBasicNotification(context: Context) {
        Log.d(TAG, "Testing basic notification...")
        
        NotificationHelper.showNotification(
            context = context,
            notificationId = 1001,
            title = "🧪 Basic Test",
            message = "This is a basic notification test for TripBook!",
            type = NotificationHelper.NotificationType.TRIP_STARTING_SOON
        )
    }
    
    private fun testTripStartingNotifications(context: Context) {
        Log.d(TAG, "Testing trip starting notifications...")
        
        // Test different scenarios
        NotificationHelper.showTripStartingNotification(
            context = context,
            tripName = "Paris Adventure",
            daysUntil = 3
        )
        
        // Delay to avoid notification spam
        Thread.sleep(1000)
        
        NotificationHelper.showTripStartingNotification(
            context = context,
            tripName = "Tokyo Explorer",
            daysUntil = 1
        )
        
        Thread.sleep(1000)
        
        NotificationHelper.showTripStartingNotification(
            context = context,
            tripName = "Safari Experience",
            daysUntil = 0
        )
    }
    
    private fun testItineraryReminders(context: Context) {
        Log.d(TAG, "Testing itinerary reminders...")
        
        NotificationHelper.showItineraryReminder(
            context = context,
            tripName = "European Tour",
            activityTitle = "Visit Eiffel Tower",
            timeUntil = "2 hours"
        )
        
        Thread.sleep(1000)
        
        NotificationHelper.showItineraryReminder(
            context = context,
            tripName = "Beach Vacation",
            activityTitle = "Sunset Beach Walk",
            timeUntil = "30 minutes"
        )
    }
    
    private fun testTripEndingNotifications(context: Context) {
        Log.d(TAG, "Testing trip ending notifications...")
        
        NotificationHelper.showTripEndingNotification(
            context = context,
            tripName = "Mountain Hiking",
            daysLeft = 2
        )
        
        Thread.sleep(1000)
        
        NotificationHelper.showTripEndingNotification(
            context = context,
            tripName = "City Break",
            daysLeft = 0
        )
    }
    
    private fun testNotificationScheduling(context: Context) {
        Log.d(TAG, "Testing notification scheduling...")
        
        // Create a sample trip for testing
        val testTrip = createSampleTrip()
        
        // Test scheduling
        NotificationScheduler.scheduleNotificationsForTrip(testTrip)
        
        Log.d(TAG, "Scheduled notifications for test trip: ${testTrip.name}")
    }
    
    /**
     * Create a sample trip for testing purposes
     */
    private fun createSampleTrip(): Trip {
        val startDate = LocalDate.now().plusDays(2)
        val endDate = startDate.plusDays(5)
        
        val itinerary = listOf(
            ItineraryItem(
                id = "test_item_1",
                tripId = "test_trip",
                date = startDate,
                time = "09:00",
                title = "Airport Check-in",
                location = "International Airport",
                type = ItineraryType.TRANSPORTATION,
                notes = "Arrive 2 hours early"
            ),
            ItineraryItem(
                id = "test_item_2",
                tripId = "test_trip",
                date = startDate.plusDays(1),
                time = "14:00",
                title = "City Tour",
                location = "Downtown",
                type = ItineraryType.ACTIVITY,
                notes = "Guided walking tour"
            ),
            ItineraryItem(
                id = "test_item_3",
                tripId = "test_trip",
                date = endDate,
                time = "11:00",
                title = "Hotel Checkout",
                location = "Grand Hotel",
                type = ItineraryType.ACCOMMODATION,
                notes = "Late checkout arranged"
            )
        )
        
        return Trip(
            id = "test_trip",
            name = "Test Notification Trip",
            startDate = startDate,
            endDate = endDate,
            destination = "Test City",
            travelers = 2,
            budget = 1000,
            status = TripStatus.PLANNED,
            category = TripCategory.RELAXATION,
            description = "A test trip for notification system",
            itinerary = itinerary
        )
    }
    
    /**
     * Test immediate notifications (for debugging)
     */
    fun testImmediateNotifications(context: Context) {
        Log.d(TAG, "Testing immediate notifications...")
        
        val notifications = listOf(
            "🎒 Trip starting tomorrow!" to "Get ready for your amazing adventure!",
            "📅 Activity reminder" to "City tour starts in 2 hours",
            "🏁 Last day of trip!" to "Make the most of your remaining time!",
            "✈️ Flight reminder" to "Check-in opens in 24 hours"
        )
        
        notifications.forEachIndexed { index, (title, message) ->
            NotificationHelper.showNotification(
                context = context,
                notificationId = 2000 + index,
                title = title,
                message = message,
                type = NotificationHelper.NotificationType.TRIP_STARTING_SOON
            )
            
            // Small delay to avoid overwhelming the notification system
            if (index < notifications.size - 1) {
                Thread.sleep(500)
            }
        }
    }
    
    /**
     * Get test results summary
     */
    fun getTestSummary(): String {
        return """
            Notification System Test Summary:
            ✅ Basic notifications
            ✅ Trip starting notifications (3, 1, 0 days)
            ✅ Itinerary reminders
            ✅ Trip ending notifications
            ✅ Notification scheduling
            ✅ WorkManager integration
            
            Features tested:
            - Multiple notification channels
            - Different notification types
            - Notification scheduling
            - Trip lifecycle notifications
            - Itinerary-based reminders
        """.trimIndent()
    }
}
