package com.android.tripbook.repository

import android.util.Log
import com.android.tripbook.data.SupabaseConfig
import com.android.tripbook.model.Agency
import com.android.tripbook.model.Bus
import com.android.tripbook.model.Destination
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.PostgrestRequestBuilder
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class FilterOperator {
    EQ,    // equals
    IN,    // in array
    LIKE,  // case-sensitive pattern matching
    ILIKE  // case-insensitive pattern matching
}

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
            timeOfDeparture = parseDateTime(time_of_departure),
            agencyId = agency_id,
            destinationId = destination_id,
            destinationName = destinationName,
            agencyName = agencyName
        )
    }

    private fun parseDateTime(dateTimeStr: String): LocalDateTime {
        return try {
            // Try parsing as ISO format first
            if (dateTimeStr.contains('T')) {
                LocalDateTime.parse(dateTimeStr)
            } else {
                // Handle non-ISO format (e.g., "2024-06-10 09:00:00")
                LocalDateTime.parse(dateTimeStr.replace(" ", "T"))
            }
        } catch (e: Exception) {
            // Fallback to custom pattern if both attempts fail
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }
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

    suspend fun loadAgencies(): List<Agency> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Loading agencies from Supabase")
            
            val response = supabase.from(AGENCIES_TABLE)
                .select()
                .decodeAs<List<SupabaseAgency>>()

            Log.d(TAG, "Successfully loaded ${response.size} agencies")

            response.map { it.toAgency() }.also { agencies ->
                _agencies.value = agencies
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading agencies: ${e.message}", e)
            _error.value = "Failed to load agencies: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun loadDestinationsForAgency(agencyId: Int): List<Destination> {
        return try {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Loading destinations for agency $agencyId...")

            val destinations = supabase.from(DESTINATIONS_TABLE)
                .select() {
                    filter {
                        eq("agencyid", agencyId)
                    }
                }
                .decodeList<SupabaseDestination>()
                .map { destination -> destination.toDestination() }
                .sortedBy { destination -> destination.destinationName }

            Log.d(TAG, "Loaded ${destinations.size} destinations for agency $agencyId")

            destinations
        } catch (e: Exception) {
            Log.e(TAG, "Error loading destinations: ${e.message}", e)
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

            // First, load all destinations that match the query
            val matchingDestinations = supabase.from(DESTINATIONS_TABLE)
                .select() {
                    filter {
                        ilike("destination_name", "%$destinationQuery%")
                    }
                }
                .decodeList<SupabaseDestination>()
                .map { it.toDestination() }

            Log.d(TAG, "Found ${matchingDestinations.size} matching destinations for query: $destinationQuery")

            if (matchingDestinations.isEmpty()) {
                return emptyList()
            }

            // Get unique agency IDs from matching destinations
            val agencyIds = matchingDestinations.map { it.agencyId }.distinct()

            // Load all agencies and filter in memory
            // This is a fallback approach when direct filtering doesn't work
            val allAgencies = supabase.from(AGENCIES_TABLE)
                .select()
                .decodeList<SupabaseAgency>()
                .map { it.toAgency() }

            val filteredAgencies = allAgencies
                .filter { agency -> agencyIds.contains(agency.agencyId) }
                .sortedBy { it.agencyName }

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

            // Filter buses at database level
            val buses = supabase.from(BUS_TABLE)
                .select() {
                    filter {
                        eq("agency_id", agencyId)
                    }
                }
                .decodeList<SupabaseBus>()

            if (buses.isEmpty()) {
                return emptyList()
            }

            // Get destination IDs from buses
            val destinationIds = buses.map { bus -> bus.destination_id }.distinct()

            // Load all destinations and filter in memory
            val allDestinations = supabase.from(DESTINATIONS_TABLE)
                .select()
                .decodeList<SupabaseDestination>()
                .map { destination -> destination.toDestination() }

            val filteredDestinations = allDestinations
                .filter { destination -> destinationIds.contains(destination.id) }

            // Load only the required agency - THIS IS WHERE THE ERROR MIGHT BE
            // Instead of using find, let's load the specific agency directly
            val agencyResponse = supabase.from(AGENCIES_TABLE)
                .select() {
                    filter {
                        eq("agency_id", agencyId)
                    }
                }
                .decodeList<SupabaseAgency>()
            
            val agencyName = if (agencyResponse.isNotEmpty()) {
                agencyResponse.first().agency_name
            } else {
                "Unknown Agency"
            }

            // Map the results
            buses.map { bus ->
                val destination = filteredDestinations.find { dest -> dest.id == bus.destination_id }
                bus.toBus(
                    destinationName = destination?.destinationName ?: "Unknown Destination",
                    agencyName = agencyName
                )
            }.sortedBy { bus -> bus.timeOfDeparture }
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

            // First find matching destinations using ilike for case-insensitive search
            val matchingDestinations = supabase.from(DESTINATIONS_TABLE)
                .select() {
                    filter {
                        ilike("destination_name", "%$destinationQuery%")
                    }
                }
                .decodeList<SupabaseDestination>()

            if (matchingDestinations.isEmpty()) {
                return emptyList()
            }

            val destinationIds = matchingDestinations.map { dest -> dest.id }

            // Load all buses and filter in memory
            val allBuses = supabase.from(BUS_TABLE)
                .select()
                .decodeList<SupabaseBus>()

            val filteredBuses = allBuses
                .filter { bus -> destinationIds.contains(bus.destination_id) }

            if (filteredBuses.isEmpty()) {
                return emptyList()
            }

            // Get agency IDs from filtered buses
            val agencyIds = filteredBuses.map { bus -> bus.agency_id }.distinct()

            // Load all agencies and filter in memory
            val allAgencies = supabase.from(AGENCIES_TABLE)
                .select()
                .decodeList<SupabaseAgency>()
                .map { agency -> agency.toAgency() }

            val filteredAgencies = allAgencies
                .filter { agency -> agencyIds.contains(agency.agencyId) }

            // Map the results
            filteredBuses.map { bus ->
                val destination = matchingDestinations.find { dest -> dest.id == bus.destination_id }
                val agency = filteredAgencies.find { ag -> ag.agencyId == bus.agency_id }
                bus.toBus(
                    destinationName = destination?.destination_name ?: "Unknown Destination",
                    agencyName = agency?.agencyName ?: "Unknown Agency"
                )
            }.sortedBy { bus -> bus.timeOfDeparture }
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

        @Volatile
        private var INSTANCE: SupabaseAgencyRepository? = null

        fun getInstance(): SupabaseAgencyRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SupabaseAgencyRepository().also { INSTANCE = it }
            }
        }
    }
}
