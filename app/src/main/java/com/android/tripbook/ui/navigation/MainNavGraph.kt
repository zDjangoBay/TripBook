package com.android.tripbook.ui.navigation

import com.android.tripbook.ui.screens.DetailReviewScreen
import AllReviewsScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.tripbook.ViewModel.MainViewModel
import com.android.tripbook.ui.screens.*
import com.android.tripbook.ui.screens.booking.BookingScreen

import androidx.navigation.NavType
import androidx.navigation.navArgument


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
            TripCatalogScreen(
                modifier = Modifier.fillMaxSize(),
                onTripClick = { tripId ->
                    navController.navigate("detail/$tripId")
                }
            )
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("detail/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: 0
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




        // New Bus Companies Screen Route
        composable("bus_companies") {
            val busCompaniesViewModel: BusCompaniesViewModel = viewModel()
            val busCompanies by busCompaniesViewModel.busCompanies.collectAsState()
            val popularDestinations by busCompaniesViewModel.popularDestinations.collectAsState()
            val isLoadingCompanies by busCompaniesViewModel.isLoadingCompanies.collectAsState()
            val isLoadingDestinations by busCompaniesViewModel.isLoadingDestinations.collectAsState()

            BusCompaniesScreen(
                navController = navController,
                busCompanies = busCompanies,
                popularDestinations = popularDestinations,
                isLoadingCompanies = isLoadingCompanies,
                isLoadingDestinations = isLoadingDestinations,
                onCompanyClick = { company ->
                    busCompaniesViewModel.onCompanyClick(company)

                     navController.navigate("company_details/${company.id}")
                },
                onDestinationClick = { destination ->
                    busCompaniesViewModel.onDestinationClick(destination)

                     navController.navigate("destination_details/${destination.id}")
                }
            )
        }
        composable("boat_companies") {
            val boatCompaniesViewModel: BoatCompanyViewModel = viewModel()
            val boatCompanies by boatCompaniesViewModel.boatCompanies.collectAsState()
            val popularDestinations by boatCompaniesViewModel.popularDestinations.collectAsState()
            val isLoadingCompanies by boatCompaniesViewModel.isLoadingCompanies.collectAsState()
            val isLoadingDestinations by boatCompaniesViewModel.isLoadingDestinations.collectAsState()

            BoatCompaniesScreen(
                navController = navController,
                boatCompanies = boatCompanies,
                destinations = popularDestinations,
                isLoadingCompanies = isLoadingCompanies,
                isLoadingDestinations = isLoadingDestinations,
                onCompanyClick = { company ->
                    boatCompaniesViewModel.onCompanyClick(company)
                    navController.navigate("company_details/${company.id}")
                },
                onDestinationClick = { destination ->
                    boatCompaniesViewModel.onDestinationClick(destination)
                    navController.navigate("destination_details/${destination.id}")
                }
            )
        }

        composable("airline_companies") {
            AirlineScreen(navController = navController)
        }

        composable("train_companies") {
            TrainCompaniesScreen(navController = navController)
        }

        composable(
            route = "addPlace/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddPlaceScreen(
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