package com.android.tripbook.data.models

import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Utility object for safe date parsing with error handling and fallback values
 * Specifically designed for serialization/deserialization with backend data
 */
object DateUtils {
    internal const val TAG = "DateUtils"
    
    /**
     * Safely parse a date string to LocalDate with fallback to current date
     */
    fun parseLocalDateSafely(dateString: String?, fallback: LocalDate = LocalDate.now()): LocalDate {
        if (dateString.isNullOrBlank()) {
            Log.w(TAG, "Date string is null or blank, using fallback: $fallback")
            return fallback
        }
        
        return try {
            LocalDate.parse(dateString)
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Failed to parse date '$dateString' as ISO LocalDate: ${e.message}")
            
            // Try alternative formats
            val alternativeFormats = listOf(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("MM-dd-yyyy")
            )
            
            for (formatter in alternativeFormats) {
                try {
                    val parsed = LocalDate.parse(dateString, formatter)
                    Log.i(TAG, "Successfully parsed date '$dateString' using alternative format")
                    return parsed
                } catch (e2: DateTimeParseException) {
                    // Continue to next format
                }
            }
            
            Log.w(TAG, "All date parsing attempts failed for '$dateString', using fallback: $fallback")
            fallback
        }
    }
    
    /**
     * Safely parse a datetime string to LocalDateTime with fallback to current datetime
     */
    fun parseLocalDateTimeSafely(dateTimeString: String?, fallback: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        if (dateTimeString.isNullOrBlank()) {
            Log.w(TAG, "DateTime string is null or blank, using fallback: $fallback")
            return fallback
        }
        
        return try {
            // Handle common format variations
            val normalizedString = dateTimeString.replace(" ", "T")
            LocalDateTime.parse(normalizedString)
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Failed to parse datetime '$dateTimeString' as ISO LocalDateTime: ${e.message}")
            
            // Try alternative formats
            val alternativeFormats = listOf(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
            )
            
            for (formatter in alternativeFormats) {
                try {
                    val parsed = LocalDateTime.parse(dateTimeString, formatter)
                    Log.i(TAG, "Successfully parsed datetime '$dateTimeString' using alternative format")
                    return parsed
                } catch (e2: DateTimeParseException) {
                    // Continue to next format
                }
            }
            
            Log.w(TAG, "All datetime parsing attempts failed for '$dateTimeString', using fallback: $fallback")
            fallback
        }
    }
    
    /**
     * Format LocalDate to ISO string safely
     */
    fun formatLocalDateSafely(date: LocalDate?): String {
        return try {
            date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to format LocalDate: ${e.message}")
            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }
    
    /**
     * Format LocalDateTime to ISO string safely
     */
    fun formatLocalDateTimeSafely(dateTime: LocalDateTime?): String {
        return try {
            dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to format LocalDateTime: ${e.message}")
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }
    
    /**
     * Safely parse enum values with fallback
     */
    fun <T : Enum<T>> parseEnumSafely(
        enumString: String?,
        fallback: T,
        enumName: String,
        enumClass: Class<T>
    ): T {
        if (enumString.isNullOrBlank()) {
            Log.w(TAG, "$enumName string is null or blank, using fallback: $fallback")
            return fallback
        }
        
        return try {
            java.lang.Enum.valueOf(enumClass, enumString)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Failed to parse $enumName '$enumString': ${e.message}, using fallback: $fallback")
            fallback
        }
    }
}
