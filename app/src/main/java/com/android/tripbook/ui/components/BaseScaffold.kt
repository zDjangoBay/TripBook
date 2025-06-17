package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.tripbook.ui.theme.Purple40
import com.android.tripbook.model.Trip

@Composable
fun BaseScaffold(
    navController: NavController,
    trips: List<Trip> = emptyList(),
    isLoading: Boolean = false,
    onTripSelected: (Trip) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val title = when {
        currentRoute.startsWith("home") -> "Home"
        currentRoute.startsWith("schedule") -> "Schedule Trips"
        currentRoute.startsWith("catalog") -> "Trip Catalog"
        currentRoute.startsWith("profile") -> "Profile"
        currentRoute.startsWith("detail") -> "Trip Details"
        currentRoute.startsWith("reviews") -> "User Reviews"
        else -> "TripBook"
    }

    Scaffold(
        topBar = { 
            TopBar(
                title = title,
                currentRoute = currentRoute,
                trips = trips,
                onTripSelected = onTripSelected
            ) 
        },
        bottomBar = {
            BottomNavigationBar(
                current = currentRoute,
                onNavigate = {
                    if (navController.currentDestination?.route != it) {
                        navController.navigate(it) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                selectedColor = Purple40
            )
        },
        content = { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                content(innerPadding)
                ScreenLoader(isLoading)
            }
        }
    )
}
