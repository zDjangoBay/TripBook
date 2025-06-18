package com.android.tripbook.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.Reservation
import com.android.tripbook.data.models.ReservationRequest
import com.android.tripbook.data.models.ReservationStatus
import com.android.tripbook.data.models.ReservationType
import com.android.tripbook.domain.usecases.ReservationManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ReservationUiState(
    val reservations: List<Reservation> = emptyList(),
    val currentReservation: Reservation? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCreatingReservation: Boolean = false,
    val reservationCreated: Boolean = false
)

class ReservationViewModel(
    private val reservationManager: ReservationManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()
    
    private val _currentUserId = MutableStateFlow("")
    
    fun setCurrentUser(userId: String) {
        _currentUserId.value = userId
        loadUserReservations(userId)
    }
    
    fun loadUserReservations(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            reservationManager.getUserReservations(userId)
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = exception.message ?: "Unknown error occurred"
                        ) 
                    }
                }
                .collect { reservations ->
                    _uiState.update { 
                        it.copy(
                            reservations = reservations, 
                            isLoading = false,
                            errorMessage = null
                        ) 
                    }
                }
        }
    }
    
    fun loadReservationsByStatus(status: ReservationStatus) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            reservationManager.getReservationsByStatus(status)
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = exception.message ?: "Unknown error occurred"
                        ) 
                    }
                }
                .collect { reservations ->
                    _uiState.update { 
                        it.copy(
                            reservations = reservations, 
                            isLoading = false,
                            errorMessage = null
                        ) 
                    }
                }
        }
    }
    
    fun loadReservationsByType(type: ReservationType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            reservationManager.getReservationsByType(type)
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = exception.message ?: "Unknown error occurred"
                        ) 
                    }
                }
                .collect { reservations ->
                    _uiState.update { 
                        it.copy(
                            reservations = reservations, 
                            isLoading = false,
                            errorMessage = null
                        ) 
                    }
                }
        }
    }
    
    fun createReservation(request: ReservationRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingReservation = true, errorMessage = null, reservationCreated = false) }
            
            reservationManager.createReservation(request)
                .onSuccess { reservation ->
                    _uiState.update { 
                        it.copy(
                            isCreatingReservation = false,
                            reservationCreated = true,
                            currentReservation = reservation,
                            errorMessage = null
                        ) 
                    }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        it.copy(
                            isCreatingReservation = false,
                            reservationCreated = false,
                            errorMessage = exception.message ?: "Failed to create reservation"
                        ) 
                    }
                }
        }
    }
    
    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            reservationManager.cancelReservation(reservationId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    // Refresh the current user's reservations
                    if (_currentUserId.value.isNotEmpty()) {
                        loadUserReservations(_currentUserId.value)
                    }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to cancel reservation"
                        ) 
                    }
                }
        }
    }
    
    fun confirmReservation(reservationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            reservationManager.confirmReservation(reservationId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    // Refresh the current user's reservations
                    if (_currentUserId.value.isNotEmpty()) {
                        loadUserReservations(_currentUserId.value)
                    }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to confirm reservation"
                        ) 
                    }
                }
        }
    }
    
    fun loadReservationById(reservationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val reservation = reservationManager.getReservationById(reservationId)
                _uiState.update { 
                    it.copy(
                        currentReservation = reservation,
                        isLoading = false,
                        errorMessage = if (reservation == null) "Reservation not found" else null
                    ) 
                }
            } catch (exception: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load reservation"
                    ) 
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun clearReservationCreated() {
        _uiState.update { it.copy(reservationCreated = false) }
    }
}