package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.service.AgencyService
import com.android.tripbook.service.GoogleMapsService
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
import com.android.tripbook.ui.uis.*
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate

// Add Firebase import
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase FIRST
        FirebaseApp.initializeApp(this)

        val nominatimService = NominatimService()
        val travelAgencyService = TravelAgencyService()

       //api hardcoded by me for testing purposes use wisely
        val apiKey = "AIzaSyDub6cdRg9_19vQn_qV4oQurf9L67LKPPA"

        enableEdgeToEdge()

        setContent {
            TripBookTheme {
                TripBookApp(
                    nominatimService = nominatimService,
                    travelAgencyService = travelAgencyService,
                    apiKey = apiKey // Pass the API key to TripBookApp
                )
            }
        }
    }


    @Composable
    fun TripBookApp(
        nominatimService: NominatimService,
        travelAgencyService: TravelAgencyService,
        apiKey: String // Accept API key here
    ) {
        var currentScreen by remember { mutableStateOf("MyTrips") }
        var selectedTrip by remember { mutableStateOf<Trip?>(null) }
        var selectedDestination by remember { mutableStateOf<String?>(null) }


        var trips by remember {
            mutableStateOf(
                listOf(
                    Trip(
                        id = "1",
                        name = "Safari Adventure",
                        startDate = LocalDate.of(2024, 12, 15),
                        endDate = LocalDate.of(2024, 12, 22),
                        destination = "Kenya, Tanzania",
                        travelers = 4,
                        budget = 2400,
                        status = TripStatus.PLANNED,
                        type = "Safari",
                        description = "An amazing safari adventure through Kenya and Tanzania",
                        itinerary = listOf()
                    ),
                    Trip(
                        id = "2",
                        name = "Morocco Discovery",
                        startDate = LocalDate.of(2025, 1, 10),
                        endDate = LocalDate.of(2025, 1, 18),
                        destination = "Marrakech, Morocco",
                        travelers = 2,
                        budget = 1800,
                        status = TripStatus.ACTIVE,
                        itinerary = listOf()
                    ),
                    Trip(
                        id = "3",
                        name = "Cape Town Explorer",
                        startDate = LocalDate.of(2024, 9, 5),
                        endDate = LocalDate.of(2024, 9, 12),
                        destination = "Cape Town, South Africa",
                        travelers = 6,
                        budget = 3200,
                        status = TripStatus.COMPLETED,
                        itinerary = listOf()
                    )
                )
            )
        }

        when (currentScreen) {
            "MyTrips" -> MyTripsScreen(
                trips = trips,
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
                    trips = trips + newTrip
                    currentScreen = "MyTrips"
                },
                nominatimService = nominatimService,
                travelAgencyService = travelAgencyService,
                apiKey = apiKey, // Pass apiKey to PlanNewTripScreen
                onBrowseAgencies = { destination ->
                    selectedDestination = destination
                    currentScreen = "TravelAgency"
                }
            )

            "TripDetails" -> TripDetailsScreen(
                trip = selectedTrip ?: trips.first(),
                onBackClick = {
                    currentScreen = "MyTrips"
                },
                onEditItineraryClick = {
                    currentScreen = "ItineraryBuilder"
                },
                apiKey = apiKey
            )

            "ItineraryBuilder" -> ItineraryBuilderScreen(
                trip = selectedTrip ?: trips.first(),
                onBackClick = {
                    currentScreen = "TripDetails"
                },
                onItineraryUpdated = { updatedItinerary ->
                    selectedTrip?.let { trip ->
                        trips = trips.map {
                            if (it.id == trip.id)
                                it.copy(itinerary = updatedItinerary)
                            else
                                it
                        }
                        selectedTrip = selectedTrip?.copy(itinerary = updatedItinerary)
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

                        selectedTrip = trip.copy(
                            itinerary = trip.itinerary + newItem
                        )

                        trips = trips.map {
                            if (it.id == trip.id) it.copy(itinerary = trip.itinerary + newItem)
                            else it
                        }
                    }
                    currentScreen = if (selectedTrip == null) "PlanNewTrip" else "ItineraryBuilder"
                }
            )
        }
    }
}