package com.android.tripbook.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Agencies : BottomNavItem("agencies", "Agencies", Icons.Default.Home)
    object MyTrips : BottomNavItem("my_trips", "My Trips", Icons.Default.List)
    object PlanTrip : BottomNavItem("plan_trip", "Plan Trip", Icons.Default.Add)

}
