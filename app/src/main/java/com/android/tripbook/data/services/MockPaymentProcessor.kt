package com.android.tripbook.data.services

import com.android.tripbook.data.models.PaymentStatus
import kotlinx.coroutines.delay

/**
 * Mock payment processor for simulating payment operations
 */
class MockPaymentProcessor {
    
    data class PaymentResult(
        val success: Boolean,
        val transactionId: String?,
        val errorMessage: String?
    )
    
    data class PaymentMethod(
        val id: String,
        val name: String,
        val type: PaymentType,
        val lastFourDigits: String? = null
    )
    
    enum class PaymentType {
        CREDIT_CARD,
        DEBIT_CARD,
        MOBILE_MONEY,
        BANK_TRANSFER
    }
    
    suspend fun processPayment(
        amount: Double,
        paymentMethod: PaymentMethod,
        reservationId: String
    ): PaymentResult {
        // Simulate network delay
        delay(2000)
        
        // Simulate random success/failure (90% success rate)
        val isSuccess = (1..10).random() <= 9
        
        return if (isSuccess) {
            PaymentResult(
                success = true,
                transactionId = "TXN_${System.currentTimeMillis()}",
                errorMessage = null
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                errorMessage = "Payment failed. Please try again."
            )
        }
    }
    
    fun getAvailablePaymentMethods(): List<PaymentMethod> = listOf(
        PaymentMethod(
            id = "card_1",
            name = "Visa Credit Card",
            type = PaymentType.CREDIT_CARD,
            lastFourDigits = "4532"
        ),
        PaymentMethod(
            id = "card_2",
            name = "Mastercard Debit",
            type = PaymentType.DEBIT_CARD,
            lastFourDigits = "8901"
        ),
        PaymentMethod(
            id = "mobile_1",
            name = "M-Pesa",
            type = PaymentType.MOBILE_MONEY
        ),
        PaymentMethod(
            id = "bank_1",
            name = "Bank Transfer",
            type = PaymentType.BANK_TRANSFER
        )
    )
    
    suspend fun refundPayment(transactionId: String): PaymentResult {
        // Simulate network delay
        delay(1500)
        
        return PaymentResult(
            success = true,
            transactionId = "REFUND_${System.currentTimeMillis()}",
            errorMessage = null
        )
    }
    
    companion object {
        @Volatile
        private var INSTANCE: MockPaymentProcessor? = null
        
        fun getInstance(): MockPaymentProcessor {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MockPaymentProcessor().also { INSTANCE = it }
            }
        }
    }
}
