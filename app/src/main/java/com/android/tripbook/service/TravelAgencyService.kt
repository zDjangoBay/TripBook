package com.android.tripbook.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Foursquare API Response Models
data class FoursquareResponse(
    val results: List<FoursquarePlace>
)

data class FoursquarePlace(
    val fsq_id: String,
    val name: String,
    val categories: List<FoursquareCategory>,
    val location: FoursquareLocation,
    val rating: Double?,
    val price: Int?,
    val photos: List<FoursquarePhoto>?,
    val distance: Int?,
    val popularity: Double?
)

data class FoursquareCategory(
    val id: String,
    val name: String,
    val icon: FoursquareIcon?
)

data class FoursquareIcon(
    val prefix: String,
    val suffix: String
)

data class FoursquareLocation(
    val address: String?,
    val locality: String?,
    val region: String?,
    val country: String?,
    val formatted_address: String?
)

data class FoursquarePhoto(
    val id: String,
    val prefix: String,
    val suffix: String,
    val width: Int,
    val height: Int
)

// Retrofit API Interface for Foursquare
interface FoursquareApi {
    @GET("places/search")
    suspend fun searchPlaces(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("near") near: String,
        @Query("categories") categories: String = "16000", // Travel and Transportation
        @Query("limit") limit: Int = 20
    ): Response<FoursquareResponse>

    @GET("places/nearby")
    suspend fun searchNearby(
        @Header("Authorization") authorization: String,
        @Query("ll") latLng: String,
        @Query("categories") categories: String = "16000",
        @Query("limit") limit: Int = 20,
        @Query("radius") radius: Int = 50000
    ): Response<FoursquareResponse>
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
    val photoUrl: String? = null,
    val distance: String? = null
)

data class TravelAgency(
    val id: String,
    val name: String,
    val services: List<AgencyService>,
    val rating: Float,
    val address: String? = null,
    val isFromApi: Boolean = false,
    val category: String? = null,
    val url: String? = null // Added URL field
)

class TravelAgencyService {
    private val apiKey = "fsq34wFRdgoUG9b3+ThZXU4nl1RX+wYAnZUi8+T49HFNuaw=" //free tier api for testing security concerns are acknowledged

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.foursquare.com/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val foursquareApi = retrofit.create(FoursquareApi::class.java)

    // Mock data for Cameroon specifically
    private val mockAgencies = listOf(
        TravelAgency(
            id = "agency1",
            name = "Cameroon Adventure Tours",
            services = listOf(
                AgencyService(
                    id = "service1",
                    name = "Mount Cameroon Hiking Tour",
                    type = "Tour",
                    description = "3-day guided hiking expedition to Mount Cameroon peak",
                    price = 250,
                    rating = 4.5f,
                    location = "Buea, Cameroon"
                ),
                AgencyService(
                    id = "service2",
                    name = "Douala City Tour",
                    type = "Tour",
                    description = "Full day guided tour of Douala's main attractions",
                    price = 80,
                    rating = 4.2f,
                    location = "Douala, Cameroon"
                )
            ),
            rating = 4.3f,
            url = "https://example.com/cameroon-adventure-tours" // Example mock URL
        ),
        TravelAgency(
            id = "agency2",
            name = "Yaoundé Travel Services",
            services = listOf(
                AgencyService(
                    id = "service3",
                    name = "Safari to Waza National Park",
                    type = "Tour",
                    description = "2-day wildlife safari in Waza National Park",
                    price = 320,
                    rating = 4.7f,
                    location = "Yaoundé, Cameroon"
                ),
                AgencyService(
                    id = "service4",
                    name = "Hotel Booking Service",
                    type = "Accommodation",
                    description = "Premium hotel reservations in Yaoundé",
                    price = 150,
                    rating = 4.4f,
                    location = "Yaoundé, Cameroon"
                )
            ),
            rating = 4.5f,
            url = "https://example.com/yaounde-travel-services" // Example mock URL
        ),
        TravelAgency(
            id = "agency3",
            name = "Limbe Beach Resort Tours",
            services = listOf(
                AgencyService(
                    id = "service5",
                    name = "Limbe Beach Experience",
                    type = "Tour",
                    description = "Beach resort tour with cultural activities",
                    price = 120,
                    rating = 4.6f,
                    location = "Limbe, Cameroon"
                ),
                AgencyService(
                    id = "service6",
                    name = "Transportation Service",
                    type = "Transportation",
                    description = "Airport transfers and city transportation",
                    price = 50,
                    rating = 4.1f,
                    location = "Yaoundé, Cameroon"
                )
            ),
            rating = 4.3f,
            url = "https://example.com/limbe-beach-resort-tours" // Example mock URL
        )
    )

    // Cameroon cities coordinates
    private val cameroonCities = mapOf(
        "yaoundé" to "3.8480,11.5021",
        "yaounde" to "3.8480,11.5021",
        "douala" to "4.0511,9.7679",
        "bamenda" to "5.9597,10.1494",
        "bafoussam" to "5.4737,10.4158",
        "garoua" to "9.3265,13.3958",
        "maroua" to "10.5913,14.3159",
        "ngaoundéré" to "7.3167,13.5833",
        "bertoua" to "4.5777,13.6848",
        "buea" to "4.1549,9.2920",
        "limbe" to "4.0186,9.2065",
        "kribi" to "2.9373,9.9073"
    )

    suspend fun getAgenciesForDestination(destination: String): List<TravelAgency> {
        return withContext(Dispatchers.IO) {
            val combinedAgencies = mutableListOf<TravelAgency>()

            // Add mock agencies first
            val mockResults = getMockAgenciesForDestination(destination)
            combinedAgencies.addAll(mockResults)

            // Try to fetch real agencies from Foursquare API
            try {
                val apiResults = fetchAgenciesFromFoursquare(destination)
                combinedAgencies.addAll(apiResults)
                println("Foursquare API returned ${apiResults.size} agencies")
            } catch (e: Exception) {
                println("Foursquare API call failed: ${e.message}")
                e.printStackTrace()
            }

            combinedAgencies
        }
    }

    private fun getMockAgenciesForDestination(destination: String): List<TravelAgency> {
        val normalizedDestination = destination.lowercase()
        return mockAgencies.filter { agency ->
            agency.services.any { service ->
                service.location.lowercase().contains(normalizedDestination) ||
                        normalizedDestination.contains("cameroon") ||
                        normalizedDestination.contains("yaoundé") ||
                        normalizedDestination.contains("yaounde")
            }
        }
    }

    private suspend fun fetchAgenciesFromFoursquare(destination: String): List<TravelAgency> {
        val authHeader = apiKey

        // Try coordinate-based search first
        val coordinates = findLocationCoordinates(destination)
        if (coordinates != null) {
            try {
                val nearbyResponse = foursquareApi.searchNearby(
                    authorization = authHeader,
                    latLng = coordinates,
                    categories = "16000,12000,13000", // Travel, Professional Services, Retail
                    limit = 15,
                    radius = 25000
                )

                if (nearbyResponse.isSuccessful && nearbyResponse.body() != null) {
                    val nearbyResults = convertFoursquarePlacesToAgencies(nearbyResponse.body()!!.results, destination)
                    if (nearbyResults.isNotEmpty()) {
                        return nearbyResults
                    }
                }
            } catch (e: Exception) {
                println("Nearby search failed: ${e.message}")
            }
        }

        // Fallback to text search
        return searchAgenciesByText(destination, authHeader)
    }

    private suspend fun searchAgenciesByText(destination: String, authHeader: String): List<TravelAgency> {
        val queries = listOf(
            "travel agency",
            "tour operator",
            "travel services",
            "tourism office",
            "hotel booking"
        )

        val allResults = mutableListOf<TravelAgency>()

        for (query in queries) {
            try {
                val response = foursquareApi.searchPlaces(
                    authorization = authHeader,
                    query = query,
                    near = "$destination, Cameroon",
                    categories = "16000,12000",
                    limit = 10
                )

                if (response.isSuccessful && response.body() != null) {
                    val results = convertFoursquarePlacesToAgencies(response.body()!!.results, destination)
                    allResults.addAll(results)
                }
            } catch (e: Exception) {
                println("Text search failed for query '$query': ${e.message}")
            }
        }

        // Remove duplicates by ID
        return allResults.distinctBy { it.id }
    }

    private fun findLocationCoordinates(destination: String): String? {
        val normalizedDestination = destination.lowercase()
        return cameroonCities.entries.find {
            normalizedDestination.contains(it.key) || it.key.contains(normalizedDestination)
        }?.value
    }

    private fun convertFoursquarePlacesToAgencies(places: List<FoursquarePlace>, destination: String): List<TravelAgency> {
        return places.mapNotNull { place ->
            try {
                val category = place.categories.firstOrNull()?.name ?: "Travel Service"
                val photoUrl = place.photos?.firstOrNull()?.let { photo ->
                    "${photo.prefix}300x300${photo.suffix}"
                }
                val foursquareUrl = "https://foursquare.com/v/${place.fsq_id}" // Construct Foursquare URL

                val services = generateServicesForPlace(place, destination, photoUrl)

                TravelAgency(
                    id = place.fsq_id,
                    name = place.name,
                    services = services,
                    rating = (place.rating?.toFloat() ?: (3.5f + (0..10).random() * 0.1f)),
                    address = place.location.formatted_address ?: place.location.address,
                    isFromApi = true,
                    category = category,
                    url = foursquareUrl // Assign the constructed URL
                )
            } catch (e: Exception) {
                println("Error converting place ${place.name}: ${e.message}")
                null
            }
        }
    }

    private fun generateServicesForPlace(place: FoursquarePlace, destination: String, photoUrl: String?): List<AgencyService> {
        val baseRating = place.rating?.toFloat() ?: (3.5f + (0..10).random() * 0.1f)
        val basePrice = estimatePriceFromPlace(place)
        val distance = place.distance?.let { "${it}m away" }

        val category = place.categories.firstOrNull()?.name?.lowercase() ?: ""

        return when {
            category.contains("hotel") || category.contains("accommodation") -> {
                listOf(
                    AgencyService(
                        id = "${place.fsq_id}_accommodation",
                        name = "Hotel Booking Service",
                        type = "Accommodation",
                        description = "Professional hotel and accommodation booking services",
                        price = basePrice,
                        rating = baseRating,
                        location = place.location.locality ?: destination,
                        isFromApi = true,
                        photoUrl = photoUrl,
                        distance = distance
                    )
                )
            }
            category.contains("transport") || category.contains("car") || category.contains("taxi") -> {
                listOf(
                    AgencyService(
                        id = "${place.fsq_id}_transport",
                        name = "Transportation Service",
                        type = "Transportation",
                        description = "Reliable transportation and transfer services",
                        price = basePrice / 2,
                        rating = baseRating,
                        location = place.location.locality ?: destination,
                        isFromApi = true,
                        photoUrl = photoUrl,
                        distance = distance
                    )
                )
            }
            else -> {
                listOf(
                    AgencyService(
                        id = "${place.fsq_id}_tour",
                        name = "Local Tours & Experiences",
                        type = "Tour",
                        description = "Guided tours and authentic local experiences",
                        price = basePrice,
                        rating = baseRating,
                        location = place.location.locality ?: destination,
                        isFromApi = true,
                        photoUrl = photoUrl,
                        distance = distance
                    ),
                    AgencyService(
                        id = "${place.fsq_id}_planning",
                        name = "Travel Planning Service",
                        type = "Tour",
                        description = "Complete travel planning and consultation services",
                        price = (basePrice * 0.7).toInt(),
                        rating = baseRating,
                        location = place.location.locality ?: destination,
                        isFromApi = true,
                        distance = distance
                    )
                )
            }
        }
    }

    private fun estimatePriceFromPlace(place: FoursquarePlace): Int {
        val priceLevel = place.price ?: 2
        val popularity = place.popularity ?: 0.5

        val basePrice = when (priceLevel) {
            1 -> 50
            2 -> 120
            3 -> 250
            4 -> 400
            else -> 150
        }

        // Adjust by popularity
        return (basePrice * (0.8 + popularity * 0.4)).toInt()
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