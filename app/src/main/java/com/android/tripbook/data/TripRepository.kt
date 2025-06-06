hpackage com.android.tripbook.data // This package is used in the ViewModel, ensure it's consistent

import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Repository for managing Trip data.
 * In a real application, this would handle data operations with a database (e.g., Room, Firestore)
 * or a network API. For this example, it provides dummy data.
 */
class TripRepository {

    // Using MutableStateFlow to simulate a live data source
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    init {
        // Initialize with some dummy data when the repository is created
        _trips.value = generateDummyTrips()
    }

    /**
     * Provides a Flow of all trips.
     * Other parts of the application (like ViewModels) can collect this Flow
     * to react to changes in the list of trips in real-time.
     *
     * @return A [Flow] emitting the current list of [Trip] objects.
     */
    fun getTrips(): Flow<List<Trip>> {
        return _trips
    }

    /**
     * Adds a new trip to the repository's in-memory list.
     * In a real app, this would involve saving to a persistent storage (e.g., database, backend).
     *
     * @param trip The [Trip] object to add.
     */
    fun addTrip(trip: Trip) {
        _trips.value = _trips.value + trip // Creates a new list with the added trip
    }

    /**
     * Updates an existing trip in the repository's in-memory list.
     * It finds the trip by its ID and replaces it with the updated version.
     *
     * @param updatedTrip The [Trip] object with updated details.
     */
    fun updateTrip(updatedTrip: Trip) {
        _trips.value = _trips.value.map {
            if (it.id == updatedTrip.id) updatedTrip else it // Replace if ID matches, otherwise keep original
        }
    }

    /**
     * Deletes a trip from the repository's in-memory list by its ID.
     *
     * @param tripId The ID of the trip to delete.
     */
    fun deleteTrip(tripId: String) {
        _trips.value = _trips.value.filter { it.id != tripId } // Creates a new list excluding the deleted trip
    }

    /**
     * Generates a list of dummy [Trip] objects for demonstration purposes.
     * This list includes various trip scenarios to help test filtering and search functionalities
     * across different dates, budgets, destinations, and statuses.
     */
    private fun generateDummyTrips(): List<Trip> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return listOf(
            Trip(
                id = UUID.randomUUID().toString(),
                name = "Summer Adventure in Paris",
                destination = "Paris, France",
                startDate = LocalDate.parse("15/07/2024", formatter),
                endDate = LocalDate.parse("22/07/2024", formatter),
                travelers = 2,
                budget = 35000,
                description = "A romantic getaway to the city of lights, exploring museums and enjoying French cuisine.",
                status = TripStatus.UPCOMING,
                itinerary = listOf(
                    ItineraryItem(UUID.randomUUID().toString(), LocalDate.parse("15/07/2024", formatter), "10:00 AM", "Flight to Paris", "CDG Airport", ItineraryType.TRAVEL, false),
                    ItineraryItem(UUID.randomUUID().toString(), LocalDate.parse("15/07/2024", formatter), "02:00 PM", "Hotel Check-in", "Hotel Le Littré", ItineraryType.ACCOMMODATION, false),
                    ItineraryItem(UUID.randomUUID().toString(), LocalDate.parse("16/07/2024", formatter), "09:00 AM", "Eiffel Tower Visit", "Champ de Mars", ItineraryType.ATTRACTION, false)
                )
            ),
            Trip(
                id = UUID.randomUUID().toString(),
                name = "Hiking Trip to Patagonia",
                destination = "Patagonia, Argentina",
                startDate = LocalDate.parse("10/11/2024", formatter),
                endDate = LocalDate.parse("25/11/2024", formatter),
                travelers = 4,
                budget = 60000,
                description = "An adventurous trek through the stunning landscapes of Patagonia.",
                status = TripStatus.UPCOMING,
                itinerary = emptyList() // No itinerary yet
            ),
            Trip(
                id = UUID.randomUUID().toString(),
                name = "Historical Tour of Rome",
                destination = "Rome, Italy",
                startDate = LocalDate.parse("05/05/2024", formatter),
                endDate = LocalDate.parse("12/05/2024", formatter),
                travelers = 3,
                budget = 28000,
                description = "Discovering ancient Roman history and enjoying Italian culture.",
                status = TripStatus.COMPLETED,
                itinerary = listOf(
                    ItineraryItem(UUID.randomUUID().toString(), LocalDate.parse("05/05/2024", formatter), "09:00 AM", "Colosseum Tour", "Colosseum", ItineraryType.ATTRACTION, true),
                    ItineraryItem(UUID.randomUUID().toString(), LocalDate.parse("06/05/2024", formatter), "02:00 PM", "Vatican City Visit", "Vatican City", ItineraryType.ATTRACTION, true)
                )
            ),
            Trip(
                id = UUID.randomUUID().toString(),
                name = "Weekend Getaway to Bali",
                destination = "Bali, Indonesia",
                startDate = LocalDate.parse("01/03/2025", formatter),
                endDate = LocalDate.parse("07/03/2025", formatter),
                travelers = 2,
                budget = 40000,
                description = "Relaxing beach vacation with cultural experiences in Bali.",
                status = TripStatus.UPCOMING,
                itinerary = emptyList()
            ),
            // --- Additional Dummy Trips for Diverse Functionalities ---
            Trip(
                id = UUID.randomUUID().toString(),
                name = "Business Conference in Tokyo",
                destination = "Tokyo, Japan",
                startDate = LocalDate.parse("20/09/2025", formatter),
                endDate = LocalDate.parse("24/09/2025", formatter),
                travelers = 1,
                budget = 55000,
                description = "Attending the annual tech conference and networking events.",
                status = TripStatus.UPCOMING,
                itinerary = listOf(
                    ItineraryItem(UUID.randomUUID().toString(), LocalDate.parse("20/09/2025", formatter), "08:00 AM", "Conference Registration", "Tokyo Convention Center", ItineraryType.EVENT, false),
                    ItineraryItem(UUID.randomUUID().toString(), LocalDate.parse("21/09/2025", formatter), "07:00 PM", "Networking Dinner", "Shinjuku Restaurant", ItineraryType.DINING, false)
                )
            ),
            Trip(
                id = UUID.randomUUID().toString(),
                name = "Family Beach Vacation (Completed)",
                destination = "Cancun, Mexico",
                startDate = LocalDate.parse("10/01/2023", formatter),
                endDate = LocalDate.parse("17/01/2023", formatter),
                travelers = 5,
                budget = 7000,
                description = "A fun-filled family trip to the beaches of Cancun.",
                status = TripStatus.COMPLETED,
                itinerary = emptyList() // Itinerary might be too long for dummy data
            ),
            Trip(
                id = UUID.randomUUID().toString(),
                name = "Cancelled Ski Trip",
                destination = "Whistler, Canada",
                startDate = LocalDate.parse("01/12/2024", formatter),
                endDate = LocalDate.parse("08/12/2024", formatter),
                travelers = 3,
                budget = 45000,
                description = "Planned ski trip, but cancelled due to unforeseen circumstances.",
                status = TripStatus.CANCELLED,
                itinerary = emptyList()
            ),
            Trip(
                id = UUID.randomUUID().toString(),
                name = "Solo Backpacking in Thailand",
                destination = "Bangkok, Thailand",
                startDate = LocalDate.parse("01/02/2025", formatter),
                endDate = LocalDate.parse("28/02/2025", formatter),
                travelers = 1,
                budget = 2000,
                description = "Exploring temples and street food in Southeast Asia.",
                status = TripStatus.UPCOMING,
                itinerary = emptyList()
            )
        )
    }

    // Removed the incomplete private function ItineraryItem as it is not used and causes errors
}
