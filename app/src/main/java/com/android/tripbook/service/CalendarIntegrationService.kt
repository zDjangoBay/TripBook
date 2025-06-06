package com.android.tripbook.service

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.android.tripbook.model.ItineraryItem
import java.util.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CalendarIntegrationService {

    private const val CALENDAR_NAME = "TripBook"
    private const val CALENDAR_ACCOUNT_NAME = "TripBook"
    private const val CALENDAR_DISPLAY_NAME = "TripBook Itinerary"
    private const val CALENDAR_COLOR = 0x667EEA // Purple color

    fun hasCalendarPermission(context: Context): Boolean {
        val write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
        val read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
        return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
    }

    fun getCalendars(context: Context): List<Pair<Long, String>> {
        if (!hasCalendarPermission(context)) {
            throw SecurityException("Calendar permissions not granted")
        }
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )
        val cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI, projection, null, null, null
        )
        val calendars = mutableListOf<Pair<Long, String>>()
        cursor?.use {
            while (it.moveToNext()) {
                calendars.add(it.getLong(0) to it.getString(1))
            }
        }
        return calendars
    }

    fun isEventAlreadySynced(context: Context, item: ItineraryItem, calendarId: Long): Boolean {
        val projection = arrayOf(CalendarContract.Events._ID)
        val selection = ("(${CalendarContract.Events.TITLE} = ?) AND " +
                "(${CalendarContract.Events.DTSTART} BETWEEN ? AND ?) AND " +
                "(${CalendarContract.Events.CALENDAR_ID} = ?)")
        // Calculate time range (1 hour window)
        val startTime = convertToEpochMillis(item.date, item.time)
        val endTime = startTime + 3600000 // 1 hour later
        val selectionArgs = arrayOf(
            item.title,
            startTime.toString(),
            endTime.toString(),
            calendarId.toString()
        )
        val cursor = context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
        val exists = cursor?.moveToFirst() ?: false
        cursor?.close()
        return exists
    }

    fun insertEvent(
        context: Context,
        item: ItineraryItem,
        calendarId: Long,
        durationMinutes: Int = 60,
        reminderMinutes: Int? = null
    ): Long? {
        try {
            // Convert date and time to milliseconds
            val startMillis = convertToEpochMillis(item.date, item.time)
            val endMillis = startMillis + durationMinutes * 60 * 1000
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, item.title)
                put(CalendarContract.Events.DESCRIPTION, buildEventDescription(item))
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.EVENT_LOCATION, item.location)
            }
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()
            // Add reminder if specified
            if (eventId != null && reminderMinutes != null) {
                val reminderValues = ContentValues().apply {
                    put(CalendarContract.Reminders.EVENT_ID, eventId)
                    put(CalendarContract.Reminders.MINUTES, reminderMinutes)
                    put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                }
                context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
            }
            return eventId
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun syncTripToCalendar(
        context: Context,
        itinerary: List<ItineraryItem>,
        calendarId: Long,
        durationMinutes: Int = 60,
        reminderMinutes: Int? = null
    ): List<Long?> {
        return itinerary.map { item ->
            if (!isEventAlreadySynced(context, item, calendarId)) {
                insertEvent(context, item, calendarId, durationMinutes, reminderMinutes)
            } else null
        }
    }

    // Check if calendar sync is available
    fun isCalendarAvailable(): Boolean {
        return true // For simplicity, we assume calendar is always available
    }

    private fun getOrCreateTripBookCalendar(context: Context): Long {
        // First try to find existing TripBook calendar
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.NAME} = ?"
        val selectionArgs = arrayOf(CALENDAR_NAME)

        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getLong(0)
            }
        }

        // If not found, create a new calendar
        val values = ContentValues().apply {
            put(CalendarContract.Calendars.NAME, CALENDAR_NAME)
            put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_ACCOUNT_NAME)
            put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
            put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_DISPLAY_NAME)
            put(CalendarContract.Calendars.CALENDAR_COLOR, CALENDAR_COLOR)
            put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
            put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDAR_ACCOUNT_NAME)
            put(CalendarContract.Calendars.VISIBLE, 1)
            put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        }

        val uri = context.contentResolver.insert(
            CalendarContract.Calendars.CONTENT_URI,
            values
        )
        return uri?.lastPathSegment?.toLongOrNull() ?: -1
    }

    private fun convertToEpochMillis(date: LocalDate, timeString: String): Long {
        try {
            // Parse time string (assuming format like "10:00 AM")
            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
            val time = LocalTime.parse(timeString, timeFormatter)
            return date.atTime(time)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            // Fallback to noon if time parsing fails
            return date.atTime(12, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }

    private fun buildEventDescription(item: ItineraryItem): String {
        return buildString {
            append("TripBook Itinerary Item\n\n")
            append("Type: ${item.type}\n")
            append("Location: ${item.location}\n")
            if (item.notes.isNotEmpty()) {
                append("\nNotes:\n${item.notes}")
            }
            if (item.agencyService != null) {
                append("\n\nBooked via: ${item.agencyService.name}")
                append("\nPrice: $${item.agencyService.price}")
            }
        }
    }
}