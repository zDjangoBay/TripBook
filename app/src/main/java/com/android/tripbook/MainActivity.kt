package com.android.tripbook


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.android.tripbook.manager.NotificationManager
import com.android.tripbook.model.*
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
                    description = "An amazing safari adventure through Kenya and Tanzania with wildlife viewing and cultural experiences",
                    activities = listOf(
                        Activity(
                            date = LocalDate.of(2024, 12, 16),
                            time = "06:00",
                            title = "Masai Mara Game Drive",
                            location = "Masai Mara National Reserve",
                            description = "Early morning game drive to spot the Big Five"
                        ),
                        Activity(
                            date = LocalDate.of(2024, 12, 17),
                            time = "14:00",
                            title = "Maasai Village Visit",
                            location = "Local Maasai Village",
                            description = "Cultural experience with traditional Maasai community"
                        ),
                        Activity(
                            date = LocalDate.of(2024, 12, 19),
                            time = "08:00",
                            title = "Serengeti Migration",
                            location = "Serengeti National Park",
                            description = "Witness the great wildebeest migration"
                        )
                    ),
                    expenses = listOf(
                        Expense(category = "Accommodation", description = "Safari Lodge", amount = 800),
                        Expense(category = "Transportation", description = "4WD Vehicle Rental", amount = 600),
                        Expense(category = "Food", description = "Meals & Drinks", amount = 400),
                        Expense(category = "Activities", description = "Game Drives & Tours", amount = 600)
                    ),
                    travelersList = listOf(
                        Traveler(name = "Oliver Chowaine", isLeader = true),
                        Traveler(name = "Sarah Johnson", isLeader = false),
                        Traveler(name = "Michael Chen", isLeader = false),
                        Traveler(name = "Emma Williams", isLeader = false)
                    )
                ),
                Trip(
                    id = "2",
                    name = "Morocco Discovery",
                    startDate = LocalDate.of(2025, 1, 10),
                    endDate = LocalDate.of(2025, 1, 18),
                    destination = "Marrakech, Fez",
                    travelers = 2,
                    budget = 1800,
                    status = TripStatus.ACTIVE,
                    type = "Cultural",
                    description = "Explore the imperial cities of Morocco with their rich history and vibrant markets",
                    activities = listOf(
                        Activity(
                            date = LocalDate.of(2025, 1, 11),
                            time = "10:00",
                            title = "Jemaa el-Fnaa Square",
                            location = "Marrakech",
                            description = "Explore the famous main square and marketplace"
                        ),
                        Activity(
                            date = LocalDate.of(2025, 1, 13),
                            time = "09:00",
                            title = "Atlas Mountains Day Trip",
                            location = "High Atlas Mountains",
                            description = "Hiking and Berber village visit"
                        )
                    ),
                    expenses = listOf(
                        Expense(category = "Accommodation", description = "Riad Booking", amount = 600),
                        Expense(category = "Transportation", description = "Domestic Flights", amount = 400),
                        Expense(category = "Food", description = "Traditional Cuisine", amount = 300),
                        Expense(category = "Shopping", description = "Souvenirs & Crafts", amount = 500)
                    ),
                    travelersList = listOf(
                        Traveler(name = "NguRussel", isLeader = true),
                        Traveler(name = "Amina Hassan", isLeader = false)
                    )
                ),
                Trip(
                    id = "3",
                    name = "Cape Town Explorer",
                    startDate = LocalDate.of(2024, 9, 5),
                    endDate = LocalDate.of(2024, 9, 12),
                    destination = "South Africa",
                    travelers = 6,
                    budget = 3200,
                    status = TripStatus.COMPLETED,
                    type = "Adventure",
                    description = "Complete South African experience with wine tours, wildlife, and stunning landscapes",
                    activities = listOf(
                        Activity(
                            date = LocalDate.of(2024, 9, 6),
                            time = "08:00",
                            title = "Table Mountain Cable Car",
                            location = "Table Mountain",
                            description = "Scenic cable car ride with panoramic city views"
                        ),
                        Activity(
                            date = LocalDate.of(2024, 9, 8),
                            time = "10:00",
                            title = "Wine Tasting Tour",
                            location = "Stellenbosch",
                            description = "Visit multiple wineries in the Cape Winelands"
                        ),
                        Activity(
                            date = LocalDate.of(2024, 9, 10),
                            time = "07:00",
                            title = "Penguin Colony Visit",
                            location = "Boulders Beach",
                            description = "See African penguins in their natural habitat"
                        )
                    ),
                    expenses = listOf(
                        Expense(category = "Accommodation", description = "Luxury Hotel", amount = 1200),
                        Expense(category = "Transportation", description = "Car Rental & Fuel", amount = 500),
                        Expense(category = "Food", description = "Fine Dining", amount = 800),
                        Expense(category = "Activities", description = "Tours & Excursions", amount = 700)
                    ),
                    travelersList = listOf(
                        Traveler(name = "David Thompson", isLeader = true),
                        Traveler(name = "Lisa Anderson", isLeader = false),
                        Traveler(name = "James Wilson", isLeader = false),
                        Traveler(name = "Maria Garcia", isLeader = false),
                        Traveler(name = "Robert Brown", isLeader = false),
                        Traveler(name = "Jennifer Davis", isLeader = false)
                    )
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