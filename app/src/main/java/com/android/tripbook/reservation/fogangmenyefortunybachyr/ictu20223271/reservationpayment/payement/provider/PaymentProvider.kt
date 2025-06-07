package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.provider

import android.icu.math.BigDecimal
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.PaymentRequest
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model.PaymentResponse

interface PaymentProvider {
    suspend fun processPayment(request: PaymentRequest): PaymentResponse
    suspend fun refundPayment(transactionId: String, amount: BigDecimal? = null): PaymentResponse
    suspend fun getPaymentStatus(transactionId: String): PaymentResponse
    fun isSupported(paymentMethodType: String): Boolean
}