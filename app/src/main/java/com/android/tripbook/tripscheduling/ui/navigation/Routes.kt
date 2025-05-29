package com.android.tripbook.tripscheduling.ui.navigation

sealed class SchedulingRoutes(val route: String) {

    // Route for the schedule list screen
    object List : SchedulingRoutes("schedule_list")

    // Route for the schedule creation screen
    object Creation : SchedulingRoutes("schedule_creation")

    // Constants for the details screen route and argument
    object Details : SchedulingRoutes("$DETAILS_ROUTE/{$DETAILS_ARG}") {
        fun createRoute(scheduleId: String) = "$DETAILS_ROUTE/$scheduleId"
    }

    companion object {
        const val DETAILS_ROUTE = "schedule_details"
        const val DETAILS_ARG = "scheduleId"
    }
}
