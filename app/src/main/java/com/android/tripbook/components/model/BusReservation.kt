package com.android.tripbook.model

package com.android.tripbook.model

import java.time.LocalDate
import java.time.LocalTime

data class BusReservation(
    val id: String = "",
    val from: String = "",
    val to: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val passengers: Int = 1,
    val selectedSeat: String = "",
    val passengerName: String = "",
    val passengerEmail: String = "",
    val passengerPhone: String = "",
    val totalPrice: Double = 47.50,
    val isConfirmed: Boolean = false
)

data class Seat(
    val number: String,
    val isBooked: Boolean = false,
    val isSelected: Boolean = false
)

enum class ReservationStep {
    TRIP_DETAILS,
    SEAT_SELECTION,
    PAYMENT_CONFIRMATION
}