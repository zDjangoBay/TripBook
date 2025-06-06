package com.android.tripbook.ui.utils

/**
 * Standardized default text values for consistent UI/UX
 * Provides meaningful fallback text for null/empty fields
 */
object TextDefaults {
    
    // Trip-related defaults
    const val UNTITLED_TRIP = "Untitled Trip"
    const val NO_DESTINATION = "Destination not set"
    const val NO_DESCRIPTION = "No description provided"
    const val NO_AGENCY_SELECTED = "No agency selected"
    const val NO_BUS_SELECTED = "No bus selected"
    const val SOLO_TRIP = "Solo trip"
    
    // Date and time defaults
    const val NO_DATE_SELECTED = "Date not selected"
    const val NO_TIME_SELECTED = "Time not set"
    const val NO_DURATION = "Duration not specified"
    
    // Location defaults
    const val NO_LOCATION = "Location not specified"
    const val NO_ADDRESS = "Address not available"
    
    // Activity and itinerary defaults
    const val NO_ACTIVITIES_PLANNED = "No activities planned for this day"
    const val ADD_FIRST_ACTIVITY = "Tap the + button to add your first activity"
    const val NO_NOTES = "No notes added"
    
    // Search and filter defaults
    const val SEARCH_PLACEHOLDER = "Search trips..."
    const val NO_RESULTS_FOUND = "No trips found"
    const val LOADING_TEXT = "Loading..."
    
    // Contact defaults
    const val NO_PHONE = "Phone not provided"
    const val NO_EMAIL = "Email not provided"
    
    // Budget and cost defaults
    const val NO_BUDGET = "Budget not set"
    const val NO_COST = "Cost not specified"
    
    // Status defaults
    const val STATUS_UNKNOWN = "Status unknown"
}

/**
 * Extension functions for consistent default text handling
 */
fun String?.orDefault(default: String): String = if (this.isNullOrBlank()) default else this

fun String.ifEmptyDefault(default: String): String = if (this.isEmpty()) default else this

fun Int?.orDefaultBudget(): String = this?.let { "FCFA $it" } ?: TextDefaults.NO_BUDGET

fun Double?.orDefaultCost(): String = this?.let { "FCFA $it" } ?: TextDefaults.NO_COST
