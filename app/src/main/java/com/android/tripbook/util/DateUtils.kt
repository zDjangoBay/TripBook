package com.android.tripbook.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Utility class for date formatting and calculations
 */
object DateUtils {
    
    private val fullDateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM d")
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    
    /**
     * Format a date in the full format (e.g., "Mon, Jan 1, 2023")
     */
    fun formatFullDate(date: LocalDateTime): String {
        return date.format(fullDateFormatter)
    }
    
    /**
     * Format a date in the short format (e.g., "Jan 1")
     */
    fun formatShortDate(date: LocalDateTime): String {
        return date.format(shortDateFormatter)
    }
    
    /**
     * Format a date as month and year (e.g., "January 2023")
     */
    fun formatMonthYear(date: LocalDateTime): String {
        return date.format(monthYearFormatter)
    }
    
    /**
     * Format a time (e.g., "2:30 PM")
     */
    fun formatTime(date: LocalDateTime): String {
        return date.format(timeFormatter)
    }
    
    /**
     * Format a date range (e.g., "Jan 1 - Jan 5, 2023")
     */
    fun formatDateRange(startDate: LocalDateTime, endDate: LocalDateTime): String {
        return if (startDate.month == endDate.month && startDate.year == endDate.year) {
            "${startDate.format(shortDateFormatter)} - ${endDate.dayOfMonth}, ${endDate.year}"
        } else {
            "${startDate.format(shortDateFormatter)} - ${endDate.format(fullDateFormatter)}"
        }
    }
}
