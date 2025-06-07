package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.provider

import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.exception.PaymentException
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model.*
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.PaymentRequest
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.PaymentStatus
import kotlinx.coroutines.delay
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.provider.PaymentProvider
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class MockPaymentProvider : PaymentProvider {
    
    private val transactions = mutableMapOf<String, PaymentResponse>()
    
    override suspend fun processPayment(request: PaymentRequest): PaymentResponse {
        // Simulate network delay
        delay(2000)
        
        val transactionId = UUID.randomUUID().toString()
        
        // Simulate different scenarios based on amount
        val status = when {
            request.amount >= BigDecimal("10000") -> PaymentStatus.FAILED
            request.amount >= BigDecimal("5000") -> PaymentStatus.REQUIRES_ACTION
            request.amount <= BigDecimal("0") -> throw PaymentException.ValidationException("Amount must be positive")
            else -> PaymentStatus.SUCCEEDED
        }
        
        val response = PaymentResponse(
            transactionId = transactionId,
            status = status,
            amount = request.amount,
            currency = request.currency,
            paymentMethodId = request.paymentMethodId,
            reservationId = request.reservationId,
            createdAt = LocalDateTime.now(),
            message = when (status) {
                PaymentStatus.SUCCEEDED -> "Payment processed successfully"
                PaymentStatus.FAILED -> "Insufficient funds"
                PaymentStatus.REQUIRES_ACTION -> "3D Secure authentication required"
                else -> null
            }
        )
        
        transactions[transactionId] = response
        return response
    }
    
    override suspend fun refundPayment(transactionId: String, amount: BigDecimal?): PaymentResponse {
        delay(1500)
        
        val originalTransaction = transactions[transactionId]
            ?: throw PaymentException.RefundException("Transaction not found")
        
        if (originalTransaction.status != PaymentStatus.SUCCEEDED) {
            throw PaymentException.RefundException("Cannot refund non-successful payment")
        }
        
        val refundAmount = amount ?: originalTransaction.amount
        
        val refundResponse = originalTransaction.copy(
            transactionId = UUID.randomUUID().toString(),
            status = PaymentStatus.REFUNDED,
            amount = refundAmount,
            createdAt = LocalDateTime.now(),
            message = "Refund processed successfully"
        )
        
        transactions[refundResponse.transactionId] = refundResponse
        return refundResponse
    }
    
    override suspend fun getPaymentStatus(transactionId: String): PaymentResponse {
        return transactions[transactionId]
            ?: throw PaymentException.ProcessingException("Transaction not found")
    }
    
    override fun isSupported(paymentMethodType: String): Boolean {
        return paymentMethodType in listOf("credit_card", "paypal", "google_pay", "apple_pay")
    }
}