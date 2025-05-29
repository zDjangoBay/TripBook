package com.android.tripbook.tripscheduling.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.tripscheduling.ui.navigation.SchedulingNavGraph

class TripSchedulingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripBookTheme {
                val navController = rememberNavController()
                SchedulingNavGraph(navController = navController)
            }
        }
    }
}
