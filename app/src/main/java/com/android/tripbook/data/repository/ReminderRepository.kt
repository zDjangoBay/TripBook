package com.android.tripbook.data.repository

import com.android.tripbook.data.model.Reminder
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining operations for managing reminders.
 * This abstracts the data source (local mock or remote server).
 */
interface ReminderRepository {
    /**
     * Adds a new reminder to the data source.
     * @param reminder The reminder to add.
     * @return A Result indicating success or failure, containing the added reminder (with potential ID update).
     */
    suspend fun addReminder(reminder: Reminder): Result<Reminder>

    /**
     * Retrieves a specific reminder by its ID.
     * @param id The ID of the reminder to retrieve.
     * @return A Result containing the Reminder if found, or null, or an error.
     */
    suspend fun getReminder(id: Long): Result<Reminder?>

    /**
     * Retrieves all reminders from the data source.
     * Uses Flow for potential real-time updates if backed by Room or a reactive source.
     * For mock implementation, it might just return a snapshot.
     * @return A Flow emitting a list of reminders or an error.
     */
    fun getAllReminders(): Flow<Result<List<Reminder>>>

    /**
     * Updates an existing reminder.
     * @param reminder The reminder with updated information.
     * @return A Result indicating success or failure of the update.
     */
    suspend fun updateReminder(reminder: Reminder): Result<Unit>

    /**
     * Deletes a reminder by its ID.
     * @param id The ID of the reminder to delete.
     * @return A Result indicating success or failure of the deletion.
     */
    suspend fun deleteReminder(id: Long): Result<Unit>
}
