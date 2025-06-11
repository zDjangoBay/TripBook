package com.android.tripbook.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.navigation.BottomNavItem

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = BottomNavItem.entries
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Extract the base route (remove parameters like detail/{tripId})
    val currentBaseRoute = when {
        currentRoute?.startsWith("detail/") == true -> "catalog" // Show catalog as selected when on detail
        currentRoute?.startsWith("reviews/") == true -> "catalog" // Show catalog as selected when on reviews
        else -> currentRoute
    }

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 3.dp
    ) {
        items.forEach { item ->
            val isSelected = currentBaseRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navigateToBottomNavDestination(navController, item.route, currentBaseRoute)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = stringResource(id = item.labelRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.labelRes),
                        color = if (isSelected) Color.Black else Color(0xFF191970)
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFE8EAF6),
                    unselectedIconColor = Color(0xFF191970),
                    selectedIconColor = Color.Black
                )
            )
        }
    }
}

private fun navigateToBottomNavDestination(
    navController: NavController,
    destination: String,
    currentRoute: String?
) {
    if (currentRoute != destination) {
        navController.navigate(destination) {
            // Pop up to the start destination and save state
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            // Avoid multiple copies of the same destination
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}