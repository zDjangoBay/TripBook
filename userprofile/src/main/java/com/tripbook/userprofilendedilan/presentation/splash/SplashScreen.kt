package com.tripbook.userprofilendedilan.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tripbook.userprofilendedilan.R
import com.tripbook.userprofilendedilan.data.repository.UserPreferencesRepository
import com.tripbook.userprofilendedilan.data.repository.dataStore
import com.tripbook.userprofilendedilan.presentation.navigation.Screen
import com.tripbook.userprofilendedilan.presentation.onboarding.viewmodels.OnboardingViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext

@Composable
fun SplashScreen(navController: NavController) {
    // Get context for DataStore
    val context = LocalContext.current

    // Create repository and viewModel
    val userPreferencesRepository = UserPreferencesRepository(context.dataStore)
    val viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.Factory(userPreferencesRepository)
    )

    // Collect the onboarding state
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    // Display logo
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.app_logo), // Add your logo to resources
            contentDescription = "TripBook Logo"
        )
    }

    // Navigate after a delay based on onboarding status
    LaunchedEffect(key1 = true) {
        delay(2000) // 2 second splash delay
        if (isOnboardingCompleted) {
            // Navigate to Register instead of Login
            navController.navigate(Screen.Register.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            // Navigate to onboarding if it hasn't been completed
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }
}
