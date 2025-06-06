package com.android.tripbook.repository

import android.util.Log
import com.android.tripbook.data.SupabaseConfig
import com.android.tripbook.data.models.SupabaseTrip
import com.android.tripbook.data.models.SupabaseTravelCompanion
import com.android.tripbook.model.Trip
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class SupabaseTripRepository {
    private val supabase = SupabaseConfig.client
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    


    suspend fun loadTrips() {
        try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Loading trips from Supabase...")
            
            // Fetch trips from Supabase
            val tripsResponse = supabase.from(TRIPS_TABLE)
                .select()
                .decodeList<SupabaseTrip>()
            
            Log.d(TAG, "Loaded ${tripsResponse.size} trips from database")
            
            // Fetch companions for each trip
            val tripsWithCompanions = mutableListOf<Trip>()
            
            for (supabaseTrip in tripsResponse) {
                val companions = try {
                    supabase.from(COMPANIONS_TABLE)
                        .select()
                        {
                            filter {
                                eq("trip_id", supabaseTrip.id ?: "")
                            }
                        }
                      //  .eq("trip_id", supabaseTrip.id ?: "")
                        .decodeList<SupabaseTravelCompanion>()
                        .map { it.toTravelCompanion() }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load companions for trip ${supabaseTrip.id}: ${e.message}")
                    emptyList()
                }
                
                tripsWithCompanions.add(supabaseTrip.toTrip(companions))
            }
            
            _trips.value = tripsWithCompanions.sortedByDescending { it.startDate }
            Log.d(TAG, "Successfully loaded and processed ${tripsWithCompanions.size} trips")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading trips: ${e.message}", e)
            _error.value = "Failed to load trips: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addTrip(trip: Trip): Result<Trip> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Adding trip to Supabase: ${trip.name}")
            
            // Insert trip into database
            val supabaseTrip = SupabaseTrip.fromTrip(trip)
            val insertedTrip = supabase.from(TRIPS_TABLE)
                .insert(supabaseTrip) {
                    select()
                }
                .decodeSingle<SupabaseTrip>()
            
            Log.d(TAG, "Trip inserted with ID: ${insertedTrip.id}")
            
            // Insert companions if any
            val companions = mutableListOf<SupabaseTravelCompanion>()
            if (trip.companions.isNotEmpty() && insertedTrip.id != null) {
                val supabaseCompanions = trip.companions.map { 
                    SupabaseTravelCompanion.fromTravelCompanion(it, insertedTrip.id)
                }
                
                val insertedCompanions = supabase.from(COMPANIONS_TABLE)
                    .insert(supabaseCompanions) {
                        select()
                    }
                    .decodeList<SupabaseTravelCompanion>()
                
                companions.addAll(insertedCompanions)
                Log.d(TAG, "Inserted ${insertedCompanions.size} companions")
            }
            
            // Convert back to Trip model
            val finalTrip = insertedTrip.toTrip(companions.map { it.toTravelCompanion() })
            
            // Update local state
            val currentTrips = _trips.value.toMutableList()
            currentTrips.add(finalTrip)
            _trips.value = currentTrips.sortedByDescending { it.startDate }
            
            Log.d(TAG, "Successfully added trip: ${finalTrip.name}")
            Result.success(finalTrip)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding trip: ${e.message}", e)
            _error.value = "Failed to create trip: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun updateTrip(trip: Trip): Result<Trip> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Updating trip: ${trip.id}")
            
            // Update trip in database
            val supabaseTrip = SupabaseTrip.fromTrip(trip)
            val updatedTrip = supabase.from(TRIPS_TABLE)
                .update(supabaseTrip) {
                    filter {
                        eq("id", trip.id)
                    }
                    select()
                }
                .decodeSingle<SupabaseTrip>()
            
            // Delete existing companions
            supabase.from(COMPANIONS_TABLE)
                .delete {
                    filter {
                        eq("trip_id", trip.id)
                    }
                }
            
            // Insert new companions
            val companions = mutableListOf<SupabaseTravelCompanion>()
            if (trip.companions.isNotEmpty()) {
                val supabaseCompanions = trip.companions.map { 
                    SupabaseTravelCompanion.fromTravelCompanion(it, trip.id)
                }
                
                val insertedCompanions = supabase.from(COMPANIONS_TABLE)
                    .insert(supabaseCompanions) {
                        select()
                    }
                    .decodeList<SupabaseTravelCompanion>()
                
                companions.addAll(insertedCompanions)
            }
            
            // Convert back to Trip model
            val finalTrip = updatedTrip.toTrip(companions.map { it.toTravelCompanion() })
            
            // Update local state
            val currentTrips = _trips.value.toMutableList()
            val index = currentTrips.indexOfFirst { it.id == trip.id }
            if (index != -1) {
                currentTrips[index] = finalTrip
                _trips.value = currentTrips.sortedByDescending { it.startDate }
            }
            
            Log.d(TAG, "Successfully updated trip: ${finalTrip.name}")
            Result.success(finalTrip)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating trip: ${e.message}", e)
            _error.value = "Failed to update trip: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun deleteTrip(tripId: String): Result<Unit> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Deleting trip: $tripId")
            
            // Delete trip from database (companions will be deleted automatically due to CASCADE)
            supabase.from(TRIPS_TABLE)
                .delete {
                    filter {
                        eq("id", tripId)
                    }
                }
            
            // Update local state
            val currentTrips = _trips.value.toMutableList()
            currentTrips.removeAll { it.id == tripId }
            _trips.value = currentTrips
            
            Log.d(TAG, "Successfully deleted trip: $tripId")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting trip: ${e.message}", e)
            _error.value = "Failed to delete trip: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    fun getTripById(tripId: String): Trip? {
        return _trips.value.find { it.id == tripId }
    }
    
    fun clearError() {
        _error.value = null
    }

    companion object {
        private const val TAG = "SupabaseTripRepository"
        private const val TRIPS_TABLE = "trips"
        private const val COMPANIONS_TABLE = "travel_companions"

        @Volatile
        private var INSTANCE: SupabaseTripRepository? = null

        fun getInstance(): SupabaseTripRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SupabaseTripRepository().also { INSTANCE = it }
            }
        }
    }
}
