package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ViewModel.MainViewModel
import com.android.tripbook.ui.components.BottomNavItem
import com.android.tripbook.ui.components.BottomNavigationBar
import com.android.tripbook.ui.screens.*
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Immediately launch TripCatalogActivity
        val intent = Intent(this, TripCatalogActivity::class.java)
        startActivity(intent)

        // Finish MainActivity so user can't navigate back to it
        finish()
    }
}
