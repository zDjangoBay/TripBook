package com.android.tripbook.data.database.dao

import androidx.room.*
import com.android.tripbook.data.database.entities.PaymentTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface PaymentDao {
    @Insert
    suspend fun insertTransaction(transaction: PaymentTransactionEntity)

    @Query("SELECT * FROM payment_transactions ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<PaymentTransactionEntity>>

    @Query("SELECT * FROM payment_transactions WHERE status = :status ORDER BY transactionDate DESC")
    fun getTransactionsByStatus(status: String): Flow<List<PaymentTransactionEntity>>

    @Query("SELECT * FROM payment_transactions WHERE paymentMethodType = :methodType ORDER BY transactionDate DESC")
    fun getTransactionsByMethod(methodType: String): Flow<List<PaymentTransactionEntity>>

    @Query("SELECT * FROM payment_transactions WHERE transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate DESC")
    fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<PaymentTransactionEntity>>

    @Query("SELECT SUM(amount) FROM payment_transactions WHERE status = :status")
    fun getTotalAmountByStatus(status: String): Flow<Double>

    @Query("SELECT COUNT(*) FROM payment_transactions WHERE status = :status")
    fun getTransactionCountByStatus(status: String): Flow<Int>
}
