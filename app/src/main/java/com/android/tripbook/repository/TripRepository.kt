package com.android.tripbook.repository

import androidx.lifecycle.LiveData
import com.android.tripbook.dao.BudgetCategoryDao
import com.android.tripbook.dao.ExpenseDao
import com.android.tripbook.dao.TripDao
import com.android.tripbook.model.Trip

class TripRepository(
    private val tripDao: TripDao,
    private val budgetCategoryDao: BudgetCategoryDao, // Will be used more in later commits
    private val expenseDao: ExpenseDao         // Will be used more in later commits
) {

    // Trip operations
    val allTrips: LiveData<List<Trip>> = tripDao.getAllTrips()

    suspend fun insertTrip(trip: Trip): Long {
        return tripDao.insert(trip)
    }

    suspend fun updateTrip(trip: Trip) {
        tripDao.update(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.delete(trip)
    }

    suspend fun deleteAllTrips() {
        tripDao.deleteAllTrips()
    }

    fun getTripById(tripId: String): LiveData<Trip?> {
        return tripDao.getTripById(tripId)
    }

    // Placeholder for BudgetCategory operations - to be added
    // Placeholder for Expense operations - to be added
}