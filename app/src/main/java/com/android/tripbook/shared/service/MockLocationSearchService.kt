package com.android.tripbook.shared.service

import com.android.tripbook.shared.model.Coordinates
import com.android.tripbook.shared.model.Location
import kotlinx.coroutines.delay

/**
 * Mock implementation of LocationSearchService for testing and development.
 * Returns predefined sample locations based on search queries.
 */
class MockLocationSearchService : LocationSearchService {

    private val sampleLocations = listOf(
        Location(
            name = "Eiffel Tower",
            city = "Paris",
            country = "France",
            coordinates = Coordinates(48.8584, 2.2945),
            placeId = "eiffel_tower_paris",
            address = "Champ de Mars, 5 Avenue Anatole France, 75007 Paris, France"
        ),
        Location(
            name = "Times Square",
            city = "New York",
            country = "United States",
            coordinates = Coordinates(40.7580, -73.9855),
            placeId = "times_square_nyc",
            address = "Times Square, New York, NY 10036, USA"
        ),
        Location(
            name = "Sydney Opera House",
            city = "Sydney",
            country = "Australia",
            coordinates = Coordinates(-33.8568, 151.2153),
            placeId = "sydney_opera_house",
            address = "Bennelong Point, Sydney NSW 2000, Australia"
        ),
        Location(
            name = "Big Ben",
            city = "London",
            country = "United Kingdom",
            coordinates = Coordinates(51.4994, -0.1245),
            placeId = "big_ben_london",
            address = "Westminster, London SW1A 0AA, UK"
        ),
        Location(
            name = "Statue of Liberty",
            city = "New York",
            country = "United States",
            coordinates = Coordinates(40.6892, -74.0445),
            placeId = "statue_of_liberty",
            address = "New York, NY 10004, USA"
        ),
        Location(
            name = "Colosseum",
            city = "Rome",
            country = "Italy",
            coordinates = Coordinates(41.8902, 12.4922),
            placeId = "colosseum_rome",
            address = "Piazza del Colosseo, 1, 00184 Roma RM, Italy"
        ),
        Location(
            name = "Machu Picchu",
            city = "Cusco",
            country = "Peru",
            coordinates = Coordinates(-13.1631, -72.5450),
            placeId = "machu_picchu",
            address = "08680, Peru"
        ),
        Location(
            name = "Golden Gate Bridge",
            city = "San Francisco",
            country = "United States",
            coordinates = Coordinates(37.8199, -122.4783),
            placeId = "golden_gate_bridge",
            address = "Golden Gate Bridge, San Francisco, CA, USA"
        ),
        Location(
            name = "Christ the Redeemer",
            city = "Rio de Janeiro",
            country = "Brazil",
            coordinates = Coordinates(-22.9519, -43.2105),
            placeId = "christ_redeemer_rio",
            address = "Corcovado - Alto da Boa Vista, Rio de Janeiro - RJ, Brazil"
        ),
        Location(
            name = "Great Wall of China",
            city = "Beijing",
            country = "China",
            coordinates = Coordinates(40.4319, 116.5704),
            placeId = "great_wall_china",
            address = "Huairou District, Beijing, China"
        )
    )

    override suspend fun searchLocations(
        query: String,
        maxResults: Int
    ): List<Location> {
        // Simulate network delay
        delay(500)

        if (query.length < 2) {
            return emptyList()
        }

        return sampleLocations
            .filter { location ->
                location.name.contains(query, ignoreCase = true) ||
                location.city.contains(query, ignoreCase = true) ||
                location.country.contains(query, ignoreCase = true) ||
                location.address.contains(query, ignoreCase = true)
            }
            .take(maxResults)
    }

    override suspend fun getLocationDetails(placeId: String): Location? {
        // Simulate network delay
        delay(300)

        return sampleLocations.find { it.placeId == placeId }
    }

    override suspend fun getNearbyLocations(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int,
        maxResults: Int
    ): List<Location> {
        // Simulate network delay
        delay(400)

        // Simple distance calculation (not accurate, just for demo)
        return sampleLocations
            .filter { location ->
                location.coordinates?.let { coords ->
                    val deltaLat = Math.abs(coords.latitude - latitude)
                    val deltaLng = Math.abs(coords.longitude - longitude)
                    // Rough approximation: 1 degree ≈ 111km
                    val distanceKm = Math.sqrt(deltaLat * deltaLat + deltaLng * deltaLng) * 111
                    distanceKm * 1000 <= radiusMeters
                } ?: false
            }
            .take(maxResults)
    }
}
