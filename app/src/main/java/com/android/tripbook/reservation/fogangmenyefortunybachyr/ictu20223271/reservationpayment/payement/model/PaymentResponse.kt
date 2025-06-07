package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model

import java.time.LocalDateTime

data class PaymentResponse(
    val transactionId: String,
    val status: PaymentStatus,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethodId: String,
    val reservationId: String,
    val createdAt: LocalDateTime,
    val message: String? = null,
    val errorCode: String? = null
)