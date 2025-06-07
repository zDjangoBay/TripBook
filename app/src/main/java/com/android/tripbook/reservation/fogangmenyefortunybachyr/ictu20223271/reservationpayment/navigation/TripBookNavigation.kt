package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.DashboardActivity
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.PaymentScreen
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.payment.PaymentSuccessScreen
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.payment.PaymentFailureScreen
import java.math.BigDecimal

@Composable
fun TripBookNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardActivity()
        }
        
        composable("payment/{reservationId}/{amount}") { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount")?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            
            PaymentScreen(
                reservationId = reservationId,
                amount = amount,
                onPaymentSuccess = {
                    navController.navigate("payment_success/$reservationId") {
                        popUpTo("payment/{reservationId}/{amount}") { inclusive = true }
                    }
                },
                onPaymentFailure = { error ->
                    navController.navigate("payment_failure?error=$error") {
                        popUpTo("payment/{reservationId}/{amount}") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("payment_success/{reservationId}") { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
            
            PaymentSuccessScreen(
                reservationId = reservationId,
                onNavigateHome = {
                    navController.navigate("dashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onViewReservation = {
                    navController.navigate("reservations") {
                        popUpTo("dashboard")
                    }
                }
            )
        }
        
        composable("payment_failure?error={error}") { backStackEntry ->
            val error = backStackEntry.arguments?.getString("error") ?: "Payment failed"
            
            PaymentFailureScreen(
                errorMessage = error,
                onRetry = {
                    navController.popBackStack()
                },
                onNavigateHome = {
                    navController.navigate("dashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}