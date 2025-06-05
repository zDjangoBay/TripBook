package com.android.tripbook.service

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.util.Log // Import Log for logging exceptions

// Data class for a simplified Attraction, as before.
data class Attraction(
    val name: String,
    val location: String
)

// Data class for Nominatim API response, adjusting for potential type granularity.
data class NominatimPlace(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String,
    @SerializedName("type") val type: String? // Keep type, but be aware of its potential granularity
)

// Retrofit interface for Nominatim API interactions.
interface NominatimApi {
    @GET("search")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("extratags") extraTags: Int = 1 // Request extra tags for more detailed information
        // Removed 'featureType' as it's not a standard Nominatim parameter
    ): List<NominatimPlace>
}

// Interceptor to add User-Agent header to all Nominatim requests.
class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithUserAgent = originalRequest.newBuilder()
            .header("User-Agent", "TripBookAndroidApp/1.0 (contact@example.com)") // Replace with your app name and contact
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}

class NominatimService {

    private val api: NominatimApi

    init {
        // Configure OkHttpClient with the User-Agent interceptor.
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor())
            .build()

        // Build Retrofit instance using the configured OkHttpClient.
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(okHttpClient) // Set the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NominatimApi::class.java)
    }

    // Searches for locations based on a query.
    suspend fun searchLocation(query: String): List<NominatimPlace> {
        return try {
            api.searchPlaces(query = query, extratags = 1) // Pass extratags here
        } catch (e: Exception) {
            Log.e("NominatimService", "Error searching location for query: $query", e) // Log the exception
            emptyList()
        }
    }

    // Fetches nearby attractions based on a query.
    suspend fun getNearbyAttractions(query: String): List<Attraction> {
        return try {
            val places = api.searchPlaces(query = "$query tourist attraction", extratags = 1)

            places.mapNotNull { place ->
                val name = place.displayName.split(",")[0].trim()
                val location = place.displayName
                // Filter based on the 'type' field from Nominatim response.
                // Keep the existing filtering logic, as Nominatim's 'type' can still be useful.
                // The teacher's comment about granularity means 'type' might be broader,
                // so consider if more precise filtering is needed based on your UI requirements.
                if (name.isNotEmpty() && location.isNotEmpty() &&
                    place.type in listOf("monument", "viewpoint", "tourist_attraction", "attraction", "tourism", "park", "heritage", "museum", "artwork")) { // Expanded types for broader matching
                    Attraction(name, location)
                } else null
            }.distinctBy { it.name }.take(5)
        } catch (e: Exception) {
            Log.e("NominatimService", "Error getting nearby attractions for query: $query", e) // Log the exception
            emptyList()
        }
    }
}