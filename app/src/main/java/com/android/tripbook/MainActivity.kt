package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.service.GoogleMapsService
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
import com.android.tripbook.ui.uis.*
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate

// --- NEW IMPORTS FOR NOTIFICATIONS AND PERMISSIONS ---
import com.android.tripbook.notifications.NotificationHelper
import com.android.tripbook.notifications.NotificationScheduler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.os.Build // Required for Build.VERSION.SDK_INT
import android.util.Log // For logging in the scheduler (optional, but good for debugging)
import androidx.compose.ui.platform.LocalContext // To get context within a Composable
import java.util.UUID


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- NEW: Create Notification Channel on app start ---
        // This should be called once when your app starts.
        NotificationHelper.createNotificationChannel(this)

        val nominatimService = NominatimService()
        val travelAgencyService = TravelAgencyService()

        // ✅ Get API key from manifest
        val apiKey = applicationContext.packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            .metaData.getString("com.google.android.geo.API_KEY") ?: ""

        val googleMapsService = GoogleMapsService(applicationContext, apiKey)

        enableEdgeToEdge()

        setContent {
            // --- NEW: Request Notification Permission within the Composable context ---
            // This launcher handles the result of the permission request.
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("Permission", "POST_NOTIFICATIONS permission granted")
                    // You can now confidently schedule notifications
                } else {
                    Log.d("Permission", "POST_NOTIFICATIONS permission denied")
                    // Optionally, inform the user that notifications won't work
                    // or guide them to app settings to enable notifications.
                }
            }

            // --- NEW: Check and request permission on app start (once) ---
            // This LaunchedEffect runs once when the composable enters the composition.
            LaunchedEffect(Unit) {
                // Check if the Android version is Tiramisu (API 33) or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Check if the permission is already granted
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity, // Use this@MainActivity for the Activity context
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request the permission if it's not granted
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) // Corrected reference
                    }
                }
            }

            TripBookTheme {
                TripBookApp(
                    nominatimService = nominatimService,
                    travelAgencyService = travelAgencyService,
                    googleMapsService = googleMapsService
                )
            }
        }
    }


    @Composable
    fun TripBookApp(
        nominatimService: NominatimService,
        travelAgencyService: TravelAgencyService,
        googleMapsService: GoogleMapsService
    ) {
        // --- NEW: Get the current context within the Composable scope ---
        val context = LocalContext.current

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
                        itinerary = listOf(),
                        type = "Safari"
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
                        itinerary = listOf(),
                        type = "Safari"
                    )
                )
            )
        }

        when (currentScreen) {
            "MyTrips" -> {
                MyTripsScreen(
                    trips = trips,
                    onPlanNewTripClick = {
                        currentScreen = "PlanNewTrip"
                    },
                    onTripClick = { trip ->
                        selectedTrip = trip
                        currentScreen = "TripDetails"
                    },
                    tripViewModel = TODO(),
                    onAgenciesClick = TODO()
                )
            }

            "PlanNewTrip" -> PlanNewTripScreen(
                onBackClick = {
                    currentScreen = "MyTrips"
                },
                onTripCreated = { newTrip ->
                    trips = trips + newTrip
                    // --- NEW: Schedule trip start notifications when a new trip is created ---
                    // Schedule notifications for 7, 3, and 1 day(s) before the trip starts.
                    NotificationScheduler.scheduleTripStartNotification(context, newTrip, 7)
                    NotificationScheduler.scheduleTripStartNotification(context, newTrip, 3)
                    NotificationScheduler.scheduleTripStartNotification(context, newTrip, 1)

                    // --- NEW: Schedule itinerary empty reminder (e.g., 2 days after creation) ---
                    // This is a simple check. For a more robust solution, you might
                    // need a periodic WorkManager task that checks all active trips.
                    if (newTrip.itinerary.isEmpty()) {
                        NotificationScheduler.scheduleItineraryReminder(context, newTrip, 2)
                    }

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

                        // --- NEW: Schedule/Reschedule notifications for each itinerary item ---
                        // When the itinerary is updated, re-schedule notifications for all items.
                        // WorkManager's ExistingWorkPolicy.REPLACE handles updating existing ones.
                        updatedItinerary.forEach { item ->
                            NotificationScheduler.scheduleItineraryItemNotification(
                                context,
                                trip.name,
                                item
                            )
                        }
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
                        // --- IMPORTANT: Ensure ItineraryItem has an 'id' field, as discussed previously. ---
                        // If you haven't updated your ItineraryItem data class, please do so.
                        val newItem = ItineraryItem(
                            id = UUID.randomUUID()
                                .toString(), // Generate a unique ID for the new item
                            date = trip.startDate, // Default to trip start date, user can change later
                            time = "10:00 AM", // Default time, user can change later
                            title = service.name,
                            location = service.location,
                            type = type,
                            agencyService = service
                        )

                        // Update selectedTrip and trips list with the new item
                        val newItinerary = trip.itinerary + newItem
                        selectedTrip = trip.copy(itinerary = newItinerary)
                        trips = trips.map {
                            if (it.id == trip.id) it.copy(itinerary = newItinerary)
                            else it
                        }

                        // --- NEW: Schedule notification for the newly added itinerary item ---
                        NotificationScheduler.scheduleItineraryItemNotification(
                            context,
                            trip.name,
                            newItem
                        )
                    }
                    currentScreen = if (selectedTrip == null) "PlanNewTrip" else "ItineraryBuilder"
                }
            )
        }
    }
}
