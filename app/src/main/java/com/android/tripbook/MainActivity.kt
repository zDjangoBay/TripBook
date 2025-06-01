package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ViewModel.MainViewModel
import com.android.tripbook.ui.components.BottomNavItem
import com.android.tripbook.ui.components.BottomNavigationBar
import com.android.tripbook.ui.screens.*
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge content
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TripBookTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController, mainViewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: androidx.navigation.NavHostController, mainViewModel: MainViewModel) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {


        composable(BottomNavItem.Home.route) {

            TripCatalogScreen(
                navController = navController,
                upcomingTrips = mainViewModel.upcomingTrips,
                recommendedPlaces = mainViewModel.recommendedPlaces,
                isLoadingUpcoming = mainViewModel.isLoadingUpcoming,
                isLoadingRecommended = mainViewModel.isLoadingRecommended
            )

        }
        composable(BottomNavItem.Schedule.route) {


        }
        composable(BottomNavItem.Catalog.route) {
            TripCatalogScreenWrapper(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
        composable(BottomNavItem.Profile.route) {
            ProfileActivity()

        }
    }

    BottomNavigationBar(navController = navController)
}
