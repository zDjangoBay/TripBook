package com.android.tripbook.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.model.Expense

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY date DESC")
    fun getExpensesForTrip(tripId: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesForCategory(categoryId: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE tripId = :tripId AND categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByTripAndCategory(tripId: String, categoryId: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    fun getExpenseById(expenseId: Long): LiveData<Expense?>

    @Query("DELETE FROM expenses WHERE tripId = :tripId")
    suspend fun deleteExpensesForTrip(tripId: String)

    @Query("DELETE FROM expenses WHERE categoryId = :categoryId")
    suspend fun deleteExpensesForCategory(categoryId: Long)

    // Query to get total expenses for a specific category in a trip
    @Query("SELECT SUM(amount) FROM expenses WHERE tripId = :tripId AND categoryId = :categoryId")
    fun getTotalExpensesForCategory(tripId: String, categoryId: Long): LiveData<Double?>

    // Query to get total expenses for a trip
    @Query("SELECT SUM(amount) FROM expenses WHERE tripId = :tripId")
    fun getTotalExpensesForTrip(tripId: String): LiveData<Double?>
}