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
    @SerializedName("type") val type: String?,
    @SerializedName("class") val placeClass: String? // Add class field for better filtering
)

// Retrofit interface for Nominatim API interactions.
interface NominatimApi {
    @GET("search")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("addressdetails") addressDetails: Int = 1 // Get address details
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
            api.searchPlaces(query = query)
        } catch (e: Exception) {
            Log.e("NominatimService", "Error searching location for query: $query", e)
            emptyList()
        }
    }

    // Fetches nearby attractions based on a query.
    suspend fun getNearbyAttractions(query: String): List<Attraction> {
        return try {
            val places = api.searchPlaces(query = "$query tourist attraction")

            places.mapNotNull { place ->
                val name = place.displayName.split(",")[0].trim()
                val location = place.displayName

                // Filter based on both 'type' and 'class' fields from Nominatim response
                val isAttraction = when {
                    place.type in listOf(
                        "monument", "viewpoint", "tourist_attraction", "attraction",
                        "park", "heritage", "museum", "artwork", "memorial", "castle"
                    ) -> true
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
            Log.e("NominatimService", "Error getting nearby attractions for query: $query", e)
            emptyList()
        }
    }
}