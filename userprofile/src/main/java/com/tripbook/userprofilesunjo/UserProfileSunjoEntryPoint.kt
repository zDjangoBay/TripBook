package com.tripbook.userprofilesunjo

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
import com.tripbook.userprofilesunjo.presentation.navigation.Screen
import com.tripbook.userprofilesunjo.presentation.splash.SplashScreen
import com.tripbook.userprofilesunjo.presentation.theme.TripBookTheme
import com.tripbook.userprofilesunjo.presentation.auth.screens.LoginScreen
import com.tripbook.userprofilesunjo.presentation.auth.screens.RegisterScreen
import com.tripbook.userprofilesunjo.presentation.auth.screens.ForgotPasswordScreen
import com.tripbook.userprofilesunjo.presentation.auth.screens.ResetPasswordScreen
import com.tripbook.userprofilesunjo.presentation.auth.screens.VerifyOtpScreen

class UserProfileSunjoActivity : ComponentActivity() {
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
                    UserProfileSunjoApp()
                }
            }
        }
    }
}

@Composable
fun UserProfileSunjoApp() {
    UserProfileSunjoEntryPoint()
}

@Composable
fun UserProfileSunjoEntryPoint(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }

        composable("${Screen.VerifyOtp.route}/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyOtpScreen(navController, email)
        }

        composable("${Screen.ResetPassword.route}/{email}/{token}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val token = backStackEntry.arguments?.getString("token") ?: ""
            ResetPasswordScreen(navController, email, token)
        }

        composable(Screen.Home.route) {
            // HomeScreen - placeholder for now
        }
    }
}