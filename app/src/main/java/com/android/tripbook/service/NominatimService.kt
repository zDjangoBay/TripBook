package com.android.tripbook.service

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.util.Log // ADDED: Import Log for logging exceptions (This was requested requested by teacher)

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
    @SerializedName("type") val type: String?, // NOTED: Teacher mentioned type can be more granular
    @SerializedName("class") val placeClass: String? // ADDED: Additional field for better filtering
)

// Retrofit interface for Nominatim API interactions.
interface NominatimApi {
    @GET("search")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("extratags") extraTags: Int = 1, // FIXED: Changed from "featuretype" to "extratags" as teacher suggested
        @Query("addressdetails") addressDetails: Int = 1 // ADDED: For more detailed location information
    ): List<NominatimPlace>
}

// ADDED: Interceptor to add User-Agent header to all Nominatim requests (as required by teacher).
// This prevents HTTP 403 errors that Nominatim can throw when no User-Agent is provided.
class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithUserAgent = originalRequest.newBuilder()
            .header("User-Agent", "TripBookAndroidApp/1.0 (contact@example.com)")
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}

class NominatimService {

    private val api: NominatimApi

    init {
        // ADDED: Configure OkHttpClient with the User-Agent interceptor (teacher's requirement).
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor()) // This adds User-Agent header to prevent 403 errors
            .build()

        // MODIFIED: Build Retrofit instance using the configured OkHttpClient.
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(okHttpClient) // ADDED: Set the custom OkHttpClient with User-Agent
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NominatimApi::class.java)
    }

    // Searches for locations based on a query.
    suspend fun searchLocation(query: String): List<NominatimPlace> {
        return try {
            api.searchPlaces(query = query)
        } catch (e: Exception) {
            Log.e("NominatimService", "Error searching location for query: $query", e) // ADDED: Exception logging as teacher suggested
            emptyList()
        }
    }

    // Fetches nearby attractions based on a query.
    suspend fun getNearbyAttractions(query: String): List<Attraction> {
        return try {
            // Build query by appending "tourist attraction" as teacher noted this makes sense
            val places = api.searchPlaces(query = "$query tourist attraction")

            places.mapNotNull { place ->
                val name = place.displayName.split(",")[0].trim()
                val location = place.displayName

                // IMPROVED: Enhanced filtering logic considering teacher's note about type granularity
                // Using both 'type' and 'class' fields for more accurate attraction identification
                val isAttraction = when {
                    // teacher mentioned type field can be granular this is my check for it
                    place.type in listOf(
                        "monument", "viewpoint", "tourist_attraction", "attraction",
                        "tourism", "park", "heritage", "museum", "artwork", "memorial",
                        "castle", "ruins", "gallery"
                    ) -> true
                    // Check class field for broader categorization
                    place.placeClass in listOf(
                        "tourism", "amenity", "historic", "leisure"
                    ) -> true
                    else -> false
                }

                if (name.isNotEmpty() && location.isNotEmpty() && isAttraction) {
                    Attraction(name, location)
                } else null
            }.distinctBy { it.name }.take(5)
        } catch (e: Exception) {
            Log.e("NominatimService", "Error getting nearby attractions for query: $query", e) // ADDED: Exception logging as teacher suggested
            emptyList()
        }
    }
}