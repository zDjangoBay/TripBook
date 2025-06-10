package com.android.tripbook.repository

import android.util.Log
import com.android.tripbook.data.SupabaseConfig
import com.android.tripbook.data.models.SupabaseTrip
import com.android.tripbook.data.models.SupabaseTravelCompanion
import com.android.tripbook.data.models.SupabaseItineraryItem
import com.android.tripbook.data.models.TripWithDetails
import com.android.tripbook.model.Trip
import com.android.tripbook.model.ItineraryItem
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

            val tripsResponse = supabase.from(TRIPS_TABLE)
                .select()
                .decodeList<SupabaseTrip>()

            Log.d(TAG, "Loaded ${tripsResponse.size} trips from database")

            val tripsWithCompanions = mutableListOf<Trip>()

            for (supabaseTrip in tripsResponse) {
                val companions = try {
                    if (supabaseTrip.id != null) {
                        supabase.from(COMPANIONS_TABLE)
                            .select() {
                                filter {
                                    eq("trip_id", supabaseTrip.id)
                                }
                            }
                            .decodeList<SupabaseTravelCompanion>()
                            .map { it.toTravelCompanion() }
                    } else {
                        emptyList()
                    }
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

            val supabaseTrip = SupabaseTrip.fromTrip(trip)
            val insertedTrip = supabase.from(TRIPS_TABLE)
                .insert(supabaseTrip) {
                    select()
                }
                .decodeSingle<SupabaseTrip>()

            Log.d(TAG, "Trip inserted with ID: ${insertedTrip.id}")

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

            val finalTrip = insertedTrip.toTrip(companions.map { it.toTravelCompanion() })

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

            val finalTrip = updatedTrip.toTrip(companions.map { it.toTravelCompanion() })

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

            // Delete companions first (if no CASCADE is set up)
            supabase.from(COMPANIONS_TABLE)
                .delete {
                    filter {
                        eq("trip_id", tripId)
                    }
                }

            // Delete itinerary items
            supabase.from(ITINERARY_TABLE)
                .delete {
                    filter {
                        eq("trip_id", tripId)
                    }
                }

            // Delete trip
            supabase.from(TRIPS_TABLE)
                .delete {
                    filter {
                        eq("id", tripId)
                    }
                }

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

    suspend fun getTripWithDetails(tripId: String): Trip? {
        return try {
            Log.d(TAG, "Loading trip with details: $tripId")

            val tripResponse = supabase.from(TRIPS_TABLE)
                .select() {
                    filter {
                        eq("id", tripId)
                    }
                }
                .decodeSingle<SupabaseTrip>()

            val companions = try {
                supabase.from(COMPANIONS_TABLE)
                    .select() {
                        filter {
                            eq("trip_id", tripId)
                        }
                    }
                    .decodeList<SupabaseTravelCompanion>()
                    .map { it.toTravelCompanion() }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load companions for trip $tripId: ${e.message}")
                emptyList()
            }

            val itineraryItems = try {
                supabase.from(ITINERARY_TABLE)
                    .select() {
                        filter {
                            eq("trip_id", tripId)
                        }
                    }
                    .decodeList<SupabaseItineraryItem>()
                    .map { it.toItineraryItem() }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load itinerary for trip $tripId: ${e.message}")
                emptyList()
            }

            tripResponse.toTrip(companions).copy(itinerary = itineraryItems)

        } catch (e: Exception) {
            Log.e(TAG, "Error loading trip with details: ${e.message}", e)
            null
        }
    }

    suspend fun addItineraryItem(item: ItineraryItem): Result<ItineraryItem> {
        return try {
            Log.d(TAG, "Adding itinerary item: ${item.title}")

            val supabaseItem = SupabaseItineraryItem.fromItineraryItem(item)
            val insertedItem = supabase.from(ITINERARY_TABLE)
                .insert(supabaseItem) {
                    select()
                }
                .decodeSingle<SupabaseItineraryItem>()

            Log.d(TAG, "Successfully added itinerary item: ${insertedItem.id}")
            Result.success(insertedItem.toItineraryItem())

        } catch (e: Exception) {
            Log.e(TAG, "Error adding itinerary item: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateItineraryItem(item: ItineraryItem): Result<ItineraryItem> {
        return try {
            Log.d(TAG, "Updating itinerary item: ${item.id}")

            val supabaseItem = SupabaseItineraryItem.fromItineraryItem(item)
            val updatedItem = supabase.from(ITINERARY_TABLE)
                .update(supabaseItem) {
                    filter {
                        eq("id", item.id)
                    }
                    select()
                }
                .decodeSingle<SupabaseItineraryItem>()

            Log.d(TAG, "Successfully updated itinerary item: ${updatedItem.id}")
            Result.success(updatedItem.toItineraryItem())

        } catch (e: Exception) {
            Log.e(TAG, "Error updating itinerary item: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteItineraryItem(itemId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting itinerary item: $itemId")

            supabase.from(ITINERARY_TABLE)
                .delete {
                    filter {
                        eq("id", itemId)
                    }
                }

            Log.d(TAG, "Successfully deleted itinerary item: $itemId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error deleting itinerary item: ${e.message}", e)
            Result.failure(e)
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
        private const val ITINERARY_TABLE = "itinerary_items"

        @Volatile
        private var INSTANCE: SupabaseTripRepository? = null

        fun getInstance(): SupabaseTripRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SupabaseTripRepository().also { INSTANCE = it }
            }
        }
    }
}