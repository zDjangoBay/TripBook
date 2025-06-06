package com.android.tripbook.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)
// defines the item of the navigation bar
object NavigationItems {
    val items = listOf(
        NavigationItem(
            route = "home",
            title = "Home",
            icon = Icons.Default.Home,
            selectedIcon = Icons.Filled.Home
        ),
        NavigationItem(
            route = "my_trips",
            title = "Trips",
            icon = Icons.Default.List,
            selectedIcon = Icons.Filled.List
        ),
        NavigationItem(
            route = "add_trip",
            title = "Add Trip",
            icon = Icons.Default.Add,
            selectedIcon = Icons.Filled.Add
        ),
        NavigationItem(
            route = "profile",
            title = "Profile",
            icon = Icons.Default.Person,
            selectedIcon = Icons.Filled.Person
        ),
        NavigationItem(
            route = "settings",
            title = "Settings",
            icon = Icons.Default.Settings,
            selectedIcon = Icons.Filled.Settings
        )
    )
}