package com.android.tripbook.tests

import android.content.Context
import androidx.room.Room
import com.android.tripbook.data.database.TripBookDatabase
import com.android.tripbook.data.database.entities.HotelEntity
import com.android.tripbook.data.database.entities.ReservationEntity
import com.android.tripbook.data.models.ReservationStatus
import com.android.tripbook.data.models.PaymentStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Hotel Booking Test Runner
 * 
 * This class provides a comprehensive test suite for hotel booking functionality.
 * It tests the complete flow from hotel search to booking confirmation.
 * 
 * Features tested:
 * - Hotel search and filtering
 * - Hotel availability checking
 * - Hotel booking creation
 * - Room capacity management
 * - Booking cancellation
 * - Data integrity
 * 
 * Usage:
 * val testRunner = HotelBookingTestRunner(context)
 * testRunner.runAllTests()
 */
class HotelBookingTestRunner(private val context: Context) {
    
    private lateinit var database: TripBookDatabase
    
    // Test data
    private val testHotels = listOf(
        HotelEntity(
            id = "test_hotel_1",
            name = "Serengeti Safari Lodge",
            rating = 5,
            roomType = "Luxury Suite",
            pricePerNight = 350.0,
            imageUrl = "https://example.com/serengeti_lodge.jpg",
            amenities = "WiFi,Pool,Spa,Restaurant,Bar,Game Drive",
            description = "Luxury safari lodge in the heart of Serengeti",
            location = "Serengeti National Park",
            address = "Central Serengeti, Tanzania",
            phone = "+255-123-456-789",
            email = "reservations@serengetilodge.com",
            isAvailable = true,
            totalRooms = 30,
            availableRooms = 15
        ),
        HotelEntity(
            id = "test_hotel_2",
            name = "Kilimanjaro View Hotel",
            rating = 4,
            roomType = "Mountain View Room",
            pricePerNight = 180.0,
            imageUrl = "https://example.com/kilimanjaro_hotel.jpg",
            amenities = "WiFi,Restaurant,Gym,Mountain View",
            description = "Beautiful hotel with stunning Kilimanjaro views",
            location = "Moshi",
            address = "Kilimanjaro Road, Moshi",
            phone = "+255-987-654-321",
            email = "info@kilimanjaroview.com",
            isAvailable = true,
            totalRooms = 50,
            availableRooms = 25
        ),
        HotelEntity(
            id = "test_hotel_3",
            name = "Zanzibar Beach Resort",
            rating = 5,
            roomType = "Ocean View Suite",
            pricePerNight = 280.0,
            imageUrl = "https://example.com/zanzibar_resort.jpg",
            amenities = "WiFi,Pool,Beach Access,Spa,Restaurant,Bar,Water Sports",
            description = "Luxury beachfront resort in Zanzibar",
            location = "Stone Town, Zanzibar",
            address = "Forodhani Gardens, Stone Town",
            phone = "+255-555-123-456",
            email = "bookings@zanzibarresort.com",
            isAvailable = true,
            totalRooms = 40,
            availableRooms = 20
        )
    )
    
    fun initializeDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            context,
            TripBookDatabase::class.java
        ).allowMainThreadQueries().build()
    }
    
    fun closeDatabase() {
        database.close()
    }
    
    /**
     * Run all hotel booking tests
     */
    fun runAllTests(): TestResults {
        val results = TestResults()
        
        try {
            initializeDatabase()
            
            // Insert test data
            insertTestData()
            
            // Run individual tests
            results.addResult("Hotel Search Test", testHotelSearch())
            results.addResult("Hotel Filtering Test", testHotelFiltering())
            results.addResult("Hotel Booking Test", testHotelBooking())
            results.addResult("Room Availability Test", testRoomAvailability())
            results.addResult("Booking Cancellation Test", testBookingCancellation())
            results.addResult("Price Range Test", testPriceRangeQueries())
            results.addResult("Location Search Test", testLocationSearch())
            results.addResult("Amenity Filtering Test", testAmenityFiltering())
            
        } catch (e: Exception) {
            results.addError("Test Execution Error", e.message ?: "Unknown error")
        } finally {
            closeDatabase()
        }
        
        return results
    }
    
    private fun insertTestData() = runBlocking {
        database.hotelDao().insertHotels(testHotels)
    }
    
    /**
     * Test hotel search functionality
     */
    private fun testHotelSearch(): Boolean = runBlocking {
        try {
            val hotelDao = database.hotelDao()
            
            // Test search by name
            val serengetiResults = hotelDao.searchHotels("Serengeti").first()
            if (serengetiResults.size != 1 || serengetiResults[0].name != "Serengeti Safari Lodge") {
                return@runBlocking false
            }
            
            // Test search by description
            val luxuryResults = hotelDao.searchHotels("luxury").first()
            if (luxuryResults.size < 2) {
                return@runBlocking false
            }
            
            // Test search with no results
            val noResults = hotelDao.searchHotels("nonexistent").first()
            if (noResults.isNotEmpty()) {
                return@runBlocking false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Test hotel filtering by various criteria
     */
    private fun testHotelFiltering(): Boolean = runBlocking {
        try {
            val hotelDao = database.hotelDao()
            
            // Test filter by rating
            val highRatedHotels = hotelDao.getHotelsByMinRating(5).first()
            if (highRatedHotels.size != 2) { // Serengeti and Zanzibar are 5-star
                return@runBlocking false
            }
            
            // Test filter by price range
            val midRangeHotels = hotelDao.getHotelsByPriceRange(150.0, 200.0).first()
            if (midRangeHotels.size != 1 || midRangeHotels[0].name != "Kilimanjaro View Hotel") {
                return@runBlocking false
            }
            
            // Test filter by room type
            val suiteRooms = hotelDao.getHotelsByRoomType("Suite").first()
            if (suiteRooms.size != 2) { // Serengeti and Zanzibar have suites
                return@runBlocking false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Test hotel booking creation
     */
    private fun testHotelBooking(): Boolean = runBlocking {
        try {
            val hotelDao = database.hotelDao()
            val reservationDao = database.reservationDao()
            
            // Get a hotel to book
            val hotel = hotelDao.getHotelById("test_hotel_1")
            if (hotel == null) return@runBlocking false
            
            val initialAvailableRooms = hotel.availableRooms ?: 0
            
            // Create a reservation
            val reservation = ReservationEntity(
                id = "test_reservation_1",
                userId = "test_user_1",
                tripId = "test_trip_1",
                hotelId = hotel.id,
                status = ReservationStatus.PENDING.name,
                paymentStatus = PaymentStatus.PENDING.name,
                totalAmount = hotel.pricePerNight * 3, // 3 nights
                checkInDate = LocalDate.now().plusDays(30),
                checkOutDate = LocalDate.now().plusDays(33),
                guestCount = 2,
                specialRequests = "Ocean view room if available"
            )
            
            // Insert reservation
            reservationDao.insertReservation(reservation)
            
            // Decrement available rooms
            hotelDao.decrementAvailableRooms(hotel.id, 1)
            
            // Verify reservation was created
            val createdReservation = reservationDao.getReservationById(reservation.id)
            if (createdReservation == null) return@runBlocking false
            
            // Verify room count was decremented
            val updatedHotel = hotelDao.getHotelById(hotel.id)
            if (updatedHotel?.availableRooms != initialAvailableRooms - 1) {
                return@runBlocking false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Test room availability management
     */
    private fun testRoomAvailability(): Boolean = runBlocking {
        try {
            val hotelDao = database.hotelDao()
            
            // Test hotel with available rooms
            val hasRooms1 = hotelDao.hasAvailableRooms("test_hotel_1")
            if (!hasRooms1) return@runBlocking false
            
            // Set hotel to have no available rooms
            hotelDao.updateAvailableRooms("test_hotel_2", 0)
            val hasRooms2 = hotelDao.hasAvailableRooms("test_hotel_2")
            if (hasRooms2) return@runBlocking false
            
            // Test hotels with available rooms query
            val availableHotels = hotelDao.getHotelsWithAvailableRooms().first()
            if (availableHotels.any { it.id == "test_hotel_2" }) {
                return@runBlocking false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Test booking cancellation
     */
    private fun testBookingCancellation(): Boolean = runBlocking {
        try {
            val hotelDao = database.hotelDao()
            val reservationDao = database.reservationDao()
            
            // Get current available rooms
            val hotel = hotelDao.getHotelById("test_hotel_1")
            val currentRooms = hotel?.availableRooms ?: 0
            
            // Cancel a reservation (increment rooms)
            hotelDao.incrementAvailableRooms("test_hotel_1", 1)
            
            // Update reservation status
            reservationDao.updateReservationStatus("test_reservation_1", ReservationStatus.CANCELLED.name)
            
            // Verify room count increased
            val updatedHotel = hotelDao.getHotelById("test_hotel_1")
            if (updatedHotel?.availableRooms != currentRooms + 1) {
                return@runBlocking false
            }
            
            // Verify reservation status updated
            val cancelledReservation = reservationDao.getReservationById("test_reservation_1")
            if (cancelledReservation?.status != ReservationStatus.CANCELLED.name) {
                return@runBlocking false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Test price range queries
     */
    private fun testPriceRangeQueries(): Boolean = runBlocking {
        try {
            val hotelDao = database.hotelDao()
            
            // Test price range calculation
            val priceRange = hotelDao.getHotelPriceRange()
            if (priceRange == null) return@runBlocking false
            
            if (priceRange.minPrice != 180.0 || priceRange.maxPrice != 350.0) {
                return@runBlocking false
            }
            
            // Test cheapest hotels
            val cheapestHotels = hotelDao.getCheapestHotels(2).first()
            if (cheapestHotels.isEmpty() || cheapestHotels[0].pricePerNight != 180.0) {
                return@runBlocking false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Test location-based search
     */
    private fun testLocationSearch(): Boolean = runBlocking {
        try {
            val hotelDao = database.hotelDao()
            
            // Test location search
            val zanzibarHotels = hotelDao.getHotelsByLocation("Zanzibar").first()
            if (zanzibarHotels.size != 1 || zanzibarHotels[0].name != "Zanzibar Beach Resort") {
                return@runBlocking false
            }
            
            // Test get all locations
            val locations = hotelDao.getAllLocations()
            if (locations.size != 3) {
                return@runBlocking false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Test amenity filtering
     */
    private fun testAmenityFiltering(): Boolean = runBlocking {
        try {
            val hotelDao = database.hotelDao()
            
            // Test spa amenity
            val spaHotels = hotelDao.getHotelsByAmenity("Spa").first()
            if (spaHotels.size != 2) { // Serengeti and Zanzibar have spa
                return@runBlocking false
            }
            
            // Test beach access amenity
            val beachHotels = hotelDao.getHotelsByAmenity("Beach Access").first()
            if (beachHotels.size != 1 || beachHotels[0].name != "Zanzibar Beach Resort") {
                return@runBlocking false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Test results container
 */
data class TestResults(
    private val results: MutableMap<String, Boolean> = mutableMapOf(),
    private val errors: MutableMap<String, String> = mutableMapOf()
) {
    fun addResult(testName: String, passed: Boolean) {
        results[testName] = passed
    }
    
    fun addError(testName: String, error: String) {
        errors[testName] = error
    }
    
    fun getPassedCount(): Int = results.values.count { it }
    fun getFailedCount(): Int = results.values.count { !it }
    fun getTotalCount(): Int = results.size
    fun getErrorCount(): Int = errors.size
    
    fun getAllResults(): Map<String, Boolean> = results.toMap()
    fun getAllErrors(): Map<String, String> = errors.toMap()
    
    fun hasAllPassed(): Boolean = results.values.all { it } && errors.isEmpty()
    
    fun getSummary(): String {
        return buildString {
            appendLine("=== Hotel Booking Test Results ===")
            appendLine("Total Tests: ${getTotalCount()}")
            appendLine("Passed: ${getPassedCount()}")
            appendLine("Failed: ${getFailedCount()}")
            appendLine("Errors: ${getErrorCount()}")
            appendLine("Success Rate: ${if (getTotalCount() > 0) (getPassedCount() * 100 / getTotalCount()) else 0}%")
            appendLine()
            
            if (results.isNotEmpty()) {
                appendLine("Test Results:")
                results.forEach { (test, passed) ->
                    appendLine("  ${if (passed) "✅" else "❌"} $test")
                }
            }
            
            if (errors.isNotEmpty()) {
                appendLine()
                appendLine("Errors:")
                errors.forEach { (test, error) ->
                    appendLine("  ❌ $test: $error")
                }
            }
        }
    }
}
