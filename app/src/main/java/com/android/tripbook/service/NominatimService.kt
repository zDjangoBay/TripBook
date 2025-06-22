package com.android.tripbook.service

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

/**
 * Represents a simplified Attraction with enhanced data validation.
 */
data class Attraction(
    val name: String,
    val location: String,
    val score: Double = 0.0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val type: String? = null
) {
    init {
        require(name.isNotBlank()) { "Attraction name cannot be blank" }
        require(location.isNotBlank()) { "Attraction location cannot be blank" }
        require(score >= 0.0) { "Attraction score cannot be negative" }
    }

    fun toDisplayString(): String = "$name - $location"
}

/**
 * Data class for Nominatim API response with enhanced validation.
 */
data class NominatimPlace(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String,
    @SerializedName("type") val type: String?,
    @SerializedName("class") val placeClass: String?,
    @SerializedName("osm_id") val osmId: Long = -1,
    @SerializedName("importance") val importance: Double = 0.0
) {
    /**
     * Validates if the coordinate data is properly formatted.
     */
    fun hasValidCoordinates(): Boolean {
        return try {
            val lat = latitude.toDouble()
            val lon = longitude.toDouble()
            lat in -90.0..90.0 && lon in -180.0..180.0
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * Safely converts latitude to Double.
     */
    fun getLatitudeAsDouble(): Double? {
        return try {
            latitude.toDouble()
        } catch (e: NumberFormatException) {
            Log.w("NominatimPlace", "Invalid latitude format: $latitude")
            null
        }
    }

    /**
     * Safely converts longitude to Double.
     */
    fun getLongitudeAsDouble(): Double? {
        return try {
            longitude.toDouble()
        } catch (e: NumberFormatException) {
            Log.w("NominatimPlace", "Invalid longitude format: $longitude")
            null
        }
    }

    /**
     * Determines if this place represents a major tourist attraction.
     */
    fun isMajorAttraction(): Boolean {
        return type in MAJOR_ATTRACTION_TYPES ||
                placeClass in MAJOR_ATTRACTION_CLASSES ||
                importance > 0.5
    }

    companion object {
        private val MAJOR_ATTRACTION_TYPES = setOf(
            "monument", "viewpoint", "tourist_attraction", "attraction",
            "tourism", "park", "heritage", "museum", "artwork", "memorial",
            "castle", "ruins", "gallery", "cathedral", "church", "temple"
        )

        private val MAJOR_ATTRACTION_CLASSES = setOf(
            "tourism", "historic", "leisure", "amenity"
        )
    }
}

/**
 * Retrofit interface for Nominatim API interactions.
 */
interface NominatimApi {
    @GET("search")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("extratags") extraTags: Int = 1,
        @Query("addressdetails") addressDetails: Int = 1
    ): List<NominatimPlace>

    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json"
    ): NominatimPlace

    @GET("lookup")
    suspend fun lookupOsmObject(
        @Query("osm_ids") osmIds: String,
        @Query("format") format: String = "json"
    ): List<NominatimPlace>
}

/**
 * Custom exception for Nominatim service errors.
 */
sealed class NominatimException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String, cause: Throwable) : NominatimException(message, cause)
    class TimeoutException(message: String, cause: Throwable) : NominatimException(message, cause)
    class InvalidQueryException(message: String) : NominatimException(message)
    class ApiException(message: String, cause: Throwable) : NominatimException(message, cause)
    class DataValidationException(message: String) : NominatimException(message)
}

/**
 * User-Agent interceptor to comply with Nominatim usage policy.
 */
class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithUserAgent = originalRequest.newBuilder()
            .header("User-Agent", "TripBookAndroidApp/1.0 (contact@example.com)")
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}

/**
 * Service class for interacting with the Nominatim API with enhanced error handling.
 */
class NominatimService {

    private val api: NominatimApi
    private val requestTimeoutMs = 10000L // 10 seconds

    companion object {
        private const val TAG = "NominatimService"
        private const val MIN_QUERY_LENGTH = 2
        private const val MAX_QUERY_LENGTH = 200
        private const val DEFAULT_SEARCH_LIMIT = 5
    }

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor())
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NominatimApi::class.java)
        Log.d(TAG, "NominatimService initialized successfully")
    }

    /**
     * Validates query string before making API calls.
     */
    private fun validateQuery(query: String): Result<String> {
        return when {
            query.isBlank() -> Result.failure(
                NominatimException.InvalidQueryException("Query cannot be blank")
            )
            query.length < MIN_QUERY_LENGTH -> Result.failure(
                NominatimException.InvalidQueryException("Query must be at least $MIN_QUERY_LENGTH characters")
            )
            query.length > MAX_QUERY_LENGTH -> Result.failure(
                NominatimException.InvalidQueryException("Query must be less than $MAX_QUERY_LENGTH characters")
            )
            else -> Result.success(query.trim())
        }
    }

    /**
     * Handles API exceptions and converts them to appropriate NominatimExceptions.
     */
    private fun handleApiException(e: Exception, operation: String): NominatimException {
        Log.e(TAG, "Error during $operation", e)

        return when (e) {
            is UnknownHostException -> NominatimException.NetworkException(
                "No internet connection available", e
            )
            is SocketTimeoutException -> NominatimException.TimeoutException(
                "Request timed out. Please check your connection", e
            )
            is TimeoutCancellationException -> NominatimException.TimeoutException(
                "Operation timed out", e
            )
            is SSLException -> NominatimException.NetworkException(
                "Secure connection failed", e
            )
            is IOException -> NominatimException.NetworkException(
                "Network error occurred", e
            )
            is NominatimException -> e
            else -> NominatimException.ApiException(
                "Unexpected error: ${e.message}", e
            )
        }
    }

    /**
     * Searches for locations based on a query with comprehensive error handling.
     */
    suspend fun searchLocation(query: String): Result<List<NominatimPlace>> {
        return try {
            // Validate query
            val validatedQuery = validateQuery(query).getOrElse { exception ->
                return Result.failure(exception)
            }

            // Make API call with timeout
            val places = withTimeout(requestTimeoutMs) {
                api.searchPlaces(query = validatedQuery, limit = DEFAULT_SEARCH_LIMIT)
            }

            // Validate response data
            val validPlaces = places.filter { place ->
                if (!place.hasValidCoordinates()) {
                    Log.w(TAG, "Skipping place with invalid coordinates: ${place.displayName}")
                    false
                } else {
                    true
                }
            }

            Log.d(TAG, "Successfully found ${validPlaces.size} valid places for query: $validatedQuery")
            Result.success(validPlaces)

        } catch (e: Exception) {
            val nominatimException = handleApiException(e, "searchLocation")
            Result.failure(nominatimException)
        }
    }

    /**
     * Fetches nearby attractions with enhanced filtering and error handling.
     */
    suspend fun getNearbyAttractions(query: String): Result<List<Attraction>> {
        return try {
            // Validate query
            val validatedQuery = validateQuery(query).getOrElse { exception ->
                return Result.failure(exception)
            }

            // Enhanced query for better attraction results
            val attractionQuery = "$validatedQuery tourist attraction"

            // Make API call with timeout
            val places = withTimeout(requestTimeoutMs) {
                api.searchPlaces(query = attractionQuery, limit = DEFAULT_SEARCH_LIMIT)
            }

            // Filter and map to attractions
            val attractions = places.mapNotNull { place ->
                try {
                    mapPlaceToAttraction(place)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to map place to attraction: ${place.displayName}", e)
                    null
                }
            }
                .distinctBy { it.name }
                .take(DEFAULT_SEARCH_LIMIT)

            Log.d(TAG, "Successfully found ${attractions.size} attractions for query: $validatedQuery")
            Result.success(attractions)

        } catch (e: Exception) {
            val nominatimException = handleApiException(e, "getNearbyAttractions")
            Result.failure(nominatimException)
        }
    }

    /**
     * Maps a NominatimPlace to an Attraction with validation.
     */
    private fun mapPlaceToAttraction(place: NominatimPlace): Attraction? {
        // Validate required fields
        if (place.displayName.isBlank()) {
            Log.w(TAG, "Skipping place with blank display name")
            return null
        }

        // Extract name and location
        val nameParts = place.displayName.split(",")
        val name = nameParts.firstOrNull()?.trim()

        if (name.isNullOrBlank()) {
            Log.w(TAG, "Skipping place with invalid name extraction")
            return null
        }

        // Check if it's actually an attraction
        if (!place.isMajorAttraction()) {
            Log.d(TAG, "Skipping non-attraction place: $name")
            return null
        }

        return try {
            Attraction(
                name = name,
                location = place.displayName,
                score = place.importance,
                latitude = place.getLatitudeAsDouble(),
                longitude = place.getLongitudeAsDouble(),
                type = place.type
            )
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Failed to create attraction due to validation error: ${e.message}")
            null
        }
    }

    /**
     * Reverse geocoding with error handling.
     */
    suspend fun reverseGeocode(latitude: Double, longitude: Double): Result<NominatimPlace> {
        return try {
            // Validate coordinates
            if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
                return Result.failure(
                    NominatimException.InvalidQueryException("Invalid coordinates: lat=$latitude, lon=$longitude")
                )
            }

            val place = withTimeout(requestTimeoutMs) {
                api.reverseGeocode(latitude, longitude)
            }

            if (!place.hasValidCoordinates()) {
                return Result.failure(
                    NominatimException.DataValidationException("Received invalid coordinate data from API")
                )
            }

            Log.d(TAG, "Successfully reverse geocoded coordinates: ($latitude, $longitude)")
            Result.success(place)

        } catch (e: Exception) {
            val nominatimException = handleApiException(e, "reverseGeocode")
            Result.failure(nominatimException)
        }
    }
}