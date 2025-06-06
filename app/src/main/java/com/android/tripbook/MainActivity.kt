package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.model.TripViewModel
import com.android.tripbook.ui.BottomNavItem
import com.android.tripbook.ui.uis.PlanNewTripScreen
import com.android.tripbook.ui.uis.MyTripsScreen
import com.android.tripbook.ui.theme.TripBookTheme
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.android.tripbook.model.TravelAgency
import com.android.tripbook.ui.uis.TravelAgencyListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                TripBookApp()
            }
        }
    }
}

@Composable
fun TripBookApp() {
    val navController = rememberNavController()
    val viewModel = remember { TripViewModel() }

    val items = listOf(
        BottomNavItem.Agencies,
        BottomNavItem.MyTrips,
        BottomNavItem.PlanTrip
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = items)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Agencies.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Agencies.route) {
                val agencies = listOf(
                    TravelAgency("StarExpress", "Douala", 12, 18),
                    TravelAgency("SpeedGo", "Yaoundé", 10, 20),
                    TravelAgency("VIP Trans", "Bamenda", 15, 15),
                )

                TravelAgencyListScreen(
                    agencies = agencies,
                    onPlanTripClick = {
                        navController.navigate(BottomNavItem.PlanTrip.route)
                    }
                )
            }

            composable(BottomNavItem.MyTrips.route) {
                MyTripsScreen(
                    viewModel = viewModel,
                    onPlanNewTripClick = {
                        navController.navigate(BottomNavItem.PlanTrip.route)
                    },
                    onTripClick = { /* handle trip click */ }
                )
            }
            composable(BottomNavItem.PlanTrip.route) {
                PlanNewTripScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.navigate(BottomNavItem.MyTrips.route)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Avoid multiple copies of the same destination
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = true
            )
        }
    }
}
