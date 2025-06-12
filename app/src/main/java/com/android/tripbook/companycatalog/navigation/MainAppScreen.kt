
package com.android.tripbook.companycatalog.navigation

sealed class MainAppScreen(val route: String) {
    object Home : MainAppScreen("home")
    object Catalog : MainAppScreen("catalog")
    object Explore : MainAppScreen("explore")
    object Categories : MainAppScreen("categories")
    object Settings : MainAppScreen("settings")
    
    // New screens for enhanced functionality
    object Stories : MainAppScreen("stories")
    object PhotoGallery : MainAppScreen("photo_gallery")
    object Tips : MainAppScreen("tips")
    object TravelerNetwork : MainAppScreen("traveler_network")
    object HiddenGems : MainAppScreen("hidden_gems")
    object Safety : MainAppScreen("safety")
}
