package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                AuthApp()
            }
        }
    }
}

@Composable
fun AuthApp() {
    var currentScreen by remember { mutableStateOf("login") }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                onLoginSuccess = {
                    currentScreen = "home"
                },
                onNavigateToSignup = {
                    currentScreen = "signup"
                },
                onNavigateToForgotPassword = {
                    currentScreen = "forgot_password"
                }
            )
        }

        "signup" -> {
            SignupScreen(
                onSignupSuccess = {
                    currentScreen = "login"
                },
                onNavigateToLogin = {
                    currentScreen = "login"
                }
            )
        }

        "forgot_password" -> {
            ForgotPasswordScreen(
                onPasswordReset = {
                    currentScreen = "login"
                },
                onNavigateBack = {
                    currentScreen = "login"
                }
            )
        }

        "home" -> {
            HomeScreen(
                onLogout = {
                    currentScreen = "login"
                }
            )
        }
    }
}
