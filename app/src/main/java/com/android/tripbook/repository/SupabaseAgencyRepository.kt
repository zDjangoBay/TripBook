package com.android.tripbook.repository

import android.util.Log
import com.android.tripbook.data.SupabaseConfig
import com.android.tripbook.model.Agency
import com.android.tripbook.model.Bus
import com.android.tripbook.model.Destination
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SupabaseAgency(
    val agency_id: Int,
    val agency_name: String,
    val agency_description: String? = null,
    val agency_address: String? = null,
    val contact_phone: String? = null,
    val website: String? = null,
    val is_active: Boolean = true
) {
    fun toAgency(): Agency {
        return Agency(
            agencyId = agency_id,
            agencyName = agency_name,
            agencyDescription = agency_description,
            agencyAddress = agency_address,
            contactPhone = contact_phone,
            website = website,
            isActive = is_active
        )
    }
}

@Serializable
data class SupabaseDestination(
    val id: Int,
    val agencyid: Int,
    val agency_rating: Int,
    val destination_name: String,
    val destination_tarif: Double,
    val bus_id: Int? = null // Added bus_id foreign key
) {
    fun toDestination(): Destination {
        return Destination(
            id = id,
            agencyId = agencyid,
            agencyRating = agency_rating,
            destinationName = destination_name,
            destinationTarif = destination_tarif
        )
    }
}

@Serializable
data class SupabaseBus(
    val bus_id: Int,
    val bus_name: String,
    val time_of_departure: String, // ISO timestamp string
    val agency_id: Int,
    val destination_id: Int
) {
    fun toBus(destinationName: String = "", agencyName: String = ""): Bus {
        return Bus(
            busId = bus_id,
            busName = bus_name,
            timeOfDeparture = LocalDateTime.parse(time_of_departure.replace(" ", "T")),
            agencyId = agency_id,
            destinationId = destination_id,
            destinationName = destinationName,
            agencyName = agencyName
        )
    }
}



class SupabaseAgencyRepository {
    private val supabase = SupabaseConfig.client
    private val _agencies = MutableStateFlow<List<Agency>>(emptyList())
    val agencies: StateFlow<List<Agency>> = _agencies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    @Serializable
    data class SupabaseReview(
        val id: String,
        val agency_id: Int,
        val user_id: String,
        val user_name: String,
        val rating: Int,
        val comment: String,
        val created_at: String
    ) {
        fun toReview(): com.android.tripbook.model.Review {
            return com.android.tripbook.model.Review(
                id = id,
                agencyId = agency_id.toString(),
                userId = user_id,
                userName = user_name,
                rating = rating,
                comment = comment,
                createdAt = java.time.LocalDateTime.parse(created_at)
            )
        }
    }

    suspend fun loadAgencies(): List<Agency> {
        return try {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Loading agencies from Supabase...")

            val agenciesResponse = supabase.from(AGENCIES_TABLE)
                .select()
                .decodeList<SupabaseAgency>()

            Log.d(TAG, "Loaded ${agenciesResponse.size} agencies from database")

            val agencies = agenciesResponse.map { it.toAgency() }
                .sortedBy { it.agencyName }

            _agencies.value = agencies

            Log.d(TAG, "Successfully loaded and processed ${agenciesResponse.size} agencies")

            agencies
        } catch (e: Exception) {
            Log.e(TAG, "Error loading agencies: ${e.message}", e)
            _error.value = "Failed to load agencies: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun loadReviewsForAgency(agencyId: Int): List<com.android.tripbook.model.Review> {
        return try {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Loading reviews for agency $agencyId...")

            val reviewsResponse = supabase.from(REVIEWS_TABLE)
                .select()
                .filter("agency_id", "eq", agencyId.toString())
                .order("created_at", ascending = false)
                .decodeList<SupabaseReview>()

            Log.d(TAG, "Loaded ${reviewsResponse.size} reviews from database")

            reviewsResponse.map { it.toReview() }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading reviews: ${e.message}", e)
            _error.value = "Failed to load reviews: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addReview(review: com.android.tripbook.model.Review): Boolean {
        return try {
            _isLoading.value = true
            _error.value = null

            val reviewMap = mapOf(
                "agency_id" to review.agencyId.toInt(),
                "user_id" to review.userId,
                "user_name" to review.userName,
                "rating" to review.rating,
                "comment" to review.comment,
                "created_at" to review.createdAt.toString()
            )

            val response = supabase.from(REVIEWS_TABLE)
                .insert(reviewMap)

            Log.d(TAG, "Inserted review: $response")

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding review: ${e.message}", e)
            _error.value = "Failed to add review: ${e.message}"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun loadDestinationsForAgency(agencyId: Int): List<Destination> {
        return try {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Loading destinations for agency $agencyId...")

            // Load all destinations first, then filter in-memory
            val allSupabaseDestinations = supabase.from(DESTINATIONS_TABLE)
                .select()
                .decodeList<SupabaseDestination>()

            Log.d(TAG, "Loaded ${allSupabaseDestinations.size} destinations from database")
            Log.d(TAG, "Raw Supabase destinations: $allSupabaseDestinations")

            val filteredDestinations = allSupabaseDestinations
                ?.map { it.toDestination() }
                ?.filter { it.agencyId == agencyId }
                ?.sortedBy { it.destinationName }
                ?: emptyList()

            Log.d(TAG, "Filtered ${filteredDestinations.size} destinations for agency $agencyId: $filteredDestinations")

            filteredDestinations
        } catch (e: Exception) {
            Log.e(TAG, "Error loading destinations: ${e.message}", e)
            Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            _error.value = "Failed to load destinations: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun loadAgenciesForDestination(destinationQuery: String): List<Agency> {
        return try {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Loading agencies for destination: $destinationQuery")

            // Load all destinations first
            val allSupabaseDestinations = supabase.from(DESTINATIONS_TABLE)
                .select()
                .decodeList<SupabaseDestination>()

            Log.d(TAG, "Loaded ${allSupabaseDestinations.size} destinations from database")

            // Filter destinations that match the query (case-insensitive partial match)
            val matchingDestinations = allSupabaseDestinations
                .map { it.toDestination() }
                .filter { destination ->
                    destination.destinationName.contains(destinationQuery, ignoreCase = true) ||
                    destinationQuery.contains(destination.destinationName, ignoreCase = true)
                }

            Log.d(TAG, "Found ${matchingDestinations.size} matching destinations for query: $destinationQuery")

            // Get unique agency IDs from matching destinations
            val agencyIds = matchingDestinations.map { it.agencyId }.distinct()

            Log.d(TAG, "Found agency IDs: $agencyIds")

            // Load all agencies and filter by the matching agency IDs
            val allAgencies = loadAgencies()
            val filteredAgencies = allAgencies.filter { agency ->
                agencyIds.contains(agency.agencyId)
            }.sortedBy { it.agencyName }

            Log.d(TAG, "Filtered ${filteredAgencies.size} agencies for destination: $destinationQuery")

            filteredAgencies
        } catch (e: Exception) {
            Log.e(TAG, "Error loading agencies for destination: ${e.message}", e)
            _error.value = "Failed to load agencies for destination: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun loadBusesForAgency(agencyId: Int): List<Bus> {
        return try {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Loading buses for agency $agencyId...")

            // Load buses for the specific agency
            val allSupabaseBuses = supabase.from(BUS_TABLE)
                .select()
                .decodeList<SupabaseBus>()

            Log.d(TAG, "Loaded ${allSupabaseBuses.size} buses from database")

            // Filter buses for the specific agency
            val agencyBuses = allSupabaseBuses.filter { it.agency_id == agencyId }

            Log.d(TAG, "Found ${agencyBuses.size} buses for agency $agencyId")

            // Load destinations and agencies to get names for display
            val allDestinations = supabase.from(DESTINATIONS_TABLE)
                .select()
                .decodeList<SupabaseDestination>()
                .map { it.toDestination() }

            val allAgencies = loadAgencies()

            // Convert to Bus objects with destination and agency names
            val buses = agencyBuses.map { supabaseBus ->
                val destination = allDestinations.find { it.id == supabaseBus.destination_id }
                val agency = allAgencies.find { it.agencyId == supabaseBus.agency_id }

                supabaseBus.toBus(
                    destinationName = destination?.destinationName ?: "Unknown Destination",
                    agencyName = agency?.agencyName ?: "Unknown Agency"
                )
            }.sortedBy { it.timeOfDeparture }

            Log.d(TAG, "Processed ${buses.size} buses for agency $agencyId")

            buses
        } catch (e: Exception) {
            Log.e(TAG, "Error loading buses for agency: ${e.message}", e)
            _error.value = "Failed to load buses: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun loadBusesForDestination(destinationQuery: String): List<Bus> {
        return try {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Loading buses for destination: $destinationQuery")

            // Load all buses
            val allSupabaseBuses = supabase.from(BUS_TABLE)
                .select()
                .decodeList<SupabaseBus>()

            // Load destinations to filter by destination name
            val allDestinations = supabase.from(DESTINATIONS_TABLE)
                .select()
                .decodeList<SupabaseDestination>()
                .map { it.toDestination() }

            // Find destinations that match the query
            val matchingDestinations = allDestinations.filter { destination ->
                destination.destinationName.contains(destinationQuery, ignoreCase = true) ||
                destinationQuery.contains(destination.destinationName, ignoreCase = true)
            }

            val matchingDestinationIds = matchingDestinations.map { it.id }

            // Filter buses for matching destinations
            val matchingBuses = allSupabaseBuses.filter { bus ->
                matchingDestinationIds.contains(bus.destination_id)
            }

            // Load agencies for display names
            val allAgencies = loadAgencies()

            // Convert to Bus objects with names
            val buses = matchingBuses.map { supabaseBus ->
                val destination = allDestinations.find { it.id == supabaseBus.destination_id }
                val agency = allAgencies.find { it.agencyId == supabaseBus.agency_id }

                supabaseBus.toBus(
                    destinationName = destination?.destinationName ?: "Unknown Destination",
                    agencyName = agency?.agencyName ?: "Unknown Agency"
                )
            }.sortedBy { it.timeOfDeparture }

            Log.d(TAG, "Found ${buses.size} buses for destination: $destinationQuery")

            buses
        } catch (e: Exception) {
            Log.e(TAG, "Error loading buses for destination: ${e.message}", e)
            _error.value = "Failed to load buses: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        private const val TAG = "SupabaseAgencyRepository"
        private const val AGENCIES_TABLE = "agency"
        private const val DESTINATIONS_TABLE = "destination"
        private const val BUS_TABLE = "bus"
        private const val REVIEWS_TABLE = "review"

        @Volatile
        private var INSTANCE: SupabaseAgencyRepository? = null

        fun getInstance(): SupabaseAgencyRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SupabaseAgencyRepository().also { INSTANCE = it }
            }
        }
    }
}