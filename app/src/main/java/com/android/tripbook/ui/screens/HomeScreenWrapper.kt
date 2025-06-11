package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.tripbook.viewmodel.MainViewModel

@Composable
fun HomeScreenWrapper(
    navController: NavHostController,
    mainViewModel: MainViewModel = viewModel()
) {
    val upcomingTrips by mainViewModel.upcomingTrips.observeAsState(emptyList())
    val recommendedPlaces by mainViewModel.recommendedPlaces.observeAsState(emptyList())
    val isLoadingUpcoming by mainViewModel.isLoadingUpcoming.observeAsState(true)
    val isLoadingRecommended by mainViewModel.isLoadingRecommended.observeAsState(true)

    HomeScreen(
        navController = navController,
        upcomingTrips = upcomingTrips,
        recommendedPlaces = recommendedPlaces,
        isLoadingUpcoming = isLoadingUpcoming,
        isLoadingRecommended = isLoadingRecommended,
        onTripClick = { tripId ->
            navController.navigate("detail/$tripId")
        },
        modifier = Modifier.fillMaxSize()
    )
}
