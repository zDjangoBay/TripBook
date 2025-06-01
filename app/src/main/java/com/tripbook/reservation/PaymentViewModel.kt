package com.tripbook.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbook.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Initial)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun processPayment(
        reservationId: String,
        amount: Double,
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvv: String,
        cardholderName: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = PaymentUiState.Processing

                val cardDetails = CardDetails(
                    number = cardNumber,
                    expiryMonth = expiryMonth,
                    expiryYear = expiryYear,
                    cvv = cvv,
                    cardholderName = cardholderName
                )

                val paymentRequest = PaymentRequest(
                    reservationId = reservationId,
                    amount = amount,
                    paymentMethod = "CREDIT_CARD",
                    cardDetails = cardDetails
                )

                val updatedReservation = RetrofitClient.reservationApi.processPayment(paymentRequest)
                _uiState.value = PaymentUiState.Success(updatedReservation)
            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error(e.message ?: "Payment processing failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = PaymentUiState.Initial
    }
}

sealed class PaymentUiState {
    object Initial : PaymentUiState()
    object Processing : PaymentUiState()
    data class Success(val reservation: Reservation) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
} 