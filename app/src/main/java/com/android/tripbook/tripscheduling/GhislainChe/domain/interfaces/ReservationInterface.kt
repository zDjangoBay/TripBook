package com.android.tripbook.tripscheduling.GhislainChe.domain.interfaces

interface ReservationInterface {
    suspend fun createBooking(bookingRequest: BookingRequest): BookingResult
    suspend fun confirmPayment(reservationId: String): PaymentResult
}

// Placeholder data classes
data class BookingRequest(
    val userId: String,
    val scheduleId: String,
    val seatCount: Int
)

data class BookingResult(
    val reservationId: String,
    val status: String
)

data class PaymentResult(
    val reservationId: String,
    val paymentStatus: String
)