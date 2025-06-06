package com.android.tripbook

import android.Manifest
import android.content.pm.PackageManager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.tripbook.ui.components.DateTimePicker
import com.android.tripbook.ui.components.ItineraryBuilder
import com.android.tripbook.ui.components.LocationSelector

import androidx.compose.runtime.*
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.ui.uis.*

import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ Now we're inside the class — 'this' works
        requestLocationPermission()

        setContent {
            TripBookTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ScheduleCreationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    // ✅ This is now inside MainActivity, so 'this' is valid
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
}
@Composable
fun ScheduleCreationScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DateTimePicker { }
        LocationSelector { }
        ItineraryBuilder { }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleCreationScreenPreview() {
    TripBookTheme {
        ScheduleCreationScreen()

                TripBookApp()
            }
        }
    }
}

@Composable
fun TripBookApp() {
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

    when (currentScreen) {
        "MyTrips" -> MyTripsScreen(
            trips = trips,
            onPlanNewTripClick = { currentScreen = "PlanNewTrip" },
            onTripClick = {
                selectedTrip = it
                currentScreen = "TripDetails"
            }
        )
        "PlanNewTrip" -> PlanNewTripScreen(
            onBackClick = { currentScreen = "MyTrips" },
            onTripCreated = { newTrip ->
                trips = trips + newTrip
                currentScreen = "MyTrips"
            }
        )
        "TripDetails" -> TripDetailsScreen(
            trip = selectedTrip ?: trips.first(),
            onBackClick = { currentScreen = "MyTrips" }
      
    }
}
