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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.di.ServiceLocator
import com.android.tripbook.navigation.AppNavigation
import com.android.tripbook.ui.theme.TripBookTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                // Set status bar color
                val systemUiController = rememberSystemUiController()
                val statusBarColor = MaterialTheme.colorScheme.primary
                
                systemUiController.setStatusBarColor(
                    color = statusBarColor
                )

                // Main app content
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
    AppNavigation(navController = navController)
}

@Preview
@Composable
fun TripBookAppPreview() {
    TripBookTheme {
        TripBookApp()
    }
}