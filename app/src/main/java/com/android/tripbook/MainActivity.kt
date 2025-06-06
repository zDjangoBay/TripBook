package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ui.budget.TripBudgetScreen
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.ui.trip.TripDetailScreen
import com.android.tripbook.ui.trip.TripListScreen
import com.android.tripbook.viewmodel.BudgetViewModel
import com.android.tripbook.viewmodel.BudgetViewModelFactory
import com.android.tripbook.viewmodel.TripViewModel

class MainActivity : ComponentActivity() {
    private val tripViewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripBookTheme {
                TripBookNavHost()
            }
        }
    }

    @Composable
    fun TripBookNavHost() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "trip_list") {
            composable("trip_list") {
                TripListScreen(
                    tripViewModel = tripViewModel,
                    onNavigateToTripDetail = { tripId ->
                        navController.navigate("trip_detail/$tripId")
                    },
                    onNavigateToCreateTrip = { /* TODO */ }
                )
            }
            composable("trip_detail/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")
                tripId?.let {
                    TripDetailScreen(
                        tripViewModel = tripViewModel,
                        tripId = it,
                        onNavigateToBudget = {
                            navController.navigate("trip_budget/$it")
                        }
                    )
                }
            }
            composable("trip_budget/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")
                tripId?.let {
                    val budgetViewModel: BudgetViewModel = viewModel(
                        factory = BudgetViewModelFactory(application, it)
                    )
                    TripBudgetScreen(
                        budgetViewModel = budgetViewModel,
                        tripId = it,
                        navController = navController
                    )
                }
            }
        }
    }
} 