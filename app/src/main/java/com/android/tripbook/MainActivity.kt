package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.components.BaseScaffold
import com.android.tripbook.ui.navigation.navigateTo
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                val context = LocalContext.current
                var isLoading by remember { mutableStateOf(false) }
                var pendingNavigation by remember { mutableStateOf<String?>(null) }

                // Handle delayed navigation in a safe Composable scope
                LaunchedEffect(pendingNavigation) {
                    pendingNavigation?.let { destination ->
                        isLoading = true
                        kotlinx.coroutines.delay(600)
                        navigateTo(context, destination)
                        isLoading = false
                        pendingNavigation = null
                    }
                }

                BaseScaffold(
                    currentRoute = "home",
                    title = "Home",
                    onNavigate = { destination ->
                        pendingNavigation = destination
                    },
                    isLoading = isLoading
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        Text("Welcome to the TripBook Home Page!")
                    }
                }
            }
        }
    }
}
