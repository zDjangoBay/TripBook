package com.android.tripbook.domain.usecases

import com.android.tripbook.data.models.Reservation
import com.android.tripbook.data.models.ReservationRequest
import com.android.tripbook.data.repositories.ReservationRepository
import java.util.Date

class CreateReservationUseCase(
    private val reservationRepository: ReservationRepository
) {
    
    suspend operator fun invoke(request: ReservationRequest): Result<Reservation> {
        return try {
            // Validate request
            validateReservationRequest(request)
            
            // Create reservation
            reservationRepository.createReservation(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun validateReservationRequest(request: ReservationRequest) {
        require(request.userId.isNotBlank()) { "User ID cannot be empty" }
        require(request.title.isNotBlank()) { "Title cannot be empty" }
        require(request.location.isNotBlank()) { "Location cannot be empty" }
        require(request.providerName.isNotBlank()) { "Provider name cannot be empty" }
        require(request.totalPrice >= 0) { "Price cannot be negative" }
        require(request.guestCount > 0) { "Guest count must be positive" }
        require(request.checkInDate.after(Date()) || isSameDay(request.checkInDate, Date())) { 
            "Check-in date cannot be in the past" 
        }
        
        request.checkOutDate?.let { checkOut ->
            require(checkOut.after(request.checkInDate)) { 
                "Check-out date must be after check-in date" 
            }
        }
    }
    
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { time = date1 }
        val cal2 = java.util.Calendar.getInstance().apply { time = date2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }
}