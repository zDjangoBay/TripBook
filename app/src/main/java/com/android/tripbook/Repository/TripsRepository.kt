package com.android.tripbook.Repository

// TEMPORARILY COMMENTED OUT FOR DATABASE TESTING
// Firebase dependencies are disabled to test Room database functionality
// TODO: Uncomment when Firebase is needed

/*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.tripbook.Model.Place
import com.android.tripbook.model.Trip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TripsRepository {
    private val database = FirebaseDatabase.getInstance().reference

    fun getUpcomingTrips(): LiveData<List<Trip>>{
        val data= MutableLiveData<List<Trip>>()
        database.child("Trips")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   val list=snapshot.children.mapNotNull { it.getValue(Trip::class.java)}
                    data.postValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                   data.postValue(emptyList())
                }
            })
        return data
    }

    fun getRecommendedTrips(): LiveData<List<Place>>{
        val data= MutableLiveData<List<Place>>()
        database.child("RecommendedPlace")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list=snapshot.children.mapNotNull { it.getValue(Place::class.java)}
                    data.postValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    data.postValue(emptyList())
                }
            })
        return data
    }

}
*/

