import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.tripbook.ui.BottomNavItem

@Composable
fun MyBottomNavigationBar(navController: NavController) {
    val items = kotlin.collections.listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Search,
        BottomNavItem.Profile
    )

    BottomNavigation(
        // backgroundColor = MaterialTheme.colors.primary, // Customize as needed
        // contentColor = Color.White // Customize as needed
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                // selectedContentColor = Color.White, // Customize
                // unselectedContentColor = Color.White.copy(0.4f) // Customize
            )
        }
    }
}