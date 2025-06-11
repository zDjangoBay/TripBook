package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

        // TODO: Consider using dependency injection framework like Hilt for better testability
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

    // Helper function to get current trip safely
    fun getCurrentTrip(): Trip? {
        return selectedTrip ?: trips.firstOrNull()
    }

    // Helper function to safely map service type to ItineraryType
    fun mapServiceTypeToItineraryType(serviceType: String): ItineraryType? {
        return when (serviceType.uppercase()) {
            "ACTIVITY", "TOUR" -> ItineraryType.ACTIVITY
            "ACCOMMODATION", "HOTEL" -> ItineraryType.ACCOMMODATION
            "TRANSPORTATION", "TRANSPORT" -> ItineraryType.TRANSPORTATION
            else -> null
        }
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

        "TripDetails" -> {
            val currentTrip = getCurrentTrip()
            if (currentTrip != null) {
                TripDetailsScreen(
                    trip = currentTrip,
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
            } else {
                // Handle case where no trips exist - redirect to MyTrips
                currentScreen = "MyTrips"
            }
        }

        "ItineraryBuilder" -> {
            val currentTrip = getCurrentTrip()
            if (currentTrip != null) {
                ItineraryBuilderScreen(
                    trip = currentTrip,
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
            } else {
                // Handle case where no trips exist - redirect to MyTrips
                currentScreen = "MyTrips"
            }
        }

        "TravelAgency" -> {
            // TODO: Implement TravelAgencyScreen or create a placeholder
            // For now, we'll create a simple placeholder screen
            TravelAgencyPlaceholderScreen(
                destination = selectedDestination ?: "",
                onBackClick = {
                    currentScreen = if (selectedTrip == null) "PlanNewTrip" else "ItineraryBuilder"
                },
                onServiceSelected = { serviceName: String, serviceType: String, serviceLocation: String ->
                    // Fixed: Explicit parameter types for the lambda
                    val itineraryType = mapServiceTypeToItineraryType(serviceType)

                    if (itineraryType != null) {
                        selectedTrip?.let { trip ->
                            val newItem = ItineraryItem(
                                date = trip.startDate,
                                time = "10:00 AM",
                                title = serviceName,
                                location = serviceLocation,
                                type = itineraryType,
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
                    } else {
                        // Handle unknown service type - could show error message or log
                        println("Warning: Unknown service type '$serviceType' - defaulting to ACTIVITY")

                        selectedTrip?.let { trip ->
                            val newItem = ItineraryItem(
                                date = trip.startDate,
                                time = "10:00 AM",
                                title = serviceName,
                                location = serviceLocation,
                                type = ItineraryType.ACTIVITY, // Default fallback
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
}

// Temporary placeholder screen until TravelAgencyScreen is implemented
@Composable
fun TravelAgencyPlaceholderScreen(
    destination: String,
    onBackClick: () -> Unit,
    onServiceSelected: (String, String, String) -> Unit
) {
    // This is a placeholder implementation
    // Replace this with your actual TravelAgencyScreen implementation
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Travel Agency Screen for $destination")
        Button(onClick = onBackClick) {
            Text("Back")
        }
        Button(onClick = {
            onServiceSelected("Sample Service", "ACTIVITY", destination)
        }) {
            Text("Add Sample Service")
        }
    }
}