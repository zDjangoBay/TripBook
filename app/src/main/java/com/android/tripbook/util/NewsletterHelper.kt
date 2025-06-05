package com.android.tripbook.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.android.tripbook.R
import kotlin.random.Random

object NewsletterHelper {

    // Simple placeholder data - replace with real data source if needed
    private val locations = listOf(
        Pair(R.string.location_zanzibar_title, R.string.location_zanzibar_desc),
        Pair(R.string.location_marrakech_title, R.string.location_marrakech_desc)
        // Add more locations here
    )

    private val tips = listOf(
        R.string.tip_hydration,
        R.string.tip_currency
        // Add more tips here
    )

    /**
     * Generates the subject and body for the fake newsletter.
     */
    fun generateNewsletterContent(context: Context): Pair<String, String> {
        val subject = context.getString(R.string.newsletter_subject)

        // --- Select random content ---
        val randomLocationPair = locations.random()
        val randomTipResId = tips.random()

        // --- Build Body String ---
        // Consider fetching user name if available
        val greeting = context.getString(R.string.newsletter_greeting_generic)
        val locationTitle = context.getString(randomLocationPair.first)
        val locationDesc = context.getString(randomLocationPair.second)
        val tipTitle = context.getString(R.string.newsletter_tip_title)
        val tip = context.getString(randomTipResId)
        val closing = context.getString(R.string.newsletter_closing)

        val body = """
            $greeting

            $locationTitle
            $locationDesc

            $tipTitle
            $tip

            $closing
        """.trimIndent()

        return Pair(subject, body)
    }

    /**
     * Launches an email Intent to allow the user to send the generated newsletter.
     */
    fun launchEmailIntent(context: Context, subject: String, body: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps should handle this
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            val chooserIntent = Intent.createChooser(emailIntent, context.getString(R.string.newsletter_chooser_title))
            // Check if Activity resolves before starting
            if (emailIntent.resolveActivity(context.packageManager) != null) {
                 context.startActivity(chooserIntent)
            } else {
                 Toast.makeText(context, context.getString(R.string.newsletter_no_email_client), Toast.LENGTH_SHORT).show()
            }
        } catch (e: ActivityNotFoundException) {
            // Handle case where no email app is installed (though resolveActivity should catch this)
            Toast.makeText(context, context.getString(R.string.newsletter_no_email_client), Toast.LENGTH_SHORT).show()
        }
    }
}
