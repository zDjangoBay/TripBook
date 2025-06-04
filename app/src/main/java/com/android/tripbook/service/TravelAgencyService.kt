package com.android.tripbook.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// API Response Models
data class GooglePlacesResponse(
    val results: List<GooglePlace>,
    val status: String
)

data class GooglePlace(
    val place_id: String,
    val name: String,
    val rating: Float?,
    val types: List<String>,
    val vicinity: String?,
    val formatted_address: String?,
    val price_level: Int?,
    val photos: List<GooglePhoto>?
)

data class GooglePhoto(
    val photo_reference: String,
    val height: Int,
    val width: Int
)

// Retrofit API Interface
interface GooglePlacesApi {
    @GET("nearbysearch/json")
    suspend fun searchNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int = 50000,
        @Query("type") type: String = "travel_agency",
        @Query("key") apiKey: String
    ): Response<GooglePlacesResponse>

    @GET("textsearch/json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("key") apiKey: String
    ): Response<GooglePlacesResponse>
}

// Enhanced Service Classes
data class AgencyService(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val price: Int,
    val rating: Float,
    val location: String,
    val isFromApi: Boolean = false,
    val photoReference: String? = null
)

data class TravelAgency(
    val id: String,
    val name: String,
    val services: List<AgencyService>,
    val rating: Float,
    val address: String? = null,
    val isFromApi: Boolean = false
)

class TravelAgencyService {
    private val apiKey = "YOUR_GOOGLE_PLACES_API_KEY" // Replace with your API key

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/place/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val placesApi = retrofit.create(GooglePlacesApi::class.java)

    // Mock data (existing)
    private val mockAgencies = listOf(
        TravelAgency(
            id = "agency1",
            name = "Nile Adventures",
            services = listOf(
                AgencyService(
                    id = "service1",
                    name = "Nile River Cruise",
                    type = "Tour",
                    description = "A 3-day cruise along the Nile River",
                    price = 500,
                    rating = 4.5f,
                    location = "Cairo, Egypt"
                ),
                AgencyService(
                    id = "service2",
                    name = "Pyramids Tour",
                    type = "Tour",
                    description = "Guided tour of the Giza Pyramids",
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
                    description = "2-day desert safari experience",
                    price = 300,
                    rating = 4.2f,
                    location = "Marrakech, Morocco"
                ),
                AgencyService(
                    id = "service4",
                    name = "Luxury Hotel Booking",
                    type = "Accommodation",
                    description = "5-star hotel in Marrakech",
                    price = 200,
                    rating = 4.7f,
                    location = "Marrakech, Morocco"
                )
            ),
            rating = 4.4f
        )
    )

    // African cities coordinates for API searches
    private val africanCities = mapOf(
        "cairo" to "-30.0444,31.2357",
        "marrakech" to "31.6295,7.9811",
        "cape town" to "-33.9249,18.4241",
        "nairobi" to "-1.2921,36.8219",
        "lagos" to "6.5244,3.3792",
        "casablanca" to "33.5731,7.5898",
        "tunis" to "36.8065,10.1815",
        "accra" to "5.6037,0.1870",
        "addis ababa" to "9.1450,40.4897",
        "johannesburg" to "-26.2041,28.0473"
    )

    suspend fun getAgenciesForDestination(destination: String): List<TravelAgency> {
        return withContext(Dispatchers.IO) {
            val combinedAgencies = mutableListOf<TravelAgency>()

            // Add mock agencies
            val mockResults = getMockAgenciesForDestination(destination)
            combinedAgencies.addAll(mockResults)

            // Try to fetch real agencies from API
            try {
                val apiResults = fetchAgenciesFromApi(destination)
                combinedAgencies.addAll(apiResults)
            } catch (e: Exception) {
                // API call failed, continue with mock data only
                println("API call failed: ${e.message}")
            }

            combinedAgencies
        }
    }

    private fun getMockAgenciesForDestination(destination: String): List<TravelAgency> {
        val normalizedDestination = destination.lowercase()
        return mockAgencies.filter { agency ->
            agency.services.any { service ->
                service.location.lowercase().contains(normalizedDestination)
            }
        }
    }

    private suspend fun fetchAgenciesFromApi(destination: String): List<TravelAgency> {
        val location = findLocationCoordinates(destination)
        if (location == null) {
            // Fallback to text search
            return searchAgenciesByText(destination)
        }

        val response = placesApi.searchNearbyPlaces(
            location = location,
            radius = 50000,
            type = "travel_agency",
            apiKey = apiKey
        )

        if (response.isSuccessful && response.body()?.status == "OK") {
            return response.body()?.results?.map { place ->
                convertGooglePlaceToAgency(place, destination)
            } ?: emptyList()
        }

        return emptyList()
    }

    private suspend fun searchAgenciesByText(destination: String): List<TravelAgency> {
        val query = "travel agency in $destination Africa"
        val response = placesApi.searchPlaces(
            query = query,
            apiKey = apiKey
        )

        if (response.isSuccessful && response.body()?.status == "OK") {
            return response.body()?.results?.map { place ->
                convertGooglePlaceToAgency(place, destination)
            } ?: emptyList()
        }

        return emptyList()
    }

    private fun findLocationCoordinates(destination: String): String? {
        val normalizedDestination = destination.lowercase()
        return africanCities.entries.find {
            normalizedDestination.contains(it.key) || it.key.contains(normalizedDestination)
        }?.value
    }

    private fun convertGooglePlaceToAgency(place: GooglePlace, destination: String): TravelAgency {
        val services = listOf(
            AgencyService(
                id = "${place.place_id}_service1",
                name = "Travel Planning Service",
                type = "Tour",
                description = "Professional travel planning and booking services",
                price = estimatePrice(place.price_level),
                rating = place.rating ?: 4.0f,
                location = place.vicinity ?: destination,
                isFromApi = true,
                photoReference = place.photos?.firstOrNull()?.photo_reference
            ),
            AgencyService(
                id = "${place.place_id}_service2",
                name = "Local Tours",
                type = "Tour",
                description = "Guided local tours and excursions",
                price = estimatePrice(place.price_level) - 50,
                rating = place.rating ?: 4.0f,
                location = place.vicinity ?: destination,
                isFromApi = true
            )
        )

        return TravelAgency(
            id = place.place_id,
            name = place.name,
            services = services,
            rating = place.rating ?: 4.0f,
            address = place.formatted_address,
            isFromApi = true
        )
    }

    private fun estimatePrice(priceLevel: Int?): Int {
        return when (priceLevel) {
            0 -> 50   // Free
            1 -> 100  // Inexpensive
            2 -> 200  // Moderate
            3 -> 400  // Expensive
            4 -> 600  // Very Expensive
            else -> 150 // Default
        }
    }

    fun filterAgencies(
        agencies: List<TravelAgency>,
        minRating: Float? = null,
        maxPrice: Int? = null,
        serviceType: String? = null
    ): List<TravelAgency> {
        return agencies.map { agency ->
            agency.copy(
                services = agency.services.filter { service ->
                    (minRating == null || service.rating >= minRating) &&
                            (maxPrice == null || service.price <= maxPrice) &&
                            (serviceType == null || service.type == serviceType)
                }
            )
        }.filter { it.services.isNotEmpty() }
    }
}