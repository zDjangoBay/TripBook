package com.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.tripbook.navigation.NavGraph
import com.tripbook.reservation.ReservationViewModel
import com.tripbook.reservation.PaymentViewModel
import com.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TripBookApp()
                }
            }
        }
    }
}

@Composable
fun TripBookApp() {
    val navController = rememberNavController()
    val reservationViewModel: ReservationViewModel = viewModel()
    val paymentViewModel: PaymentViewModel = viewModel()

    NavGraph(
        navController = navController,
        reservationViewModel = reservationViewModel,
        paymentViewModel = paymentViewModel
    )
} 