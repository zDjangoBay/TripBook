package com.android.tripbook.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.UUID

/**
 * Repository for managing activity data across the application.
 */
object ActivityRepository {
    // Mutable state list to hold all activities, grouped by trip ID and day
    private val _activities = mutableStateListOf<TripActivity>()
    
    // Public read-only access to activities
    val activities: SnapshotStateList<TripActivity> = _activities
    
    /**
     * Get all activities for a specific trip and day
     */
    fun getActivitiesForTripAndDay(tripId: String, day: Int): List<TripActivity> {
        return _activities.filter { it.tripId == tripId && it.day == day }
    }
    
    /**
     * Add a new activity to the repository
     */
    fun addActivity(tripId: String, day: Int, time: String, title: String, location: String): String {
        val id = UUID.randomUUID().toString()
        val newActivity = TripActivity(
            id = id,
            tripId = tripId,
            day = day,
            time = time,
            title = title,
            location = location
        )
        _activities.add(newActivity)
        return id
    }
    
    /**
     * Update an existing activity
     */
    fun updateActivity(activity: TripActivity): Boolean {
        val index = _activities.indexOfFirst { it.id == activity.id }
        if (index != -1) {
            _activities[index] = activity
            return true
        }
        return false
    }
    
    /**
     * Delete an activity by its ID
     */
    fun deleteActivity(activityId: String): Boolean {
        return _activities.removeIf { it.id == activityId }
    }
    
    /**
     * Get an activity by its ID
     */
    fun getActivityById(id: String): TripActivity? {
        return _activities.find { it.id == id }
    }
    
    /**
     * Add sample activities for testing
     */
    fun addSampleActivities(tripId: String) {
        // Day 1
        addActivity(tripId, 0, "09:00 AM", "Breakfast at Cafe Central", "1st District")
        addActivity(tripId, 0, "11:00 AM", "Visit Schönbrunn Palace", "Schönbrunn")
        addActivity(tripId, 0, "02:00 PM", "Lunch at Naschmarkt", "6th District")
        
        // Day 2
        addActivity(tripId, 1, "10:00 AM", "Belvedere Museum", "3rd District")
        addActivity(tripId, 1, "01:00 PM", "Lunch at Figlmüller", "1st District")
        addActivity(tripId, 1, "03:30 PM", "Shopping at Mariahilfer Straße", "6th District")
    }
}

/**
 * Data class representing an activity within a trip
 */
data class TripActivity(
    val id: String,
    val tripId: String,
    val day: Int,
    val time: String,
    val title: String,
    val location: String
)