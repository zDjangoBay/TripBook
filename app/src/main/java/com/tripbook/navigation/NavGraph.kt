package com.tripbook.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tripbook.reservation.*

sealed class Screen(val route: String) {
    object ReservationsDashboard : Screen("reservations_dashboard")
    object TripDetails : Screen("trip_details/{tripId}") {
        fun createRoute(tripId: String) = "trip_details/$tripId"
    }
    object SeatSelection : Screen("seat_selection/{tripId}") {
        fun createRoute(tripId: String) = "seat_selection/$tripId"
    }
    object PaymentInformation : Screen("payment/{reservationId}") {
        fun createRoute(reservationId: String) = "payment/$reservationId"
    }
    object BookingConfirmation : Screen("booking_confirmation/{reservationId}") {
        fun createRoute(reservationId: String) = "booking_confirmation/$reservationId"
    }
    object Cancellation : Screen("cancellation/{reservationId}") {
        fun createRoute(reservationId: String) = "cancellation/$reservationId"
    }
    object RefundConfirmation : Screen("refund_confirmation/{reservationId}") {
        fun createRoute(reservationId: String) = "refund_confirmation/$reservationId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    reservationViewModel: ReservationViewModel,
    paymentViewModel: PaymentViewModel,
    startDestination: String = Screen.ReservationsDashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.ReservationsDashboard.route) {
            val uiState by reservationViewModel.uiState.collectAsState()
            ReservationsDashboardScreen(
                uiState = uiState,
                onReservationClick = { reservationId ->
                    navController.navigate(Screen.TripDetails.createRoute(reservationId))
                },
                onLoadReservations = {
                    reservationViewModel.loadUserReservations("current_user_id") // Replace with actual user ID
                }
            )
        }

        composable(Screen.TripDetails.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: return@composable
            val uiState by reservationViewModel.uiState.collectAsState()
            
            TripDetailsScreen(
                uiState = uiState,
                tripId = tripId,
                onBookNowClick = {
                    navController.navigate(Screen.SeatSelection.createRoute(tripId))
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLoadTripDetails = {
                    reservationViewModel.loadTripDetails(tripId)
                }
            )
        }

        composable(Screen.SeatSelection.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: return@composable
            val uiState by reservationViewModel.uiState.collectAsState()
            val selectedSeats by reservationViewModel.selectedSeats.collectAsState()
            val availableSeats by reservationViewModel.availableSeats.collectAsState()

            SeatSelectionScreen(
                uiState = uiState,
                availableSeats = availableSeats,
                selectedSeats = selectedSeats,
                onSeatSelected = { seat ->
                    reservationViewModel.selectSeat(seat)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    reservationViewModel.createReservation(tripId, "current_user_id") // Replace with actual user ID
                    val reservationId = (uiState as? ReservationUiState.ReservationCreated)?.reservation?.id
                    if (reservationId != null) {
                        navController.navigate(Screen.PaymentInformation.createRoute(reservationId))
                    }
                },
                onLoadSeats = {
                    reservationViewModel.loadAvailableSeats(tripId)
                }
            )
        }

        composable(Screen.PaymentInformation.route) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: return@composable
            val uiState by paymentViewModel.uiState.collectAsState()

            PaymentInformationScreen(
                uiState = uiState,
                amount = 0.0, // Get from reservation
                reservationId = reservationId,
                onPaymentComplete = { cardDetails ->
                    paymentViewModel.processPayment(
                        reservationId = reservationId,
                        amount = 0.0, // Get from reservation
                        cardNumber = cardDetails.number,
                        expiryMonth = cardDetails.expiryMonth,
                        expiryYear = cardDetails.expiryYear,
                        cvv = cardDetails.cvv,
                        cardholderName = cardDetails.cardholderName
                    )
                    navController.navigate(Screen.BookingConfirmation.createRoute(reservationId))
                }
            )
        }

        composable(Screen.BookingConfirmation.route) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: return@composable
            val uiState by reservationViewModel.uiState.collectAsState()

            BookingConfirmationScreen(
                uiState = uiState,
                onViewReservationClick = {
                    navController.navigate(Screen.ReservationsDashboard.route) {
                        popUpTo(Screen.ReservationsDashboard.route) { inclusive = true }
                    }
                },
                onBackToHomeClick = {
                    navController.navigate(Screen.ReservationsDashboard.route) {
                        popUpTo(Screen.ReservationsDashboard.route) { inclusive = true }
                    }
                },
                onLoadReservation = {
                    // Load reservation details if needed
                }
            )
        }

        composable(Screen.Cancellation.route) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: return@composable
            val uiState by reservationViewModel.uiState.collectAsState()

            CancellationScreen(
                uiState = uiState,
                onCancellationComplete = { reason ->
                    reservationViewModel.cancelReservation(reservationId, reason)
                    navController.navigate(Screen.RefundConfirmation.createRoute(reservationId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.RefundConfirmation.route) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: return@composable
            val uiState by reservationViewModel.uiState.collectAsState()

            RefundConfirmationScreen(
                uiState = uiState,
                onBackToHomeClick = {
                    navController.navigate(Screen.ReservationsDashboard.route) {
                        popUpTo(Screen.ReservationsDashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
} 