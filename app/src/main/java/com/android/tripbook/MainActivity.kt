package com.android.tripbook


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.android.tripbook.manager.NotificationManager
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.ui.uis.*
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize notification manager
        notificationManager = NotificationManager(this)
        notificationManager.initialize()

        setContent {
            TripBookTheme {
                TripBookApp(notificationManager = notificationManager)
            }
        }
    }
}

@Composable
fun TripBookApp(notificationManager: NotificationManager) {
    var currentScreen by remember { mutableStateOf("MyTrips") }
    var selectedTrip by remember { mutableStateOf<Trip?>(null) }
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
                    activities = listOf(),
                    expenses = listOf(),
                    travelersList = listOf()
                ),
                Trip(
                    id = "2",
                    name = "Morocco Discovery",
                    startDate = LocalDate.of(2025, 1, 10),
                    endDate = LocalDate.of(2025, 1, 18),
                    destination = "Marrakech, Fez",
                    travelers = 2,
                    budget = 1800,
                    status = TripStatus.ACTIVE
                ),
                Trip(
                    id = "3",
                    name = "Cape Town Explorer",
                    startDate = LocalDate.of(2024, 9, 5),
                    endDate = LocalDate.of(2024, 9, 12),
                    destination = "South Africa",
                    travelers = 6,
                    budget = 3200,
                    status = TripStatus.COMPLETED
                )
            )
        )
    }

    // Initialize sample notifications for demo
    LaunchedEffect(trips) {
        if (trips.isNotEmpty()) {
            notificationManager.createSampleNotifications(trips)
            // Schedule notifications for all planned trips
            trips.filter { it.status == TripStatus.PLANNED }.forEach { trip ->
                notificationManager.scheduleNotificationsForTrip(trip)
            }
        }
    }

    when (currentScreen) {
        "MyTrips" -> MyTripsScreen(
            trips = trips,
            notificationManager = notificationManager,
            onPlanNewTripClick = { currentScreen = "PlanNewTrip" },
            onNotificationsClick = { currentScreen = "Notifications" },
            onTestNotificationsClick = { currentScreen = "TestNotifications" },
            onTripClick = {
                selectedTrip = it
                currentScreen = "TripDetails"
            }
        )
        "PlanNewTrip" -> PlanNewTripScreen(
            onBackClick = { currentScreen = "MyTrips" },
            onTripCreated = { newTrip ->
                trips = trips + newTrip
                // Schedule notifications for the new trip
                notificationManager.scheduleNotificationsForTrip(newTrip)
                currentScreen = "MyTrips"
            }
        )
        "TripDetails" -> TripDetailsScreen(
            trip = selectedTrip ?: trips.first(),
            onBackClick = { currentScreen = "MyTrips" }
        )

        "Notifications" -> NotificationsScreen(
            notificationManager = notificationManager,
            onBackClick = { currentScreen = "MyTrips" },
            onSettingsClick = { currentScreen = "NotificationSettings" },
            onNotificationClick = { notification ->
                // Navigate to related trip if available
                val trip = trips.find { it.id == notification.tripId }
                if (trip != null) {
                    selectedTrip = trip
                    currentScreen = "TripDetails"
                }
            }
        )

        "NotificationSettings" -> NotificationSettingsScreen(
            preferences = notificationManager.preferences.collectAsState().value,
            onPreferencesChanged = { newPreferences ->
                notificationManager.updatePreferences(newPreferences, trips)
            },
            onBackClick = { currentScreen = "Notifications" }
        )

        "TestNotifications" -> TestNotificationScreen(
            notificationManager = notificationManager,
            trips = trips,
            onBackClick = { currentScreen = "MyTrips" }
        )
    }
}