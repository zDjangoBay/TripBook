package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.dashboard.DashboardScreen
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.reservations.ReservationListScreen
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.notifications.NotificationScreen
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.profile.ProfileScreen
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.reservation.ReservationFlowActivity

/**
 * Main Dashboard Activity with bottom navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardActivity() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    onTripClick = { tripId ->
                        navController.navigate("trip_details/$tripId")
                    }
                )
            }
            composable("trip_details/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                ReservationFlowActivity(
                    tripId = tripId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPayment = { reservationId ->
                        navController.navigate("payment/$reservationId")
                    }
                )
            }
            composable("reservations") {
                ReservationListScreen(
                    onReservationClick = { reservationId ->
                        navController.navigate("reservation_details/$reservationId")
                    }
                )
            }
            composable("notifications") {
                NotificationScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("payment/{reservationId}") { backStackEntry ->
                val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
                PaymentScreen(
                    reservationId = reservationId,
                    amount = 3500.5, // Example amount, replace with actual data
                    onNavigateBack = { navController.popBackStack() },
                    onPaymentSuccess = {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                    onPaymentFailure = {
                        // Gérer l'échec du paiement ici
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("dashboard", "Home", Icons.Default.Home),
        BottomNavItem("reservations", "Trips", Icons.Default.BookmarkBorder),
        BottomNavItem("notifications", "Alerts", Icons.Default.Notifications),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
