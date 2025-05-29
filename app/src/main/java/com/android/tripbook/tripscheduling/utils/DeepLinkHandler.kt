package com.android.tripbook.tripscheduling.utils

import android.net.Uri
import androidx.navigation.NavController
import com.android.tripbook.tripscheduling.ui.navigation.SchedulingRoutes

fun parseScheduleId(uri: Uri): String? {
    return uri.pathSegments
        .takeIf { it.size >= 2 && it[0] == "schedule_details" }
        ?.get(1)
}

fun handleDeepLink(uri: Uri, navController: NavController) {
    val segments = uri.pathSegments

    if (segments.isEmpty()) return

    when (segments[0]) {
        "schedule_details" -> {
            val scheduleId = parseScheduleId(uri)
            if (scheduleId != null) {
                navController.navigate(SchedulingRoutes.Details.createRoute(scheduleId))
            } else {
                // Optional: Handle invalid or missing ID
            }
        }

        // Going to put more routes like this:
        // "create_schedule" -> navController.navigate(SchedulingRoutes.Creation.route)

        else -> {
            //  for my unknown route handling
        }
    }
}
