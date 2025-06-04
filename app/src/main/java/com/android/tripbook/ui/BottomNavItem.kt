package com.android.tripbook.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object Search : BottomNavItem("search", Icons.Filled.Search, "Search")
    object Explore : BottomNavItem("explore", Icons.Filled.Explore, "Explore")
    object Profile : BottomNavItem("profile", Icons.Filled.Person, "Profile")
}