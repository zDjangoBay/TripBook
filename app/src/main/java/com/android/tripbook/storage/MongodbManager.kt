package com.tripscheduler.storage

import com.mongodb.MongoClientSettings
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.tripscheduler.data.Location
import com.tripscheduler.data.ItineraryItem
import com.tripscheduler.data.Trip
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import java.util.concurrent.TimeUnit

object MongoDBManager {

    private const val DB_NAME = "trip_scheduler_db"
    private lateinit var mongoClient: MongoClient
    private lateinit var tripCollection: MongoCollection<Trip>
    private lateinit var locationCollection: MongoCollection<Location>
    private lateinit var itineraryItemCollection: MongoCollection<ItineraryItem>

    fun init() {
        // Register PojoCodecProvider to automatically map Kotlin data classes to BSON documents
        val pojoCodecRegistry: CodecRegistry = CodecRegistries.fromProviders(
            PojoCodecProvider.builder().automatic(true).build()
        )
        val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            pojoCodecRegistry
        )

        val settings = MongoClientSettings.builder()
            .applyToClusterSettings { builder ->
                builder.hosts(listOf(ServerAddress("localhost", 27017)))
            }
            .credential(
                com.mongodb.MongoCredential.createCredential("user", DB_NAME, "password".toCharArray())
            )
            .codecRegistry(codecRegistry)
            .applyToSocketSettings { builder ->
                builder.connectTimeout(5, TimeUnit.SECONDS)
            }
            .build()

        mongoClient = MongoClients.create(settings)
        val database = mongoClient.getDatabase(DB_NAME)

        // Initialize collections
        tripCollection = database.getCollection("trips", Trip::class.java)
        locationCollection = database.getCollection("locations", Location::class.java)
        itineraryItemCollection = database.getCollection("itinerary_items", ItineraryItem::class.java)

        // Create indexes (optional, but good for performance)
        try {
            tripCollection.createIndex(Filters.eq("id", 1))
            locationCollection.createIndex(Filters.eq("id", 1))
            itineraryItemCollection.createIndex(Filters.eq("id", 1))
            tripCollection.createIndex(Filters.eq("userId", 1))
            println("MongoDB collections and indexes initialized successfully.")
        } catch (e: Exception) {
            System.err.println("Error creating MongoDB indexes: ${e.message}")
            // This might happen if indexes already exist, safe to ignore in many cases for a demo
        }
    }

    fun close() {
        mongoClient.close()
        println("MongoDB client closed.")
    }

    // CRUD operations for Location
    fun saveLocation(location: Location) {
        locationCollection.replaceOne(Filters.eq("id", location.id), location, ReplaceOptions().upsert(true))
    }

    fun findLocationById(id: String): Location? {
        return locationCollection.find(Filters.eq("id", id)).firstOrNull()
    }

    fun findAllLocations(): List<Location> {
        return locationCollection.find().toList()
    }

    // CRUD operations for ItineraryItem
    fun saveItineraryItem(item: ItineraryItem) {
        itineraryItemCollection.replaceOne(Filters.eq("id", item.id), item, ReplaceOptions().upsert(true))
    }

    fun findItineraryItemById(id: String): ItineraryItem? {
        return itineraryItemCollection.find(Filters.eq("id", id)).firstOrNull()
    }

    fun findItineraryItemsByTripId(tripId: String): List<ItineraryItem> {
        return itineraryItemCollection.find(Filters.eq("tripId", tripId)).toList()
    }

    // CRUD operations for Trip
    fun saveTrip(trip: Trip) {
        tripCollection.replaceOne(Filters.eq("id", trip.id), trip, ReplaceOptions().upsert(true))
    }

    fun findTripById(id: String): Trip? {
        return tripCollection.find(Filters.eq("id", id)).firstOrNull()
    }

    fun findTripsByUserId(userId: String): List<Trip> {
        return tripCollection.find(Filters.eq("userId", userId)).toList()
    }

    fun findAllTrips(): List<Trip> {
        return tripCollection.find().toList()
    }
}