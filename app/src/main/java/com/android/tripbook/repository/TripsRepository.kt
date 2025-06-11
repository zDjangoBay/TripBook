package com.android.tripbook.repository

import com.android.tripbook.model.Place
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.util.Log
import com.android.tripbook.model.Triphome

class TripsRepository {
    private val database by lazy {
        try {
            FirebaseDatabase.getInstance().reference
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Firebase is not initialized. Ensure FirebaseApp.initializeApp(context) is called in Application class.", e)
        }
    }

    // Generic fetch function
    private suspend inline fun <reified T> fetchItemsFromFirebase(path: String): List<T> =
        suspendCancellableCoroutine { continuation ->
            database.child(path)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = snapshot.children.mapNotNull { it.getValue(T::class.java) }
                        Log.d("TripsRepository", "Fetched ${list.size} items from $path")
                        continuation.resume(list)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TripsRepository", "Failed to fetch from $path", error.toException())
                        continuation.resumeWithException(error.toException())
                    }
                })
        }

    // Updated functions using generic fetch
    suspend fun getUpcomingTrips(): List<Triphome> {
        return try {
            fetchItemsFromFirebase<Triphome>("Trips")
        } catch (e: Exception) {
            Log.e("TripsRepository", "Error loading trips: ${e.message}")
            emptyList()
        }
    }

    suspend fun getRecommendedTrips(): List<Place> {
        return try {
            fetchItemsFromFirebase<Place>("RecommendedPlace")
        } catch (e: Exception) {
            Log.e("TripsRepository", "Error loading recommended places: ${e.message}")
            emptyList()
        }
    }
}
