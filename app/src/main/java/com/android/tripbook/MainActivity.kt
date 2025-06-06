package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.android.tripbook.notifications.services.NotificationService
import com.android.tripbook.notifications.utils.NotificationUtils
import com.android.tripbook.ui.theme.TripBookTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var notificationService: NotificationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize notification service
        notificationService = NotificationService(this)

        // Test notifications on app start
        testNotifications()

        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "TripBook",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun testNotifications() {
        // Test booking confirmation
        lifecycleScope.launch {
            val bookingNotification = NotificationUtils.createBookingConfirmation(
                userId = "test123",
                destination = "Douala",
                date = "Demain",
                transport = "Bus"
            )
            notificationService.processNotification(bookingNotification)
        }

        // Test payment success (additional example)
        lifecycleScope.launch {
            val paymentNotification = NotificationUtils.createPaymentSuccess(
                userId = "test123",
                amount = "25,000 FCFA",
                paymentMethod = "Mobile Money"
            )
            notificationService.processNotification(paymentNotification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationService.cleanup()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview
@Composable
fun GreetingPreview() {
    TripBookTheme {
        Greeting("TripBook")
    }
}