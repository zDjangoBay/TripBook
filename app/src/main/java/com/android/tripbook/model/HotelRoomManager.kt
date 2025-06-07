package com.example.hotel.logic

import java.time.LocalDate
import java.time.temporal.ChronoUnit

// Enum for different room types
enum class RoomType(val displayName: String) {
    STANDARD("Standard"),
    DELUXE("Deluxe"),
    SUITE("Suite")
}

// Enum for optional add-ons
enum class AddOn(val description: String, val pricePerNight: Double) {
    BREAKFAST("Breakfast", 15.00),
    EXTRA_BED("Extra Bed", 25.00),
    AIRPORT_TRANSFER("Airport Transfer", 50.00) // One-time fee, handled in calculateRate
}

// Data class representing a Hotel Room
data class Room(
    val id: String,
    val hotelId: String,
    val roomNumber: String,
    val type: RoomType,
    val basePricePerNight: Double
)

// Data class representing a Hotel
data class Hotel(
    val id: String,
    val name: String,
    val rooms: List<Room>
)

// Data class representing a Booking
// checkOutDate is exclusive (e.g., checkIn 2025-06-01, checkOut 2025-06-03 means 2 nights)
data class Booking(
    val id: String,
    val roomId: String,
    val hotelId: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val addOns: Set<AddOn> = emptySet(),
    val totalPrice: Double
) {
    // Helper to check if a date range overlaps with this booking
    fun overlaps(
        otherCheckInDate: LocalDate,
        otherCheckOutDate: LocalDate
    ): Boolean {
        // No overlap if one range ends before the other starts
        return !(otherCheckOutDate.isBefore(checkInDate) ||
            otherCheckOutDate.isEqual(checkInDate) ||
            otherCheckInDate.isAfter(checkOutDate) ||
            otherCheckInDate.isEqual(checkOutDate))
    }
}

// The HotelManager class
class HotelManager(
    private val hotels: List<Hotel>,
    private val bookings: List<Booking> // In a real app, this would come from a repository/database
) {

    /**
     * Finds available rooms for a given hotel and date range.
     * @param hotelId The ID of the hotel to search in.
     * @param checkInDate The desired check-in date.
     * @param checkOutDate The desired check-out date (exclusive).
     * @return A list of available rooms.
     * @throws IllegalArgumentException if checkOutDate is not after checkInDate or hotelId is not found.
     */
    fun getAvailableRooms(
        hotelId: String,
        checkInDate: LocalDate,
        checkOutDate: LocalDate
    ): List<Room> {
        if (!checkOutDate.isAfter(checkInDate)) {
            throw IllegalArgumentException(
                "Check-out date must be after check-in date."
            )
        }

        val hotel =
            hotels.find { it.id == hotelId }
                ?: throw IllegalArgumentException("Hotel with ID $hotelId not found.")

        val bookedRoomIdsForPeriod =
            bookings
                .filter { it.hotelId == hotelId }
                .filter { booking ->
                    booking.overlaps(checkInDate, checkOutDate)
                }
                .map { it.roomId }
                .toSet()

        return hotel.rooms.filterNot { room -> room.id in bookedRoomIdsForPeriod }
    }

    /**
     * Calculates the total rate for a room booking.
     * @param room The room for which to calculate the rate.
     * @param checkInDate The check-in date.
     * @param checkOutDate The check-out date (exclusive).
     * @param addOns A set of optional add-ons.
     * @return The total calculated rate.
     * @throws IllegalArgumentException if checkOutDate is not after checkInDate.
     */
    fun calculateRate(
        room: Room,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        addOns: Set<AddOn> = emptySet()
    ): Double {
        if (!checkOutDate.isAfter(checkInDate)) {
            throw IllegalArgumentException(
                "Check-out date must be after check-in date."
            )
        }

        val numberOfNights =
            ChronoUnit.DAYS.between(checkInDate, checkOutDate)
        if (numberOfNights <= 0L) {
            return 0.0
        }

        val baseRoomCost = room.basePricePerNight * numberOfNights
        val addOnsCost =
            addOns.sumOf { addOn ->
                // Logic to differentiate between per-night and one-time fees
                when (addOn) {
                    AddOn.AIRPORT_TRANSFER -> addOn.pricePerNight // One-time
                    else -> addOn.pricePerNight * numberOfNights // Per-night
                }
            }

        return baseRoomCost + addOnsCost
    }
}