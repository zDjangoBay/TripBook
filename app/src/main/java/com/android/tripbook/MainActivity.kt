package com.android.tripbook

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.reservation.navigation.ReservationNavGraph
import com.android.tripbook.reservation.navigation.ReservationDestinations
import com.android.tripbook.ui.theme.TripBookTheme

private const val TAG = "TripBookApp"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Log app startup for debugging
        Log.d(TAG, "Application starting...")

        try {
            enableEdgeToEdge()

            setContent {
                TripBookTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Set up the main navigation
                        val navController = rememberNavController()
                        MainNavHost(navController = navController)
                    }
                }
            }
        } catch (e: Exception) {
            // Log any exceptions during startup
            Log.e(TAG, "Error during app startup", e)
        }
    }
}

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        // Welcome screen
        composable("welcome") {
            WelcomeScreen(
                onReservationsClick = {
                    navController.navigate("reservations")
                }
            )
        }

        // Reservation module
        composable("reservations") {
            ReservationNavGraph(navController = rememberNavController())
        }
    }
}

@Composable
fun WelcomeScreen(onReservationsClick: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Welcome to Trip Book",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your travel companion for exploring Africa and beyond",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onReservationsClick) {
                Text("View My Reservations")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Features:",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 32.dp, end = 32.dp)
            ) {
                Text("• User Profiles")
                Text("• Trip Catalog")
                Text("• Reservations")
                Text("• Company Listings")
                Text("• Trip Scheduling")
                Text("• Social Sharing")
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    TripBookTheme {
        WelcomeScreen(onReservationsClick = {})
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    TripBookTheme {
        ErrorScreen(
            message = "Could not load reservation data",
            onRetry = {}
        )
    }
}