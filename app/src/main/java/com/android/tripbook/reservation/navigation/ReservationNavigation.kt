package com.android.tripbook.reservation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.tripbook.reservation.model.ReservationType
import com.android.tripbook.reservation.ui.screens.ReservationDetailScreen
import com.android.tripbook.reservation.ui.screens.ReservationListScreen

/**
 * Navigation routes for the reservation module
 */
object ReservationDestinations {
    const val RESERVATION_LIST = "reservation_list"
    const val RESERVATION_DETAIL = "reservation_detail"
    const val NEW_BOOKING = "new_booking"

    // Helper functions to create routes with arguments
    fun reservationDetail(reservationId: String) = "$RESERVATION_DETAIL/$reservationId"
    fun newBooking(type: String) = "$NEW_BOOKING/$type"
}

/**
 * Navigation graph for the reservation module
 */
@Composable
fun ReservationNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ReservationDestinations.RESERVATION_LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Reservation list screen
        composable(ReservationDestinations.RESERVATION_LIST) {
            ReservationListScreen(
                onReservationClick = { reservationId ->
                    navController.navigate(ReservationDestinations.reservationDetail(reservationId))
                },
                onNewBookingClick = { type ->
                    navController.navigate(ReservationDestinations.newBooking(type))
                }
            )
        }

        // Reservation detail screen
        composable(
            route = "${ReservationDestinations.RESERVATION_DETAIL}/{reservationId}",
            arguments = listOf(
                navArgument("reservationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
            ReservationDetailScreen(
                reservationId = reservationId,
                onBackClick = {
                    navController.popBackStack()
                },
                onCancelReservation = {
                    // After cancellation, go back to list
                    navController.popBackStack()
                }
            )
        }

        // New booking screen
        composable(
            route = "${ReservationDestinations.NEW_BOOKING}/{reservationType}",
            arguments = listOf(
                navArgument("reservationType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val reservationType = backStackEntry.arguments?.getString("reservationType") ?:
                ReservationType.ACCOMMODATION.name

            // For now, we'll just navigate back to the list screen
            // In a real app, this would be a form to create a new booking
            ReservationListScreen(
                onReservationClick = { reservationId ->
                    navController.navigate(ReservationDestinations.reservationDetail(reservationId))
                },
                onNewBookingClick = { type ->
                    navController.navigate(ReservationDestinations.newBooking(type))
                }
            )
        }
    }
}
