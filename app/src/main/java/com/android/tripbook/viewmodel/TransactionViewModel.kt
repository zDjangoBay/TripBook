package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.repository.TransactionRepository
import com.android.tripbook.repository.Transaction
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository = TransactionRepository.getInstance()
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.transactions

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.addTransaction(transaction) }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.updateTransaction(transaction) }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch { repository.deleteTransaction(transactionId) }
    }

    fun getTransactionsForTrip(tripId: String): List<Transaction> {
        return repository.getTransactionsForTrip(tripId)
    }

    fun getTotalAmountForTrip(tripId: String): Double {
        return repository.getTotalAmountForTrip(tripId)
    }

    fun getTransactionsByCategory(category: String): List<Transaction> {
        return repository.getTransactionsByCategory(category)
    }
}