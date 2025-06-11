package com.android.tripbook.shared.service

import com.android.tripbook.shared.model.Location

/**
 * Interface for location search services.
 * Implementations can use different providers like Google Places API, Nominatim, etc.
 */
interface LocationSearchService {

    /**
     * Searches for locations based on a query string.
     *
     * @param query The search query (place name, address, etc.)
     * @param maxResults Maximum number of results to return (default 10)
     * @return List of matching locations
     * @throws LocationSearchException if the search fails
     */
    suspend fun searchLocations(
        query: String,
        maxResults: Int = 10
    ): List<Location>

    /**
     * Gets location details by place ID.
     *
     * @param placeId Unique identifier for the place
     * @return Location details or null if not found
     * @throws LocationSearchException if the request fails
     */
    suspend fun getLocationDetails(placeId: String): Location?

    /**
     * Gets nearby locations around given coordinates.
     *
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @param radiusMeters Search radius in meters
     * @param maxResults Maximum number of results to return
     * @return List of nearby locations
     * @throws LocationSearchException if the search fails
     */
    suspend fun getNearbyLocations(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 1000,
        maxResults: Int = 10
    ): List<Location>
}

/**
 * Exception thrown when location search operations fail.
 */
class LocationSearchException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
