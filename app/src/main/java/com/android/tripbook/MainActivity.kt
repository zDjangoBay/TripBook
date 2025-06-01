package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ViewModel.MainViewModel
import com.android.tripbook.ui.screens.TripCatalogScreenWrapper
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge content (same as TripCatalogActivity)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TripBookTheme {
                val navController = rememberNavController()
                TripCatalogScreenWrapper(
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}
