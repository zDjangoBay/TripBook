package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.viewmodel.MainViewModel
import com.android.tripbook.ui.navigation.BottomNavigationBar
import com.android.tripbook.ui.navigation.MainNavGraph
import com.android.tripbook.ui.theme.TripBookTheme

class HomeActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge content
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TripBookTheme {
                TripBookApp(mainViewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun TripBookApp(mainViewModel: MainViewModel) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MainNavGraph(
                navController = navController,
                mainViewModel = mainViewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}