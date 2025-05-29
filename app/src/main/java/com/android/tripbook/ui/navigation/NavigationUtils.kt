package com.android.tripbook.ui.navigation

import android.content.Context
import android.content.Intent
import com.android.tripbook.*

fun navigateTo(context: Context, destination: String) {
    val intent = when (destination) {
        "home" -> Intent(context, MainActivity::class.java)
        "catalog" -> Intent(context, TripCatalogActivity::class.java)
        // Add others when created
        "schedule" -> Intent(context, BookTripsActivity::class.java)
        "profile" -> Intent(context, ProfileActivity::class.java)
        else -> null
    }
    intent?.let {
        context.startActivity(it)
    }
}
