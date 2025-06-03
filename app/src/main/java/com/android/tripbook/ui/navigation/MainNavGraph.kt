package com.android.tripbook.ui.navigation
import com.android.tripbook.ui.screens.DetailReviewScreen

import AllReviewsScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.tripbook.ui.screens.*
import com.android.tripbook.ui.screens.booking.BookingScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
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
                navController = navController,
                onBack = { navController.popBackStack() },
                onSeeAllReviews = { id ->
                    navController.navigate("reviews/$id")
                },
                onBookTrip = { id ->
                    navController.navigate("booking/$id")
                }
            )
        }

        composable("reviews/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: return@composable
            AllReviewsScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onLikeClicked = { reviewId ->
                    println("Liked review ID: $reviewId")
                },
                onFlagClicked = { reviewId ->
                    println("Flagged review ID: $reviewId")
                }
            )


    }
        composable("detailReview/{reviewId}/{tripId}") { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId")?.toIntOrNull() ?: return@composable
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: return@composable

            DetailReviewScreen(
                reviewId = reviewId,
                tripId = tripId,
                onLikeClicked = { println("Liked review ID: $it") },
                onFlagClicked = { println("Flagged review ID: $it") },
                onBack = { navController.popBackStack() }
            )
        }


        composable("booking/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: return@composable
            BookingScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onBookingComplete = {
                    // Navigate back to the catalog after booking is complete
                    navController.navigate("catalog") {
                        popUpTo("catalog") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
