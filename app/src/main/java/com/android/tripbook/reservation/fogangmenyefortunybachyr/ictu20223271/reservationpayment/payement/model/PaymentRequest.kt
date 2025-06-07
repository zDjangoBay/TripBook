package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model

import java.math.BigDecimal

data class PaymentRequest(
    val amount: BigDecimal,
    val currency: String = "USD",
    val paymentMethodId: String,
    val reservationId: String,
    val customerId: String,
    val description: String? = null,
    val metadata: Map<String, String> = emptyMap()
)