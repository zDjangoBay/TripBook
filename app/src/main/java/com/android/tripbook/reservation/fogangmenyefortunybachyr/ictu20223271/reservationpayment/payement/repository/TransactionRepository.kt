package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.repository

import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.Transaction

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Gère les opérations de persistance et de récupération des transactions
 */
class TransactionRepository {

    private val transactions = mutableMapOf<String, Transaction>()

    suspend fun insertTransaction(transaction: Transaction) {
        transactions[transaction.id] = transaction
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactions[transaction.id] = transaction
    }

    suspend fun getTransactionById(id: String): Transaction? {
        return transactions[id]
    }

    suspend fun getTransactionsByCustomerId(customerId: String): List<Transaction> {
        return transactions.values.filter { it.customerId == customerId }
    }

    suspend fun getTransactionsByReservationId(reservationId: String): List<Transaction> {
        return transactions.values.filter { it.reservationId == reservationId }
    }

    fun getTransactionFlow(id: String): Flow<Transaction?> {
        return flowOf(transactions[id])
    }
}