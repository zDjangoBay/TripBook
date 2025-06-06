package com.android.tripbook


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ui.uis.TravelAgencyListScreen
import com.android.tripbook.ui.theme.TripBookTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Required for Hilt, even if not fully used yet
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                // A surface container using the 'background' color from the theme
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
    NavHost(navController = navController, startDestination = "travel_agency_list") {
        composable("travel_agency_list") {
            // Pass navController to the screen
            TravelAgencyListScreen(navController = navController)
        }
        // Add more composables for detail screen, booking screen etc. later
        // composable("agency_detail/{agencyId}") { backStackEntry ->
        //     val agencyId = backStackEntry.arguments?.getString("agencyId")
        //     AgencyDetailScreen(navController, agencyId)
        // }
    }
}