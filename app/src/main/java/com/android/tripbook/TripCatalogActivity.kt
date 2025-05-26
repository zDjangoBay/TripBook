package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.android.tripbook.ui.components.BaseScaffold
import com.android.tripbook.ui.navigation.TripNavGraph
import com.android.tripbook.ui.navigation.navigateTo
import com.android.tripbook.ui.theme.TripBookTheme

class TripCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                val context = LocalContext.current
                var isLoading by remember { mutableStateOf(false) }
                var pendingNavigation by remember { mutableStateOf<String?>(null) }

                // Handle app-wide delayed navigation (if needed)
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
                    currentRoute = "trip_catalog",
                    title = "Trip Catalog",
                    onNavigate = { destination ->
                        pendingNavigation = destination
                    },
                    isLoading = isLoading
                ) { paddingValues ->
                    // 👇 Insert your trip navigation graph here
                    TripNavGraph(modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }
}
