package com.android.tripbook.companycatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.android.tripbook.companycatalog.ui.screens.*
import com.android.tripbook.ui.theme.TripBookTheme
import androidx.compose.material.icons.filled.Category


sealed class MainAppScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : MainAppScreen("home", "Home", Icons.Default.Home)
    object Explore : MainAppScreen("explore", "Explore", Icons.Default.CompassCalibration)
    object Catalog : MainAppScreen("catalog", "Catalog", Icons.Default.Store)
    object Profile : MainAppScreen("profile", "Profile", Icons.Default.Person)
    object Settings : MainAppScreen("settings", "Settings", Icons.Default.Settings)
    object Categories : MainAppScreen("categories", "Categories", Icons.Default.Category)

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
        MainAppScreen.Profile,
        MainAppScreen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selected,
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
        Box(modifier = Modifier.padding(padding)) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = MainAppScreen.Home.route
                ) {
                    composable(MainAppScreen.Home.route) {
                        HomeScreen(navController)
                    }
                    composable(MainAppScreen.Explore.route) {
                        ExploreScreen()
                    }
                    composable(MainAppScreen.Catalog.route) {
                        CatalogScreen(navController)
                    }
                    composable(MainAppScreen.Profile.route) {
                        ProfileScreen()
                    }
                    composable(MainAppScreen.Settings.route) {
                        SettingsScreen()
                    }
                    composable(MainAppScreen.Categories.route) {
                        CategoriesScreen() // or without navController if not needed
                    }

                    composable(
                        "companyDetail/{companyId}?tab={tab}",
                        arguments = listOf(
                            navArgument("companyId") { type = NavType.StringType },
                            navArgument("tab") {
                                type = NavType.StringType
                                defaultValue = "0"
                            }
                        )
                    ) { backStackEntry ->
                        val companyId = backStackEntry.arguments?.getString("companyId")
                        val tabIndex = backStackEntry.arguments?.getString("tab")?.toIntOrNull() ?: 0
                        if (companyId != null) {
                            CompanyDetailScreen(companyId, initialTab = tabIndex)
                        } else {
                            Text(
                                "Company not found",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
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
