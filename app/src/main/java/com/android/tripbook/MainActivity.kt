package com.android.tripbook


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.model.Agency
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripCategory
import com.android.tripbook.model.TripStatus
import com.android.tripbook.repository.SupabaseAgencyRepository
import com.android.tripbook.service.AgencyService
import com.android.tripbook.service.GoogleMapsService
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
import com.android.tripbook.ui.uis.*
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.viewmodel.TripViewModel
import com.android.tripbook.viewmodel.AgencyViewModel
import java.time.LocalDate
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("MainActivity", "onCreate started")

        val nominatimService = NominatimService()
        val travelAgencyService = TravelAgencyService()
        val agencyRepository = SupabaseAgencyRepository.getInstance()

        val apiKey = applicationContext.packageManager
            .getApplicationInfo(packageName, android.content.pm.PackageManager.GET_META_DATA)
            .metaData.getString("com.google.android.geo.API_KEY") ?: ""
        
        Log.d("MainActivity", "Google Maps API Key: ${if (apiKey.isNotEmpty()) "Found" else "Not found"}")

        val googleMapsService = GoogleMapsService(applicationContext, apiKey)

        enableEdgeToEdge()

        try {
            setContent {
                Log.d("MainActivity", "Setting content")
                TripBookTheme {
                    TripBookApp(
                        nominatimService = nominatimService,
                        travelAgencyService = travelAgencyService,
                        googleMapsService = googleMapsService,
                        agencyRepository = agencyRepository
                    )
                }
            }
            Log.d("MainActivity", "Content set successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting content: ${e.message}", e)
        }
    }

    @Composable
    fun TripBookApp(
        nominatimService: NominatimService,
        travelAgencyService: TravelAgencyService,
        googleMapsService: GoogleMapsService,
        agencyRepository: SupabaseAgencyRepository
    ) {
        var currentScreen by remember { mutableStateOf("MyTrips") }
        var selectedTrip by remember { mutableStateOf<Trip?>(null) }
        var selectedDestination by remember { mutableStateOf<String?>(null) }
        var selectedAgency by remember { mutableStateOf<Agency?>(null) } // Added for agency details
        val tripViewModel: TripViewModel = viewModel()
        val agencyViewModel = remember { AgencyViewModel(agencyRepository) }

        LaunchedEffect(Unit) {
            agencyRepository.loadAgencies()
        }

        when (currentScreen) {
            "MyTrips" -> MyTripsScreen(
                tripViewModel = tripViewModel,
                onPlanNewTripClick = { currentScreen = "CreateTrip" },
                onTripClick = { trip ->
                    selectedTrip = trip
                    currentScreen = "TripDetails"
                },
                onAgenciesClick = { currentScreen = "AllAgencies" }
            )

            "CreateTrip" -> TripCreationFlowScreen(
                tripViewModel = tripViewModel,
                agencyViewModel = agencyViewModel,
                onBackClick = { currentScreen = "MyTrips" },
                onTripCreated = { currentScreen = "MyTrips" }
            )

            "PlanNewTrip" -> PlanNewTripScreen(
                onBackClick = { currentScreen = "MyTrips" },
                onTripCreated = { newTrip -> currentScreen = "MyTrips" },
                nominatimService = nominatimService,
                travelAgencyService = travelAgencyService,
                googleMapsService = googleMapsService,
                onBrowseAgencies = { destination ->
                    selectedDestination = destination
                    currentScreen = "TravelAgency"
                }
            )

            "TripDetails" -> TripDetailsScreen(
                trip = selectedTrip ?: Trip(
                    id = "1",
                    name = "Default Trip",
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().plusDays(1),
                    destination = "Unknown",
                    travelers = 1,
                    budget = 0.0,
                    category = TripCategory.CULTURAL,
                    status = TripStatus.PLANNED
                ),
                onBackClick = { currentScreen = "MyTrips" },
                onEditItineraryClick = { currentScreen = "ItineraryBuilder" }
            )

            "ItineraryBuilder" -> ItineraryBuilderScreen(
                trip = selectedTrip ?: Trip(
                    id = "1",
                    name = "Default Trip",
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().plusDays(1),
                    destination = "Unknown",
                    travelers = 1,
                    budget = 0.0,
                    category = TripCategory.CULTURAL,
                    status = TripStatus.PLANNED
                ),
                onBackClick = { currentScreen = "TripDetails" },
                onItineraryUpdated = { updatedItinerary ->
                    selectedTrip?.let { trip ->
                        selectedTrip = trip.copy(itinerary = updatedItinerary)
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
                            tripId = trip.id,
                            date = trip.startDate,
                            time = "10:00",
                            title = service.name,
                            location = service.location,
                            type = type,
                            agencyService = service
                        )

                        selectedTrip = trip.copy(
                            itinerary = trip.itinerary + newItem
                        )
                    }
                    currentScreen = if (selectedTrip == null) "PlanNewTrip" else "ItineraryBuilder"
                }
            )

            "AllAgencies" -> AllAgenciesScreen(
                onBackClick = { currentScreen = "MyTrips" },
                agencyViewModel = agencyViewModel,
                onAgencyClick = { agency ->
                    selectedAgency = agency
                    currentScreen = "AgencyDetail"
                }
            )

            "AgencyDetail" -> AgencyDetailScreen(
                agency = selectedAgency ?: Agency(),
                agencyViewModel = agencyViewModel,
                onBackClick = { currentScreen = "AllAgencies" }
            )
        }
    }
}