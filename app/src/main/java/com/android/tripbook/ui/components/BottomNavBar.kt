package com.android.tripbook.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tripbook.ui.theme.Purple40
import com.android.tripbook.ui.theme.WhiteSmoke

data class NavItem(val label: String, val icon: ImageVector, val route: String)

val navItems = listOf(
    NavItem("Home", Icons.Default.Home, "home"),
    NavItem("Schedule", Icons.Default.LocationOn, "schedule"),
    NavItem("Catalog", Icons.Default.List, "catalog"),
    NavItem("Profile", Icons.Default.Person, "profile")
)

@Composable
fun BottomNavigationBar(
    current: String,
    onNavigate: (String) -> Unit,
    selectedColor: Color = MaterialTheme.colorScheme.primary
) {
    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = item.route == current,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = selectedColor,        // Highlight background
                    selectedIconColor = WhiteSmoke,        // Icon on highlight
                    selectedTextColor = Purple40,        // Text on highlight
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
