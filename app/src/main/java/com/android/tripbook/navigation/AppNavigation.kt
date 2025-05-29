package com.android.tripbook.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ui.screens.dashboard.TripDashboard
import com.android.tripbook.ui.screens.itinerary.ItineraryEditor
import com.android.tripbook.ui.screens.trip.TripCreation

object AppDestinations {
    const val TRIP_DASHBOARD = "dashboard"
    const val TRIP_CREATION = "trip_creation"
    const val ITINERARY_EDITOR = "itinerary_editor"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.TRIP_DASHBOARD
    ) {
        composable(AppDestinations.TRIP_DASHBOARD) {
            TripDashboard(
                onCreateTrip = { navController.navigate(AppDestinations.TRIP_CREATION) },
                onOpenItinerary = { tripId -> 
                    navController.navigate("${AppDestinations.ITINERARY_EDITOR}/$tripId")
                }
            )
        }
        
        composable(AppDestinations.TRIP_CREATION) {
            TripCreation(
                onNavigateBack = { navController.popBackStack() },
                onTripCreated = { tripId ->
                    navController.navigate("${AppDestinations.ITINERARY_EDITOR}/$tripId") {
                        popUpTo(AppDestinations.TRIP_DASHBOARD)
                    }
                }
            )
        }
        
        composable(
            route = "${AppDestinations.ITINERARY_EDITOR}/{tripId}"
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")
            ItineraryEditor(
                tripId = tripId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}