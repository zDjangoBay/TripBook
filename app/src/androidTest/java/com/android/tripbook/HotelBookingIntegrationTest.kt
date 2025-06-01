package com.android.tripbook

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import androidx.room.Room
import com.android.tripbook.data.database.TripBookDatabase
import com.android.tripbook.data.database.entities.HotelEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Integration test for Hotel Booking functionality
 *
 * This test runs on an Android device/emulator and validates
 * the complete hotel booking workflow using the actual database.
 *
 * Run with: ./gradlew connectedAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class HotelBookingIntegrationTest {

    @Test
    fun testHotelDatabaseOperations() = runBlocking {
        // Get application context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Create in-memory database for testing
        val database = Room.inMemoryDatabaseBuilder(
            appContext,
            TripBookDatabase::class.java
        ).allowMainThreadQueries().build()

        val hotelDao = database.hotelDao()

        try {
            // Test data
            val testHotel = HotelEntity(
                id = "test_hotel_1",
                name = "Test Safari Lodge",
                rating = 5,
                roomType = "Luxury Suite",
                pricePerNight = 250.0,
                imageUrl = "https://example.com/hotel.jpg",
                amenities = "WiFi,Pool,Spa,Restaurant",
                description = "Test luxury safari lodge",
                location = "Serengeti",
                isAvailable = true,
                totalRooms = 30,
                availableRooms = 15
            )

            // Test 1: Insert and retrieve hotel
            hotelDao.insertHotel(testHotel)
            val retrievedHotel = hotelDao.getHotelById(testHotel.id)
            assertNotNull("Hotel should be retrievable after insertion", retrievedHotel)
            assertEquals("Hotel name should match", testHotel.name, retrievedHotel?.name)

            // Test 2: Get all hotels
            val allHotels = hotelDao.getAllHotels().first()
            assertEquals("Should have 1 hotel", 1, allHotels.size)
            assertTrue("Hotel should be available", allHotels[0].isAvailable)

            // Test 3: Search hotels
            val searchResults = hotelDao.searchHotels("Safari").first()
            assertEquals("Search should find 1 hotel", 1, searchResults.size)
            assertEquals("Found hotel should match", testHotel.name, searchResults[0].name)

            // Test 4: Filter by location
            val locationResults = hotelDao.getHotelsByLocation("Serengeti").first()
            assertEquals("Location filter should find 1 hotel", 1, locationResults.size)

            // Test 5: Update available rooms
            hotelDao.decrementAvailableRooms(testHotel.id, 2)
            val updatedHotel = hotelDao.getHotelById(testHotel.id)
            assertEquals("Available rooms should be decremented", 13, updatedHotel?.availableRooms)

            // Test 6: Check room availability
            val hasRooms = hotelDao.hasAvailableRooms(testHotel.id)
            assertTrue("Hotel should still have available rooms", hasRooms)

            println("✅ All hotel database operations completed successfully!")

        } finally {
            database.close()
        }
    }
}
