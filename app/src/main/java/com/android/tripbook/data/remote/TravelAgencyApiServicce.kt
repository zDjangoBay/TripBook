package com.android.tripbook.data.remote


import com.android.tripbook.data.model.Agency
import retrofit2.http.GET
import retrofit2.http.Query 
import retrofit2.http.Path
import retrofit2.http.POST
import com.android.tripbook.data.model.BookingRequest
import com.android.tripbook.data.model.BookingResponse
import retrofit2.http.Body


interface TravelAgencyApiService {

    @GET("agencies")
    suspend fun getAgencies(
        @Query("minRating") minRating: Double? = null,
        @Query("maxprice") maxPrice: Double? = null,
        @Query("service") service: String? = null,
        @Query("searchQuery") search: String? = null
    ): List<Agency>

    @POST("bookings")
    suspend fun createBookings(
        @Body bookingRequest: BookingRequest
    ): BookingResponse

    @GET("agencies/{id}")
    suspend fun getAgencyById(@Path("id") agencyId: String): Agency
}