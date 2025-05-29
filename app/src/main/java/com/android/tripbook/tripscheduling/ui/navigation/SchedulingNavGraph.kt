package com.android.tripbook.tripscheduling.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.android.tripbook.tripscheduling.ui.screens.ScheduleListScreen
import com.android.tripbook.tripscheduling.ui.screens.ScheduleDetailsScreen
import com.android.tripbook.tripscheduling.ui.screens.ScheduleCreationScreen

@Composable
fun SchedulingNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SchedulingRoutes.List.route
    ) {
        composable(SchedulingRoutes.List.route) {
            ScheduleListScreen(
                onNavigateToCreation = {
                    navController.navigate(SchedulingRoutes.Creation.route)
                },
                onNavigateToDetails = { id ->
                    navController.navigate(SchedulingRoutes.Details.createRoute(id))
                }
            )
        }

        composable(SchedulingRoutes.Creation.route) {
            ScheduleCreationScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }

        composable(
            route = SchedulingRoutes.Details.route,
            arguments = listOf(navArgument(SchedulingRoutes.DETAILS_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val scheduleId = backStackEntry.arguments?.getString(SchedulingRoutes.DETAILS_ARG) ?: ""
            ScheduleDetailsScreen(scheduleId = scheduleId, onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}
