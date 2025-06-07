package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.ReservationsDashboard
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReservationsDashboard(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}