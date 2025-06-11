package com.android.tripbook.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ==============================
// Data Models
// ==============================
data class AgencyService(
    val id: String,
    val name: String,
    val type: String,  // e.g., "Tour", "Accommodation", "Transportation"
    val description: String,
    val price: Double, // Fixed: Changed from Int to Double for consistency with Expense
    val rating: Float, // 0.0 to 5.0
    val location: String,
    val agencyName: String = "" // Added to track which agency provides this service
)

data class TravelAgency(
    val id: String,
    val name: String,
    val services: List<AgencyService>,
    val rating: Float
)

// For UI compatibility - flattened service representation
data class TravelAgencyServiceItem(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val price: Double, // Fixed: Changed from Int to Double for consistency
    val rating: Float,
    val location: String,
    val agencyName: String,
    val agencyId: String,
    val agencyRating: Float
)

// ==============================
// Retrofit API Interface
// ==============================
interface TravelAgencyApi {
    @GET("agencies")
    suspend fun getAgenciesByDestination(@Query("destination") destination: String): Response<List<TravelAgency>>

    @GET("agencies/filter")
    suspend fun getFilteredAgencies(
        @Query("destination") destination: String,
        @Query("minRating") minRating: Float?,
        @Query("maxPrice") maxPrice: Double?, // Fixed: Changed from Int to Double
        @Query("serviceType") serviceType: String?
    ): Response<List<TravelAgency>>
}

// ==============================
// Main Service Class
// ==============================
class TravelAgencyService {

    // Retrofit setup
    private val api: TravelAgencyApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tripbook.com/") // TODO: Replace with actual backend URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TravelAgencyApi::class.java)
    }

    // Mock data for development (used until API is connected)
    private val mockAgencies = listOf(
        TravelAgency(
            id = "agency1",
            name = "Nile Adventures",
            services = listOf(
                AgencyService("s1", "Nile River Cruise", "Tour", "3-day cruise on the Nile", 500.0, 4.5f, "Cairo, Egypt", "Nile Adventures"),
                AgencyService("s2", "Pyramids Tour", "Tour", "Giza pyramids with guide", 150.0, 4.8f, "Giza, Egypt", "Nile Adventures")
            ),
            rating = 4.6f
        ),
        TravelAgency(
            id = "agency2",
            name = "Sahara Travels",
            services = listOf(
                AgencyService("s3", "Desert Safari", "Tour", "Camel trekking safari", 300.0, 4.2f, "Marrakech, Morocco", "Sahara Travels"),
                AgencyService("s4", "Luxury Hotel Booking", "Accommodation", "5-star hotel in Marrakech", 200.0, 4.7f, "Marrakech, Morocco", "Sahara Travels")
            ),
            rating = 4.4f
        ),
        TravelAgency(
            id = "agency3",
            name = "African Explorer",
            services = listOf(
                AgencyService("s5", "Safari Adventure", "Tour", "Wildlife safari in Kenya", 800.0, 4.9f, "Nairobi, Kenya", "African Explorer"),
                AgencyService("s6", "Airport Transfer", "Transportation", "Private airport transfer", 50.0, 4.3f, "Nairobi, Kenya", "African Explorer")
            ),
            rating = 4.6f
        )
    )

    /**
     * Get agencies based on a destination (mocked or via API in future).
     */
    suspend fun getAgenciesForDestination(destination: String): List<TravelAgency> {
        if (!isValidDestination(destination)) return emptyList()

        return try {
            // Future API call - currently commented out
            // val response = api.getAgenciesByDestination(destination)
            // if (response.isSuccessful) response.body() ?: emptyList() else emptyList()

            withContext(Dispatchers.IO) {
                val search = destination.trim().lowercase()
                mockAgencies.filter { agency ->
                    agency.services.any { service ->
                        service.location.lowercase().contains(search) ||
                                service.name.lowercase().contains(search) ||
                                service.description.lowercase().contains(search)
                    }
                }
            }
        } catch (e: Exception) {
            // TODO: Add proper logging
            emptyList()
        }
    }

    /**
     * Get flattened services for UI consumption based on destination.
     * This method matches what the UI expects.
     */
    suspend fun getServicesForDestination(destination: String): List<TravelAgencyServiceItem> {
        val agencies = getAgenciesForDestination(destination)

        return agencies.flatMap { agency ->
            agency.services.map { service ->
                TravelAgencyServiceItem(
                    id = service.id,
                    name = service.name,
                    type = service.type,
                    description = service.description,
                    price = service.price,
                    rating = service.rating,
                    location = service.location,
                    agencyName = agency.name,
                    agencyId = agency.id,
                    agencyRating = agency.rating
                )
            }
        }
    }

    /**
     * Filter agencies based on optional criteria: rating, price, service type.
     */
    fun filterAgencies(
        agencies: List<TravelAgency>,
        minRating: Float? = null,
        maxPrice: Double? = null, // Fixed: Changed from Int to Double
        serviceType: String? = null
    ): List<TravelAgency> {
        return agencies.mapNotNull { agency ->
            val matchingServices = agency.services.filter { service ->
                val matchesRating = minRating?.let { service.rating >= it } ?: true
                val matchesPrice = maxPrice?.let { service.price <= it } ?: true
                val matchesType = serviceType?.let { service.type.equals(it, ignoreCase = true) } ?: true

                matchesRating && matchesPrice && matchesType
            }

            if (matchingServices.isNotEmpty()) agency.copy(services = matchingServices) else null
        }
    }

    /**
     * Filter services (flattened) based on criteria.
     */
    fun filterServices(
        services: List<TravelAgencyServiceItem>,
        minRating: Float? = null,
        maxPrice: Double? = null, // Fixed: Changed from Int to Double
        serviceType: String? = null
    ): List<TravelAgencyServiceItem> {
        return services.filter { service ->
            val matchesRating = minRating?.let { service.rating >= it } ?: true
            val matchesPrice = maxPrice?.let { service.price <= it } ?: true
            val matchesType = serviceType?.let { service.type.equals(it, ignoreCase = true) } ?: true

            matchesRating && matchesPrice && matchesType
        }
    }

    /**
     * Combines get + filter logic for one-step querying.
     */
    suspend fun getFilteredAgencies(
        destination: String,
        minRating: Float? = null,
        maxPrice: Double? = null, // Fixed: Changed from Int to Double
        serviceType: String? = null
    ): List<TravelAgency> {
        val baseList = getAgenciesForDestination(destination)
        return filterAgencies(baseList, minRating, maxPrice, serviceType)
    }

    /**
     * Get filtered services (flattened) for UI.
     */
    suspend fun getFilteredServices(
        destination: String,
        minRating: Float? = null,
        maxPrice: Double? = null, // Fixed: Changed from Int to Double
        serviceType: String? = null
    ): List<TravelAgencyServiceItem> {
        val baseList = getServicesForDestination(destination)
        return filterServices(baseList, minRating, maxPrice, serviceType)
    }

    /**
     * List available service types for filtering options.
     */
    fun getAvailableServiceTypes(): List<String> {
        return listOf("Tour", "Accommodation", "Transportation")
    }

    /**
     * Validate user input before querying.
     */
    fun isValidDestination(destination: String?): Boolean {
        return !destination.isNullOrBlank() && destination.trim().length >= 2
    }

    /**
     * Get a specific service by ID.
     */
    suspend fun getServiceById(serviceId: String): TravelAgencyServiceItem? {
        val allServices = mockAgencies.flatMap { agency ->
            agency.services.map { service ->
                TravelAgencyServiceItem(
                    id = service.id,
                    name = service.name,
                    type = service.type,
                    description = service.description,
                    price = service.price,
                    rating = service.rating,
                    location = service.location,
                    agencyName = agency.name,
                    agencyId = agency.id,
                    agencyRating = agency.rating
                )
            }
        }
        return allServices.find { it.id == serviceId }
    }
}