package com.android.tripbook.repository



import android.util.Log
import com.android.tripbook.data.SupabaseConfig
import com.android.tripbook.model.Agency
import com.android.tripbook.model.Bus
import com.android.tripbook.model.Destination
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import com.android.tripbook.BuildConfig
import io.github.jan.supabase.postgrest.query.Columns.Companion.raw

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

    companion object {
        fun fromAgency(agency: Agency): SupabaseAgency {
            return SupabaseAgency(
                agency_id = agency.agencyId,
                agency_name = agency.agencyName,
                agency_description = agency.agencyDescription,
                agency_address = agency.agencyAddress,
                contact_phone = agency.contactPhone,
                website = agency.website,
                is_active = agency.isActive
            )
        }
    }
}

@Serializable
data class SupabaseDestination(
    val id: Int,
    val agencyid: Int, // Note: keeping original field name for DB compatibility
    val agency_rating: Int,
    val destination_name: String,
    val destination_tarif: Double,
    val bus_id: Int? = null
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

    companion object {
        fun fromDestination(destination: Destination): SupabaseDestination {
            return SupabaseDestination(
                id = destination.id,
                agencyid = destination.agencyId,
                agency_rating = destination.agencyRating,
                destination_name = destination.destinationName,
                destination_tarif = destination.destinationTarif
            )
        }
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
        // Define a formatter that supports multiple timestamp formats
        val formatter = DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // e.g., 2023-06-10T14:30:00
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) // e.g., 2023-06-10 14:30:00
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) // e.g., 2023-06-10 14:30
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter()

        return Bus.create(
            busId = bus_id,
            busName = bus_name,
            departureTime = try {
                LocalDateTime.parse(time_of_departure, formatter)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.w("SupabaseBus", "Failed to parse timestamp: $time_of_departure, using current time", e)
                }
                LocalDateTime.now()
            },
            agencyId = agency_id,
            destinationId = destination_id,
            destinationName = destinationName,
            agencyName = agencyName
        )
    }

    companion object {
        fun fromBus(bus: Bus): SupabaseBus {
            return SupabaseBus(
                bus_id = bus.busId,
                bus_name = bus.busName,
                time_of_departure = bus.timeOfDeparture.toString(), // Ensure consistent ISO format
                agency_id = bus.agencyId,
                destination_id = bus.destinationId
            )
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

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Loading agencies from Supabase...")
            }

            val agenciesResponse = supabase.from(AGENCIES_TABLE)
                .select()
                .decodeList<SupabaseAgency>()

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Loaded ${agenciesResponse.size} agencies from database")
            }

            val agencies = agenciesResponse.map { it.toAgency() }
                .sortedBy { it.agencyName }

            _agencies.value = agencies

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Successfully loaded and processed ${agencies.size} agencies")
            }

            agencies
        } catch (e: Exception) {
            Log.e(TAG, "Error loading agencies: ${e.message}", e)
            _error.value = "Failed to load agencies: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addAgency(agency: Agency): Result<Agency> {
        return try {
            _isLoading.value = true
            _error.value = null

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Adding agency: ${agency.agencyName}")
            }

            val supabaseAgency = SupabaseAgency.fromAgency(agency)
            val insertedAgency = supabase.from(AGENCIES_TABLE)
                .insert(supabaseAgency) {
                    select()
                }
                .decodeSingle<SupabaseAgency>()

            val newAgency = insertedAgency.toAgency()

            // Update local state
            val currentAgencies = _agencies.value.toMutableList()
            currentAgencies.add(newAgency)
            _agencies.value = currentAgencies.sortedBy { it.agencyName }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Successfully added agency: ${newAgency.agencyName}")
            }
            Result.success(newAgency)

        } catch (e: Exception) {
            Log.e(TAG, "Error adding agency: ${e.message}", e)
            _error.value = "Failed to add agency: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun loadDestinationsForAgency(agencyId: Int): List<Destination> {
        return try {
            _isLoading.value = true
            _error.value = null

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Loading destinations for agency $agencyId...")
            }

            val supabaseDestinations = supabase.from(DESTINATIONS_TABLE)
                .select {
                    filter {
                        eq("agencyid", agencyId)
                    }
                }
                .decodeList<SupabaseDestination>()

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Loaded ${supabaseDestinations.size} destinations for agency $agencyId")
            }

            val destinations = supabaseDestinations
                .map { it.toDestination() }
                .sortedBy { it.destinationName }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Processed ${destinations.size} destinations for agency $agencyId")
            }

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

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Loading agencies for destination: $destinationQuery")
            }

            // Fetch matching destinations
            val matchingDestinations = supabase.from(DESTINATIONS_TABLE)
                .select {
                    filter {
                        ilike("destination_name", "%$destinationQuery%")
                    }
                }
                .decodeList<SupabaseDestination>()

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Found ${matchingDestinations.size} matching destinations")
            }

            val agencyIds = matchingDestinations.map { it.agencyid }.distinct()

            // Fetch agencies using a condition for multiple agency IDs
            val filteredAgencies = if (agencyIds.isNotEmpty()) {
                val query = agencyIds.joinToString(separator = ",") { it.toString() }
                supabase.from(AGENCIES_TABLE)
                    .select {
                        filter {
                            // Use raw SQL-like condition with inList if supported
                            raw("agency_id IN ($query)")
                        }
                    }
                    .decodeList<SupabaseAgency>()
                    .map { it.toAgency() }
                    .sortedBy { it.agencyName }
            } else {
                emptyList()
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Filtered ${filteredAgencies.size} agencies for destination: $destinationQuery")
            }

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

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Loading buses for agency $agencyId...")
            }

            val agencyBuses = supabase.from(BUS_TABLE)
                .select {
                    filter {
                        eq("agency_id", agencyId)
                    }
                }
                .decodeList<SupabaseBus>()

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Found ${agencyBuses.size} buses for agency $agencyId")
            }

            // Load additional data for display names
            val allDestinations = supabase.from(DESTINATIONS_TABLE)
                .select()
                .decodeList<SupabaseDestination>()
                .map { it.toDestination() }

            val allAgencies = loadAgencies()

            val buses = agencyBuses.map { supabaseBus ->
                val destination = allDestinations.find { it.id == supabaseBus.destination_id }
                val agency = allAgencies.find { it.agencyId == supabaseBus.agency_id }

                supabaseBus.toBus(
                    destinationName = destination?.destinationName ?: "Unknown Destination",
                    agencyName = agency?.agencyName ?: "Unknown Agency"
                )
            }.sortedBy { it.getDepartureDateTime() }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Processed ${buses.size} buses for agency $agencyId")
            }

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

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Loading buses for destination: $destinationQuery")
            }

            // Fetch matching destinations
            val matchingDestinations = supabase.from(DESTINATIONS_TABLE)
                .select {
                    filter {
                        ilike("destination_name", "%$destinationQuery%")
                    }
                }
                .decodeList<SupabaseDestination>()
                .map { it.toDestination() }

            val matchingDestinationIds = matchingDestinations.map { it.id }

            // Fetch buses for matching destination IDs
            val matchingBuses = if (matchingDestinationIds.isNotEmpty()) {
                val query = matchingDestinationIds.joinToString(separator = ",") { it.toString() }
                supabase.from(BUS_TABLE)
                    .select {
                        filter {
                            raw("destination_id IN ($query)")
                        }
                    }
                    .decodeList<SupabaseBus>()
            } else {
                emptyList()
            }

            // Load additional data for display names
            val allDestinations = matchingDestinations // Already fetched
            val allAgencies = supabase.from(AGENCIES_TABLE)
                .select()
                .decodeList<SupabaseAgency>()
                .map { it.toAgency() }

            val buses = matchingBuses.map { supabaseBus ->
                val destination = allDestinations.find { it.id == supabaseBus.destination_id }
                val agency = allAgencies.find { it.agencyId == supabaseBus.agency_id }

                supabaseBus.toBus(
                    destinationName = destination?.destinationName ?: "Unknown Destination",
                    agencyName = agency?.agencyName ?: "Unknown Agency"
                )
            }.sortedBy { it.getDepartureDateTime() }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Found ${buses.size} buses for destination: $destinationQuery")
            }

            buses
        } catch (e: Exception) {
            Log.e(TAG, "Error loading buses for destination: ${e.message}", e)
            _error.value = "Failed to load buses: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addBus(bus: Bus): Result<Bus> {
        return try {
            _isLoading.value = true
            _error.value = null

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Adding bus: ${bus.busName}")
            }

            val supabaseBus = SupabaseBus.fromBus(bus)
            val insertedBus = supabase.from(BUS_TABLE)
                .insert(supabaseBus) {
                    select()
                }
                .decodeSingle<SupabaseBus>()

            val newBus = insertedBus.toBus(bus.destinationName, bus.agencyName)

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Successfully added bus: ${newBus.busName}")
            }
            Result.success(newBus)

        } catch (e: Exception) {
            Log.e(TAG, "Error adding bus: ${e.message}", e)
            _error.value = "Failed to add bus: ${e.message}"
            Result.failure(e)
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