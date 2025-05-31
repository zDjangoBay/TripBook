package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationStatus

class ReservationsDashboardViewModel : ViewModel() {

    private val _allReservations = MutableLiveData<List<Reservation>>()
    val allReservations: LiveData<List<Reservation>> = _allReservations

    val upcomingReservations: LiveData<List<Reservation>> = _allReservations.map { reservations ->
        reservations.filter { res -> res.status == ReservationStatus.CONFIRMED || res.status == ReservationStatus.PENDING }
    }

    val pastReservations: LiveData<List<Reservation>> = _allReservations.map { reservations ->
        reservations.filter { res -> res.status == ReservationStatus.COMPLETED }
    }

    fun loadReservations(userId: String) {
        // Replace with repository or Firebase call
        val dummyData = listOf(
            Reservation(
                id = "res123",
                title = "ExpressLine Bus Trip",
                destination = "Douala",
                startDate = java.time.LocalDateTime.now().plusDays(7),
                endDate = java.time.LocalDateTime.now().plusDays(7).plusHours(4),
                status = ReservationStatus.CONFIRMED,
                imageUrl = null,
                price = 47.50,
                currency = "USD",
                bookingReference = "BG1234",
                notes = "Window seat requested",
                accommodationName = null,
                accommodationAddress = null,
                transportInfo = "Seat A12"
            )
        )
        _allReservations.value = dummyData
    }


}
