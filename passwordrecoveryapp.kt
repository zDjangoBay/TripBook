package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ui.screens.*

enum class Screen {
    HOME, SIGN_IN, FORGOT_PASSWORD, RESET_PASSWORD, SUCCESS
}

@Composable
fun PasswordRecoveryApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var resetToken by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                onNavigateToSignIn = { currentScreen = Screen.SIGN_IN },
                onNavigateToForgotPassword = { currentScreen = Screen.FORGOT_PASSWORD }
            )
        }
        Screen.SIGN_IN -> {
            SignInScreen(
                onNavigateBack = { currentScreen = Screen.HOME },
                onNavigateToForgotPassword = { currentScreen = Screen.FORGOT_PASSWORD },
                onSignInSuccess = { currentScreen = Screen.HOME }
            )
        }
        Screen.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                onNavigateBack = { currentScreen = Screen.SIGN_IN },
                onEmailSent = { email ->
                    userEmail = email
                    currentScreen = Screen.SUCCESS
                }
            )
        }
        Screen.RESET_PASSWORD -> {
            ResetPasswordScreen(
                token = resetToken,
                onNavigateBack = { currentScreen = Screen.SIGN_IN },
                onPasswordReset = { currentScreen = Screen.SUCCESS }
            )
        }
        Screen.SUCCESS -> {
            SuccessScreen(
                message = if (resetToken.isNotEmpty()) "Password reset successfully!" else "Reset email sent to $userEmail",
                onNavigateToSignIn = { 
                    currentScreen = Screen.SIGN_IN
                    resetToken = ""
                    userEmail = ""
                }
            )
        }
    }
}