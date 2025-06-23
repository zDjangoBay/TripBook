package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
// import com.android.tripbook.ui.screens.booking.BookingScreen // Temporarily commented out
import com.android.tripbook.ui.theme.TripBookTheme

class BookingActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tripId = intent.getIntExtra("TRIP_ID", 0)

        setContent {
            TripBookTheme {
                var isLoading by remember { mutableStateOf(false) }

                Scaffold { padding ->
                    // BookingScreen temporarily commented out for team compatibility
                    // BookingScreen(
                    //     modifier = Modifier.padding(padding),
                    //     tripId = tripId,
                    //     onBack = { finish() },
                    //     onBookingComplete = { finish() }
                    // )
                }
            }
        }
    }
}