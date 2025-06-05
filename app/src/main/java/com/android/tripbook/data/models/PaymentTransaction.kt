package com.android.tripbook.data.models

data class PaymentTransaction(
    val id: String,
    val amount: Double,
    val currency: String,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus,
    val reservationId: String,
    val transactionDate: Long,
    val referenceNumber: String
)

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED
}
