package com.tripbook.userprofilendedilan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tripbook.userprofileManfoIngrid.presentation.media.MediaOrganizationScreen
import com.tripbook.userprofilendedilan.presentation.auth.screens.RegisterScreen
import com.tripbook.userprofilendedilan.presentation.navigation.Screen
import com.tripbook.userprofilendedilan.presentation.splash.SplashScreen
import com.tripbook.userprofilendedilan.presentation.theme.TripBookTheme
import com.tripbook.userprofilendedilan.presentation.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TripBookTheme {
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
    UserProfileNdeDilanEntryPoint()
}

@Composable
fun UserProfileNdeDilanEntryPoint(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            MediaOrganizationScreen(navController)
        }
//        composable(Screen.Splash.route) {
//            SplashScreen(navController)
//        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(Screen.Login.route) {
            // LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Home.route) {
            // HomeScreen()
        }
    }
}
