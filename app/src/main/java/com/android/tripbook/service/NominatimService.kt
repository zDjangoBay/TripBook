package com.android.tripbook.service


import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class Attraction(
    val name: String,
    val location: String
)

data class NominatimPlace(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String,
    @SerializedName("type") val type: String?
)

interface NominatimApi {
    @GET("search")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("featuretype") featureType: String = "monument,viewpoint,tourist_attraction"
    ): List<NominatimPlace>
}

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