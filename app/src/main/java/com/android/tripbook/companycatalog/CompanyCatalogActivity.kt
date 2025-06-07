package com.android.tripbook.companycatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

import com.android.tripbook.companycatalog.ui.screens.*
import com.android.tripbook.ui.theme.TripBookTheme

// Represents each screen in the bottom navigation bar
sealed class MainAppScreen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : MainAppScreen("home", "Home", Icons.Default.Home)
    data object Explore : MainAppScreen("explore", "Explore", Icons.Default.CompassCalibration)
    data object Catalog : MainAppScreen("catalog", "Catalog", Icons.Default.Store)
    data object Profile : MainAppScreen("profile", "Profile", Icons.Default.Person)
}

class CompanyCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripbookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRootNavHost()
                }
            }
        }
    }
}

@Composable
fun AppRootNavHost() {
    val navController = rememberNavController()
    val navItems = listOf(
        MainAppScreen.Home,
        MainAppScreen.Explore,
        MainAppScreen.Catalog,
        MainAppScreen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainAppScreen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(MainAppScreen.Home.route) {
                HomeScreen()
            }
            composable(MainAppScreen.Explore.route) {
                ExploreScreen()
            }
            composable(MainAppScreen.Catalog.route) {
                CatalogScreen(navController = navController)
            }
            composable(MainAppScreen.Profile.route) {
                ProfileScreen()
            }

            // Add more routes here as needed (e.g., details, reviews, search)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppRootNavHostPreview() {
    TripbookTheme {
        AppRootNavHost()
    }
}
