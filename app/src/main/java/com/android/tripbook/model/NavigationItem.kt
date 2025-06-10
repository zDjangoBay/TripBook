package com.android.tripbook.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class NavigationItem(
    val route: String,
    val title: String,
    @Transient val icon: ImageVector = Icons.Default.Home,
    @Transient val selectedIcon: ImageVector = icon
) {
    init {
        require(route.isNotBlank()) { "Route cannot be blank" }
        require(title.isNotBlank()) { "Title cannot be blank" }
    }
}


object NavigationItems {
    val items = listOf(
        NavigationItem(
            route = "home",
            title = "Home",
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home
        ),
        NavigationItem(
            route = "my_trips",
            title = "Trips",
            icon = Icons.Outlined.List,
            selectedIcon = Icons.Filled.List
        ),
        NavigationItem(
            route = "add_trip",
            title = "Add Trip",
            icon = Icons.Outlined.Add,
            selectedIcon = Icons.Filled.Add
        ),
        NavigationItem(
            route = "profile",
            title = "Profile",
            icon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person
        ),
        NavigationItem(
            route = "settings",
            title = "Settings",
            icon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings
        )
    )


    fun findByRoute(route: String): NavigationItem? {
        return items.find { it.route == route }
    }


    val routes: List<String> = items.map { it.route }
}