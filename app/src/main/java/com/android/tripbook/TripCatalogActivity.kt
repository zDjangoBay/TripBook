package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ui.components.BaseScaffold
import com.android.tripbook.ui.theme.TripBookTheme

class TripCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                val navController = rememberNavController()
                var isLoading by remember { mutableStateOf(false) }


                BaseScaffold(
                    navController = navController,
                    title = "Trip Catalog",
                    isLoading = isLoading
                ) { }
            }
        }
    }
}
