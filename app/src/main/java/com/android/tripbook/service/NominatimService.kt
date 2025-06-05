package com.android.tripbook.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 10
    ): List<NominatimPlace>
}

data class NominatimPlace(
    val place_id: Long,
    val licence: String,
    val osm_type: String,
    val osm_id: Long,
    val boundingbox: List<String>,
    val lat: String,
    val lon: String,
    val display_name: String,
    val class_: String,
    val type: String,
    val importance: Double,
    val icon: String?
) {
    val displayName: String
        get() = display_name
}

data class Attraction(
    val name: String,
    val location: String
)

class NominatimService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://nominatim.openstreetmap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(NominatimApi::class.java)

    suspend fun getNearbyAttractions(query: String): List<Attraction> {
        return try {
            val places = api.searchPlaces(query = "$query tourist attraction")
            places.mapNotNull { place ->
                val name = place.displayName.split(",")[0].trim()
                val location = place.displayName
                if (name.isNotEmpty() && location.isNotEmpty() && place.type in listOf("monument", "viewpoint", "tourist_attraction")) {
                    Attraction(name, location)
                } else null
            }.distinctBy { it.name }.take(5)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
