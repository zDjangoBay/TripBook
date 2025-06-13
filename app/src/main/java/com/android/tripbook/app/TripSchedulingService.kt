package com.tripscheduler.app

import com.tripscheduler.cache.CacheManager
import com.tripscheduler.data.AppJson
import com.tripscheduler.data.Trip
import com.tripscheduler.data.Location
import com.tripscheduler.data.ItineraryItem
import com.tripscheduler.generator.DataGenerator
import com.tripscheduler.storage.MongoDBManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

class TripSchedulingService {

    // Simulates retrieving a trip with its full details (including referenced locations and itinerary items)
    fun getFullTripDetails(tripId: String): String? {
        var trip: Trip? = null
        var originLocation: Location? = null
        var destinationLocation: Location? = null
        val itineraryItems = mutableListOf<ItineraryItem>()
        val referencedLocations = mutableListOf<Location>() // For itinerary item locations

        // --- 1. Try to get Trip from Cache ---
        trip = CacheManager.getTrip(tripId)

        if (trip == null) {
            // --- 2. Cache Miss: Retrieve Trip from DB ---
            println("Trip '$tripId' not in cache. Retrieving from MongoDB...")
            trip = MongoDBManager.findTripById(tripId)

            if (trip != null) {
                println("Trip '$tripId' found in DB. Caching it.")
                CacheManager.cacheTrip(trip)
            } else {
                println("Trip '$tripId' not found in DB.")
                return null // Trip not found at all
            }
        }

        // --- Retrieve Associated Locations and Itinerary Items (Cache-aside for these too) ---
        trip?.let { t ->
            // Origin Location
            originLocation = CacheManager.getLocation(t.originLocationId)
            if (originLocation == null) {
                originLocation = MongoDBManager.findLocationById(t.originLocationId)
                originLocation?.let { CacheManager.cacheLocation(it) }
            }

            // Destination Location
            destinationLocation = CacheManager.getLocation(t.destinationLocationId)
            if (destinationLocation == null) {
                destinationLocation = MongoDBManager.findLocationById(t.destinationLocationId)
                destinationLocation?.let { CacheManager.cacheLocation(it) }
            }

            // Itinerary Items (we'll fetch all from DB for simplicity here, could cache individually)
            MongoDBManager.findItineraryItemsByTripId(t.id).forEach { item ->
                itineraryItems.add(item)
                // Also try to cache locations for itinerary items if they're not already there
                if (CacheManager.getLocation(item.locationId) == null) {
                    MongoDBManager.findLocationById(item.locationId)?.let { loc ->
                        CacheManager.cacheLocation(loc)
                        referencedLocations.add(loc)
                    }
                }
            }
        }

        // --- Construct a richer DTO or Map for JSON output ---
        val fullTripDetails = trip?.let { t ->
            mapOf(
                "trip" to t,
                "originLocation" to originLocation,
                "destinationLocation" to destinationLocation,
                "itineraryItems" to itineraryItems,
                "referencedLocations" to referencedLocations.distinctBy { it.id } // Include other unique locations
            )
        }

        // Return JSON string
        return fullTripDetails?.let { AppJson.encodeToString(it) }
    }
}

fun main() = runBlocking {
    println("--- Starting Trip Scheduling Application ---")

    // 1. Initialize MongoDB and Redis
    MongoDBManager.init()
    CacheManager.init()

    // 2. Generate and Store Fake Data (only if DB is empty or for a fresh start)
    println("\nChecking if data exists in MongoDB...")
    if (MongoDBManager.findAllTrips().isEmpty()) {
        DataGenerator.generateAndStoreFakeData(numTrips = 15, numLocations = 30)
        println("Generated initial fake data.")
    } else {
        println("MongoDB already contains data. Skipping data generation.")
    }

    val tripService = TripSchedulingService()

    // Get a sample Trip ID to work with
    val sampleTripId = MongoDBManager.findAllTrips().firstOrNull()?.id

    if (sampleTripId == null) {
        println("No trips found in the database. Exiting.")
        MongoDBManager.close()
        CacheManager.close()
        return@runBlocking
    }

    println("\n--- Demonstrating Cache Mechanism ---")

    // --- First Retrieval (Should be a Cache MISS, then DB hit, then Cache PUT) ---
    println("\nAttempting to retrieve trip '$sampleTripId' for the first time...")
    val jsonResult1 = tripService.getFullTripDetails(sampleTripId)
    println("\nFirst Retrieval Result (JSON):")
    println(jsonResult1)
    println("------------------------------------")

    // Add a small delay to ensure cache TTL is not immediately expired (though 5 mins is long enough)
    delay(100.milliseconds)

    // --- Second Retrieval (Should be a Cache HIT) ---
    println("\nAttempting to retrieve trip '$sampleTripId' for the second time (should be from cache)...")
    val jsonResult2 = tripService.getFullTripDetails(sampleTripId)
    println("\nSecond Retrieval Result (JSON):")
    println(jsonResult2)
    println("------------------------------------")

    // Optional: Retrieve a different trip to see another cache miss/hit
    println("\nAttempting to retrieve a different random trip (might be cache miss or hit for its components)...")
    val anotherTripId = MongoDBManager.findAllTrips().drop(1).firstOrNull()?.id
    if (anotherTripId != null) {
        val jsonResult3 = tripService.getFullTripDetails(anotherTripId)
        println("\nAnother Trip Retrieval Result (JSON):")
        println(jsonResult3)
        println("------------------------------------")
    }

    // 3. Clean up
    MongoDBManager.close()
    CacheManager.close()
    println("\n--- Application Finished ---")
}