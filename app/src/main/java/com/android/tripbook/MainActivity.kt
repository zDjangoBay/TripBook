package com.android.tripbook

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.lifecycleScope
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.Location
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
import com.android.tripbook.service.FirebaseTripService
import com.android.tripbook.ui.uis.*
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private val firebaseTripService = FirebaseTripService()

    // Define your mock trips directly here
    private val mockTrips = listOf(
        Trip(
            id = "1",
            name = "Safari Adventure",
            startDate = LocalDate.of(2024, 12, 15),
            endDate = LocalDate.of(2024, 12, 22),
            destination = "Yaounde, Cameroon",
            destinationCoordinates = Location(latitude = 3.8480, longitude = 11.5021, name = "Yaounde, Cameroon", address = "Yaounde, Cameroon", rating = 0.0, types = emptyList(), placeId = null),
            travelers = 4,
            budget = 2400,
            status = TripStatus.PLANNED,
            type = "Safari",
            description = "An amazing safari adventure through Yaounde and Cameroon",
            itinerary = listOf()
        ),
        Trip(
            id = "2",
            name = "Buea Tchop et Yamo ",
            startDate = LocalDate.of(2025, 1, 10),
            endDate = LocalDate.of(2025, 1, 18),
            destination = "Buea, Cameroon",
            destinationCoordinates = Location(latitude = 4.1481, longitude = 9.2323, name = "Buea, Cameroon", address = "Buea, Cameroon", rating = 0.0, types = emptyList(), placeId = null),
            travelers = 2,
            budget = 1800,
            status = TripStatus.ACTIVE,
            itinerary = listOf()
        ),
        Trip(
            id = "3",
            name = "Bamenda Rocky Slope Explore",
            startDate = LocalDate.of(2024, 9, 5),
            endDate = LocalDate.of(2024, 9, 12),
            destination = "Bamenda, Cameroon",
            destinationCoordinates = Location(latitude = 5.9622, longitude = 10.1587, name = "Bamenda, Cameroon", address = "Bamenda, Cameroon", rating = 0.0, types = emptyList(), placeId = null),
            travelers = 6,
            budget = 3200,
            status = TripStatus.COMPLETED,
            itinerary = listOf()
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase. This should ideally be done once at application startup.
        // If you have an Application class, initialize there.
        // Placing it here is fine for simpler apps but consider an Application class for scalability.
        try {
            FirebaseApp.initializeApp(this)
            Log.d("MainActivity", "FirebaseApp initialized successfully.")
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to initialize FirebaseApp: ${e.message}", e)
            // Consider displaying a critical error to the user or gracefully degrading features
        }


        val nominatimService = NominatimService()
        val travelAgencyService = TravelAgencyService()

        val apiKey = "AIzaSyDub6cdRg9_19vQn_qV4oQurf9L67LKPPA" // Ensure this is handled securely for production

        enableEdgeToEdge()

        setContent {
            TripBookApp(
                nominatimService = nominatimService,
                travelAgencyService = travelAgencyService,
                firebaseTripService = firebaseTripService,
                mockTrips = mockTrips, // Pass mock data
                apiKey = apiKey
            )
        }
    }


    @Composable
    fun TripBookApp(
        nominatimService: NominatimService,
        travelAgencyService: TravelAgencyService,
        firebaseTripService: FirebaseTripService,
        mockTrips: List<Trip>, // Receive mock data
        apiKey: String
    ) {
        var currentScreen by remember { mutableStateOf("MyTrips") }
        var selectedTrip by remember { mutableStateOf<Trip?>(null) }
        var selectedDestination by remember { mutableStateOf<String?>(null) }

        // This list will hold both mock data and Firebase data
        val allTrips = remember { mutableStateListOf<Trip>() }

        // Using LaunchedEffect to load trips when the Composable first enters the composition
        LaunchedEffect(Unit) { // Use Unit as key to run once when the composable enters composition
            // Ensure the initial load attempts to get Firebase trips and falls back gracefully
            loadCombinedTrips(firebaseTripService, mockTrips, allTrips)
        }

        when (currentScreen) {
            "MyTrips" -> MyTripsScreen(
                trips = allTrips, // Pass the combined observable list
                onPlanNewTripClick = {
                    currentScreen = "PlanNewTrip"
                },
                onTripClick = { trip ->
                    selectedTrip = trip
                    currentScreen = "TripDetails"
                }
            )

            "PlanNewTrip" -> PlanNewTripScreen(
                onBackClick = {
                    currentScreen = "MyTrips"
                },
                onTripCreated = { newTrip ->
                    lifecycleScope.launch {
                        try {
                            val result = firebaseTripService.saveTrip(newTrip)
                            if (result.isSuccess) {
                                Log.d("MainActivity", "New trip '${newTrip.name}' saved successfully to Firebase.")
                                // After saving, reload all trips to update the UI
                                // This ensures consistency, especially if Firebase adds an ID or modifies the trip.
                                loadCombinedTrips(firebaseTripService, mockTrips, allTrips)
                                currentScreen = "MyTrips" // Navigate back only on successful save
                            } else {
                                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                                Log.e("MainActivity", "Failed to save new trip '${newTrip.name}': $errorMessage")
                                // TODO: Show user a Snackbar or Toast about the failure
                                // You might keep the user on the PlanNewTripScreen to retry or indicate the error.
                                // For now, we'll still navigate back to avoid blocking the UI.
                                currentScreen = "MyTrips"
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Exception during new trip save: ${e.message}", e)
                            // TODO: Show a general error message to the user
                            currentScreen = "MyTrips"
                        }
                    }
                },
                nominatimService = nominatimService,
                travelAgencyService = travelAgencyService,
                apiKey = apiKey,
                onBrowseAgencies = { destination ->
                    selectedDestination = destination
                    currentScreen = "TravelAgency"
                }
            )

            "TripDetails" -> TripDetailsScreen(
                // Provide a non-null Trip. If selectedTrip is null, try to find a trip in allTrips.
                // As a last resort, create a minimal default trip to avoid crashes.
                trip = selectedTrip
                    ?: allTrips.firstOrNull()
                    ?: Trip(
                        id = "default_details", // Unique ID for default trip
                        name = "Default Trip (No Trip Selected)",
                        startDate = LocalDate.now(), endDate = LocalDate.now(),
                        destination = "Unknown", travelers = 0, budget = 0, status = TripStatus.PLANNED,
                        description = "Please select a trip to view its details."
                    ),
                onBackClick = {
                    currentScreen = "MyTrips"
                },
                onEditItineraryClick = {
                    // Ensure a trip is selected before navigating to ItineraryBuilder
                    if (selectedTrip != null) {
                        currentScreen = "ItineraryBuilder"
                    } else {
                        Log.w("MainActivity", "Cannot edit itinerary: No trip selected for details.")
                        // TODO: Potentially show a toast or error message to the user
                    }
                },
                apiKey = apiKey
            )

            "ItineraryBuilder" -> ItineraryBuilderScreen(
                // Similar robust default for ItineraryBuilder
                trip = selectedTrip
                    ?: allTrips.firstOrNull()
                    ?: Trip(
                        id = "default_itinerary", // Unique ID for default trip
                        name = "Default Trip (No Trip Selected)",
                        startDate = LocalDate.now(), endDate = LocalDate.now(),
                        destination = "Unknown", travelers = 0, budget = 0, status = TripStatus.PLANNED,
                        itinerary = listOf(ItineraryItem(date = LocalDate.now(), time = "N/A", title = "No trip selected", location = "N/A", type = com.android.tripbook.model.ItineraryType.ACTIVITY))
                    ),
                onBackClick = {
                    currentScreen = "TripDetails" // Always go back to trip details after editing itinerary
                },
                onItineraryUpdated = { updatedItinerary ->
                    // Only proceed if a trip was actually selected
                    selectedTrip?.let { currentSelectedTrip ->
                        // Create a new Trip object with the updated itinerary
                        val updatedTrip = currentSelectedTrip.copy(itinerary = updatedItinerary)

                        // Update the trip locally in allTrips for immediate UI reflection
                        val index = allTrips.indexOfFirst { it.id == updatedTrip.id }
                        if (index != -1) {
                            allTrips[index] = updatedTrip
                            Log.d("MainActivity", "Local trip '${updatedTrip.name}' updated with new itinerary.")
                        } else {
                            Log.w("MainActivity", "Updated trip '${updatedTrip.name}' not found in allTrips list for local update.")
                        }

                        // Save to Firebase if it's a non-mock trip with a proper ID
                        // The condition `!mockTrips.contains(updatedTrip)` can be tricky if mock trips gain Firebase IDs.
                        // A more robust check would be `if (updatedTrip.id != "default_details" && updatedTrip.id != "default_itinerary" && !mockTrips.any { it.id == updatedTrip.id })`
                        // or better, have a field in Trip model to indicate its origin (mock vs Firebase).
                        // For now, sticking close to your original logic:
                        if (updatedTrip.id != "default_details" && updatedTrip.id != "default_itinerary" && !mockTrips.any { it.id == updatedTrip.id }) {
                            lifecycleScope.launch {
                                try {
                                    val result = firebaseTripService.saveTrip(updatedTrip)
                                    if (result.isSuccess) {
                                        Log.d("MainActivity", "Trip itinerary for '${updatedTrip.name}' updated and saved to Firebase.")
                                    } else {
                                        val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                                        Log.e("MainActivity", "Failed to update trip itinerary for '${updatedTrip.name}': $errorMessage")
                                        // TODO: Show a Snackbar/Toast to user about Firebase save failure
                                    }
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Exception during itinerary save: ${e.message}", e)
                                    // TODO: Show a general error message to the user
                                }
                            }
                        } else {
                            Log.d("MainActivity", "Skipping Firebase save for mock or default trip: '${updatedTrip.name}'")
                        }
                        selectedTrip = updatedTrip // Keep selectedTrip updated
                    } ?: Log.w("MainActivity", "onItineraryUpdated called but no trip was selected.")
                },
                nominatimService = nominatimService,
                travelAgencyService = travelAgencyService,
                onBrowseAgencies = { destination ->
                    selectedDestination = destination
                    currentScreen = "TravelAgency"
                }
            )

            "TravelAgency" -> TravelAgencyScreen(
                destination = selectedDestination ?: "", // Ensure a default empty string if selectedDestination is null
                travelAgencyService = travelAgencyService,
                onBackClick = {
                    // Navigate back to the screen where TravelAgency was launched from
                    currentScreen = if (selectedTrip == null) "PlanNewTrip" else "ItineraryBuilder"
                },
                onServiceSelected = { service, type ->
                    selectedTrip?.let { trip ->
                        val newItem = ItineraryItem(
                            // It's crucial to generate a unique ID for new itinerary items if they are to be distinguishable
                            // For simplicity, using a random UUID or Firebase-generated ID would be ideal for a real app.
                            // For now, assuming title+location is "unique enough" for demo, or Firebase handles ID generation
                            date = trip.startDate, // Consider if this should be today's date or user selected
                            time = "10:00 AM", // Consider making this selectable
                            title = service.name,
                            location = service.location, // Assuming service.location is a String
                            type = type,
                            agencyService = service
                            // If service object has a unique ID, pass it here for proper equality checks
                        )

                        // Update the local itinerary for immediate UI reflection
                        val updatedItinerary = trip.itinerary + newItem
                        val updatedTrip = trip.copy(itinerary = updatedItinerary)

                        // Update the trip in the main 'allTrips' list
                        val index = allTrips.indexOfFirst { it.id == updatedTrip.id }
                        if (index != -1) {
                            allTrips[index] = updatedTrip
                            Log.d("MainActivity", "Local trip '${updatedTrip.name}' updated with new itinerary item: ${newItem.title}.")
                        } else {
                            Log.w("MainActivity", "Updated trip '${updatedTrip.name}' not found in allTrips list for local update after adding service.")
                        }

                        // Save the updated trip to Firebase (only if it's a non-mock trip)
                        if (updatedTrip.id != "default_details" && updatedTrip.id != "default_itinerary" && !mockTrips.any { it.id == updatedTrip.id }) {
                            lifecycleScope.launch {
                                try {
                                    val result = firebaseTripService.saveTrip(updatedTrip)
                                    if (result.isSuccess) {
                                        Log.d("MainActivity", "Itinerary item '${newItem.title}' added and trip saved to Firebase.")
                                    } else {
                                        val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                                        Log.e("MainActivity", "Failed to add itinerary item '${newItem.title}': $errorMessage")
                                        // TODO: Show a Snackbar/Toast to user about Firebase save failure
                                    }
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Exception during adding itinerary item and saving trip: ${e.message}", e)
                                    // TODO: Show a general error message to the user
                                }
                            }
                        } else {
                            Log.d("MainActivity", "Skipping Firebase save for mock or default trip after adding itinerary item.")
                        }
                        selectedTrip = updatedTrip // Keep selectedTrip updated
                    } ?: Log.w("MainActivity", "onServiceSelected called but no trip was selected to add service to.")

                    // Navigate back after service selection, regardless of save success
                    currentScreen = if (selectedTrip == null) "PlanNewTrip" else "ItineraryBuilder"
                }
            )
        }
    }

    // Helper function to load trips (combining mock and Firebase data)
    private fun loadCombinedTrips(
        firebaseTripService: FirebaseTripService,
        mockTrips: List<Trip>,
        allTripsList: SnapshotStateList<Trip>
    ) {
        lifecycleScope.launch {
            try {
                val firebaseResult = firebaseTripService.getTrips()
                if (firebaseResult.isSuccess) {
                    val firebaseTrips = firebaseResult.getOrThrow()
                    // Clear and re-populate to ensure UI reflects current state
                    allTripsList.clear()
                    // Add mock data first, then Firebase data.
                    // This simple merge strategy might lead to duplicates if mock IDs clash with Firebase IDs.
                    // A more robust strategy would involve merging based on unique IDs,
                    // e.g., if a Firebase trip has an ID matching a mock trip, replace the mock one.
                    allTripsList.addAll(mockTrips)
                    // Add Firebase trips, ensuring no duplicates if Firebase IDs overlap with mock IDs
                    firebaseTrips.forEach { firebaseTrip ->
                        if (!allTripsList.any { it.id == firebaseTrip.id }) {
                            allTripsList.add(firebaseTrip)
                        } else {
                            // If a mock trip with the same ID exists, replace it with the Firebase version
                            val index = allTripsList.indexOfFirst { it.id == firebaseTrip.id }
                            if (index != -1) {
                                allTripsList[index] = firebaseTrip
                                Log.d("MainActivity", "Replaced mock trip with Firebase version: ${firebaseTrip.name}")
                            }
                        }
                    }
                    Log.d("MainActivity", "Trips loaded (Mock + Firebase) successfully. Total: ${allTripsList.size}")
                } else {
                    val errorMessage = firebaseResult.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e("MainActivity", "Failed to load Firebase trips: $errorMessage. Showing mock data only.", firebaseResult.exceptionOrNull())
                    // If Firebase fails, still show mock data to prevent empty list
                    allTripsList.clear()
                    allTripsList.addAll(mockTrips)
                    // TODO: Optionally show a persistent error message to the user about sync issues
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception during combined trip load: ${e.message}", e)
                // Fallback to mock data if any unexpected error occurs during load
                allTripsList.clear()
                allTripsList.addAll(mockTrips)
                // TODO: Show a critical error message to the user
            }
        }
    }
}