package com.android.tripbook.utils

import com.android.tripbook.data.models.ReservationRequest
import java.util.Date

object ReservationValidationUtils {
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )
    
    fun validateReservationRequest(request: ReservationRequest): ValidationResult {
        val errors = mutableListOf<String>()
        
        // User ID validation
        if (request.userId.isBlank()) {
            errors.add("User ID cannot be empty")
        }
        
        // Title validation
        if (request.title.isBlank()) {
            errors.add("Title cannot be empty")
        } else if (request.title.length > 200) {
            errors.add("Title cannot exceed 200 characters")
        }
        
        // Location validation
        if (request.location.isBlank()) {
            errors.add("Location cannot be empty")
        } else if (request.location.length > 300) {
            errors.add("Location cannot exceed 300 characters")
        }
        
        // Provider validation
        if (request.providerName.isBlank()) {
            errors.add("Provider name cannot be empty")
        } else if (request.providerName.length > 200) {
            errors.add("Provider name cannot exceed 200 characters")
        }
        
        // Price validation
        if (request.totalPrice < 0) {
            errors.add("Price cannot be negative")
        } else if (request.totalPrice > 10_000_000) {
            errors.add("Price seems unreasonably high")
        }
        
        // Guest count validation
        if (request.guestCount <= 0) {
            errors.add("Guest count must be positive")
        } else if (request.guestCount > 100) {
            errors.add("Guest count cannot exceed 100")
        }
        
        // Date validation
        val now = Date()
        if (request.checkInDate.before(stripTime(now))) {
            errors.add("Check-in date cannot be in the past")
        }
        
        request.checkOutDate?.let { checkOut ->
            if (checkOut.before(request.checkInDate) || checkOut.equals(request.checkInDate)) {
                errors.add("Check-out date must be after check-in date")
            }
            
            val daysDifference = (checkOut.time - request.checkInDate.time) / (24 * 60 * 60 * 1000)
            if (daysDifference > 365) {
                errors.add("Reservation period cannot exceed 1 year")
            }
        }
        
        // Optional fields validation
        request.description?.let { desc ->
            if (desc.length > 1000) {
                errors.add("Description cannot exceed 1000 characters")
            }
        }
        
        request.providerContact?.let { contact ->
            if (contact.length > 100) {
                errors.add("Provider contact cannot exceed 100 characters")
            }
        }
        
        request.specialRequests?.let { requests ->
            if (requests.length > 500) {
                errors.add("Special requests cannot exceed 500 characters")
            }
        }
        
        // Currency validation
        val validCurrencies = setOf("XAF", "USD", "EUR", "GBP", "NGN", "GHS", "KES", "UGX", "TZS")
        if (!validCurrencies.contains(request.currency)) {
            errors.add("Invalid currency code: ${request.currency}")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    private fun stripTime(date: Date): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.time
    }
    
    fun formatValidationErrors(errors: List<String>): String {
        return errors.joinToString("\n• ", prefix = "Please fix the following issues:\n• ")
    }
}