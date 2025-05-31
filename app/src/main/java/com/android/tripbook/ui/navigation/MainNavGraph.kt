// Update your MainNavGraph.kt to share the same ViewModel instance

package com.android.tripbook.ui.navigation

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.ui.screens.*
import com.android.tripbook.viewmodel.ReviewViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // CREATE SINGLE INSTANCE HERE
    val sharedReviewViewModel: ReviewViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Home Screen",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        composable("schedule") {
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
        composable("catalog") {
            TripCatalogScreen(
                modifier = Modifier.fillMaxSize(),
                onTripClick = { tripId ->
                    navController.navigate("detail/$tripId")
                }
            )
        }
        composable("profile") {
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
        composable("detail/{tripId}") {
            val tripId = it.arguments?.getString("tripId")?.toIntOrNull() ?: 0
            TripDetailScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onSeeAllReviews = { id ->
                    navController.navigate("reviews/$id")
                },
                reviewViewModel = sharedReviewViewModel // PASS THE SAME INSTANCE
            )
        }
        composable("reviews/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: return@composable
            AllReviewsScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                reviewViewModel = sharedReviewViewModel // PASS THE SAME INSTANCE
            )
        }
    }
}