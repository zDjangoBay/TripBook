package com.tripbook.userprofilesunjo.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object VerifyOtp : Screen("verify_otp")
    object ResetPassword : Screen("reset_password")
    object Home : Screen("home")
}