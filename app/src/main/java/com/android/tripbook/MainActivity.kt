package com.android.tripbook

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.android.tripbook.model.*
import com.android.tripbook.service.AgencyService
import com.android.tripbook.service.GoogleMapsService
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
import com.android.tripbook.ui.uis.*
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nominatimService = NominatimService()
        val travelAgencyService = TravelAgencyService()

        // Get API key from manifest
        val apiKey = applicationContext.packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            .metaData.getString("com.google.android.geo.API_KEY") ?: ""

        val googleMapsService = GoogleMapsService(applicationContext, apiKey)

        enableEdgeToEdge()

        setContent {
            TripBookTheme {
                TripBookApp(
                    nominatimService = nominatimService,
                    travelAgencyService = travelAgencyService,
                    googleMapsService = googleMapsService
                )
            }
        }
    }
}

@Composable
fun TripBookApp(
    nominatimService: NominatimService,
    travelAgencyService: TravelAgencyService,
    googleMapsService: GoogleMapsService
) {
    var currentScreen by remember { mutableStateOf("MyTrips") }
    var selectedTrip by remember {
        mutableStateOf<Trip?>(null)
    }
    var selectedDestination by remember { mutableStateOf<String?>(null) }

    var trips by remember {
        mutableStateOf(
            listOf(
                Trip(
                    id = "1",
                    name = "Safari Adventure",
                    destination = "Kenya, Tanzania",
                    startDate = LocalDate.of(2024, 12, 15),
                    endDate = LocalDate.of(2024, 12, 22),
                    budget = 2400.0,
                    itinerary = listOf(),
                    travelers = 4,
                    status = TripStatus.PLANNED,
                    type = "Safari",
                    destinationCoordinates = Coordinates(latitude = -1.2921, longitude = 36.8219) // Nairobi
                ),
                Trip(
                    id = "2",
                    name = "Morocco Discovery",
                    destination = "Marrakech, Morocco",
                    startDate = LocalDate.of(2025, 1, 10),
                    endDate = LocalDate.of(2025, 1, 18),
                    budget = 1800.0,
                    itinerary = listOf(),
                    travelers = 2,
                    destinationCoordinates = Coordinates(latitude = 31.6295, longitude = -7.9811), // Marrakech
                    status = TripStatus.PLANNED,
                    type = "Cultural"
                ),
                Trip(
                    id = "3",
                    name = "Cape Town Explorer",
                    destination = "Cape Town, South Africa",
                    startDate = LocalDate.of(2024, 9, 5),
                    endDate = LocalDate.of(2024, 9, 12),
                    budget = 3200.0,
                    itinerary = listOf(),
                    travelers = 6,
                    destinationCoordinates = Coordinates(latitude = -33.9249, longitude = 18.4241), // Cape Town
                    status = TripStatus.PLANNED,
                    type = "Adventure"
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
            googleMapsService = googleMapsService,
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
            }
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
                    // Create a new ItineraryItem with the correct parameters
                    val newItem = ItineraryItem(
                        id = UUID.randomUUID().toString(),
                        title = service.name,
                        date = trip.startDate,
                        time = "10:00 AM",
                        location = service.location,
                        type = type,
                        notes = "Booked via ${service.name} for $${service.price}",
                        coordinates = null,
                        routeToNext = null,
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