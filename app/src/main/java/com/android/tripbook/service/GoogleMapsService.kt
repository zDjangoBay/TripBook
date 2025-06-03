package com.android.tripbook.service
//managing maps services

import android.content.Context
import com.android.tripbook.model.Location
import com.android.tripbook.model.RouteInfo
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
import okhttp3.Request
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json"
private const val PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json"
private const val PLACE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo"

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

class GoogleMapsService(
    private val context: Context,
    private val apiKey: String
) {
    private val placesClient: PlacesClient
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
    }

    // Places API - Get autocomplete suggestions
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

    // Places API - Get place details from place ID
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
                        placeId = place.id ?: ""
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    // Directions API - Get route between two points
    suspend fun getDirections(origin: LatLng, destination: LatLng): RouteInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val originStr = "${origin.latitude},${origin.longitude}"
                val destinationStr = "${destination.latitude},${destination.longitude}"

                val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=$originStr" +
                        "&destination=$destinationStr" +
                        "&key=$apiKey"

                val request = Request.Builder().url(url).build()
                val response = httpClient.newCall(request).execute()
                val jsonResponse = response.body?.string()

                if (jsonResponse != null) {
                    parseDirectionsResponse(jsonResponse)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun parseDirectionsResponse(jsonResponse: String): RouteInfo? {
        return try {
            val json = JSONObject(jsonResponse)
            val routes = json.getJSONArray("routes")

            if (routes.length() > 0) {
                val route = routes.getJSONObject(0)
                val legs = route.getJSONArray("legs")
                val leg = legs.getJSONObject(0)

                val distance = leg.getJSONObject("distance").getString("text")
                val duration = leg.getJSONObject("duration").getString("text")
                val polyline = route.getJSONObject("overview_polyline").getString("points")

                RouteInfo(
                    distance = distance,
                    duration = duration,
                    polyline = polyline
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Utility function to decode polyline for map display
    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
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
     * Get detailed information about a specific place using Web API
     */
    suspend fun getPlaceDetailsWeb(placeId: String): PlaceDetails? = withContext(Dispatchers.IO) {
        try {
            val fields = "place_id,name,formatted_address,formatted_phone_number,website," +
                    "rating,price_level,reviews,photos,opening_hours,geometry,types"
            val urlString = "$PLACE_DETAILS_URL?place_id=$placeId&fields=$fields&key=$apiKey"

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
                val detailsResponse = json.decodeFromString<PlaceDetailsResponse>(response)

                if (detailsResponse.status == "OK" && detailsResponse.result != null) {
                    return@withContext detailsResponse.result.toPlaceDetails()
                } else {
                    throw Exception("Place Details API error: ${detailsResponse.status}")
                }
            } else {
                throw Exception("HTTP error: $responseCode")
            }
        } catch (e: Exception) {
            println("Failed to get place details: ${e.message}")
            return@withContext null
        }
    }

    /**
     * Get photo URL for a place photo reference
     */
    fun getPhotoUrl(photoReference: String, maxWidth: Int = 400): String {
        return "$PLACE_PHOTO_URL?photoreference=$photoReference&maxwidth=$maxWidth&key=$apiKey"
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
     * Search for hotels near a location
     */
    suspend fun searchNearbyHotels(
        latitude: Double,
        longitude: Double,
        radius: Int = 10000
    ): List<PlaceResult> {
        val location = "$latitude,$longitude"
        return searchPlaces(
            query = "hotel",
            location = location,
            radius = radius,
            type = "lodging"
        )
    }

    // Extension functions to convert API models to public models
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