package com.android.tripbook.model

data class BudgetItem(
    val id: Int,
    val name: String,
    val amount: Double,
    val description: String? = null
)