package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ui.components.BaseScaffold
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.model.Trip
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.ui.screens.TripCatalogScreen

class TripCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                val navController = rememberNavController()
                var isLoading by remember { mutableStateOf(false) }

                BaseScaffold(
                    navController = navController,
                    isLoading = isLoading,
                    trips = SampleTrips.get(),
                    onTripSelected = { trip ->
                        // Handle trip selection, e.g., navigate to detail
                        navController.navigate("detail/${trip.id}")
                    }
                ) { innerPadding ->
                    TripCatalogScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onTripClick = { tripId ->
                            navController.navigate("detail/$tripId")
                        },
                        navController = navController
                    )
                }
            }
        }
    }
}