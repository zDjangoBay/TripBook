package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.service.*
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.ui.uis.*
import com.android.tripbook.utils.ChatLauncher
import com.google.firebase.FirebaseApp
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val nominatimService = NominatimService()
        val travelAgencyService = TravelAgencyService()

        val apiKey = applicationContext.packageManager
            .getApplicationInfo(packageName, android.content.pm.PackageManager.GET_META_DATA)
            .metaData.getString("com.google.android.geo.API_KEY") ?: ""

        val googleMapsService = GoogleMapsService(applicationContext, apiKey)

        enableEdgeToEdge()

        setContent {
            TripBookTheme {
                TripBookApp(
                    nominatimService = nominatimService,
                    travelAgencyService = travelAgencyService,
                    googleMapsService = googleMapsService,
                    openGroupChat = { trip ->
                        ChatLauncher.openGroupChat(this, trip.id, trip.name)
                    }
                )
            }
        }
    }
}

@Composable
fun TripBookApp(
    nominatimService: NominatimService,
    travelAgencyService: TravelAgencyService,
    googleMapsService: GoogleMapsService,
    openGroupChat: (Trip) -> Unit
) {
    var currentScreen by remember { mutableStateOf("MyTrips") }
    var selectedTrip by remember { mutableStateOf<Trip?>(null) }
    var selectedDestination by remember { mutableStateOf<String?>(null) }

    var trips by remember {
        mutableStateOf(
            listOf(
                Trip(
                    id = "1",
                    name = "seme beach",
                    startDate = LocalDate.of(2024, 12, 15),
                    endDate = LocalDate.of(2024, 12, 22),
                    destination = "limbe , Yaounde",
                    travelers = 4,
                    budget = 2400,
                    status = TripStatus.PLANNED,
                    type = "Safari",
                    description = "An amazing safari adventure through Kenya and Tanzania",
                    itinerary = listOf()
                ),
                Trip(
                    id = "2",
                    name = "Sanaga beach",
                    startDate = LocalDate.of(2025, 1, 10),
                    endDate = LocalDate.of(2025, 1, 18),
                    destination = "Bandjock Cameroon",
                    travelers = 2,
                    budget = 1800,
                    status = TripStatus.ACTIVE,
                    itinerary = listOf()
                ),
                Trip(
                    id = "3",
                    name = "Mount Cameroon",
                    startDate = LocalDate.of(2024, 9, 5),
                    endDate = LocalDate.of(2024, 9, 12),
                    destination = "limbe, Cammeroon",
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
            },
            onGroupChatClick = {
                selectedTrip?.let { openGroupChat(it) }
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
            onServiceSelected = { service ->
                val typeResult = runCatching {
                    ItineraryType.valueOf(service.type.uppercase())
                }

                if (typeResult.isSuccess) {
                    val itineraryType = typeResult.getOrNull()

                    selectedTrip?.let { trip ->
                        val newItem = ItineraryItem(
                            date = trip.startDate,
                            time = "10:00 AM",
                            title = service.name,
                            location = service.location,
                            type = itineraryType!!,
                            agencyService = null
                        )

                        selectedTrip = trip.copy(
                            itinerary = trip.itinerary + newItem
                        )

                        trips = trips.map {
                            if (it.id == trip.id) it.copy(itinerary = trip.itinerary + newItem)
                            else it
                        }
                    }
                }

                currentScreen = if (selectedTrip == null) "PlanNewTrip" else "ItineraryBuilder"
            }
        )
    }
}
