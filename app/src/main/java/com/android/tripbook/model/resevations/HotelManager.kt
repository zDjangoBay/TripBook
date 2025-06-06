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
    AIRPORT_TRANSFER("Airport Transfer", 50.00) // Assuming this is a one-time fee, adjust if per night
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
        if (numberOfNights <= 0) {
            // This case should ideally be caught by the checkOutDate.isAfter(checkInDate)
            // but as a safeguard for ChronoUnit behavior.
            return 0.0
        }

        val baseRoomCost = room.basePricePerNight * numberOfNights
        val addOnsCost =
            addOns.sumOf { addOn ->
                // Assuming AddOn.pricePerNight is indeed per night.
                // If some add-ons are one-time, this logic needs adjustment.
                // For this example, all add-ons are treated as per night.
                addOn.pricePerNight * numberOfNights
            }

        return baseRoomCost + addOnsCost
    }

    // --- Potentially, methods to add/manage bookings would go here ---
    // fun createBooking(roomId: String, hotelId: String, ...): Booking
    // This would typically involve updating the `bookings` list (or a database)
    // and returning the new Booking object. For simplicity, it's omitted here
    // as the request focused on querying and rate calculation.
}

// Example Usage
fun main() {
    // Sample Data
    val room101 =
        Room(
            "r101",
            "h1",
            "101",
            RoomType.STANDARD,
            100.0
        )
    val room102 =
        Room(
            "r102",
            "h1",
            "102",
            RoomType.STANDARD,
            100.0
        )
    val room201 =
        Room(
            "r201",
            "h1",
            "201",
            RoomType.DELUXE,
            150.0
        )
    val room301 =
        Room(
            "r301",
            "h1",
            "301",
            RoomType.SUITE,
            250.0
        )

    val hotel1 =
        Hotel(
            "h1",
            "Grand Kotlin Hotel",
            listOf(room101, room102, room201, room301)
        )

    val existingBookings =
        mutableListOf(
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
                totalPrice = (150.0 * 3) + (AddOn.BREAKFAST.pricePerNight * 3)
            )
        )

    val hotelManager = HotelManager(listOf(hotel1), existingBookings)

    // --- Test Case 1: Query available rooms ---
    val checkIn = LocalDate.of(2025, 6, 11)
    val checkOut = LocalDate.of(2025, 6, 13)
    println(
        "Looking for rooms in ${hotel1.name} from $checkIn to $checkOut:"
    )

    try {
        val availableRooms =
            hotelManager.getAvailableRooms("h1", checkIn, checkOut)
        if (availableRooms.isEmpty()) {
            println("No rooms available for the selected dates.")
        } else {
            println("Available rooms:")
            availableRooms.forEach { room ->
                println(
                    " - Room ${room.roomNumber} (${room.type.displayName}) at \$${room.basePricePerNight}/night"
                )
            }

            // --- Test Case 2: Calculate rate for an available room ---
            val roomToBook = availableRooms.firstOrNull { it.type == RoomType.DELUXE }
            if (roomToBook != null) {
                val rateWithoutAddons =
                    hotelManager.calculateRate(roomToBook, checkIn, checkOut)
                println(
                    "\nRate for Room ${roomToBook.roomNumber} (${roomToBook.type.displayName}) for 2 nights: \$${String.format("%.2f", rateWithoutAddons)}"
                )

                val addOns = setOf(AddOn.BREAKFAST, AddOn.EXTRA_BED)
                val rateWithAddons =
                    hotelManager.calculateRate(roomToBook, checkIn, checkOut, addOns)
                println(
                    "Rate for Room ${roomToBook.roomNumber} with Breakfast & Extra Bed for 2 nights: \$${String.format("%.2f", rateWithAddons)}"
                )
            } else {
                 val firstAvailable = availableRooms.first()
                 val rateWithoutAddons =
                    hotelManager.calculateRate(firstAvailable, checkIn, checkOut)
                println(
                    "\nRate for Room ${firstAvailable.roomNumber} (${firstAvailable.type.displayName}) for 2 nights: \$${String.format("%.2f", rateWithoutAddons)}"
                )
            }
        }
    } catch (e: IllegalArgumentException) {
        println("Error: ${e.message}")
    }


    // --- Test Case 3: Query for dates that conflict with existing bookings ---
    val checkInConflict = LocalDate.of(2025, 6, 9)
    val checkOutConflict = LocalDate.of(2025, 6, 11) // Conflicts with r101
    println(
        "\nLooking for rooms in ${hotel1.name} from $checkInConflict to $checkOutConflict (expecting r101 to be unavailable):"
    )
    val availableRoomsConflict =
        hotelManager.getAvailableRooms("h1", checkInConflict, checkOutConflict)
    availableRoomsConflict.forEach { room ->
        println(
            " - Available: Room ${room.roomNumber} (${room.type.displayName})"
        )
    }
    if (!availableRoomsConflict.any{ it.id == "r101"}) {
        println("Room r101 is correctly unavailable.")
    }

    // --- Test Case 4: Invalid date range ---
    println("\nTesting invalid date range:")
    try {
        hotelManager.getAvailableRooms("h1", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 14))
    } catch (e: IllegalArgumentException) {
        println("Caught expected error: ${e.message}")
    }
}

