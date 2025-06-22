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

class GoogleMapsService(private val context: Context, private val apiKey: String) {
    private val client = OkHttpClient()
    private var placesClient: PlacesClient? = null

    init {
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
        placesClient = Places.createClient(context)
    }

    suspend fun searchPlaces(query: String): List<PlaceResult> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "$PLACES_SEARCH_URL?query=$encodedQuery&key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Search request failed")

                val jsonResponse = response.body?.string() ?: throw Exception("Empty response")
                val searchResponse = Json.decodeFromString<PlacesSearchResponse>(jsonResponse)

                if (searchResponse.status != "OK") {
                    throw Exception(searchResponse.errorMessage ?: "Search failed")
                }

                searchResponse.results.map { it.toPlaceResult() }
            }
        } catch (e: Exception) {
            throw Exception("Failed to search places: ${e.message}")
        }
    }

    suspend fun getPlaceDetails(placeId: String): PlaceDetails = withContext(Dispatchers.IO) {
        try {
            val url = "$PLACE_DETAILS_URL?place_id=$placeId&key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Details request failed")

                val jsonResponse = response.body?.string() ?: throw Exception("Empty response")
                val detailsResponse = Json.decodeFromString<PlaceDetailsResponse>(jsonResponse)

                if (detailsResponse.status != "OK") {
                    throw Exception(detailsResponse.errorMessage ?: "Failed to get place details")
                }

                detailsResponse.result?.toPlaceDetails()
                    ?: throw Exception("No details found for place")
            }
        } catch (e: Exception) {
            throw Exception("Failed to get place details: ${e.message}")
        }
    }

    suspend fun getRouteInfo(origin: Location, destination: Location): RouteInfo {
        return withContext(Dispatchers.IO) {
            try {
                val url = buildDirectionsUrl(origin, destination)
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw Exception("Route request failed")

                    val jsonResponse = JSONObject(response.body?.string() ?: throw Exception("Empty response"))
                    if (jsonResponse.getString("status") != "OK") {
                        throw Exception("Failed to get route")
                    }

                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() == 0) throw Exception("No routes found")

                    val route = routes.getJSONObject(0)
                    val leg = route.getJSONArray("legs").getJSONObject(0)

                    RouteInfo(
                        distance = leg.getJSONObject("distance").getString("text"),
                        duration = leg.getJSONObject("duration").getString("text"),
                        polyline = route.getJSONObject("overview_polyline").getString("points")
                    )
                }
            } catch (e: Exception) {
                throw Exception("Failed to get route info: ${e.message}")
            }
        }
    }

    suspend fun getPlaceAutocomplete(query: String): List<AutocompletePrediction> {
        return suspendCoroutine { continuation ->
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build()

            placesClient?.findAutocompletePredictions(request)
                ?.addOnSuccessListener { response ->
                    continuation.resume(response.autocompletePredictions)
                }
                ?.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
                ?: continuation.resumeWithException(Exception("PlacesClient not initialized"))
        }
    }

    private fun buildDirectionsUrl(origin: Location, destination: Location): String {
        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}&" +
                "destination=${destination.latitude},${destination.longitude}&" +
                "key=$apiKey"
    }

    private fun PlaceResultApi.toPlaceResult() = PlaceResult(
        placeId = place_id,
        name = name,
        address = formatted_address,
        types = types,
        rating = rating,
        priceLevel = price_level,
        photoReference = photos.firstOrNull()?.photo_reference,
        geometry = geometry?.toGeometry()
    )

    private fun PlaceDetailsApi.toPlaceDetails() = PlaceDetails(
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

    private fun GeometryApi.toGeometry() = Geometry(
        location = location.toGeoLocation(),
        viewport = viewport?.toViewport()
    )

    private fun GeoLocationApi.toGeoLocation() = GeoLocation(lat, lng)

    private fun ViewportApi.toViewport() = Viewport(
        northeast = northeast.toGeoLocation(),
        southwest = southwest.toGeoLocation()
    )

    private fun ReviewApi.toReview() = Review(
        authorName = author_name,
        rating = rating,
        text = text,
        time = time
    )

    private fun PhotoApi.toPhoto() = Photo(
        photoReference = photo_reference,
        height = height,
        width = width
    )

    private fun OpeningHoursApi.toOpeningHours() = OpeningHours(
        openNow = open_now,
        weekdayText = weekday_text
    )

    companion object {
        private const val TAG = "GoogleMapsService"
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