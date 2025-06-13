package com.tripscheduler.generator

import com.tripscheduler.data.ItineraryItem
import com.tripscheduler.data.Location
import com.tripscheduler.data.Trip
import com.tripscheduler.storage.MongoDBManager
import io.github.serpro69.kfaker.Faker
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.random.Random

object DataGenerator {

    private val faker = Faker()

    fun generateAndStoreFakeData(numTrips: Int = 10, numLocations: Int = 20, numItineraryItemsPerTrip: Int = 5) {
        println("Generating fake data...")

        val generatedLocations = mutableListOf<Location>()
        val generatedUsers = mutableListOf<String>() // Simple user IDs for now

        // Generate distinct users
        repeat(numTrips / 2) { // Roughly half the number of trips, to have some trips per user
            generatedUsers.add(UUID.randomUUID().toString())
        }
        if (generatedUsers.isEmpty()) generatedUsers.add(UUID.randomUUID().toString()) // Ensure at least one user

        // Generate Locations
        repeat(numLocations) {
            val location = Location(
                name = faker.address.city(),
                address = faker.address.fullAddress(),
                latitude = faker.address.latitude().toDouble(),
                longitude = faker.address.longitude().toDouble()
            )
            MongoDBManager.saveLocation(location)
            generatedLocations.add(location)
        }
        println("Generated ${generatedLocations.size} locations.")

        // Generate Trips and Itinerary Items
        repeat(numTrips) {
            if (generatedLocations.size < 2) {
                System.err.println("Not enough locations to generate trips. Need at least 2.")
                return
            }

            val origin = generatedLocations[Random.nextInt(generatedLocations.size)]
            var destination = generatedLocations[Random.nextInt(generatedLocations.size)]
            while (destination == origin) { // Ensure origin and destination are different
                destination = generatedLocations[Random.nextInt(generatedLocations.size)]
            }

            val startDate = Instant.now().plus(Random.nextLong(1, 30), ChronoUnit.DAYS)
            val endDate = startDate.plus(Random.nextLong(1, 7), ChronoUnit.DAYS)

            val userId = generatedUsers[Random.nextInt(generatedUsers.size)]

            val trip = Trip(
                name = faker.travel.eventName(),
                description = faker.lorem.paragraph(),
                startDate = startDate,
                endDate = endDate,
                originLocationId = origin.id,
                destinationLocationId = destination.id,
                userId = userId
            )

            val itineraryItemIds = mutableListOf<String>()
            repeat(Random.nextInt(1, numItineraryItemsPerTrip + 1)) {
                val itemLocation = generatedLocations[Random.nextInt(generatedLocations.size)]
                val itemStartTime = trip.startDate.plus(Random.nextLong(0, ChronoUnit.DAYS.between(trip.startDate, trip.endDate)), ChronoUnit.DAYS)
                    .plus(Random.nextLong(0, 23), ChronoUnit.HOURS)
                val itemEndTime = itemStartTime.plus(Random.nextLong(1, 4), ChronoUnit.HOURS)

                val itineraryItem = ItineraryItem(
                    tripId = trip.id,
                    activity = faker.company.buzzword() + " " + faker.company.bs(),
                    startTime = itemStartTime,
                    endTime = itemEndTime,
                    locationId = itemLocation.id
                )
                MongoDBManager.saveItineraryItem(itineraryItem)
                itineraryItemIds.add(itineraryItem.id)
            }
            // Update trip with generated itinerary item IDs
            val finalTrip = trip.copy(itineraryItemIds = itineraryItemIds)
            MongoDBManager.saveTrip(finalTrip)
        }
        println("Generated $numTrips trips with itinerary items.")
        println("Fake data generation complete.")
    }
}