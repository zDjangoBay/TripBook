package com.android.tripbook.data.repository

import android.content.Context
import com.android.tripbook.data.model.Reminder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.IOException

/**
 * Mock implementation of ReminderRepository.
 * Simulates network delay and stores reminders in memory.
 *
 * TODO: Replace in-memory storage with persistent storage (SharedPreferences or Room) for production.
 */
class MockReminderRepository(private val context: Context) : ReminderRepository {

    // In-memory storage for reminders. Lost when app process is killed.
    private val remindersFlow = MutableStateFlow<MutableMap<Long, Reminder>>(mutableMapOf())

    private suspend fun simulateNetworkDelay() {
        delay(500) // Simulate 500ms network latency
    }

    override suspend fun addReminder(reminder: Reminder): Result<Reminder> {
        simulateNetworkDelay()
        return try {
            remindersFlow.update { currentMap ->
                currentMap[reminder.id] = reminder
                currentMap // Return the updated map
            }
            Result.success(reminder)
        } catch (e: Exception) {
            Result.failure(IOException("Failed to add reminder (mock error)", e))
        }
    }

    override suspend fun getReminder(id: Long): Result<Reminder?> {
        simulateNetworkDelay()
        return try {
            val reminder = remindersFlow.value[id]
            Result.success(reminder)
        } catch (e: Exception) {
            Result.failure(IOException("Failed to get reminder (mock error)", e))
        }
    }

    override fun getAllReminders(): Flow<Result<List<Reminder>>> {
        // No network delay simulation here as Flow provides updates
        return remindersFlow.map { map ->
            try {
                Result.success(map.values.toList().sortedBy { it.reminderTimeMillis })
            } catch (e: Exception) {
                Result.failure(IOException("Failed to get all reminders (mock error)", e))
            }
        }
    }

    override suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        simulateNetworkDelay()
        return try {
            if (remindersFlow.value.containsKey(reminder.id)) {
                remindersFlow.update { currentMap ->
                    currentMap[reminder.id] = reminder
                    currentMap
                }
                Result.success(Unit)
            } else {
                Result.failure(NoSuchElementException("Reminder with id ${reminder.id} not found"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Failed to update reminder (mock error)", e))
        }
    }

    override suspend fun deleteReminder(id: Long): Result<Unit> {
        simulateNetworkDelay()
        return try {
            if (remindersFlow.value.containsKey(id)) {
                remindersFlow.update { currentMap ->
                    currentMap.remove(id)
                    currentMap
                }
                Result.success(Unit)
            } else {
                Result.failure(NoSuchElementException("Reminder with id $id not found"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Failed to delete reminder (mock error)", e))
        }
    }
}
