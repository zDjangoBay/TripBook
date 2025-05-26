package com.android.tripbook.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(val label: String, val icon: ImageVector, val route: String)

val navItems = listOf(
    NavItem("Home", Icons.Default.Home, "home"),
    NavItem("Book", Icons.Default.DateRange, "book"),
    NavItem("Explore", Icons.Default.Place, "explore"),
    NavItem("Profile", Icons.Default.Person, "profile")
)

@Composable
fun BottomNavigationBar(current: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = item.route == current,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
