package com.android.tripbook.repository

import android.util.Log
import com.android.tripbook.data.SupabaseConfig
import com.android.tripbook.model.Agency
import com.android.tripbook.model.Destination
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

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
    val destination_tarif: Double
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
                .map { it.toDestination() }
                .filter { it.agencyId == agencyId }
                .sortedBy { it.destinationName }

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

    fun clearError() {
        _error.value = null
    }

    companion object {
        private const val TAG = "SupabaseAgencyRepository"
        private const val AGENCIES_TABLE = "agency"
        private const val DESTINATIONS_TABLE = "destination"

        @Volatile
        private var INSTANCE: SupabaseAgencyRepository? = null

        fun getInstance(): SupabaseAgencyRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SupabaseAgencyRepository().also { INSTANCE = it }
            }
        }
    }
}