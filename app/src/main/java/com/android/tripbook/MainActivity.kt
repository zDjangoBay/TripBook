package com.android.tripbook

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.screens.DashboardActivity
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("TripBook", "MainActivity onCreate started")

        try {
            setContent {
                TripBookTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // You can switch to SafeDashboardLoader() if testing
                        DashboardActivity()
                    }
                }
            }
            Log.d("TripBook", "MainActivity onCreate completed successfully")
        } catch (e: Exception) {
            Log.e("TripBook", "Error in MainActivity onCreate", e)
            throw e
        }
    }
}

// --- Optional reusable components for testing/debugging below ---

@Composable
fun SafeDashboardLoader() {
    var showDashboard by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (errorMessage != null) {
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
        Log.d("TripBook", "=== DASHBOARD LOADING START ===")
        DashboardActivity()
        Log.d("TripBook", "=== DASHBOARD LOADING END ===")
    } else {
        TestScreen {
            showDashboard = true
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
        Button(onClick = onRetry, modifier = Modifier.padding(8.dp)) {
            Text("Retry Dashboard")
        }
        Button(onClick = onUseTestScreen, modifier = Modifier.padding(8.dp)) {
            Text("Use Test Screen")
        }
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
        Button(onClick = {}, modifier = Modifier.padding(8.dp)) {
            Text("Test Button")
        }
        Button(onClick = onLoadDashboard, modifier = Modifier.padding(8.dp)) {
            Text("Load Dashboard")
        }
    }
}
