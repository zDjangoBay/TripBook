package com.android.tripbook.companycatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.companycatalog.ui.screens.CatalogScreen
import com.android.tripbook.companycatalog.ui.screens.ExploreScreen
import com.android.tripbook.companycatalog.ui.screens.HomeScreen
import com.android.tripbook.companycatalog.ui.screens.MapViewScreen
import com.android.tripbook.companycatalog.ui.screens.ProfileScreen
import com.android.tripbook.companycatalog.data.MockCompanyData
import com.android.tripbook.ui.theme.TripBookTheme
import org.osmdroid.util.GeoPoint

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
            TripBookTheme {
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
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } ?: false,
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
                ExploreScreen(navController = navController)
            }
            composable(MainAppScreen.Catalog.route) {
                CatalogScreen(navController = navController)
            }
            composable(MainAppScreen.Profile.route) {
                ProfileScreen()
            }
            composable("map") {
                MapViewScreen(
                    navController = navController,
                    companies = MockCompanyData.companies,
                    userLocation = GeoPoint(0.0, 0.0)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppRootNavHostPreview() {
    TripBookTheme {
        AppRootNavHost()
    }
}
