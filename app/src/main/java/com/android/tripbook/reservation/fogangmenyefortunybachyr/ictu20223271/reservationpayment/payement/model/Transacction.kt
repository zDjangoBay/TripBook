package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String,
    val reservationId: String,
    val customerId: String,
    val amount: BigDecimal,
    val currency: String,
    val status: PaymentStatus,
    val paymentMethodType: String,
    val providerTransactionId: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val metadata: String? = null
)