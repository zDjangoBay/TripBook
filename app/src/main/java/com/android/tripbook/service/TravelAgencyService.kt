package com.android.tripbook.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class AgencyService(
    val id: String,
    val name: String,
    val type: String, // e.g., "Tour", "Accommodation", "Transportation"
    val description: String,
    val price: Int, // in USD
    val rating: Float, // 0.0 to 5.0
    val location: String // City or region
)

data class TravelAgency(
    val id: String,
    val name: String,
    val services: List<AgencyService>,
    val rating: Float
)

// API interface for future backend integration
interface TravelAgencyApi {
    @GET("agencies")
    suspend fun getAgenciesByDestination(@Query("destination") destination: String): Response<List<TravelAgency>>

    @GET("agencies/filter")
    suspend fun getFilteredAgencies(
        @Query("destination") destination: String,
        @Query("minRating") minRating: Float?,
        @Query("maxPrice") maxPrice: Int?,
        @Query("serviceType") serviceType: String?
    ): Response<List<TravelAgency>>
}

class TravelAgencyService {
    private val api: TravelAgencyApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tripbook.com/") // Replace with actual API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TravelAgencyApi::class.java)
    }

    // Mock data for development/testing - will be replaced by API calls
    private val mockAgencies = listOf(
        TravelAgency(
            id = "agency1",
            name = "Nile Adventures",
            services = listOf(
                AgencyService(
                    id = "service1",
                    name = "Nile River Cruise",
                    type = "Tour",
                    description = "A 3-day cruise along the Nile River with professional guide",
                    price = 500,
                    rating = 4.5f,
                    location = "Cairo, Egypt"
                ),
                AgencyService(
                    id = "service2",
                    name = "Pyramids Tour",
                    type = "Tour",
                    description = "Guided tour of the Giza Pyramids with expert egyptologist",
                    price = 150,
                    rating = 4.8f,
                    location = "Giza, Egypt"
                )
            ),
            rating = 4.6f
        ),
        TravelAgency(
            id = "agency2",
            name = "Sahara Travels",
            services = listOf(
                AgencyService(
                    id = "service3",
                    name = "Desert Safari",
                    type = "Tour",
                    description = "2-day desert safari experience with camel trekking",
                    price = 300,
                    rating = 4.2f,
                    location = "Marrakech, Morocco"
                ),
                AgencyService(
                    id = "service4",
                    name = "Luxury Hotel Booking",
                    type = "Accommodation",
                    description = "5-star hotel in Marrakech with spa and pool",
                    price = 200,
                    rating = 4.7f,
                    location = "Marrakech, Morocco"
                )
            ),
            rating = 4.4f
        ),
        TravelAgency(
            id = "agency3",
            name = "African Explorer",
            services = listOf(
                AgencyService(
                    id = "service5",
                    name = "Safari Adventure",
                    type = "Tour",
                    description = "Wildlife safari in Kenya's national parks",
                    price = 800,
                    rating = 4.9f,
                    location = "Nairobi, Kenya"
                ),
                AgencyService(
                    id = "service6",
                    name = "Airport Transfer",
                    type = "Transportation",
                    description = "Private airport transfer service",
                    price = 50,
                    rating = 4.3f,
                    location = "Nairobi, Kenya"
                )
            ),
            rating = 4.6f
        )
    )

    suspend fun getAgenciesForDestination(destination: String): List<TravelAgency> {
        return try {
            // TODO: Replace with actual API call when backend is ready
            // val response = api.getAgenciesByDestination(destination)
            // if (response.isSuccessful) {
            //     response.body() ?: emptyList()
            // } else {
            //     emptyList()
            // }

            // For now, use mock data with improved filtering
            withContext(Dispatchers.IO) {
                val normalizedDestination = destination.trim().lowercase()
                if (normalizedDestination.isEmpty()) {
                    return@withContext emptyList<TravelAgency>()
                }

                mockAgencies.filter { agency ->
                    agency.services.any { service ->
                        service.location.lowercase().contains(normalizedDestination) ||
                                service.name.lowercase().contains(normalizedDestination) ||
                                service.description.lowercase().contains(normalizedDestination)
                    }
                }
            }
        } catch (e: Exception) {
            // Log error in production
            emptyList()
        }
    }

    fun filterAgencies(
        agencies: List<TravelAgency>,
        minRating: Float? = null,
        maxPrice: Int? = null,
        serviceType: String? = null
    ): List<TravelAgency> {
        if (agencies.isEmpty()) return emptyList()

        return agencies.mapNotNull { agency ->
            val filteredServices = agency.services.filter { service ->
                val ratingMatch = minRating?.let { service.rating >= it } != false
                val priceMatch = maxPrice?.let { service.price <= it } != false
                val typeMatch = serviceType?.let { service.type.equals(it, ignoreCase = true) } != false

                ratingMatch && priceMatch && typeMatch
            }

            if (filteredServices.isNotEmpty()) {
                agency.copy(services = filteredServices)
            } else {
                null
            }
        }
    }

    suspend fun getFilteredAgencies(
        destination: String,
        minRating: Float? = null,
        maxPrice: Int? = null,
        serviceType: String? = null
    ): List<TravelAgency> {
        val agencies = getAgenciesForDestination(destination)
        return filterAgencies(agencies, minRating, maxPrice, serviceType)
    }

    // Helper method to get all available service types
    fun getAvailableServiceTypes(): List<String> {
        return listOf("Tour", "Accommodation", "Transportation")
    }

    // Helper method to validate destination input
    fun isValidDestination(destination: String?): Boolean {
        return !destination.isNullOrBlank() && destination.trim().length >= 2
    }
}