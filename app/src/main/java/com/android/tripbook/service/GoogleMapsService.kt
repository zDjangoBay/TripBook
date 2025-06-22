package com.android.tripbook.service

// Import necessary Android system classes for context, permissions, and location services.
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

// Import custom model classes.
import com.android.tripbook.model.Location

// Import Google Play Services location and maps utilities.
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

// Import Google Places API specific classes.
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*

// Import Kotlin Coroutines for asynchronous operations.
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

// Import Kotlinx Serialization for JSON parsing.
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

// Import OkHttp for HTTP networking.
import okhttp3.OkHttpClient
import okhttp3.Request

// Import standard Java networking and utility classes.
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.annotation.SuppressLint

// Constants
private const val PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json"
private const val TAG = "GoogleMapsService"
private const val DEFAULT_TIMEOUT = 15000L // 15 seconds
private const val LOCATION_TIMEOUT = 10000L // 10 seconds for location requests

// Custom exception classes for better error handling
sealed class GoogleMapsException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String, cause: Throwable? = null) : GoogleMapsException(message, cause)
    class PermissionException(message: String) : GoogleMapsException(message)
    class ApiException(message: String, val statusCode: String? = null) : GoogleMapsException(message)
    class ParseException(message: String, cause: Throwable? = null) : GoogleMapsException(message, cause)
    class TimeoutException(message: String) : GoogleMapsException(message)
    class InvalidInputException(message: String) : GoogleMapsException(message)
}

// --- Data classes for Google Places API responses ---

@Serializable
data class PlaceResult(
    val placeId: String = "",
    val name: String,
    val address: String,
    val types: List<String> = emptyList(),
    val rating: Double? = null,
    val priceLevel: Int? = null,
    val photoReference: String? = null,
    val geometry: Geometry? = null
)

@Serializable
data class PlaceDetails(
    val placeId: String,
    val name: String,
    val address: String,
    val formattedPhoneNumber: String? = null,
    val website: String? = null,
    val rating: Double? = null,
    val priceLevel: Int? = null,
    val reviews: List<Review> = emptyList(),
    val photos: List<Photo> = emptyList(),
    val openingHours: OpeningHours? = null,
    val geometry: Geometry? = null,
    val types: List<String> = emptyList()
)

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

    fun isValid(): Boolean = lat in -90.0..90.0 && lng in -180.0..180.0
}

@Serializable
data class Viewport(
    val northeast: GeoLocation,
    val southwest: GeoLocation
)

@Serializable
data class Review(
    val authorName: String,
    val rating: Int,
    val text: String,
    val time: Long
)

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

// --- Distance Matrix API data classes ---

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

// --- Location result for geolocation ---

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val address: String? = null
) {
    fun isValid(): Boolean = GeoLocation(latitude, longitude).isValid()
}

// --- Private API response data classes ---

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
 * Comprehensive Google Maps API Integration Service with enhanced error handling.
 * This class provides robust methods to interact with various Google Maps Platform APIs.
 */
class GoogleMapsService(
    private val context: Context,
    private val apiKey: String
) {
    private val placesClient: PlacesClient
    private val fusedLocationClient: FusedLocationProviderClient
    private val httpClient: OkHttpClient
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    init {
        try {
            // Validate API key
            if (apiKey.isBlank()) {
                throw GoogleMapsException.InvalidInputException("API key cannot be empty")
            }

            // Initialize the Google Places SDK
            if (!Places.isInitialized()) {
                Places.initialize(context, apiKey)
            }

            placesClient = Places.createClient(context)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            // Configure HTTP client with timeouts and retry logic
            httpClient = OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build()

            Log.d(TAG, "GoogleMapsService initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize GoogleMapsService", e)
            throw GoogleMapsException.ApiException("Failed to initialize service: ${e.message}", null)
        }
    }

    // ===========================================
    // GEOLOCATION SERVICES
    // ===========================================

    /**
     * Retrieves the current device location with comprehensive error handling.
     * @return [LocationResult] containing location data, or throws exception if unavailable.
     * @throws GoogleMapsException.PermissionException if location permission not granted
     * @throws GoogleMapsException.TimeoutException if location request times out
     * @throws GoogleMapsException.ApiException for other location-related errors
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult {
        return withContext(Dispatchers.IO) {
            try {
                // Check permissions first
                if (!hasLocationPermission()) {
                    throw GoogleMapsException.PermissionException(
                        "Location permission not granted. Please enable location access in app settings."
                    )
                }

                // Add timeout to prevent hanging
                val location = withTimeoutOrNull(LOCATION_TIMEOUT) {
                    fusedLocationClient.lastLocation.await()
                }

                when {
                    location == null -> {
                        Log.w(TAG, "Last known location is null, attempting fresh location request")
                        getCurrentLocationFresh()
                    }
                    !LocationResult(location.latitude, location.longitude, location.accuracy).isValid() -> {
                        throw GoogleMapsException.ApiException("Invalid location coordinates received")
                    }
                    else -> {
                        LocationResult(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy
                        ).also {
                            Log.d(TAG, "Location retrieved successfully: ${it.latitude}, ${it.longitude}")
                        }
                    }
                }
            } catch (e: GoogleMapsException) {
                throw e
            } catch (e: SecurityException) {
                throw GoogleMapsException.PermissionException("Location permission denied: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error getting current location", e)
                throw GoogleMapsException.ApiException("Failed to get current location: ${e.message}")
            }
        }
    }

    /**
     * Attempts to get a fresh location using location request.
     */
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocationFresh(): LocationResult {
        return withTimeoutOrNull(LOCATION_TIMEOUT) {
            suspendCoroutine<LocationResult> { continuation ->
                val locationRequest = LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 5000
                    fastestInterval = 2000
                    numUpdates = 1
                }

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.lastLocation?.let { location ->
                            val result = LocationResult(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy
                            )
                            continuation.resume(result)
                        } ?: continuation.resumeWithException(
                            GoogleMapsException.ApiException("Fresh location request returned null")
                        )
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        } ?: throw GoogleMapsException.TimeoutException("Location request timed out")
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    // ===========================================
    // PLACES API SERVICES
    // ===========================================

    /**
     * Fetches autocomplete suggestions with enhanced error handling.
     * @param query The search query string
     * @return List of autocomplete predictions
     * @throws GoogleMapsException.InvalidInputException for invalid input
     * @throws GoogleMapsException.ApiException for API-related errors
     */
    suspend fun getPlaceSuggestions(query: String): List<AutocompletePrediction> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate input
                if (query.isBlank()) {
                    throw GoogleMapsException.InvalidInputException("Search query cannot be empty")
                }

                if (query.length < 2) {
                    Log.d(TAG, "Query too short for meaningful results: '$query'")
                    return@withContext emptyList()
                }

                val token = AutocompleteSessionToken.newInstance()
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query.trim())
                    .setSessionToken(token)
                    .build()

                val response = withTimeoutOrNull(DEFAULT_TIMEOUT) {
                    placesClient.findAutocompletePredictions(request).await()
                } ?: throw GoogleMapsException.TimeoutException("Autocomplete request timed out")

                response.autocompletePredictions.also {
                    Log.d(TAG, "Retrieved ${it.size} autocomplete suggestions for query: '$query'")
                }
            } catch (e: GoogleMapsException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error getting place suggestions for query: '$query'", e)
                throw GoogleMapsException.ApiException("Failed to get place suggestions: ${e.message}")
            }
        }
    }

    /**
     * Retrieves detailed place information with comprehensive error handling.
     * @param placeId The unique place identifier
     * @return Location object with place details
     * @throws GoogleMapsException.InvalidInputException for invalid place ID
     * @throws GoogleMapsException.ApiException for API errors
     */
    suspend fun getPlaceDetails(placeId: String): Location {
        return withContext(Dispatchers.IO) {
            try {
                // Validate input
                if (placeId.isBlank()) {
                    throw GoogleMapsException.InvalidInputException("Place ID cannot be empty")
                }

                val placeFields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS,
                    Place.Field.PHONE_NUMBER,
                    Place.Field.WEBSITE_URI,
                    Place.Field.RATING
                )

                val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                val response = withTimeoutOrNull(DEFAULT_TIMEOUT) {
                    placesClient.fetchPlace(request).await()
                } ?: throw GoogleMapsException.TimeoutException("Place details request timed out")

                val place = response.place
                val latLng = place.latLng

                if (latLng == null) {
                    throw GoogleMapsException.ApiException("Place location coordinates not available")
                }

                if (!GeoLocation(latLng.latitude, latLng.longitude).isValid()) {
                    throw GoogleMapsException.ApiException("Invalid place coordinates received")
                }

                Location(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    address = place.address?.takeIf { it.isNotBlank() } ?: "Address not available",
                    placeId = place.id ?: placeId,
                    name = place.name?.takeIf { it.isNotBlank() } ?: "Unknown Place"
                ).also {
                    Log.d(TAG, "Place details retrieved successfully for ID: $placeId")
                }
            } catch (e: GoogleMapsException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error getting place details for ID: $placeId", e)
                throw GoogleMapsException.ApiException("Failed to get place details: ${e.message}")
            }
        }
    }

    /**
     * Performs text search for places with comprehensive error handling and input validation.
     * @param query The search query
     * @param type Optional place type filter
     * @param location Optional location for biasing results
     * @param radius Search radius in meters
     * @return List of place results
     * @throws GoogleMapsException for various error conditions
     */
    suspend fun searchPlaces(
        query: String,
        type: String? = null,
        location: String? = null,
        radius: Int = 50000
    ): List<PlaceResult> = withContext(Dispatchers.IO) {
        try {
            // Input validation
            if (query.isBlank()) {
                throw GoogleMapsException.InvalidInputException("Search query cannot be empty")
            }

            if (radius <= 0 || radius > 50000) {
                throw GoogleMapsException.InvalidInputException("Radius must be between 1 and 50000 meters")
            }

            location?.let { loc ->
                if (!isValidLocationString(loc)) {
                    throw GoogleMapsException.InvalidInputException("Invalid location format. Expected: 'latitude,longitude'")
                }
            }

            // Build URL with proper encoding
            val encodedQuery = URLEncoder.encode(query.trim(), "UTF-8")
            var urlString = "$PLACES_SEARCH_URL?query=$encodedQuery&key=$apiKey"

            type?.takeIf { it.isNotBlank() }?.let {
                urlString += "&type=${URLEncoder.encode(it, "UTF-8")}"
            }
            location?.let {
                urlString += "&location=${URLEncoder.encode(it, "UTF-8")}&radius=$radius"
            }

            Log.d(TAG, "Searching places with query: '$query'")

            // Make HTTP request
            val request = Request.Builder()
                .url(urlString)
                .get()
                .build()

            val response = httpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                throw GoogleMapsException.NetworkException(
                    "HTTP request failed with code: ${response.code}"
                )
            }

            val responseBody = response.body?.string()
                ?: throw GoogleMapsException.NetworkException("Empty response body")

            // Parse JSON response
            val placesResponse = try {
                json.decodeFromString<PlacesSearchResponse>(responseBody)
            } catch (e: SerializationException) {
                Log.e(TAG, "JSON parsing error", e)
                throw GoogleMapsException.ParseException("Failed to parse API response", e)
            }

            // Handle API status
            when (placesResponse.status) {
                "OK" -> {
                    placesResponse.results.map { it.toPlaceResult() }.also { results ->
                        Log.d(TAG, "Search completed successfully. Found ${results.size} places")
                    }
                }
                "ZERO_RESULTS" -> {
                    Log.d(TAG, "No results found for query: '$query'")
                    emptyList()
                }
                "OVER_QUERY_LIMIT" -> {
                    throw GoogleMapsException.ApiException("API quota exceeded", placesResponse.status)
                }
                "REQUEST_DENIED" -> {
                    throw GoogleMapsException.ApiException("API request denied. Check API key and permissions", placesResponse.status)
                }
                "INVALID_REQUEST" -> {
                    throw GoogleMapsException.ApiException("Invalid request parameters", placesResponse.status)
                }
                else -> {
                    throw GoogleMapsException.ApiException(
                        "API error: ${placesResponse.status} - ${placesResponse.errorMessage}",
                        placesResponse.status
                    )
                }
            }
        } catch (e: GoogleMapsException) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw GoogleMapsException.TimeoutException("Request timed out")
        } catch (e: IOException) {
            Log.e(TAG, "Network error during places search", e)
            throw GoogleMapsException.NetworkException("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during places search", e)
            throw GoogleMapsException.ApiException("Search failed: ${e.message}")
        }
    }

    /**
     * Validates location string format (latitude,longitude).
     */
    private fun isValidLocationString(location: String): Boolean {
        return try {
            val parts = location.split(",")
            if (parts.size != 2) return false

            val lat = parts[0].trim().toDouble()
            val lng = parts[1].trim().toDouble()

            GeoLocation(lat, lng).isValid()
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * Searches for nearby tourist attractions with enhanced error handling.
     */
    suspend fun searchNearbyAttractions(
        latitude: Double,
        longitude: Double,
        radius: Int = 5000,
        type: String = "tourist_attraction"
    ): List<PlaceResult> {
        try {
            if (!GeoLocation(latitude, longitude).isValid()) {
                throw GoogleMapsException.InvalidInputException("Invalid coordinates provided")
            }

            val location = "$latitude,$longitude"
            return searchPlaces(
                query = type,
                location = location,
                radius = radius,
                type = type
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error searching nearby attractions", e)
            throw e
        }
    }

    /**
     * Searches for nearby restaurants with enhanced error handling.
     */
    suspend fun searchNearbyRestaurants(
        latitude: Double,
        longitude: Double,
        radius: Int = 2000
    ): List<PlaceResult> {
        try {
            if (!GeoLocation(latitude, longitude).isValid()) {
                throw GoogleMapsException.InvalidInputException("Invalid coordinates provided")
            }

            val location = "$latitude,$longitude"
            return searchPlaces(
                query = "restaurant",
                location = location,
                radius = radius,
                type = "restaurant"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error searching nearby restaurants", e)
            throw e
        }
    }

    /**
     * Searches for nearby gas stations with enhanced error handling.
     */
    suspend fun searchNearbyGasStations(
        latitude: Double,
        longitude: Double,
        radius: Int = 5000
    ): List<PlaceResult> {
        try {
            if (!GeoLocation(latitude, longitude).isValid()) {
                throw GoogleMapsException.InvalidInputException("Invalid coordinates provided")
            }

            val location = "$latitude,$longitude"
            return searchPlaces(
                query = "gas station",
                location = location,
                radius = radius,
                type = "gas_station"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error searching nearby gas stations", e)
            throw e
        }
    }

    // ===========================================
    // EXTENSION FUNCTIONS FOR API MODEL MAPPING
    // ===========================================

    private fun PlaceResultApi.toPlaceResult(): PlaceResult {
        return PlaceResult(
            placeId = place_id,
            name = name.takeIf { it.isNotBlank() } ?: "Unknown Place",
            address = formatted_address.takeIf { it.isNotBlank() } ?: "Address not available",
            types = types,
            rating = rating?.takeIf { it in 0.0..5.0 },
            priceLevel = price_level?.takeIf { it in 0..4 },
            photoReference = photos.firstOrNull()?.photo_reference?.takeIf { it.isNotBlank() },
            geometry = geometry?.toGeometry()
        )
    }

    private fun PlaceDetailsApi.toPlaceDetails(): PlaceDetails {
        return PlaceDetails(
            placeId = place_id,
            name = name.takeIf { it.isNotBlank() } ?: "Unknown Place",
            address = formatted_address.takeIf { it.isNotBlank() } ?: "Address not available",
            formattedPhoneNumber = formatted_phone_number?.takeIf { it.isNotBlank() },
            website = website?.takeIf { it.isNotBlank() },
            rating = rating?.takeIf { it in 0.0..5.0 },
            priceLevel = price_level?.takeIf { it in 0..4 },
            reviews = reviews.map { it.toReview() },
            photos = photos.map { it.toPhoto() },
            openingHours = opening_hours?.toOpeningHours(),
            geometry = geometry?.toGeometry(),
            types = types
        )
    }

    private fun GeometryApi.toGeometry(): Geometry? {
        return try {
            if (!GeoLocation(location.lat, location.lng).isValid()) {
                null
            } else {
                Geometry(
                    location = GeoLocation(location.lat, location.lng),
                    viewport = viewport?.let {
                        if (GeoLocation(it.northeast.lat, it.northeast.lng).isValid() &&
                            GeoLocation(it.southwest.lat, it.southwest.lng).isValid()) {
                            Viewport(
                                northeast = GeoLocation(it.northeast.lat, it.northeast.lng),
                                southwest = GeoLocation(it.southwest.lat, it.southwest.lng)
                            )
                        } else null
                    }
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Invalid geometry data received", e)
            null
        }
    }

    private fun ReviewApi.toReview(): Review {
        return Review(
            authorName = author_name.takeIf { it.isNotBlank() } ?: "Anonymous",
            rating = rating.coerceIn(1, 5),
            text = text,
            time = time
        )
    }

    private fun PhotoApi.toPhoto(): Photo {
        return Photo(
            photoReference = photo_reference,
            height = height.coerceAtLeast(0),
            width = width.coerceAtLeast(0)
        )
    }

    private fun OpeningHoursApi.toOpeningHours(): OpeningHours {
        return OpeningHours(
            openNow = open_now,
            weekdayText = weekday_text
        )
    }
}

/**
 * Extension function to convert Google Play Services Task to suspendable coroutine
 * with enhanced error handling.
 */
private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    return suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    continuation.resume(task.result)
                }
                task.isCanceled -> {
                    continuation.resumeWithException(
                        GoogleMapsException.ApiException("Task was cancelled")
                    )
                }
                else -> {
                    val exception = task.exception
                    Log.e(TAG, "Task failed", exception)
                    continuation.resumeWithException(
                        exception ?: GoogleMapsException.ApiException("Unknown task failure")
                    )
                }
            }
        }
    }
}