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
import com.android.tripbook.service.FirebaseTripService // Import the new service
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

        FirebaseApp.initializeApp(this)

        val nominatimService = NominatimService()
        val travelAgencyService = TravelAgencyService()

        val apiKey = "AIzaSyDub6cdRg9_19vQn_qV4oQurf9L67LKPPA"

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

        // Use LaunchedEffect to load trips when the Composable first enters the composition
        LaunchedEffect(Unit) { // Use Unit as key to run once
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
                        val result = firebaseTripService.saveTrip(newTrip)
                        if (result.isSuccess) {
                            Log.d("MainActivity", "New trip saved successfully to Firebase.")
                            // After saving, reload all trips to update the UI
                            loadCombinedTrips(firebaseTripService, mockTrips, allTrips)
                        } else {
                            Log.e("MainActivity", "Failed to save new trip: ${result.exceptionOrNull()?.message}")
                            // Handle error, maybe show a toast
                        }
                        currentScreen = "MyTrips" // Navigate back regardless of save success for now
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

            // ... (rest of your when statement remains the same)
            "TripDetails" -> TripDetailsScreen(
                // Use selectedTrip or default to the first trip if selectedTrip is null
                trip = selectedTrip ?: allTrips.firstOrNull() ?: Trip( // Safely get first or provide a default empty trip
                    id = "default", name = "Default Trip",
                    startDate = LocalDate.now(), endDate = LocalDate.now(),
                    destination = "", travelers = 0, budget = 0, status = TripStatus.PLANNED
                ),
                onBackClick = {
                    currentScreen = "MyTrips"
                },
                onEditItineraryClick = {
                    currentScreen = "ItineraryBuilder"
                },
                apiKey = apiKey
            )

            "ItineraryBuilder" -> ItineraryBuilderScreen(
                trip = selectedTrip ?: allTrips.firstOrNull() ?: Trip( // Safely get first or provide a default empty trip
                    id = "default", name = "Default Trip",
                    startDate = LocalDate.now(), endDate = LocalDate.now(),
                    destination = "", travelers = 0, budget = 0, status = TripStatus.PLANNED
                ),
                onBackClick = {
                    currentScreen = "TripDetails"
                },
                onItineraryUpdated = { updatedItinerary ->
                    selectedTrip?.let { trip ->
                        // Update the trip locally in allTrips for immediate UI reflection
                        // Find the trip and replace it
                        val updatedTrip = trip.copy(itinerary = updatedItinerary)
                        val index = allTrips.indexOfFirst { it.id == updatedTrip.id }
                        if (index != -1) {
                            allTrips[index] = updatedTrip
                        }

                        // Then save to Firebase (only if it was not a mock trip, or if it had an ID generated by Firebase)
                        // A more robust solution would distinguish between mock and Firebase-origin trips
                        // For simplicity, we'll attempt to save it if it has a proper ID.
                        if (updatedTrip.id != "default" && !mockTrips.contains(updatedTrip)) { // Avoid saving mock data back
                            lifecycleScope.launch {
                                val result = firebaseTripService.saveTrip(updatedTrip)
                                if (result.isSuccess) {
                                    Log.d("MainActivity", "Trip itinerary updated and saved to Firebase.")
                                    // No need to reload all trips here, as the local list was updated
                                } else {
                                    Log.e("MainActivity", "Failed to update trip itinerary: ${result.exceptionOrNull()?.message}")
                                }
                            }
                        }
                        selectedTrip = updatedTrip
                    }
                },
                nominatimService = nominatimService,
                travelAgencyService = travelAgencyService,
                onBrowseAgencies = { destination ->
                    selectedDestination = destination
                    currentScreen = "TravelAgency"
                }
            )

            "TravelAgency" -> TravelAgencyScreen(
                destination = selectedDestination ?: "",
                travelAgencyService = travelAgencyService,
                onBackClick = {
                    currentScreen = if (selectedTrip == null) "PlanNewTrip" else "ItineraryBuilder"
                },
                onServiceSelected = { service, type ->
                    selectedTrip?.let { trip ->
                        val newItem = ItineraryItem(
                            date = trip.startDate,
                            time = "10:00 AM",
                            title = service.name,
                            location = service.location,
                            type = type,
                            agencyService = service
                        )

                        // Update the local itinerary for immediate UI reflection
                        val updatedItinerary = trip.itinerary + newItem
                        val updatedTrip = trip.copy(itinerary = updatedItinerary)

                        // Update the trip in the main 'allTrips' list
                        val index = allTrips.indexOfFirst { it.id == updatedTrip.id }
                        if (index != -1) {
                            allTrips[index] = updatedTrip
                        }

                        // Save the updated trip to Firebase (again, only if it's not a mock trip)
                        if (updatedTrip.id != "default" && !mockTrips.contains(updatedTrip)) {
                            lifecycleScope.launch {
                                val result = firebaseTripService.saveTrip(updatedTrip)
                                if (result.isSuccess) {
                                    Log.d("MainActivity", "Itinerary item added and trip saved to Firebase.")
                                } else {
                                    Log.e("MainActivity", "Failed to add itinerary item: ${result.exceptionOrNull()?.message}")
                                }
                            }
                        }
                        selectedTrip = updatedTrip
                    }
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
            val firebaseResult = firebaseTripService.getTrips()
            if (firebaseResult.isSuccess) {
                val firebaseTrips = firebaseResult.getOrThrow()
                // Combine mock data with Firebase data
                // To avoid duplicates if mock data also has Firebase IDs,
                // you might want to filter or merge based on IDs.
                // For simplicity, we'll just add them all here.
                allTripsList.clear()
                allTripsList.addAll(mockTrips) // Add mock data first
                allTripsList.addAll(firebaseTrips) // Add Firebase data
                Log.d("MainActivity", "Trips loaded (Mock + Firebase) successfully.")
            } else {
                Log.e("MainActivity", "Failed to load Firebase trips: ${firebaseResult.exceptionOrNull()?.message}")
                // If Firebase fails, still show mock data
                allTripsList.clear()
                allTripsList.addAll(mockTrips)
            }
        }
    }
}