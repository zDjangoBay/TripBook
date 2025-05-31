package com.tripbook.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbook.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ReservationUiState>(ReservationUiState.Loading)
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    private val _selectedSeats = MutableStateFlow<List<Seat>>(emptyList())
    val selectedSeats: StateFlow<List<Seat>> = _selectedSeats.asStateFlow()

    private val _availableSeats = MutableStateFlow<List<Seat>>(emptyList())
    val availableSeats: StateFlow<List<Seat>> = _availableSeats.asStateFlow()

    fun loadTrips() {
        viewModelScope.launch {
            try {
                _uiState.value = ReservationUiState.Loading
                val trips = RetrofitClient.reservationApi.getTrips()
                _uiState.value = ReservationUiState.Success(trips)
            } catch (e: Exception) {
                _uiState.value = ReservationUiState.Error(e.message ?: "Failed to load trips")
            }
        }
    }

    fun loadTripDetails(tripId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ReservationUiState.Loading
                val trip = RetrofitClient.reservationApi.getTripDetails(tripId)
                _uiState.value = ReservationUiState.TripDetails(trip)
            } catch (e: Exception) {
                _uiState.value = ReservationUiState.Error(e.message ?: "Failed to load trip details")
            }
        }
    }

    fun loadAvailableSeats(tripId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ReservationUiState.Loading
                val seats = RetrofitClient.reservationApi.getAvailableSeats(tripId)
                _availableSeats.value = seats
                _uiState.value = ReservationUiState.Success(emptyList())
            } catch (e: Exception) {
                _uiState.value = ReservationUiState.Error(e.message ?: "Failed to load available seats")
            }
        }
    }

    fun selectSeat(seat: Seat) {
        val currentSelected = _selectedSeats.value.toMutableList()
        if (currentSelected.contains(seat)) {
            currentSelected.remove(seat)
        } else {
            currentSelected.add(seat)
        }
        _selectedSeats.value = currentSelected
    }

    fun createReservation(tripId: String, userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ReservationUiState.Loading
                val reservation = Reservation(
                    id = "",
                    tripId = tripId,
                    userId = userId,
                    seats = _selectedSeats.value,
                    totalPrice = _selectedSeats.value.sumOf { it.price },
                    status = ReservationStatus.PENDING,
                    createdAt = ""
                )
                val createdReservation = RetrofitClient.reservationApi.createReservation(reservation)
                _uiState.value = ReservationUiState.ReservationCreated(createdReservation)
            } catch (e: Exception) {
                _uiState.value = ReservationUiState.Error(e.message ?: "Failed to create reservation")
            }
        }
    }

    fun processPayment(reservationId: String, paymentRequest: PaymentRequest) {
        viewModelScope.launch {
            try {
                _uiState.value = ReservationUiState.Loading
                val updatedReservation = RetrofitClient.reservationApi.processPayment(paymentRequest)
                _uiState.value = ReservationUiState.PaymentProcessed(updatedReservation)
            } catch (e: Exception) {
                _uiState.value = ReservationUiState.Error(e.message ?: "Failed to process payment")
            }
        }
    }

    fun cancelReservation(reservationId: String, reason: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ReservationUiState.Loading
                val request = CancellationRequest(reservationId, reason)
                val updatedReservation = RetrofitClient.reservationApi.cancelReservation(reservationId, request)
                _uiState.value = ReservationUiState.ReservationCancelled(updatedReservation)
            } catch (e: Exception) {
                _uiState.value = ReservationUiState.Error(e.message ?: "Failed to cancel reservation")
            }
        }
    }

    fun loadUserReservations(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ReservationUiState.Loading
                val reservations = RetrofitClient.reservationApi.getUserReservations(userId)
                _uiState.value = ReservationUiState.UserReservations(reservations)
            } catch (e: Exception) {
                _uiState.value = ReservationUiState.Error(e.message ?: "Failed to load user reservations")
            }
        }
    }
}

sealed class ReservationUiState {
    object Loading : ReservationUiState()
    data class Success(val trips: List<Trip>) : ReservationUiState()
    data class TripDetails(val trip: Trip) : ReservationUiState()
    data class ReservationCreated(val reservation: Reservation) : ReservationUiState()
    data class PaymentProcessed(val reservation: Reservation) : ReservationUiState()
    data class ReservationCancelled(val reservation: Reservation) : ReservationUiState()
    data class UserReservations(val reservations: List<Reservation>) : ReservationUiState()
    data class Error(val message: String) : ReservationUiState()
} 