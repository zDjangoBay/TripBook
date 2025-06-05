package com.android.tripbook.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.android.tripbook.model.Location
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.annotation.SuppressLint

private const val PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json"




@Serializable
data class Geometry(
    val location: GeoLocation,
    val viewport: Viewport? = null
)

@Serializable
data class GeoLocation(
    val lat: Double,
    val lng: Double
) {
    fun toLatLng() = LatLng(lat, lng)
}





@Serializable
data class Photo(
    val photoReference: String,
    val height: Int,
    val width: Int
)

@Serializable
data class OpeningHours(
    val openNow: Boolean,
    val weekdayText: List<String> = emptyList()
)

// Distance Matrix API data classes
@Serializable
data class DistanceMatrixResult(
    val origins: List<String>,
    val destinations: List<String>,
    val elements: List<List<DistanceElement>>
)

@Serializable
data class DistanceElement(
    val distance: DistanceInfo?,
    val duration: DurationInfo?,
    val status: String
)

@Serializable
data class DistanceInfo(
    val text: String,
    val value: Int
)

@Serializable
data class DurationInfo(
    val text: String,
    val value: Int
)

// Location result for geolocation
data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val address: String? = null
)

// Private API response data classes
@Serializable
private data class PlacesSearchResponse(
    val results: List<PlaceResultApi> = emptyList(),
    val status: String,
    val errorMessage: String? = null
)

@Serializable
private data class PlaceDetailsResponse(
    val result: PlaceDetailsApi? = null,
    val status: String,
    val errorMessage: String? = null
)

@Serializable
private data class DistanceMatrixResponse(
    val destination_addresses: List<String> = emptyList(),
    val origin_addresses: List<String> = emptyList(),
    val rows: List<DistanceMatrixRow> = emptyList(),
    val status: String
)

@Serializable
private data class DistanceMatrixRow(
    val elements: List<DistanceElementApi> = emptyList()
)

@Serializable
private data class DistanceElementApi(
    val distance: DistanceInfoApi? = null,
    val duration: DurationInfoApi? = null,
    val status: String = ""
)

@Serializable
private data class DistanceInfoApi(
    val text: String = "",
    val value: Int = 0
)

@Serializable
private data class DurationInfoApi(
    val text: String = "",
    val value: Int = 0
)

@Serializable
private data class PlaceResultApi(
    val place_id: String = "",
    val name: String = "",
    val formatted_address: String = "",
    val types: List<String> = emptyList(),
    val rating: Double? = null,
    val price_level: Int? = null,
    val photos: List<PhotoApi> = emptyList(),
    val geometry: GeometryApi? = null
)

@Serializable
private data class PlaceDetailsApi(
    val place_id: String = "",
    val name: String = "",
    val formatted_address: String = "",
    val formatted_phone_number: String? = null,
    val website: String? = null,
    val rating: Double? = null,
    val price_level: Int? = null,
    val reviews: List<ReviewApi> = emptyList(),
    val photos: List<PhotoApi> = emptyList(),
    val opening_hours: OpeningHoursApi? = null,
    val geometry: GeometryApi? = null,
    val types: List<String> = emptyList()
)

@Serializable
private data class GeometryApi(
    val location: GeoLocationApi,
    val viewport: ViewportApi? = null
)

@Serializable
private data class GeoLocationApi(
    val lat: Double,
    val lng: Double
)

@Serializable
private data class ViewportApi(
    val northeast: GeoLocationApi,
    val southwest: GeoLocationApi
)

@Serializable
private data class ReviewApi(
    val author_name: String = "",
    val rating: Int = 0,
    val text: String = "",
    val time: Long = 0
)

@Serializable
private data class PhotoApi(
    val photo_reference: String = "",
    val height: Int = 0,
    val width: Int = 0
)

@Serializable
private data class OpeningHoursApi(
    val open_now: Boolean = false,
    val weekday_text: List<String> = emptyList()
)

/**
 * Comprehensive Google Maps API Integration Service
 * Handles: Map display, Places API, Directions API, Distance Matrix API, and Geolocation
 */
class GoogleMapsService(
    private val context: Context,
    private val apiKey: String
) {
    private val placesClient: PlacesClient
    private val fusedLocationClient: FusedLocationProviderClient
    private val httpClient = OkHttpClient()
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    init {
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
        placesClient = Places.createClient(context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    // ===========================================
    // GEOLOCATION SERVICES
    // ===========================================

    /**
     * Get current device location
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult? {
        return withContext(Dispatchers.IO) {
            if (!hasLocationPermission()) {
                throw SecurityException("Location permission not granted")
            }

            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    LocationResult(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        accuracy = it.accuracy
                    )
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get autocomplete suggestions for places
     */
    suspend fun getPlaceSuggestions(query: String): List<AutocompletePrediction> {
        return withContext(Dispatchers.IO) {
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setSessionToken(token)
                .build()

            try {
                val response = placesClient.findAutocompletePredictions(request).await()
                response.autocompletePredictions
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Get place details from place ID using Places SDK
     */
    suspend fun getPlaceDetails(placeId: String): Location? {
        return withContext(Dispatchers.IO) {
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )

            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            try {
                val response = placesClient.fetchPlace(request).await()
                val place = response.place
                val latLng = place.latLng
                if (latLng != null) {
                    Location(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        address = place.address ?: "",
                        placeId = place.id ?: "",
                        name = place.name ?: ""
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Search for places using text query
     */
    suspend fun searchPlaces(
        query: String,
        type: String? = null,
        location: String? = null,
        radius: Int = 50000
    ): List<PlaceResult> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            var urlString = "$PLACES_SEARCH_URL?query=$encodedQuery&key=$apiKey"

            // Add optional parameters
            type?.let { urlString += "&type=${URLEncoder.encode(it, "UTF-8")}" }
            location?.let { urlString += "&location=${URLEncoder.encode(it, "UTF-8")}&radius=$radius" }

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val placesResponse = json.decodeFromString<PlacesSearchResponse>(response)

                if (placesResponse.status == "OK") {
                    return@withContext placesResponse.results.map { it.toPlaceResult() }
                } else {
                    throw Exception("Places API error: ${placesResponse.status} - ${placesResponse.errorMessage}")
                }
            } else {
                throw Exception("HTTP error: $responseCode")
            }
        } catch (e: Exception) {
            throw Exception("Failed to search places: ${e.message}", e)
        }
    }

    /**
     * Search for nearby attractions around a location
     */
    suspend fun searchNearbyAttractions(
        latitude: Double,
        longitude: Double,
        radius: Int = 5000,
        type: String = "tourist_attraction"
    ): List<PlaceResult> {
        val location = "$latitude,$longitude"
        return searchPlaces(
            query = type,
            location = location,
            radius = radius,
            type = type
        )
    }

    /**
     * Search for restaurants near a location
     */
    suspend fun searchNearbyRestaurants(
        latitude: Double,
        longitude: Double,
        radius: Int = 2000
    ): List<PlaceResult> {
        val location = "$latitude,$longitude"
        return searchPlaces(
            query = "restaurant",
            location = location,
            radius = radius,
            type = "restaurant"
        )
    }

    /**
     * Search for gas stations near a location
     */
    suspend fun searchNearbyGasStations(
        latitude: Double,
        longitude: Double,
        radius: Int = 5000
    ): List<PlaceResult> {
        val location = "$latitude,$longitude"
        return searchPlaces(
            query = "gas station",
            location = location,
            radius = radius,
            type = "gas_station"
        )
    }

    // ===========================================
    // EXTENSION FUNCTIONS FOR API MODELS
    // ===========================================

    private fun PlaceResultApi.toPlaceResult(): PlaceResult {
        return PlaceResult(
            placeId = place_id,
            name = name,
            address = formatted_address,
            types = types,
            rating = rating,
            priceLevel = price_level,
            photoReference = photos.firstOrNull()?.photo_reference,
            geometry = geometry?.toGeometry()
        )
    }

    private fun PlaceDetailsApi.toPlaceDetails(): PlaceDetails {
        return PlaceDetails(
            placeId = place_id,
            name = name,
            address = formatted_address,
            formattedPhoneNumber = formatted_phone_number,
            website = website,
            rating = rating,
            priceLevel = price_level,
            reviews = reviews.map { it.toReview() },
            photos = photos.map { it.toPhoto() },
            openingHours = opening_hours?.toOpeningHours(),
            geometry = geometry?.toGeometry(),
            types = types
        )
    }

    private fun GeometryApi.toGeometry(): Geometry {
        return Geometry(
            location = GeoLocation(location.lat, location.lng),
            viewport = viewport?.let {
                Viewport(
                    northeast = GeoLocation(it.northeast.lat, it.northeast.lng),
                    southwest = GeoLocation(it.southwest.lat, it.southwest.lng)
                )
            }
        )
    }

    private fun ReviewApi.toReview(): Review {
        return Review(
            authorName = author_name,
            rating = rating,
            text = text,
            time = time
        )
    }

    private fun PhotoApi.toPhoto(): Photo {
        return Photo(
            photoReference = photo_reference,
            height = height,
            width = width
        )
    }

    private fun OpeningHoursApi.toOpeningHours(): OpeningHours {
        return OpeningHours(
            openNow = open_now,
            weekdayText = weekday_text
        )
    }
}

// Extension for await() function
private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    return suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(task.exception ?: Exception("Unknown error"))
            }
        }
    }
}