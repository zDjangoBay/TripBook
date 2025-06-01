package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.ui.uis.TripCreationFlowScreen
import com.android.tripbook.ui.uis.MyTripsScreen
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.viewmodel.TripViewModel

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
    val tripViewModel: TripViewModel = viewModel()

    when (currentScreen) {
        "MyTrips" -> MyTripsScreen(
            tripViewModel = tripViewModel,
            onPlanNewTripClick = { currentScreen = "CreateTrip" }
        )
        "CreateTrip" -> TripCreationFlowScreen(
            tripViewModel = tripViewModel,
            onBackClick = { currentScreen = "MyTrips" },
            onTripCreated = { currentScreen = "MyTrips" }
        )
    }
}