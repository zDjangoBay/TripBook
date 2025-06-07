package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.payement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.util.*

// Payment models
enum class PaymentStatus {
    PENDING, PROCESSING, SUCCEEDED, FAILED
}

data class PaymentResponse(
    val id: String,
    val amount: BigDecimal,
    val status: PaymentStatus,
    val message: String? = null
)

data class CreditCard(
    val cardNumber: String = "",
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val cvv: String = "",
    val holderName: String = ""
)

data class PaymentUiState(
    val selectedPaymentMethod: String = "credit_card",
    val cardData: CreditCardData = CreditCardData(),
    val isProcessing: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val paymentResult: PaymentResponse? = null
)

class PaymentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun selectPaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(
            selectedPaymentMethod = method,
            errorMessage = null
        )
        validateForm()
    }

    fun updateCardData(cardData: CreditCardData) {
        _uiState.value = _uiState.value.copy(
            cardData = cardData,
            errorMessage = null
        )
        validateForm()
    }

    fun processPayment(reservationId: String, amount: BigDecimal) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessing = true,
                errorMessage = null
            )

            try {
                // Simulate payment processing
                delay(2000)

                // 80% success rate for demo
                val isSuccess = Random().nextDouble() < 0.8

                val result = PaymentResponse(
                    id = UUID.randomUUID().toString(),
                    amount = amount,
                    status = if (isSuccess) PaymentStatus.SUCCEEDED else PaymentStatus.FAILED,
                    message = if (isSuccess) "Payment processed successfully" else "Payment failed"
                )

                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    paymentResult = result
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    errorMessage = e.message ?: "Payment failed"
                )
            }
        }
    }

    fun processPayPalPayment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            delay(2000)
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                isFormValid = true
            )
        }
    }

    fun processGooglePayPayment(paymentData: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            delay(1500)
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                isFormValid = true
            )
        }
    }

    private fun validateForm(): Boolean {
        val isValid = when (_uiState.value.selectedPaymentMethod) {
            "credit_card" -> validateCardData(_uiState.value.cardData)
            "paypal", "google_pay" -> true
            else -> false
        }

        _uiState.value = _uiState.value.copy(isFormValid = isValid)
        return isValid
    }

    private fun validateCardData(cardData: CreditCardData): Boolean {
        return cardData.cardNumber.length >= 13 &&
                cardData.expiryMonth.length == 2 &&
                cardData.expiryYear.length == 2 &&
                cardData.cvv.length >= 3 &&
                cardData.holderName.isNotBlank()
    }
}