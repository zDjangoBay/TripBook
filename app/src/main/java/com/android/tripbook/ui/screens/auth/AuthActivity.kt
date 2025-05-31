package com.android.tripbook.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Authentication activity that handles login and signup navigation
 */
@Composable
fun AuthActivity(
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = onAuthSuccess,
                onNavigateToSignup = {
                    navController.navigate("signup") {
                        popUpTo("login") { inclusive = false }
                    }
                }
            )
        }
        
        composable("signup") {
            SignupScreen(
                onSignupSuccess = onAuthSuccess,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
    }
}
