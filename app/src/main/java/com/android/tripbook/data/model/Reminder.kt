package com.android.tripbook.data.model

/**
 * Represents a reminder set by the user.
 *
 * @param id Unique identifier for the reminder (using timestamp for simplicity).
 * @param relatedItemId Optional ID of the item this reminder is related to (e.g., trip ID, location ID).
 * @param reminderTimeMillis The exact time in milliseconds (epoch) when the reminder should trigger.
 * @param message The notification message to display.
 * @param isScheduled Flag indicating if the reminder has been successfully scheduled with AlarmManager.
 */
data class Reminder(
    val id: Long = System.currentTimeMillis(),
    val relatedItemId: String? = null,
    val reminderTimeMillis: Long,
    val message: String,
    var isScheduled: Boolean = false // Use var if status needs update after scheduling
)
