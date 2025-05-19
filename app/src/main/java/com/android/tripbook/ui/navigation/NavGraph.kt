package com.android.tripbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.tripbook.ui.screens.TripCatalogScreen
import com.android.tripbook.ui.screens.TripDetailScreen

@Composable
fun TripNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "catalog") {
        composable("catalog") {
            TripCatalogScreen(
                onTripClick = { tripId ->
                    navController.navigate("detail/$tripId")
                }
            )
        }
        composable(
            route = "detail/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            TripDetailScreen(tripId = tripId, onBack = { navController.popBackStack() })
        }
    }
}
