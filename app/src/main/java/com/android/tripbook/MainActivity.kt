package com.android.tripbook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.tripscheduling.ui.navigation.SchedulingNavGraph
import com.android.tripbook.tripscheduling.ui.navigation.SchedulingRoutes
import com.android.tripbook.ui.theme.TripBookTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    private var latestIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        latestIntent = intent // Store intent in case onNewIntent is called later

        setContent {
            val navController = rememberNavController()

            // Handle deep link after content is composed
            LaunchedEffect(Unit) {
                latestIntent?.data?.let { uri ->
                    handleDeepLink(uri, navController)
                }
            }

            TripBookTheme {
                SchedulingNavGraph(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        latestIntent = intent
    }

    private fun handleDeepLink(uri: Uri, navController: NavController) {
        val segments = uri.pathSegments
        if (segments.isNotEmpty() && segments[0] == "schedule_details") {
            val scheduleId = segments.getOrNull(1)
            if (scheduleId != null) {
                navController.navigate(SchedulingRoutes.Details.createRoute(scheduleId))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivity() {
    TripBookTheme {
        // For preview, we show a simple placeholder text instead of full nav graph
        androidx.compose.material3.Text("MainActivity Preview Placeholder")
    }
}