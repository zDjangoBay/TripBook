package com.android.tripbook.data.database.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Room Type Converters for Date and Time Objects
 * 
 * These converters allow Room to store and retrieve LocalDate and LocalDateTime
 * objects by converting them to/from String representations. This is necessary
 * because SQLite doesn't have native support for these Java 8 time types.
 * 
 * Key Features:
 * - LocalDate conversion (for trip dates)
 * - LocalDateTime conversion (for timestamps, booking dates)
 * - ISO format for consistency and readability
 * - Null safety for optional date fields
 * 
 * Used by:
 * - All entities that contain date/time fields
 * - Room database for automatic conversion
 * - Ensures consistent date formatting across the app
 */
class DateTimeConverters {
    
    companion object {
        // Standard ISO formatters for consistency
        private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
    
    /**
     * Converts LocalDate to String for database storage
     * Format: YYYY-MM-DD (ISO 8601)
     */
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(DATE_FORMATTER)
    }
    
    /**
     * Converts String to LocalDate for application use
     * Expects format: YYYY-MM-DD (ISO 8601)
     */
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, DATE_FORMATTER) }
    }
    
    /**
     * Converts LocalDateTime to String for database storage
     * Format: YYYY-MM-DDTHH:MM:SS (ISO 8601)
     */
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DATE_TIME_FORMATTER)
    }
    
    /**
     * Converts String to LocalDateTime for application use
     * Expects format: YYYY-MM-DDTHH:MM:SS (ISO 8601)
     */
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, DATE_TIME_FORMATTER) }
    }
    
    /**
     * Converts Long timestamp to LocalDateTime
     * Used for converting system timestamps to LocalDateTime objects
     */
    @TypeConverter
    fun fromTimestamp(timestamp: Long?): LocalDateTime? {
        return timestamp?.let { 
            LocalDateTime.ofEpochSecond(it / 1000, ((it % 1000) * 1000000).toInt(), 
                java.time.ZoneOffset.UTC)
        }
    }
    
    /**
     * Converts LocalDateTime to Long timestamp
     * Used for storing LocalDateTime as timestamp
     */
    @TypeConverter
    fun toTimestamp(dateTime: LocalDateTime?): Long? {
        return dateTime?.toEpochSecond(java.time.ZoneOffset.UTC)?.times(1000)
    }
}
