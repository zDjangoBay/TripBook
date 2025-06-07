package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.service

import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.exception.PaymentException
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model.*
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.PaymentRequest
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.PaymentStatus
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.Transaction
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.provider.PaymentProvider
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.repository.TransactionRepository
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.util.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentService @Inject constructor(
    private val paymentProvider: PaymentProvider,
    private val transactionRepository: TransactionRepository,
    private val validationUtils: ValidationUtils
) {
    
    suspend fun processPayment(request: PaymentRequest): PaymentResponse {
        return withContext(Dispatchers.IO) {
            try {
                // Validate request
                validationUtils.validatePaymentRequest(request)
                
                // Create pending transaction
                val pendingTransaction = createPendingTransaction(request)
                transactionRepository.insertTransaction(pendingTransaction)
                
                // Process payment
                val response = paymentProvider.processPayment(request)
                
                // Update transaction with result
                val updatedTransaction = pendingTransaction.copy(
                    status = response.status,
                    providerTransactionId = response.transactionId,
                    updatedAt = LocalDateTime.now()
                )
                transactionRepository.updateTransaction(updatedTransaction)
                
                response
                
            } catch (e: Exception) {
                val errorResponse = PaymentResponse(
                    transactionId = UUID.randomUUID().toString(),
                    status = PaymentStatus.FAILED,
                    amount = request.amount,
                    currency = request.currency,
                    paymentMethodId = request.paymentMethodId,
                    reservationId = request.reservationId,
                    createdAt = LocalDateTime.now(),
                    message = e.message,
                    errorCode = when (e) {
                        is PaymentException.InvalidPaymentMethodException -> "INVALID_PAYMENT_METHOD"
                        is PaymentException.InsufficientFundsException -> "INSUFFICIENT_FUNDS"
                        is PaymentException.PaymentDeclinedException -> "PAYMENT_DECLINED"
                        is PaymentException.NetworkException -> "NETWORK_ERROR"
                        is PaymentException.ValidationException -> "VALIDATION_ERROR"
                        else -> "UNKNOWN_ERROR"
                    }
                )
                
                // Save failed transaction
                val failedTransaction = createPendingTransaction(request).copy(
                    status = PaymentStatus.FAILED,
                    updatedAt = LocalDateTime.now()
                )
                transactionRepository.updateTransaction(failedTransaction)
                
                errorResponse
            }
        }
    }
    
    suspend fun refundPayment(transactionId: String, amount: BigDecimal? = null): PaymentResponse {
        return withContext(Dispatchers.IO) {
            val transaction = transactionRepository.getTransactionById(transactionId)
                ?: throw PaymentException.RefundException("Transaction not found")
            
            if (transaction.status != PaymentStatus.SUCCEEDED) {
                throw PaymentException.RefundException("Cannot refund non-successful payment")
            }
            
            val refundResponse = paymentProvider.refundPayment(
                transaction.providerTransactionId ?: transactionId,
                amount
            )
            
            // Create refund transaction record
            val refundTransaction = Transaction(
                id = UUID.randomUUID().toString(),
                reservationId = transaction.reservationId,
                customerId = transaction.customerId,
                amount = -(amount ?: transaction.amount),
                currency = transaction.currency,
                status = refundResponse.status,
                paymentMethodType = transaction.paymentMethodType,
                providerTransactionId = refundResponse.transactionId,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            transactionRepository.insertTransaction(refundTransaction)
            refundResponse
        }
    }
    
    suspend fun getPaymentHistory(customerId: String): List<Transaction> {
        return withContext(Dispatchers.IO) {
            transactionRepository.getTransactionsByCustomerId(customerId)
        }
    }
    
    suspend fun getTransactionStatus(transactionId: String): PaymentResponse {
        return withContext(Dispatchers.IO) {
            val transaction = transactionRepository.getTransactionById(transactionId)
                ?: throw PaymentException.ProcessingException("Transaction not found")
            
            PaymentResponse(
                transactionId = transaction.id,
                status = transaction.status,
                amount = transaction.amount,
                currency = transaction.currency,
                paymentMethodId = "",
                reservationId = transaction.reservationId,
                createdAt = transaction.createdAt
            )
        }
    }
    
    private fun createPendingTransaction(request: PaymentRequest): Transaction {
        return Transaction(
            id = UUID.randomUUID().toString(),
            reservationId = request.reservationId,
            customerId = request.customerId,
            amount = request.amount,
            currency = request.currency,
            status = PaymentStatus.PENDING,
            paymentMethodType = request.paymentMethodId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            metadata = request.metadata.toString()
        )
    }
}