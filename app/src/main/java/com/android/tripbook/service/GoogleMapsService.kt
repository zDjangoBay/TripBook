package com.android.tripbook.service

// Import necessary Android system classes for context, permissions, and location services.
import android.Manifest // Used for checking location permissions.
import android.content.Context // Provides access to application-specific resources and classes.
import android.content.pm.PackageManager // Manages package information and permissions.
import androidx.core.content.ContextCompat // Utility for accessing resources and checking permissions.

// Import custom model classes.
import com.android.tripbook.model.Location // Your app's custom Location data model.

// Import Google Play Services location and maps utilities.
import com.google.android.gms.location.* // FusedLocationProviderClient for device location.
import com.google.android.gms.maps.model.LatLng // Geographic coordinates (latitude and longitude).

// Import Google Places API specific classes.
import com.google.android.libraries.places.api.Places // Main entry point for Places SDK.
import com.google.android.libraries.places.api.model.AutocompletePrediction // Represents a single autocomplete suggestion.
import com.google.android.libraries.places.api.model.AutocompleteSessionToken // Token for billing autocomplete sessions.
import com.google.android.libraries.places.api.model.Place // Represents a Place object.
import com.google.android.libraries.places.api.net.* // Network clients for Places API.

// Import Kotlin Coroutines for asynchronous operations.
import kotlinx.coroutines.Dispatchers // Standard dispatchers for coroutines.
import kotlinx.coroutines.withContext // Used to switch context for coroutine execution.

// Import Kotlinx Serialization for JSON parsing.
import kotlinx.serialization.Serializable // Annotation to mark classes for serialization.
import kotlinx.serialization.json.Json // Main JSON serialization/deserialization class.

// Import OkHttp for HTTP networking.
import okhttp3.OkHttpClient // HTTP client for making network requests.

// Import standard Java networking and utility classes.
import java.net.HttpURLConnection // For making HTTP connections.
import java.net.URL // Represents a Uniform Resource Locator.
import java.net.URLEncoder // For URL encoding strings.
import kotlin.coroutines.resume // For resuming coroutines.
import kotlin.coroutines.resumeWithException // For resuming coroutines with an exception.
import kotlin.coroutines.suspendCoroutine // For converting callback-based APIs to suspend functions.
import android.annotation.SuppressLint // Used to suppress lint warnings.

// Define a constant for the Google Places Text Search API endpoint.
// This is the base URL for searching places based on text input.
private const val PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json"

// --- Data classes for Google Places API responses ---
// These models represent the structure of data received from the Google Places API.

/**
 * Represents a simplified Place search result from the API.
 * This class is used for internal mapping and easier consumption within the app.
 */
@Serializable
data class PlaceResult(
    val placeId: String = "", // Unique identifier for the place.
    val name: String, // The human-readable name of the place.
    val address: String, // The full address of the place.
    val types: List<String> = emptyList(), // A list of types describing the nature of the place.
    val rating: Double? = null, // The user-aggregated rating of the place.
    val priceLevel: Int? = null, // The price level of the place, from 0 (free) to 4 (expensive).
    val photoReference: String? = null, // A reference to a photo of the place.
    val geometry: Geometry? = null // Geographic information about the place.
)

/**
 * Represents detailed information about a specific Place.
 * This can include phone number, website, reviews, and opening hours.
 */
@Serializable
data class PlaceDetails(
    val placeId: String, // The unique ID of the place.
    val name: String, // The name of the place.
    val address: String, // The formatted address.
    val formattedPhoneNumber: String? = null, // The place's phone number.
    val website: String? = null, // The place's official website.
    val rating: Double? = null, // The overall user rating.
    val priceLevel: Int? = null, // The price level.
    val reviews: List<Review> = emptyList(), // A list of user reviews.
    val photos: List<Photo> = emptyList(), // A list of photo references.
    val openingHours: OpeningHours? = null, // Information about the place's opening hours.
    val geometry: Geometry? = null, // Geographical data.
    val types: List<String> = emptyList() // Types of the place.
)

/**
 * Encapsulates geographic information for a place, including its coordinates and viewport.
 */
@Serializable
data class Geometry(
    val location: GeoLocation, // The latitude and longitude of the place.
    val viewport: Viewport? = null // The recommended viewport for displaying the place.
)

/**
 * Represents a geographic location with latitude and longitude.
 * This is a fundamental coordinate pair.
 */
@Serializable
data class GeoLocation(
    val lat: Double, // Latitude component of the coordinates.
    val lng: Double // Longitude component of the coordinates.
) {
    // Helper function to convert this GeoLocation to a Google Maps LatLng object.
    fun toLatLng() = LatLng(lat, lng)
}

/**
 * Defines the bounding box for a map viewport.
 * Useful for framing a location on a map.
 */
@Serializable
data class Viewport(
    val northeast: GeoLocation, // The northeast corner of the viewport.
    val southwest: GeoLocation // The southwest corner of the viewport.
)

/**
 * Represents a single user review for a place.
 */
@Serializable
data class Review(
    val authorName: String, // The name of the review's author.
    val rating: Int, // The rating given by the author (e.g., 1 to 5).
    val text: String, // The full text of the review.
    val time: Long // Timestamp of when the review was written.
)

/**
 * Represents a photo associated with a place.
 * Includes reference for fetching the actual image.
 */
@Serializable
data class Photo(
    val photoReference: String, // Unique identifier for the photo.
    val height: Int, // Height of the photo in pixels.
    val width: Int // Width of the photo in pixels.
)

/**
 * Provides information about a place's opening hours.
 */
@Serializable
data class OpeningHours(
    val openNow: Boolean, // Indicates if the place is currently open.
    val weekdayText: List<String> = emptyList() // Formatted opening hours for each weekday.
)

// --- Distance Matrix API data classes ---
// These models are for parsing responses from the Google Distance Matrix API.

/**
 * Represents the main response structure from the Distance Matrix API.
 * Contains origins, destinations, and calculated elements (distance/duration).
 */
@Serializable
data class DistanceMatrixResult(
    val origins: List<String>, // List of origin addresses.
    val destinations: List<String>, // List of destination addresses.
    val elements: List<List<DistanceElement>> // Matrix of distance and duration elements.
)

/**
 * Represents a single element in the distance matrix.
 * Includes distance, duration, and status for a specific origin-destination pair.
 */
@Serializable
data class DistanceElement(
    val distance: DistanceInfo?, // Distance information.
    val duration: DurationInfo?, // Duration information.
    val status: String // Status of the calculation (e.g., "OK").
)

/**
 * Detailed information about a calculated distance.
 */
@Serializable
data class DistanceInfo(
    val text: String, // Human-readable distance (e.g., "10 km").
    val value: Int // Distance in meters.
)

/**
 * Detailed information about a calculated duration.
 */
@Serializable
data class DurationInfo(
    val text: String, // Human-readable duration (e.g., "15 mins").
    val value: Int // Duration in seconds.
)

// --- Location result for geolocation ---

/**
 * Represents a simplified result of a device's current location.
 * Provides latitude, longitude, and accuracy.
 */
data class LocationResult(
    val latitude: Double, // The latitude of the device's location.
    val longitude: Double, // The longitude of the device's location.
    val accuracy: Float, // The estimated accuracy of this location, in meters.
    val address: String? = null // Optional: a human-readable address for the location.
)

// --- Private API response data classes ---
// These internal models mirror the API's JSON structure, often using snake_case,
// and are then mapped to the public-facing data classes.

/**
 * Internal model for the raw Places Text Search API response.
 */
@Serializable
private data class PlacesSearchResponse(
    val results: List<PlaceResultApi> = emptyList(), // List of raw place results.
    val status: String, // Status of the API request (e.g., "OK", "ZERO_RESULTS").
    val errorMessage: String? = null // Detailed error message if status is not "OK".
)

/**
 * Internal model for the raw Place Details API response.
 */
@Serializable
private data class PlaceDetailsResponse(
    val result: PlaceDetailsApi? = null, // The raw detailed place result.
    val status: String, // Status of the API request.
    val errorMessage: String? = null // Error message.
)

/**
 * Internal model for the raw Distance Matrix API response.
 */
@Serializable
private data class DistanceMatrixResponse(
    val destination_addresses: List<String> = emptyList(), // Raw destination addresses.
    val origin_addresses: List<String> = emptyList(), // Raw origin addresses.
    val rows: List<DistanceMatrixRow> = emptyList(), // Raw matrix rows.
    val status: String // Status of the API request.
)

/**
 * Internal representation of a row in the Distance Matrix response.
 */
@Serializable
private data class DistanceMatrixRow(
    val elements: List<DistanceElementApi> = emptyList() // Raw distance elements within the row.
)

/**
 * Internal representation of a distance element from the API.
 */
@Serializable
private data class DistanceElementApi(
    val distance: DistanceInfoApi? = null, // Raw distance info.
    val duration: DurationInfoApi? = null, // Raw duration info.
    val status: String = "" // Status for this element.
)

/**
 * Internal representation of distance information from the API.
 */
@Serializable
private data class DistanceInfoApi(
    val text: String = "", // Raw text representation of distance.
    val value: Int = 0 // Raw numeric value of distance.
)

/**
 * Internal representation of duration information from the API.
 */
@Serializable
private data class DurationInfoApi(
    val text: String = "", // Raw text representation of duration.
    val value: Int = 0 // Raw numeric value of duration.
)

/**
 * Internal representation of a Place Result from the API (snake_case fields).
 */
@Serializable
private data class PlaceResultApi(
    val place_id: String = "", // API's place ID.
    val name: String = "", // API's place name.
    val formatted_address: String = "", // API's formatted address.
    val types: List<String> = emptyList(), // API's types.
    val rating: Double? = null, // API's rating.
    val price_level: Int? = null, // API's price level.
    val photos: List<PhotoApi> = emptyList(), // API's photo references.
    val geometry: GeometryApi? = null // API's geometry.
)

/**
 * Internal representation of Place Details from the API (snake_case fields).
 */
@Serializable
private data class PlaceDetailsApi(
    val place_id: String = "", // API's place ID.
    val name: String = "", // API's place name.
    val formatted_address: String = "", // API's formatted address.
    val formatted_phone_number: String? = null, // API's phone number.
    val website: String? = null, // API's website.
    val rating: Double? = null, // API's rating.
    val price_level: Int? = null, // API's price level.
    val reviews: List<ReviewApi> = emptyList(), // API's reviews.
    val photos: List<PhotoApi> = emptyList(), // API's photos.
    val opening_hours: OpeningHoursApi? = null, // API's opening hours.
    val geometry: GeometryApi? = null, // API's geometry.
    val types: List<String> = emptyList() // API's types.
)

/**
 * Internal representation of Geometry from the API.
 */
@Serializable
private data class GeometryApi(
    val location: GeoLocationApi, // Raw location coordinates.
    val viewport: ViewportApi? = null // Raw viewport information.
)

/**
 * Internal representation of GeoLocation from the API.
 */
@Serializable
private data class GeoLocationApi(
    val lat: Double, // Raw latitude.
    val lng: Double // Raw longitude.
)

/**
 * Internal representation of Viewport from the API.
 */
@Serializable
private data class ViewportApi(
    val northeast: GeoLocationApi, // Raw northeast coordinates.
    val southwest: GeoLocationApi // Raw southwest coordinates.
)

/**
 * Internal representation of a Review from the API.
 */
@Serializable
private data class ReviewApi(
    val author_name: String = "", // Raw author name.
    val rating: Int = 0, // Raw rating.
    val text: String = "", // Raw review text.
    val time: Long = 0 // Raw timestamp.
)

/**
 * Internal representation of a Photo from the API.
 */
@Serializable
private data class PhotoApi(
    val photo_reference: String = "", // Raw photo reference.
    val height: Int = 0, // Raw photo height.
    val width: Int = 0 // Raw photo width.
)

/**
 * Internal representation of Opening Hours from the API.
 */
@Serializable
private data class OpeningHoursApi(
    val open_now: Boolean = false, // Raw open now status.
    val weekday_text: List<String> = emptyList() // Raw weekday text.
)

/**
 * Comprehensive Google Maps API Integration Service.
 * This class provides methods to interact with various Google Maps Platform APIs.
 * It handles:
 * - Geolocation (getting current device location).
 * - Google Places API (searching for places, getting details, autocomplete).
 * - Potentially other map-related functionalities (Directions, Distance Matrix, if implemented).
 */
class GoogleMapsService(
    private val context: Context, // Android application context for permissions and SDK initialization.
    private val apiKey: String // Your Google Maps API Key for authenticating requests.
) {
    private val placesClient: PlacesClient // Client for interacting with the Google Places SDK.
    private val fusedLocationClient: FusedLocationProviderClient // Client for accessing device location.
    private val httpClient = OkHttpClient() // HTTP client for custom network requests (e.g., Text Search API).
    private val json = Json {
        ignoreUnknownKeys = true // Ignore JSON fields that are not present in data classes.
        coerceInputValues = true // Coerce numbers/booleans to expected types, if possible.
    }

    init {
        // Initialize the Google Places SDK if it hasn't been already.
        // This is a crucial first step before using any Places API features.
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey) // Initialize with application context and API key.
        }
        // Create the PlacesClient instance.
        placesClient = Places.createClient(context)
        // Create the FusedLocationProviderClient for location services.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    // ===========================================
    // GEOLOCATION SERVICES
    // These functions deal with getting the device's current physical location.
    // ===========================================

    /**
     * Retrieves the current device location.
     * Requires location permissions to be granted by the user.
     * @return [LocationResult] containing latitude, longitude, and accuracy, or null if unavailable.
     */
    @SuppressLint("MissingPermission") // Suppress lint warning, as permission check is done internally.
    suspend fun getCurrentLocation(): LocationResult? {
        // Ensure this operation runs on an I/O optimized thread.
        return withContext(Dispatchers.IO) {
            // Check if the necessary location permissions are already granted.
            if (!hasLocationPermission()) {
                // If permissions are missing, throw a SecurityException.
                throw SecurityException("Location permission not granted")
            }

            // Attempt to get the last known location.
            try {
                // The .await() extension function converts the Task<Location> into a suspendable call.
                val location = fusedLocationClient.lastLocation.await()
                // Map the Android Location object to your custom LocationResult data class.
                location?.let {
                    LocationResult(
                        latitude = it.latitude, // Latitude from the device's location.
                        longitude = it.longitude, // Longitude from the device's location.
                        accuracy = it.accuracy // Accuracy of the location fix.
                    )
                }
            } catch (e: Exception) {
                // Log the exception for debugging purposes if location retrieval fails.
                // For instance, device location might be off, or there's a temporary issue.
                null // Return null to indicate failure.
            }
        }
    }

    /**
     * Checks if the application has either ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission.
     * This is a prerequisite for using location services.
     * @return true if permission is granted, false otherwise.
     */
    private fun hasLocationPermission(): Boolean {
        // Check for ACCESS_FINE_LOCATION (more precise location).
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Check for ACCESS_COARSE_LOCATION (less precise, cell/Wi-Fi based location).
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Return true if either of the location permissions is granted.
        // This simple check ensures basic functionality for location features.
        return fineLocationGranted || coarseLocationGranted
    }

    // ===========================================
    // PLACES API SERVICES
    // These functions interact with the Google Places API for finding information about places.
    // ===========================================

    /**
     * Fetches autocomplete suggestions for a given text query.
     * Useful for type-ahead search experiences.
     * @param query The partial text string entered by the user.
     * @return A list of [AutocompletePrediction] objects.
     */
    suspend fun getPlaceSuggestions(query: String): List<AutocompletePrediction> {
        // Perform network operation on an I/O dispatcher.
        return withContext(Dispatchers.IO) {
            // Create a new session token for autocomplete requests.
            // Session tokens help with billing and improve request quality.
            val token = AutocompleteSessionToken.newInstance()
            // Build the autocomplete prediction request.
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query) // Set the user's input query.
                .setSessionToken(token) // Attach the session token.
                .build()

            // Execute the request and handle potential exceptions.
            try {
                // Await the response from the Places client.
                val response = placesClient.findAutocompletePredictions(request).await()
                // Return the list of autocomplete predictions.
                response.autocompletePredictions
            } catch (e: Exception) {
                // Log any errors that occur during the autocomplete request.
                // This could be network issues, API key problems, etc.
                emptyList() // Return an empty list in case of error.
            }
        }
    }

    /**
     * Retrieves detailed information for a specific place using its Place ID.
     * This leverages the Google Places SDK.
     * @param placeId The unique identifier of the place.
     * @return [Location] object with detailed information, or null if not found/error.
     */
    suspend fun getPlaceDetails(placeId: String): Location? {
        // Ensure this operation runs on a background thread.
        return withContext(Dispatchers.IO) {
            // Define the specific fields of the Place object that you want to retrieve.
            // Requesting only necessary fields helps reduce billing costs.
            val placeFields = listOf(
                Place.Field.ID, // The unique ID of the place.
                Place.Field.NAME, // The name of the place.
                Place.Field.LAT_LNG, // Latitude and longitude coordinates.
                Place.Field.ADDRESS // The formatted address.
            )

            // Create a request to fetch place details using the specified place ID and fields.
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            // Execute the request and handle potential exceptions.
            try {
                // Await the response from the Places client.
                val response = placesClient.fetchPlace(request).await()
                val place = response.place // Extract the Place object from the response.
                val latLng = place.latLng // Get the LatLng object.

                // If LatLng is available, construct and return your custom Location model.
                if (latLng != null) {
                    Location(
                        latitude = latLng.latitude, // Extract latitude.
                        longitude = latLng.longitude, // Extract longitude.
                        address = place.address ?: "", // Use the address, or an empty string if null.
                        placeId = place.id ?: "", // Use the place ID, or an empty string if null.
                        name = place.name ?: "" // Use the name, or an empty string if null.
                        // Other fields from your Location model are default or can be fetched if needed.
                    )
                } else {
                    null // Return null if LatLng is not available.
                }
            } catch (e: Exception) {
                // Log any exceptions that occur during the fetching of place details.
                // This might include network errors or invalid place IDs.
                null // Return null in case of error.
            }
        }
    }

    /**
     * Performs a text search for places using a custom HTTP request to the Places Text Search API.
     * This allows for more direct control over the query parameters compared to the SDK's search.
     * @param query The text string to search for (e.g., "restaurants in Douala").
     * @param type Optional: restricts results to a specific type of place (e.g., "restaurant", "hospital").
     * @param location Optional: specifies a latitude,longitude for biasing results.
     * @param radius Optional: defines a radius in meters around the location to bias results.
     * @return A list of [PlaceResult] objects.
     * @throws Exception if the API call fails or returns an error status.
     */
    suspend fun searchPlaces(
        query: String, // The main search query string.
        type: String? = null, // Optional type filter.
        location: String? = null, // Optional location for biasing.
        radius: Int = 50000 // Optional radius for location biasing.
    ): List<PlaceResult> = withContext(Dispatchers.IO) {
        try {
            // URL encode the query to handle spaces and special characters.
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            // Construct the base URL for the text search request.
            var urlString = "$PLACES_SEARCH_URL?query=$encodedQuery&key=$apiKey"

            // Append optional parameters if they are provided.
            // URL encode each optional parameter as well.
            type?.let { urlString += "&type=${URLEncoder.encode(it, "UTF-8")}" }
            location?.let { urlString += "&location=${URLEncoder.encode(it, "UTF-8")}&radius=$radius" }

            // Create a URL object from the constructed string.
            val url = URL(urlString)
            // Open a connection to the URL.
            val connection = url.openConnection() as HttpURLConnection

            // Configure the HTTP connection properties.
            connection.apply {
                requestMethod = "GET" // Use GET method for fetching data.
                connectTimeout = 10000 // Set connection timeout to 10 seconds.
                readTimeout = 10000 // Set read timeout to 10 seconds.
            }

            // Get the HTTP response code.
            val responseCode = connection.responseCode
            // Check if the response was successful (HTTP 200 OK).
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the entire response body into a string.
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                // Deserialize the JSON response into the PlacesSearchResponse data class.
                val placesResponse = json.decodeFromString<PlacesSearchResponse>(response)

                // Check the status field within the JSON response.
                if (placesResponse.status == "OK") {
                    // Map the internal API models to the public PlaceResult models.
                    return@withContext placesResponse.results.map { it.toPlaceResult() }
                } else {
                    // If the API status is not OK, throw an exception with the error message.
                    throw Exception("Places API error: ${placesResponse.status} - ${placesResponse.errorMessage}")
                }
            } else {
                // If the HTTP response code is not OK, throw an exception.
                throw Exception("HTTP error: $responseCode")
            }
        } catch (e: Exception) {
            // Catch any exceptions that occur during the process (e.g., network errors, JSON parsing errors).
            // Re-throw a new exception with a more descriptive message.
            throw Exception("Failed to search places: ${e.message}", e)
        }
    }

    /**
     * Searches for nearby tourist attractions around a specific geographic location.
     * This is a specialized call to `searchPlaces` with pre-defined parameters.
     * @param latitude The latitude of the center point for the search.
     * @param longitude The longitude of the center point for the search.
     * @param radius The search radius in meters (defaulting to 5000m).
     * @param type The type of place to search for (defaulting to "tourist_attraction").
     * @return A list of [PlaceResult] objects representing nearby attractions.
     */
    suspend fun searchNearbyAttractions(
        latitude: Double, // Latitude of the search center.
        longitude: Double, // Longitude of the search center.
        radius: Int = 5000, // Radius in meters for the search.
        type: String = "tourist_attraction" // Specific type of attraction to look for.
    ): List<PlaceResult> {
        // Format the location coordinates into a string suitable for the API.
        val location = "$latitude,$longitude"
        // Delegate the actual search to the general `searchPlaces` function.
        return searchPlaces(
            query = type, // Use the type as the query.
            location = location, // Set the location for biasing.
            radius = radius, // Set the search radius.
            type = type // Apply the type filter.
        )
    }

    /**
     * Searches for nearby restaurants around a specific geographic location.
     * Another specialized `searchPlaces` call.
     * @param latitude The latitude of the center point.
     * @param longitude The longitude of the center point.
     * @param radius The search radius in meters (defaulting to 2000m).
     * @return A list of [PlaceResult] objects representing nearby restaurants.
     */
    suspend fun searchNearbyRestaurants(
        latitude: Double, // Latitude for the restaurant search.
        longitude: Double, // Longitude for the restaurant search.
        radius: Int = 2000 // Search radius for restaurants.
    ): List<PlaceResult> {
        // Prepare the location string.
        val location = "$latitude,$longitude"
        // Call the generic search function, specifying "restaurant" type.
        return searchPlaces(
            query = "restaurant", // General query for restaurants.
            location = location, // Central location for the search.
            radius = radius, // Search radius.
            type = "restaurant" // Explicitly filter by restaurant type.
        )
    }

    /**
     * Searches for nearby gas stations around a specific geographic location.
     * Yet another specialized `searchPlaces` call.
     * @param latitude The latitude of the center point.
     * @param longitude The longitude of the center point.
     * @param radius The search radius in meters (defaulting to 5000m).
     * @return A list of [PlaceResult] objects representing nearby gas stations.
     */
    suspend fun searchNearbyGasStations(
        latitude: Double, // Latitude for gas station search.
        longitude: Double, // Longitude for gas station search.
        radius: Int = 5000 // Search radius for gas stations.
    ): List<PlaceResult> {
        // Prepare the location string.
        val location = "$latitude,$longitude"
        // Call the generic search function, specifying "gas station" type.
        return searchPlaces(
            query = "gas station", // General query for gas stations.
            location = location, // Central location for the search.
            radius = radius, // Search radius.
            type = "gas_station" // Explicitly filter by gas_station type.
        )
    }

    // ===========================================
    // EXTENSION FUNCTIONS FOR API MODEL MAPPING
    // These functions convert internal API response models (snake_case)
    // to cleaner, more idiomatic Kotlin data models (camelCase) for app consumption.
    // ===========================================

    /**
     * Converts a raw [PlaceResultApi] from the API response to a clean [PlaceResult] model.
     * This bridges the gap between API's JSON structure and app's data structure.
     */
    private fun PlaceResultApi.toPlaceResult(): PlaceResult {
        return PlaceResult(
            placeId = place_id, // Map place_id to placeId.
            name = name, // Map name.
            address = formatted_address, // Map formatted_address to address.
            types = types, // Map types.
            rating = rating, // Map rating.
            priceLevel = price_level, // Map price_level to priceLevel.
            photoReference = photos.firstOrNull()?.photo_reference, // Get the first photo reference if available.
            geometry = geometry?.toGeometry() // Recursively map geometry.
        )
    }

    /**
     * Converts a raw [PlaceDetailsApi] to a clean [PlaceDetails] model.
     * This is for detailed place information.
     */
    private fun PlaceDetailsApi.toPlaceDetails(): PlaceDetails {
        return PlaceDetails(
            placeId = place_id, // Map place_id.
            name = name, // Map name.
            address = formatted_address, // Map formatted_address.
            formattedPhoneNumber = formatted_phone_number, // Map formatted_phone_number.
            website = website, // Map website.
            rating = rating, // Map rating.
            priceLevel = price_level, // Map price_level.
            reviews = reviews.map { it.toReview() }, // Map list of reviews.
            photos = photos.map { it.toPhoto() }, // Map list of photos.
            openingHours = opening_hours?.toOpeningHours(), // Map opening_hours.
            geometry = geometry?.toGeometry(), // Map geometry.
            types = types // Map types.
        )
    }

    /**
     * Converts a raw [GeometryApi] to a clean [Geometry] model.
     */
    private fun GeometryApi.toGeometry(): Geometry {
        return Geometry(
            location = GeoLocation(location.lat, location.lng), // Map location coordinates.
            viewport = viewport?.let { // Map viewport if available.
                Viewport(
                    northeast = GeoLocation(it.northeast.lat, it.northeast.lng), // Map northeast coordinates.
                    southwest = GeoLocation(it.southwest.lat, it.southwest.lng) // Map southwest coordinates.
                )
            }
        )
    }

    /**
     * Converts a raw [ReviewApi] to a clean [Review] model.
     */
    private fun ReviewApi.toReview(): Review {
        return Review(
            authorName = author_name, // Map author_name.
            rating = rating, // Map rating.
            text = text, // Map text.
            time = time // Map time.
        )
    }

    /**
     * Converts a raw [PhotoApi] to a clean [Photo] model.
     */
    private fun PhotoApi.toPhoto(): Photo {
        return Photo(
            photoReference = photo_reference, // Map photo_reference.
            height = height, // Map height.
            width = width // Map width.
        )
    }

    /**
     * Converts a raw [OpeningHoursApi] to a clean [OpeningHours] model.
     */
    private fun OpeningHoursApi.toOpeningHours(): OpeningHours {
        return OpeningHours(
            openNow = open_now, // Map open_now.
            weekdayText = weekday_text // Map weekday_text.
        )
    }
}

// --- Extension for await() function ---
// This utility function converts a Google Play Services Task<T> into a suspendable function,
// allowing it to be used seamlessly with Kotlin coroutines.
/**
 * Extension function to convert a Google Play Services [com.google.android.gms.tasks.Task]
 * into a suspendable coroutine, simplifying asynchronous operations.
 * @return The result of the task.
 * @throws Exception if the task fails.
 */
private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    // Suspend the current coroutine until the task completes.
    return suspendCoroutine { continuation ->
        // Add a listener to the task to handle its completion.
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // If the task completed successfully, resume the coroutine with the task's result.
                continuation.resume(task.result)
            } else {
                // If the task failed, resume the coroutine by throwing the task's exception.
                // Provide a generic exception if task.exception is null.
                continuation.resumeWithException(task.exception ?: Exception("Unknown error"))
            }
        }
    }
}