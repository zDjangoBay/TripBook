package com.android.tripbook.ui.screens.reservation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.database.TripBookDatabase
import com.android.tripbook.data.database.dao.PaymentDao
import com.android.tripbook.data.database.entities.PaymentTransactionEntity
import com.android.tripbook.data.models.PaymentMethod
import com.android.tripbook.data.models.PaymentTransaction
import com.android.tripbook.data.models.PaymentStatus
import com.android.tripbook.data.services.MockPaymentProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PaymentViewModel(application: Application) : AndroidViewModel(application) {
    private val paymentDao: PaymentDao = TripBookDatabase.getDatabase(application).paymentDao()
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState

    private val paymentProcessor = MockPaymentProcessor()

    init {
        loadPaymentMethods()
    }

    private fun loadPaymentMethods() {
        _uiState.update { it.copy(paymentMethods = PaymentMethod.createDefaultMethods()) }
    }

    fun processPayment(
        amount: Double,
        selectedMethod: PaymentMethod,
        reservationId: String,
        cardNumber: String? = null,
        mobileNumber: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Validate card number if it's a card payment
            if (selectedMethod.type == PaymentMethod.PaymentType.CREDIT_CARD || 
                selectedMethod.type == PaymentMethod.PaymentType.DEBIT_CARD) {
                val brand = PaymentMethod.validateCardNumber(cardNumber ?: "")
                if (brand == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Invalid card number"
                        ) 
                    }
                    return@launch
                }
            }
            
            // Validate mobile money number if it's mobile money payment
            if (selectedMethod.type == PaymentMethod.PaymentType.ORANGE_MONEY || 
                selectedMethod.type == PaymentMethod.PaymentType.MTN_MOBILE_MONEY) {
                if (!PaymentMethod.validateMobileNumber(
                    mobileNumber ?: "",
                    selectedMethod.phoneCountryCode ?: ""
                )) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Invalid mobile number"
                        ) 
                    }
                    return@launch
                }
            }

            val result = paymentProcessor.processPayment(
                amount = amount,
                paymentMethod = selectedMethod,
                reservationId = reservationId
            )

            if (result.success) {
                val transaction = PaymentTransaction(
                    id = result.transactionId!!,
                    amount = amount,
                    currency = "XOF",
                    paymentMethod = selectedMethod,
                    status = PaymentStatus.SUCCESS,
                    reservationId = reservationId,
                    transactionDate = System.currentTimeMillis(),
                    referenceNumber = "REF-${System.currentTimeMillis()}"
                )
                
                // Save to database
                saveTransactionToDatabase(
                    transaction,
                    cardNumber,
                    mobileNumber
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        transaction = transaction,
                        showSuccess = true
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = result.errorMessage
                    ) 
                }
            }
        }
    }

    private suspend fun saveTransactionToDatabase(
        transaction: PaymentTransaction,
        cardNumber: String? = null,
        mobileNumber: String? = null
    ) {
        val entity = PaymentTransactionEntity(
            id = transaction.id,
            amount = transaction.amount,
            currency = "XOF",
            paymentMethodId = transaction.paymentMethod.id,
            paymentMethodType = transaction.paymentMethod.type.name,
            paymentMethodBrand = transaction.paymentMethod.cardBrand?.name,
            reservationId = transaction.reservationId,
            status = transaction.status.name,
            transactionDate = LocalDateTime.now(),
            referenceNumber = transaction.referenceNumber,
            cardNumber = cardNumber,
            mobileNumber = mobileNumber,
            cardBrand = transaction.paymentMethod.cardBrand?.name
        )
        paymentDao.insertTransaction(entity)
    }

    fun resetState() {
        _uiState.update { PaymentUiState() }
    }
}

data class PaymentUiState(
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val selectedMethod: PaymentMethod? = null,
    val amount: Double = 0.0,
    val cardNumber: String = "",
    val mobileNumber: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val transaction: PaymentTransaction? = null,
    val showSuccess: Boolean = false
)
