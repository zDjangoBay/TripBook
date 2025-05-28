package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.tripbook.ui.theme.Purple40

@Composable
fun BaseScaffold(
    navController: NavController,
    title: String,
    isLoading: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    Scaffold(
        topBar = { TopBar(title = title) },
        bottomBar = {
            BottomNavigationBar(
                current = currentRoute,
                onNavigate = {
                    // Prevent multiple copies of the same destination
                    if (navController.currentDestination?.route != it) {
                        navController.navigate(it) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
                ,
                selectedColor = Purple40
            )
        },
        content = { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                content(innerPadding)
                ScreenLoader(isLoading)
            }
        }
    )
}
