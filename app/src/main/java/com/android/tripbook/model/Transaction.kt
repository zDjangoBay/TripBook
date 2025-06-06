package com.android.tripbook.model

import java.util.Date

data class Transaction(
    val id: Int,
    val amount: Double,
    val date: Date,
    val categoryId: Int,
    val description: String? = null
)