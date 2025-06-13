package com.tripbook.userprofilendedilan.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object PasswordReset : Screen("password_reset")
    object Home : Screen("home")
}
