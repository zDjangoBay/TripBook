package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ui.components.BaseScaffold
import com.android.tripbook.ui.navigation.MainNavGraph
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.data.SampleTrips

class MainActivity : ComponentActivity() {
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
        navController.navigate("detail/${trip.id}")
    }
) { padding ->
    MainNavGraph(
        navController = navController,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    )
}
            }
        }
    }
}
