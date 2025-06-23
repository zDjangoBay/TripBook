package com.android.tripbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.android.tripbook.ViewModel.MainViewModel
import com.android.tripbook.ui.screens.TripCatalogScreen
import com.android.tripbook.ui.screens.HomeScreenWrapper
import com.android.tripbook.ui.screens.ProfileScreen
import com.android.tripbook.ui.screens.ScheduleScreen
import com.android.tripbook.ui.screens.TripDetailScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreenWrapper(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
        
        composable("schedule") {
            ScheduleScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        
        composable("catalog") {
            TripCatalogScreen(
                onTripClick = { tripId ->
                    navController.navigate("detail/$tripId")
                }
            )
        }
        
        composable("profile") {
            ProfileScreen(
                navController = navController
            )
        }

        // 🎯 NEW: TripDetailScreen route
        composable(
            route = "detail/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            TripDetailScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                navController = navController,
                onSeeAllReviews = { /* TODO: Implement if needed */ },
                onBookTrip = { /* TODO: Implement if needed */ }
            )
        }
    }
}
