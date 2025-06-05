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

data class Attraction(
    val name: String,
    val location: String
)


//add the todos for wht was left from yesterday
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

