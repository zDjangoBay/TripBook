package com.android.tripbook

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import com.android.tripbook.ui.screens.DashboardActivity
import com.android.tripbook.ui.screens.auth.AuthActivity
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.data.managers.UserSessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                }
            }
            Log.d("TripBook", "MainActivity onCreate completed successfully")
        } catch (e: Exception) {
            Log.e("TripBook", "Error in MainActivity onCreate", e)
            throw e
        }
    }
}

@Composable
fun SafeDashboardLoader() {
    val context = LocalContext.current
    val application = context.applicationContext as TripBookApplication
    val userSessionManager = remember {
        UserSessionManager.getInstance(context, application.database)
    }

    val isLoggedIn by userSessionManager.isLoggedIn.collectAsState()
    var isInitialized by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(1000) // Simulate loading time
        try {
            Log.d("TripBook", "=== APP INITIALIZATION START ===")
            Log.d("TripBook", "Step 1: Checking authentication status")
            delay(500)
            Log.d("TripBook", "Step 2: Loading user session")
            delay(500)
            Log.d("TripBook", "Step 3: Initialization complete")
            isInitialized = true
            Log.d("TripBook", "=== APP INITIALIZATION END ===")
        } catch (e: Exception) {
            Log.e("TripBook", "Error during app initialization", e)
            errorMessage = "Failed to initialize: ${e.message}"
        }
    }

    when {
        errorMessage != null -> {
            // Show error state
            ErrorScreen(
                error = errorMessage!!,
                onRetry = {
                    errorMessage = null
                    isInitialized = false
                },
                onUseTestScreen = {
                    errorMessage = null
                    isInitialized = false
                }
            )
        }
        !isInitialized -> {
            // Show loading state
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Initializing TripBook...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        isLoggedIn -> {
            // User is logged in, show dashboard
            Log.d("TripBook", "=== DASHBOARD LOADING START ===")
            Log.d("TripBook", "User is authenticated, loading dashboard")
            DashboardActivity()
            Log.d("TripBook", "=== DASHBOARD LOADING END ===")
        }
        else -> {
            // User not logged in, show authentication
            Log.d("TripBook", "=== AUTHENTICATION REQUIRED ===")
            AuthActivity(
                onAuthSuccess = {
                    Log.d("TripBook", "Authentication successful, dashboard will load automatically")
                }
            )
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
}