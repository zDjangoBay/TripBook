package com.android.tripbook



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.model.Agency
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.NavigationItems
import com.android.tripbook.repository.SupabaseAgencyRepository
import com.android.tripbook.service.GoogleMapsService
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
import com.android.tripbook.ui.uis.*
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.viewmodel.TripViewModel
import com.android.tripbook.viewmodel.AgencyViewModel
import com.android.tripbook.viewmodel.UserViewModel
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nominatimService = NominatimService()
        val travelAgencyService = TravelAgencyService()
        val agencyRepository = SupabaseAgencyRepository.getInstance()

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
                    agencyRepository = agencyRepository
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TripBookApp(
        nominatimService: NominatimService,
        travelAgencyService: TravelAgencyService,
        googleMapsService: GoogleMapsService,
        agencyRepository: SupabaseAgencyRepository
    ) {
        var selectedTab by remember { mutableIntStateOf(1) } // Start with Trips (index 1)
        var currentScreen by remember { mutableStateOf("MyTrips") }
        var selectedTrip by remember { mutableStateOf<Trip?>(null) }
        var selectedDestination by remember { mutableStateOf<String?>(null) }
        var selectedAgency by remember { mutableStateOf<Agency?>(null) }

        val tripViewModel: TripViewModel = viewModel()
        val agencyViewModel = remember { AgencyViewModel(agencyRepository) }
        val userViewModel: UserViewModel = viewModel()

        LaunchedEffect(Unit) {
            agencyRepository.loadAgencies()
        }

        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar(currentScreen)) {
                    NavigationBar(
                        modifier = Modifier.height(80.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationItems.items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == index) item.selectedIcon else item.icon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) },
                                selected = selectedTab == index,
                                onClick = {
                                    selectedTab = index
                                    when (item.route) {
                                        "home" -> currentScreen = "Home"
                                        "my_trips" -> currentScreen = "MyTrips"
                                        "add_trip" -> currentScreen = "CreateTrip"
                                        "profile" -> currentScreen = "Profile"
                                        "settings" -> currentScreen = "Settings"
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    "Home" -> MyTripsScreen(
                        tripViewModel = tripViewModel,
                        onPlanNewTripClick = {
                            selectedTab = 2
                            currentScreen = "CreateTrip"
                        },
                        onTripClick = { trip ->
                            selectedTrip = trip
                            currentScreen = "TripDetails"
                        },
                        onAgenciesClick = { currentScreen = "AllAgencies" },
                        onNearbyUsersClick = { currentScreen = "NearbyUsers" }
                    )

                    "MyTrips" -> MyTripsScreen(
                        tripViewModel = tripViewModel,
                        onPlanNewTripClick = {
                            selectedTab = 2
                            currentScreen = "CreateTrip"
                        },
                        onTripClick = { trip ->
                            selectedTrip = trip
                            currentScreen = "TripDetails"
                        },
                        onAgenciesClick = { currentScreen = "AllAgencies" },
                        onNearbyUsersClick = { currentScreen = "NearbyUsers" }
                    )

                    "CreateTrip" -> TripCreationFlowScreen(
                        tripViewModel = tripViewModel,
                        agencyViewModel = agencyViewModel,
                        onBackClick = {
                            selectedTab = 1
                            currentScreen = "MyTrips"
                        },
                        onTripCreated = {
                            selectedTab = 1
                            currentScreen = "MyTrips"
                        }
                    )

                    "Profile" -> ProfileScreen()

                    "Settings" -> SettingsScreen()

                    "PlanNewTrip" -> PlanNewTripScreen(
                        onBackClick = {
                            selectedTab = 1
                            currentScreen = "MyTrips"
                        },
                        onTripCreated = { newTrip ->
                            selectedTab = 1
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
                        trip = selectedTrip ?: Trip(
                            id = "1",
                            name = "Default Trip",
                            startDate = LocalDate.now(),
                            endDate = LocalDate.now().plusDays(1),
                            destination = "Unknown",
                            travelers = 1,
                            budget = 0,
                            status = TripStatus.PLANNED
                        ),
                        onBackClick = {
                            selectedTab = 1
                            currentScreen = "MyTrips"
                        },
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
                            budget = 0,
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
                        onBackClick = {
                            selectedTab = 0
                            currentScreen = "Home"
                        },
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

                    "NearbyUsers" -> NearbyUsersScreen(
                        onBackClick = {
                            selectedTab = 1
                            currentScreen = "MyTrips"
                        },
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }

    private fun shouldShowBottomBar(currentScreen: String): Boolean {
        return when (currentScreen) {
            "Home", "MyTrips", "CreateTrip", "Profile", "Settings" -> true
            else -> false
        }
    }
}