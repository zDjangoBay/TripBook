package com.android.tripbook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.tripbook.database.AppDatabase
import com.android.tripbook.model.Trip
import com.android.tripbook.repository.TripRepository
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository
    val allTrips: LiveData<List<Trip>>

    init {
        val tripDao = AppDatabase.getDatabase(application).tripDao()
        val budgetCategoryDao = AppDatabase.getDatabase(application).budgetCategoryDao()
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = TripRepository(tripDao, budgetCategoryDao, expenseDao)
        allTrips = repository.allTrips
    }

    fun insert(trip: Trip) = viewModelScope.launch {
        repository.insertTrip(trip)
    }

    fun update(trip: Trip) = viewModelScope.launch {
        repository.updateTrip(trip)
    }

    fun delete(trip: Trip) = viewModelScope.launch {
        repository.deleteTrip(trip)
    }

    fun deleteAllTrips() = viewModelScope.launch {
        repository.deleteAllTrips()
    }

    fun getTripById(tripId: String): LiveData<Trip?> {
        return repository.getTripById(tripId)
    }

    // Factory class to create the ViewModel instance
    class TripViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TripViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}