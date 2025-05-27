package com.android.tripbook.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.android.tripbook.ui.screens.TripListScreen // Import your screen composable
import com.android.tripbook.ui.theme.TripBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripBookApp() {
    TripBookTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Scaffold(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text("My Trips") },
                    // Actions are now handled inside TripListScreen's UI
                    // If you want actions directly in the top bar, you would hoist their states here
                    // and pass them as parameters to TripListScreen. For simplicity, keeping
                    // search and sort controls within TripListScreen for now.
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            // The main content of the app, which is our TripListScreen
            TripListScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}