package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
<<<<<<< HEAD:app/src/main/java/com/android/tripbook/reservation/fogangmenyefortunybachyr/ictu20223271/reservationpayment/MainActivity.kt
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.ReservationsDashboard
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme.TripBookTheme
=======
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.screens.DashboardActivity
import com.android.tripbook.ui.theme.TripBookTheme
>>>>>>> 12242288031572a75e23f42979d5f32ce1058d8c:app/src/main/java/com/android/tripbook/MainActivity.kt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD:app/src/main/java/com/android/tripbook/reservation/fogangmenyefortunybachyr/ictu20223271/reservationpayment/MainActivity.kt
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReservationsDashboard(modifier = Modifier.padding(innerPadding))
=======
        Log.d("TripBook", "MainActivity onCreate started")

        try {
            enableEdgeToEdge()
            setContent {
                TripBookTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Progressive loading with error handling
                        SafeDashboardLoader()
                    }
>>>>>>> 12242288031572a75e23f42979d5f32ce1058d8c:app/src/main/java/com/android/tripbook/MainActivity.kt
                }
            }
            Log.d("TripBook", "MainActivity onCreate completed successfully")
        } catch (e: Exception) {
            Log.e("TripBook", "Error in MainActivity onCreate", e)
            throw e
        }
    }
<<<<<<< HEAD:app/src/main/java/com/android/tripbook/reservation/fogangmenyefortunybachyr/ictu20223271/reservationpayment/MainActivity.kt
=======
}

@Composable
fun SafeDashboardLoader() {
    var showDashboard by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (errorMessage != null) {
        // Show error screen
        ErrorScreen(
            error = errorMessage!!,
            onRetry = {
                errorMessage = null
                showDashboard = false
            },
            onUseTestScreen = {
                errorMessage = null
                showDashboard = false
            }
        )
    } else if (showDashboard) {
        // Load dashboard with logging
        Log.d("TripBook", "=== DASHBOARD LOADING START ===")
        Log.d("TripBook", "Step 1: About to call DashboardActivity composable")
        Log.d("TripBook", "Step 2: Testing DashboardActivity import")
        Log.d("TripBook", "Step 3: Calling real DashboardActivity")

        DashboardActivity()

        Log.d("TripBook", "Step 4: DashboardActivity completed successfully")
        Log.d("TripBook", "=== DASHBOARD LOADING END ===")
    } else {
        // Show test screen with option to load dashboard
        TestScreen(
            onLoadDashboard = {
                Log.d("TripBook", "=== USER CLICKED LOAD DASHBOARD ===")
                Log.d("TripBook", "Setting showDashboard = true")
                showDashboard = true
                Log.d("TripBook", "showDashboard state updated")
            }
        )
    }
}

@Composable
fun TestScreen(onLoadDashboard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TripBook Test Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Basic app functionality is working!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                Log.d("TripBook", "Test button clicked")
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Test Button")
        }

        Button(
            onClick = onLoadDashboard,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Load Dashboard")
        }
    }
}

@Composable
fun ErrorScreen(
    error: String,
    onRetry: () -> Unit,
    onUseTestScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error Occurred",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Retry Dashboard")
        }

        Button(
            onClick = onUseTestScreen,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Use Test Screen")
        }
    }
}

@Composable
fun MinimalDashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TripBook Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Dashboard loaded successfully!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                Log.d("TripBook", "Dashboard button clicked")
            }
        ) {
            Text("Dashboard Action")
        }
    }
}

@Composable
fun SimpleDashboardTest() {
    Log.d("TripBook", "SimpleDashboardTest: Starting")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Simple Dashboard Test",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "This is a basic dashboard replacement",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                Log.d("TripBook", "Simple dashboard button clicked")
            }
        ) {
            Text("Test Dashboard Button")
        }
    }

    Log.d("TripBook", "SimpleDashboardTest: Completed")
>>>>>>> 12242288031572a75e23f42979d5f32ce1058d8c:app/src/main/java/com/android/tripbook/MainActivity.kt
}