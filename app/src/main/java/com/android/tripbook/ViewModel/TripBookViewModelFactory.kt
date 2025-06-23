package com.android.tripbook.ViewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating ViewModels with Application dependency
 * Required for Room ViewModels that need Application context
 */
class TripBookViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RoomTripViewModel::class.java) -> {
                RoomTripViewModel(application) as T
            }
            modelClass.isAssignableFrom(RoomReviewViewModel::class.java) -> {
                RoomReviewViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
