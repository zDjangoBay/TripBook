package com.android.tripbook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.BusReservation
import com.android.tripbook.model.ReservationStep
import com.android.tripbook.model.Seat
import com.android.tripbook.repository.BusReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BusReservationUiState(
    val currentStep: ReservationStep = ReservationStep.TRIP_DETAILS,
    val cities: List<Pair<String, String>> = emptyList(),
    val availableTimes: List<String> = emptyList(),
    val availableSeats: List<Seat> = emptyList(),
    val isProcessingPayment: Boolean = false,
    val isConfirmed: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BusReservationViewModel @Inject constructor(
    private val repository: BusReservationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BusReservationUiState())
    val uiState: StateFlow<BusReservationUiState> = _uiState.asStateFlow()
    
    val reservation: StateFlow<BusReservation> = repository.currentReservation
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        _uiState.value = _uiState.value.copy(
            cities = repository.getCities(),
            availableTimes = repository.getAvailableTimes(),
            availableSeats = repository.getAvailableSeats()
        )
    }
    
    fun updateReservation(reservation: BusReservation) {
        repository.updateReservation(reservation)
    }
    
    fun proceedToSeatSelection() {
        _uiState.value = _uiState.value.copy(
            currentStep = ReservationStep.SEAT_SELECTION,
            availableSeats = repository.getAvailableSeats()
        )
    }
    
    fun selectSeat(seatNumber: String) {
        val currentReservation = repository.currentReservation.value
        repository.updateReservation(
            currentReservation.copy(selectedSeat = seatNumber)
        )
        _uiState.value = _uiState.value.copy(
            availableSeats = repository.getAvailableSeats()
        )
    }
    
    fun proceedToPayment() {
        _uiState.value = _uiState.value.copy(
            currentStep = ReservationStep.PAYMENT_CONFIRMATION
        )
    }
    
    fun goBack() {
        val currentStep = _uiState.value.currentStep
        val newStep = when (currentStep) {
            ReservationStep.SEAT_SELECTION -> ReservationStep.TRIP_DETAILS
            ReservationStep.PAYMENT_CONFIRMATION -> ReservationStep.SEAT_SELECTION
            else -> currentStep
        }
        _uiState.value = _uiState.value.copy(currentStep = newStep)
    }
    
    fun confirmPayment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessingPayment = true)
            
            repository.confirmReservation()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isProcessingPayment = false,
                        isConfirmed = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isProcessingPayment = false,
                        error = error.message
                    )
                }
        }
    }
}