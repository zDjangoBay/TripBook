package com.android.tripbook.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val amount: Double,
    val description: String = "",
    val category: String = "",
    val date: Long = System.currentTimeMillis()
)

class TransactionRepository {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value + transaction
    }

    fun updateTransaction(updated: Transaction) {
        _transactions.value = _transactions.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteTransaction(transactionId: String) {
        _transactions.value = _transactions.value.filterNot { it.id == transactionId }
    }

    fun getTransactionsForTrip(tripId: String): List<Transaction> {
        return _transactions.value.filter { it.tripId == tripId }
    }

    fun getTotalAmountForTrip(tripId: String): Double {
        return _transactions.value.filter { it.tripId == tripId }.sumOf { it.amount }
    }

    fun getTransactionsByCategory(category: String): List<Transaction> {
        return _transactions.value.filter { it.category == category }
    }

    companion object {
        @Volatile
        private var INSTANCE: TransactionRepository? = null

        fun getInstance(): TransactionRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TransactionRepository().also { INSTANCE = it }
            }
        }
    }
}