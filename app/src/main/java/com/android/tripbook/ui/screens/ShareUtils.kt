package com.android.tripbook.ui.screens

import android.content.Context
import android.content.Intent
import com.android.tripbook.model.Trip

object ShareUtils {
    fun shareTrip(context: Context, trip: Trip) {
        val shareText = """
            Check out this trip on TripBook!
            
            Destination: ${trip.title}
            Location: ${trip.city}, ${trip.country}
            
            ${trip.description}
            
            View more in the TripBook app!
            [Trip Link Here]
        """.trimIndent()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
} 