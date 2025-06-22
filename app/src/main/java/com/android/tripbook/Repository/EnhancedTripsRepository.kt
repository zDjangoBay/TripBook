package com.android.tripbook.Repository

import android.content.Context
import android.util.Log
import com.android.tripbook.Model.Place
import com.android.tripbook.Model.Triphome
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.entity.PlaceEntity
import com.android.tripbook.database.entity.TriphomeEntity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Enhanced Repository that combines Firebase (online) with Room Database (offline)
 * - Firebase: Primary source when available
 * - Room: Cache for offline access and user-generated content
 * - Seamless fallback between online/offline modes
 */
class EnhancedTripsRepository(private val context: Context) {
    
    private val database by lazy { TripBookDatabase.getDatabase(context) }
    private val triphomeDao by lazy { database.triphomeDao() }
    private val placeDao by lazy { database.placeDao() }
    
    private val firebaseDb by lazy {
        try {
            FirebaseDatabase.getInstance().reference
        } catch (e: IllegalStateException) {
            Log.w("EnhancedRepository", "Firebase not available, using Room only")
            null
        }
    }

    // TRIPHOME (Upcoming Trips) Methods
    suspend fun getUpcomingTrips(): List<Triphome> {
        return try {
            // Try Firebase first
            val firebaseTrips = fetchTriphomesFromFirebase()
            if (firebaseTrips.isNotEmpty()) {
                // Cache Firebase data in Room
                cacheTriphomesInRoom(firebaseTrips)
                firebaseTrips
            } else {
                // Fallback to Room cache
                getTriphomesFromRoom()
            }
        } catch (e: Exception) {
            Log.e("EnhancedRepository", "Firebase failed, using Room cache: ${e.message}")
            getTriphomesFromRoom()
        }
    }

    private suspend fun fetchTriphomesFromFirebase(): List<Triphome> {
        return firebaseDb?.let { db ->
            suspendCancellableCoroutine { continuation ->
                db.child("Trips")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val trips = snapshot.children.mapNotNull { 
                                it.getValue(Triphome::class.java) 
                            }
                            Log.d("EnhancedRepository", "✅ Firebase: ${trips.size} trips loaded")
                            continuation.resume(trips)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("EnhancedRepository", "❌ Firebase error: ${error.message}")
                            continuation.resumeWithException(error.toException())
                        }
                    })
            }
        } ?: emptyList()
    }

    private suspend fun cacheTriphomesInRoom(trips: List<Triphome>) {
        withContext(Dispatchers.IO) {
            // Clear old Firebase cache
            triphomeDao.clearFirebaseTriphomes()
            
            // Insert new Firebase data
            val entities = trips.map { trip ->
                TriphomeEntity(
                    companyLogo = trip.companyLogo,
                    companyName = trip.companyName,
                    arriveTime = trip.arriveTime,
                    date = trip.date,
                    from = trip.from,
                    fromshort = trip.fromshort,
                    price = trip.price,
                    time = trip.time,
                    to = trip.to,
                    score = trip.score,
                    toshort = trip.toshort,
                    isFromFirebase = true,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            triphomeDao.insertTriphomes(entities)
            Log.d("EnhancedRepository", "💾 Cached ${entities.size} trips in Room")
        }
    }

    private suspend fun getTriphomesFromRoom(): List<Triphome> {
        return withContext(Dispatchers.IO) {
            val entities = triphomeDao.getAllTriphomesOnce()
            val trips = entities.map { entity ->
                Triphome(
                    companyLogo = entity.companyLogo,
                    companyName = entity.companyName,
                    arriveTime = entity.arriveTime,
                    date = entity.date,
                    from = entity.from,
                    fromshort = entity.fromshort,
                    price = entity.price,
                    time = entity.time,
                    to = entity.to,
                    score = entity.score,
                    toshort = entity.toshort
                )
            }
            Log.d("EnhancedRepository", "🗄️ Room: ${trips.size} trips loaded")
            trips
        }
    }

    // PLACE (Recommended Places) Methods
    suspend fun getRecommendedTrips(): List<Place> {
        return try {
            // Try Firebase first
            val firebasePlaces = fetchPlacesFromFirebase()
            if (firebasePlaces.isNotEmpty()) {
                // Cache Firebase data in Room
                cachePlacesInRoom(firebasePlaces)
                firebasePlaces
            } else {
                // Fallback to Room cache
                getPlacesFromRoom()
            }
        } catch (e: Exception) {
            Log.e("EnhancedRepository", "Firebase failed, using Room cache: ${e.message}")
            getPlacesFromRoom()
        }
    }

    private suspend fun fetchPlacesFromFirebase(): List<Place> {
        return firebaseDb?.let { db ->
            suspendCancellableCoroutine { continuation ->
                db.child("RecommendedPlace")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val places = snapshot.children.mapNotNull { 
                                it.getValue(Place::class.java) 
                            }
                            Log.d("EnhancedRepository", "✅ Firebase: ${places.size} places loaded")
                            continuation.resume(places)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("EnhancedRepository", "❌ Firebase error: ${error.message}")
                            continuation.resumeWithException(error.toException())
                        }
                    })
            }
        } ?: emptyList()
    }

    private suspend fun cachePlacesInRoom(places: List<Place>) {
        withContext(Dispatchers.IO) {
            // Clear old Firebase cache
            placeDao.clearFirebasePlaces()
            
            // Insert new Firebase data
            val entities = places.map { place ->
                PlaceEntity(
                    title = place.title,
                    picUrl = place.picUrl,
                    isFromFirebase = true,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            placeDao.insertPlaces(entities)
            Log.d("EnhancedRepository", "💾 Cached ${entities.size} places in Room")
        }
    }

    private suspend fun getPlacesFromRoom(): List<Place> {
        return withContext(Dispatchers.IO) {
            val entities = placeDao.getAllPlacesOnce()
            val places = entities.map { entity ->
                Place(
                    title = entity.title,
                    picUrl = entity.picUrl
                )
            }
            Log.d("EnhancedRepository", "🗄️ Room: ${places.size} places loaded")
            places
        }
    }

    // Flow-based methods for reactive UI
    fun getTriphomesFlow(): Flow<List<Triphome>> {
        return triphomeDao.getAllTriphomes().map { entities ->
            entities.map { entity ->
                Triphome(
                    companyLogo = entity.companyLogo,
                    companyName = entity.companyName,
                    arriveTime = entity.arriveTime,
                    date = entity.date,
                    from = entity.from,
                    fromshort = entity.fromshort,
                    price = entity.price,
                    time = entity.time,
                    to = entity.to,
                    score = entity.score,
                    toshort = entity.toshort
                )
            }
        }
    }

    fun getPlacesFlow(): Flow<List<Place>> {
        return placeDao.getAllPlaces().map { entities ->
            entities.map { entity ->
                Place(
                    title = entity.title,
                    picUrl = entity.picUrl
                )
            }
        }
    }
}
