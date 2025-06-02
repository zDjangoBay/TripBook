package com.android.tripbook.ui.navigation

import AllReviewsScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.tripbook.ViewModel.MainViewModel
import com.android.tripbook.ui.screens.*

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
            ScheduleScreen(navController = navController)
        }

        composable("catalog") {
            HomeScreenWrapper(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("detail/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: 0
            TripDetailScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onSeeAllReviews = { id ->
                    navController.navigate("reviews/$id")
                }
            )
        }

        composable("reviews/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: return@composable
            AllReviewsScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun ScheduleScreen(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Schedule Trips Screen",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileScreen(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Profile Screen",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}