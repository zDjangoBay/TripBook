package com.tripbook.reservation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReservationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val reservations: List<Reservation> = emptyList()
)

data class Reservation(
    val id: String,
    val tripId: String,
    val userId: String,
    val status: ReservationStatus,
    val seats: List<Seat>,
    val totalAmount: Double,
    val paymentStatus: PaymentStatus,
    val createdAt: Long
)

enum class ReservationStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED
}

enum class PaymentStatus {
    PENDING, PAID, REFUNDED, FAILED
}

data class Seat(
    val seatNumber: String,
    val isAvailable: Boolean,
    val price: Double
)

class ReservationViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReservationState())
    val state: StateFlow<ReservationState> = _state.asStateFlow()

    fun loadReservations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: Implement API call to fetch reservations
                _state.value = _state.value.copy(
                    isLoading = false,
                    reservations = emptyList() // Replace with actual data
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: Implement API call to cancel reservation
                loadReservations() // Reload reservations after cancellation
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
} 