package com.android.tripbook.utils

import java.time.LocalDate

/**
 * Utility class for date validation and smart date range handling
 */
object DateValidationUtils {
    
    /**
     * Data class representing the result of a date selection operation
     */
    data class DateSelectionResult(
        val startDate: LocalDate?,
        val endDate: LocalDate?,
        val shouldSwitchToEndDate: Boolean = false,
        val message: String? = null,
        val isValid: Boolean = true
    )
    
    /**
     * Handles start date selection with smart end date adjustment
     * 
     * @param selectedStartDate The newly selected start date
     * @param currentEndDate The current end date (may be null)
     * @param minEndDate Optional minimum end date (defaults to selectedStartDate)
     * @return DateSelectionResult with updated dates and user feedback
     */
    fun handleStartDateSelection(
        selectedStartDate: LocalDate,
        currentEndDate: LocalDate?,
        minEndDate: LocalDate? = null
    ): DateSelectionResult {
        val effectiveMinEndDate = minEndDate ?: selectedStartDate
        
        return when {
            // No end date set yet - switch to end date selection
            currentEndDate == null -> {
                DateSelectionResult(
                    startDate = selectedStartDate,
                    endDate = null,
                    shouldSwitchToEndDate = true,
                    message = "Now select your end date"
                )
            }
            
            // Start date is after current end date - reset end date and switch
            selectedStartDate.isAfter(currentEndDate) -> {
                DateSelectionResult(
                    startDate = selectedStartDate,
                    endDate = null,
                    shouldSwitchToEndDate = true,
                    message = "End date reset because start date was moved later. Please select a new end date."
                )
            }
            
            // Start date equals end date - suggest extending trip
            selectedStartDate.isEqual(currentEndDate) -> {
                DateSelectionResult(
                    startDate = selectedStartDate,
                    endDate = currentEndDate,
                    shouldSwitchToEndDate = true,
                    message = "Your trip is only one day. Consider selecting a later end date."
                )
            }
            
            // Valid range - keep current end date
            else -> {
                DateSelectionResult(
                    startDate = selectedStartDate,
                    endDate = currentEndDate,
                    shouldSwitchToEndDate = false,
                    message = null
                )
            }
        }
    }
    
    /**
     * Handles end date selection with validation
     * 
     * @param selectedEndDate The newly selected end date
     * @param currentStartDate The current start date (may be null)
     * @param maxTripDuration Optional maximum trip duration in days
     * @return DateSelectionResult with validation results
     */
    fun handleEndDateSelection(
        selectedEndDate: LocalDate,
        currentStartDate: LocalDate?,
        maxTripDuration: Long? = null
    ): DateSelectionResult {
        return when {
            // No start date set - invalid state
            currentStartDate == null -> {
                DateSelectionResult(
                    startDate = null,
                    endDate = null,
                    message = "Please select a start date first",
                    isValid = false
                )
            }
            
            // End date is before start date - invalid
            selectedEndDate.isBefore(currentStartDate) -> {
                DateSelectionResult(
                    startDate = currentStartDate,
                    endDate = null,
                    message = "End date cannot be before start date (${formatDate(currentStartDate)})",
                    isValid = false
                )
            }
            
            // Check maximum duration if specified
            maxTripDuration != null && java.time.temporal.ChronoUnit.DAYS.between(currentStartDate, selectedEndDate) > maxTripDuration -> {
                DateSelectionResult(
                    startDate = currentStartDate,
                    endDate = null,
                    message = "Trip duration cannot exceed $maxTripDuration days",
                    isValid = false
                )
            }
            
            // Valid end date
            else -> {
                val duration = java.time.temporal.ChronoUnit.DAYS.between(currentStartDate, selectedEndDate) + 1
                val message = when {
                    duration == 1L -> "Single day trip selected"
                    duration <= 3L -> "Short trip: $duration days"
                    duration <= 7L -> "Week-long trip: $duration days"
                    duration <= 14L -> "Two-week trip: $duration days"
                    else -> "Extended trip: $duration days"
                }
                
                DateSelectionResult(
                    startDate = currentStartDate,
                    endDate = selectedEndDate,
                    message = message
                )
            }
        }
    }
    
    /**
     * Validates a complete date range
     */
    fun validateDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        allowSingleDay: Boolean = true,
        maxDuration: Long? = null
    ): DateValidationResult {
        return when {
            startDate == null -> DateValidationResult(
                isValid = false,
                errorMessage = "Start date is required"
            )
            
            endDate == null -> DateValidationResult(
                isValid = false,
                errorMessage = "End date is required"
            )
            
            startDate.isAfter(endDate) -> DateValidationResult(
                isValid = false,
                errorMessage = "Start date must be before or equal to end date"
            )
            
            !allowSingleDay && startDate.isEqual(endDate) -> DateValidationResult(
                isValid = false,
                errorMessage = "Trip must be more than one day"
            )
            
            startDate.isBefore(LocalDate.now()) -> DateValidationResult(
                isValid = false,
                errorMessage = "Start date cannot be in the past"
            )
            
            maxDuration != null && java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) > maxDuration -> DateValidationResult(
                isValid = false,
                errorMessage = "Trip duration cannot exceed $maxDuration days"
            )
            
            else -> DateValidationResult(
                isValid = true,
                duration = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
            )
        }
    }
    
    /**
     * Suggests a smart end date based on start date and trip type
     */
    fun suggestEndDate(
        startDate: LocalDate,
        tripType: String? = null,
        defaultDuration: Long = 3
    ): LocalDate {
        val suggestedDuration = when (tripType?.lowercase()) {
            "business" -> 2L
            "weekend" -> 2L
            "week" -> 7L
            "vacation" -> 7L
            "extended" -> 14L
            else -> defaultDuration
        }
        
        return startDate.plusDays(suggestedDuration)
    }
    
    /**
     * Formats a date for user-friendly display
     */
    private fun formatDate(date: LocalDate): String {
        return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }
    
    /**
     * Data class for date validation results
     */
    data class DateValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val duration: Long? = null
    )
}
