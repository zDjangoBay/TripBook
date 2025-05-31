package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ViewModel.MainViewModel
import com.android.tripbook.ui.screens.TripCatalogScreenWrapper

class TripCatalogActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge content
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val navController = rememberNavController()
            TripCatalogScreenWrapper(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
    }
}
