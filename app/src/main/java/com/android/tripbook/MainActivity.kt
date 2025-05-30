package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.android.tripbook.ui.uis.PlanNewTripScreen
import com.android.tripbook.ui.uis.MyTripsScreen
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                TripBookApp()
            }
        }
    }
}

@Composable
fun TripBookApp() {
    var currentScreen by remember { mutableStateOf("MyTrips") }

    when (currentScreen) {
        "MyTrips" -> MyTripsScreen(onPlanNewTripClick = { currentScreen = "PlanNewTrip" })
        "PlanNewTrip" -> PlanNewTripScreen(onBackClick = { currentScreen = "MyTrips" })
    }
}