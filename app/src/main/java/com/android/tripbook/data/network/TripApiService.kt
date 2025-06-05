package com.android.tripbook.data.network

import com.android.tripbook.data.model.TripScheduleRequest
import com.android.tripbook.data.model.TripScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service interface for TripBook API calls.
 */
interface TripApiService {

    /**
     * Schedules a new trip.
     * Placeholder endpoint - replace "/trips/schedule" with the actual endpoint.
     */
    @POST("/trips/schedule") // Placeholder endpoint
    suspend fun scheduleTrip(@Body tripRequest: TripScheduleRequest): Response<TripScheduleResponse>

    // Add other API endpoints here as needed (e.g., get trips, update trip, etc.)

}
