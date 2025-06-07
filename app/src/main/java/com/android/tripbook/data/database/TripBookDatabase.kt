package com.android.tripbook.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.android.tripbook.data.database.converters.DateTimeConverters
import com.android.tripbook.data.database.entities.*
import com.android.tripbook.data.database.dao.*

/**
 * Main Room Database for TripBook Application
 *
 * This is the central database class that defines the SQLite database
 * for the TripBook application. It includes all entities, DAOs, and
 * database configuration.
 *
 * Key Features:
 * - Complete schema with all entities and relationships
 * - Type converters for LocalDate and LocalDateTime
 * - Singleton pattern for database instance
 * - Migration support for future schema changes
 * - Comprehensive data access through DAOs
 *
 * Database Schema:
 * - trips: Core trip information
 * - users: User profiles and preferences
 * - reservations: Booking records
 * - transport_options: Available transport for trips
 * - hotels: Hotel information
 * - activities: Available activities
 * - reservation_activities: Many-to-many reservation-activity junction
 * - notifications: User notifications
 * - user_favorites: User favorite items
 *
 * Used by:
 * - All managers and repositories
 * - Database initialization
 * - Data migration
 */
@Database(
    entities = [
        TripEntity::class,
        UserEntity::class,
        ReservationEntity::class,
        TransportOptionEntity::class,
        HotelEntity::class,
        ActivityEntity::class,
        ReservationActivityEntity::class,
        NotificationEntity::class,
        UserFavoriteEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class TripBookDatabase : RoomDatabase() {

    // DAO abstract methods
    abstract fun tripDao(): TripDao
    abstract fun userDao(): UserDao
    abstract fun reservationDao(): ReservationDao
    abstract fun transportDao(): TransportDao
    abstract fun hotelDao(): HotelDao
    abstract fun activityDao(): ActivityDao
    abstract fun reservationActivityDao(): ReservationActivityDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userFavoriteDao(): UserFavoriteDao

    companion object {
        /**
         * Database name
         */
        private const val DATABASE_NAME = "tripbook_database"

        /**
         * Singleton instance
         */
        @Volatile
        private var INSTANCE: TripBookDatabase? = null

        /**
         * Get database instance using singleton pattern
         * Thread-safe implementation with double-checked locking
         */
        fun getDatabase(context: Context): TripBookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TripBookDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // For development - remove in production
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Get database instance for testing
         * Creates an in-memory database for unit tests
         */
        fun getInMemoryDatabase(context: Context): TripBookDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                TripBookDatabase::class.java
            )
                .allowMainThreadQueries() // Only for testing
                .build()
        }

        /**
         * Close database instance
         * Used for cleanup in tests or app termination
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

/**
 * Database initialization helper
 *
 * This class provides methods to initialize the database with
 * sample data for development and testing purposes.
 */
class DatabaseInitializer {

    companion object {
        /**
         * Initialize database with sample data
         * This should be called on first app launch or for testing
         */
        suspend fun initializeDatabase(database: TripBookDatabase) {
            // Check if database is already initialized
            val tripCount = database.tripDao().getTripCount()
            if (tripCount > 0) {
                return // Database already has data
            }

            // Initialize with sample data
            initializeSampleTrips(database)
            initializeSampleHotels(database)
            initializeSampleActivities(database)
            initializeSampleUsers(database)
        }

        /**
         * Initialize sample trips from existing DummyTripDataProvider
         */
        private suspend fun initializeSampleTrips(database: TripBookDatabase) {
            try {
                // Import from existing DummyTripDataProvider
                val dummyTrips = com.android.tripbook.data.providers.DummyTripDataProvider.getTrips()
                val tripEntities = dummyTrips.map { trip ->
                    TripEntity.fromDomainModel(trip)
                }

                database.tripDao().insertTrips(tripEntities)

                // Also initialize transport options for each trip
                tripEntities.forEach { tripEntity ->
                    val transportOptions = com.android.tripbook.data.providers.DummyTripDataProvider
                        .getTransportOptions(tripEntity.id)
                    val transportEntities = transportOptions.map { transport ->
                        TransportOptionEntity.fromDomainModel(transport, tripEntity.id)
                    }
                    database.transportDao().insertTransportOptions(transportEntities)
                }
            } catch (e: Exception) {
                // Fallback to basic sample data if dummy provider fails
                val fallbackTrip = TripEntity(
                    id = "trip_fallback",
                    title = "Sample Trip",
                    fromLocation = "Sample City",
                    toLocation = "Sample Destination",
                    departureDate = java.time.LocalDate.now().plusDays(30),
                    returnDate = java.time.LocalDate.now().plusDays(35),
                    imageUrl = "",
                    basePrice = 500.0,
                    description = "Sample trip for testing",
                    duration = "5 days",
                    category = "ADVENTURE"
                )
                database.tripDao().insertTrip(fallbackTrip)
            }
        }

        /**
         * Initialize sample hotels from existing DummyHotelProvider
         */
        private suspend fun initializeSampleHotels(database: TripBookDatabase) {
            try {
                // Import from existing DummyHotelProvider
                val dummyHotels = com.android.tripbook.data.providers.DummyHotelProvider.getHotels()
                val hotelEntities = dummyHotels.map { hotel ->
                    HotelEntity.fromDomainModel(hotel, "Sample Location")
                }

                database.hotelDao().insertHotels(hotelEntities)
            } catch (e: Exception) {
                // Fallback to basic sample data if dummy provider fails
                val fallbackHotel = HotelEntity(
                    id = "hotel_fallback",
                    name = "Sample Hotel",
                    rating = 3,
                    roomType = "Standard Room",
                    pricePerNight = 100.0,
                    imageUrl = "",
                    amenities = "WiFi,Breakfast",
                    description = "Sample hotel for testing",
                    location = "Sample Location"
                )
                database.hotelDao().insertHotel(fallbackHotel)
            }
        }

        /**
         * Initialize sample activities from existing DummyActivityProvider
         */
        private suspend fun initializeSampleActivities(database: TripBookDatabase) {
            try {
                // Import from existing DummyActivityProvider
                val dummyActivities = com.android.tripbook.data.providers.DummyActivityProvider.getActivities()
                val activityEntities = dummyActivities.map { activity ->
                    ActivityEntity.fromDomainModel(activity, "Sample Location")
                }

                database.activityDao().insertActivities(activityEntities)
            } catch (e: Exception) {
                // Fallback to basic sample data if dummy provider fails
                val fallbackActivity = ActivityEntity(
                    id = "activity_fallback",
                    name = "Sample Activity",
                    description = "Sample activity for testing",
                    price = 50.0,
                    duration = "2 hours",
                    category = "CULTURAL",
                    imageUrl = "",
                    location = "Sample Location"
                )
                database.activityDao().insertActivity(fallbackActivity)
            }
        }

        /**
         * Initialize sample users
         */
        private suspend fun initializeSampleUsers(database: TripBookDatabase) {
            val sampleUser = UserEntity.createNewUser(
                id = "user_1",
                username = "demo_user",
                email = "demo@tripbook.com",
                firstName = "Demo",
                lastName = "User"
            )

            database.userDao().insertUser(sampleUser)
        }
    }
}
