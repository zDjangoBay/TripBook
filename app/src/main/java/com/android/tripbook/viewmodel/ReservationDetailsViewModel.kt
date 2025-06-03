package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationStatus

class ReservationDetailsViewModel : ViewModel() {

    private val _reservation = MutableLiveData<Reservation?>()
    val reservation: LiveData<Reservation?> = _reservation

    fun loadReservationById(reservationId: String) {
        // Replace with repository or Firebase query
        _reservation.value = mockFetchReservation(reservationId)
    }

    private fun mockFetchReservation(id: String): Reservation? {
        return Reservation(
            id = id,
            title = "ExpressLine Bus Trip",
            destination = "Douala",
            startDate = java.time.LocalDateTime.now().plusDays(7),
            endDate = java.time.LocalDateTime.now().plusDays(7).plusHours(4),
            status = ReservationStatus.CONFIRMED,
            imageUrl = null,
            price = 47.50,
            currency = "USD",
            bookingReference = "BG${id.takeLast(4)}",
            notes = "Window seat requested",
            accommodationName = null,
            accommodationAddress = null,
            transportInfo = "Seat A12"
        )
    }


}
