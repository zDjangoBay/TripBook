package com.android.tripbook.ViewModel

// TEMPORARILY COMMENTED OUT FOR DATABASE TESTING
// Firebase dependencies are disabled to test Room database functionality
// TODO: Uncomment when Firebase is needed

/*
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.android.tripbook.Model.Place
import com.android.tripbook.Repository.TripsRepository
import com.android.tripbook.model.Trip

class MainviewModel (
    private val repository: TripsRepository= TripsRepository()
): ViewModel(){
    val  upcomingTrips: LiveData<List<Trip>> = repository.getUpcomingTrips()
    val  recommendedPlaces: LiveData<List<Place>> = repository.getRecommendedTrips()

}
*/