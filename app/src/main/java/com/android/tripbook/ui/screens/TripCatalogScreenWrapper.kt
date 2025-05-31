package com.android.tripbook.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import com.android.tripbook.ViewModel.MainViewModel

@Composable
fun TripCatalogScreenWrapper(
    navController: NavHostController,
    mainViewModel: MainViewModel = viewModel()
) {
    val upcomingTrips by mainViewModel.upcomingTrips.observeAsState(emptyList())
    val recommendedPlaces by mainViewModel.recommendedPlaces.observeAsState(emptyList())
    val isLoadingUpcoming by mainViewModel.isLoadingUpcoming.observeAsState(true)
    val isLoadingRecommended by mainViewModel.isLoadingRecommended.observeAsState(true)

    TripCatalogScreen(
        navController = navController,
        upcomingTrips = upcomingTrips,
        recommendedPlaces = recommendedPlaces,
        isLoadingUpcoming = isLoadingUpcoming,
        isLoadingRecommended = isLoadingRecommended
    )
}

//composable("tripCatalog") {
//    TripCatalogScreenWrapper(navController = navController)
//}
