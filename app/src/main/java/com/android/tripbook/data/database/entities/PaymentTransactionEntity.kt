package com.android.tripbook.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "payment_transactions")
data class PaymentTransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val currency: String,
    val paymentMethodId: String,
    val paymentMethodType: String,
    val paymentMethodBrand: String?,
    val reservationId: String,
    val status: String,
    val transactionDate: LocalDateTime,
    val referenceNumber: String,
    val cardNumber: String?,
    val mobileNumber: String?,
    val cardBrand: String?
)
