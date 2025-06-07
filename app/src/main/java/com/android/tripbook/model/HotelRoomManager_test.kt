package com.example.hotel.logic

import java.time.LocalDate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HotelManagerTest {

    private lateinit var hotelManager: HotelManager
    private lateinit var room101: Room
    private lateinit var room102: Room
    private lateinit var room201: Room
    private lateinit var room301: Room

    @BeforeEach
    fun setUp() {
        // Sample Data setup runs before each test
        room101 = Room("r101", "h1", "101", RoomType.STANDARD, 100.0)
        room102 = Room("r102", "h1", "102", RoomType.STANDARD, 100.0)
        room201 = Room("r201", "h1", "201", RoomType.DELUXE, 150.0)
        room301 = Room("r301", "h1", "301", RoomType.SUITE, 250.0)

        val hotel1 =
            Hotel(
                "h1",
                "Grand Kotlin Hotel",
                listOf(room101, room102, room201, room301)
            )

        val existingBookings =
            listOf(
                Booking(
                    "b1",
                    "r101",
                    "h1",
                    LocalDate.of(2025, 6, 10),
                    LocalDate.of(2025, 6, 12), // 2 nights
                    totalPrice = 200.0
                ),
                Booking(
                    "b2",
                    "r201",
                    "h1",
                    LocalDate.of(2025, 6, 15),
                    LocalDate.of(2025, 6, 18), // 3 nights
                    setOf(AddOn.BREAKFAST),
                    totalPrice = 495.0
                )
            )

        hotelManager = HotelManager(listOf(hotel1), existingBookings)
    }

    @Test
    fun `getAvailableRooms should return rooms not overlapping with bookings`() {
        val checkIn = LocalDate.of(2025, 6, 11)
        val checkOut = LocalDate.of(2025, 6, 13)

        val availableRooms =
            hotelManager.getAvailableRooms("h1", checkIn, checkOut)

        assertEquals(3, availableRooms.size)
        assertFalse(availableRooms.any { it.id == "r101" }) // r101 is booked
        assertTrue(availableRooms.any { it.id == "r102" })
        assertTrue(availableRooms.any { it.id == "r201" })
        assertTrue(availableRooms.any { it.id == "r301" })
    }

    @Test
    fun `getAvailableRooms should return all rooms when no bookings conflict`() {
        val checkIn = LocalDate.of(2025, 7, 1)
        val checkOut = LocalDate.of(2025, 7, 5)

        val availableRooms =
            hotelManager.getAvailableRooms("h1", checkIn, checkOut)

        assertEquals(4, availableRooms.size)
    }

    @Test
    fun `getAvailableRooms should throw exception for invalid date range`() {
        val checkIn = LocalDate.of(2025, 6, 15)
        val checkOut = LocalDate.of(2025, 6, 14)

        val exception =
            assertThrows<IllegalArgumentException> {
                hotelManager.getAvailableRooms("h1", checkIn, checkOut)
            }
        assertEquals(
            "Check-out date must be after check-in date.",
            exception.message
        )
    }

    @Test
    fun `calculateRate should return correct price for room without add-ons`() {
        val checkIn = LocalDate.of(2025, 8, 1)
        val checkOut = LocalDate.of(2025, 8, 4) // 3 

        val totalRate =
            hotelManager.calculateRate(room201, checkIn, checkOut)

        assertEquals(450.0, totalRate, 0.01) // 150.0 * 3
    }

    @Test
    fun `calculateRate should return correct price with per-night add-ons`() {
        val checkIn = LocalDate.of(2025, 8, 1)
        val checkOut = LocalDate.of(2025, 8, 3) // 2 nights
        val addOns = setOf(AddOn.BREAKFAST, AddOn.EXTRA_BED) // 15 + 25 = 40/night

        val totalRate =
            hotelManager.calculateRate(room301, checkIn, checkOut, addOns)

        val expected = (250.0 * 2) + (40.0 * 2) // 500 + 80 = 580
        assertEquals(580.0, totalRate, 0.01)
    }

    @Test
    fun `calculateRate should handle one-time and per-night add-ons correctly`() {
        val checkIn = LocalDate.of(2025, 9, 5)
        val checkOut = LocalDate.of(2025, 9, 8) // 3 nights
        val addOns = setOf(AddOn.BREAKFAST, AddOn.AIRPORT_TRANSFER)

        val totalRate =
            hotelManager.calculateRate(room102, checkIn, checkOut, addOns)

        // (100 * 3) + (15 * 3) + 50
        val expected = 300.0 + 45.0 + 50.0
        assertEquals(395.0, totalRate, 0.01)
    }
}