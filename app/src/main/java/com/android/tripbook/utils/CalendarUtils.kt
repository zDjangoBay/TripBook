package com.android.tripbook.utils

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.android.tripbook.model.Trip
import com.android.tripbook.model.ItineraryItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Utility class for handling calendar integration functionality
 */
object CalendarUtils {
    
    /**
     * Creates a calendar intent for the entire trip
     */
    fun createTripCalendarIntent(trip: Trip): Intent {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, trip.name)
            putExtra(CalendarContract.Events.DESCRIPTION, buildTripDescription(trip))
            putExtra(CalendarContract.Events.EVENT_LOCATION, trip.destination)
            
            // Set start and end times
            val startMillis = trip.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = trip.endDate.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.ALL_DAY, true)
        }
        return intent
    }
    
    /**
     * Creates a calendar intent for a specific itinerary item
     */
    fun createItineraryItemCalendarIntent(item: ItineraryItem, trip: Trip): Intent {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, item.title)
            putExtra(CalendarContract.Events.DESCRIPTION, buildItineraryItemDescription(item, trip))
            putExtra(CalendarContract.Events.EVENT_LOCATION, item.location)
            
            // Parse time and create datetime
            val startDateTime = parseItemDateTime(item.date, item.time)
            val endDateTime = calculateEndDateTime(startDateTime, item.duration)
            
            val startMillis = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.ALL_DAY, false)
        }
        return intent
    }
    
    /**
     * Creates multiple calendar intents for all itinerary items
     */
    fun createAllItineraryCalendarIntents(trip: Trip): List<Intent> {
        return trip.itinerary.map { item ->
            createItineraryItemCalendarIntent(item, trip)
        }
    }
    
    /**
     * Checks if the device has a calendar app available
     */
    fun isCalendarAvailable(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
        }
        return intent.resolveActivity(context.packageManager) != null
    }
    
    /**
     * Launches calendar intent with error handling
     */
    fun launchCalendarIntent(context: Context, intent: Intent): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Builds a comprehensive description for the trip calendar event
     */
    private fun buildTripDescription(trip: Trip): String {
        val description = StringBuilder()
        
        description.append("🌍 Destination: ${trip.destination}\n")
        description.append("👥 Travelers: ${trip.travelers}\n")
        description.append("💰 Budget: FCFA ${trip.budget}\n")
        description.append("📋 Category: ${trip.category.name.lowercase().replaceFirstChar { it.uppercase() }}\n")
        
        if (trip.description.isNotEmpty()) {
            description.append("\n📝 Description:\n${trip.description}\n")
        }
        
        if (trip.companions.isNotEmpty()) {
            description.append("\n👥 Travel Companions:\n")
            trip.companions.forEach { companion ->
                description.append("• ${companion.name}\n")
            }
        }
        
        if (trip.itinerary.isNotEmpty()) {
            description.append("\n📅 Itinerary Highlights:\n")
            trip.itinerary.take(5).forEach { item ->
                description.append("• ${item.date.format(DateTimeFormatter.ofPattern("MMM d"))}: ${item.title}\n")
            }
            if (trip.itinerary.size > 5) {
                description.append("• ... and ${trip.itinerary.size - 5} more activities\n")
            }
        }
        
        description.append("\n📱 Created with TripBook")
        
        return description.toString()
    }
    
    /**
     * Builds description for individual itinerary item
     */
    private fun buildItineraryItemDescription(item: ItineraryItem, trip: Trip): String {
        val description = StringBuilder()
        
        description.append("🎯 Trip: ${trip.name}\n")
        description.append("📍 Location: ${item.location}\n")
        description.append("🏷️ Type: ${item.type.name.lowercase().replaceFirstChar { it.uppercase() }}\n")
        
        if (item.duration.isNotEmpty()) {
            description.append("⏱️ Duration: ${item.duration}\n")
        }
        
        if (item.cost > 0) {
            description.append("💰 Cost: FCFA ${item.cost}\n")
        }
        
        if (item.description.isNotEmpty()) {
            description.append("\n📝 Description:\n${item.description}\n")
        }
        
        if (item.notes.isNotEmpty()) {
            description.append("\n📋 Notes:\n${item.notes}\n")
        }
        
        description.append("\n📱 Created with TripBook")
        
        return description.toString()
    }
    
    /**
     * Parses item date and time into LocalDateTime
     */
    private fun parseItemDateTime(date: LocalDate, time: String): LocalDateTime {
        return try {
            val timeParts = time.split(":")
            val hour = timeParts[0].toInt()
            val minute = if (timeParts.size > 1) timeParts[1].toInt() else 0
            date.atTime(hour, minute)
        } catch (e: Exception) {
            // Default to 9 AM if time parsing fails
            date.atTime(9, 0)
        }
    }
    
    /**
     * Calculates end datetime based on duration
     */
    private fun calculateEndDateTime(startDateTime: LocalDateTime, duration: String): LocalDateTime {
        return try {
            when {
                duration.contains("hour", ignoreCase = true) -> {
                    val hours = duration.filter { it.isDigit() }.toIntOrNull() ?: 1
                    startDateTime.plusHours(hours.toLong())
                }
                duration.contains("day", ignoreCase = true) || 
                duration.contains("all day", ignoreCase = true) -> {
                    startDateTime.plusHours(8) // Default 8-hour activity
                }
                duration.contains("minute", ignoreCase = true) -> {
                    val minutes = duration.filter { it.isDigit() }.toIntOrNull() ?: 60
                    startDateTime.plusMinutes(minutes.toLong())
                }
                else -> {
                    // Default to 2 hours if duration is unclear
                    startDateTime.plusHours(2)
                }
            }
        } catch (e: Exception) {
            // Default to 2 hours if parsing fails
            startDateTime.plusHours(2)
        }
    }
}
