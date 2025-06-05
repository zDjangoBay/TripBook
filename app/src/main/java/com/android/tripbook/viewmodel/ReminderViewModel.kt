package com.android.tripbook.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.model.Reminder
import com.android.tripbook.data.repository.ReminderRepository
import com.android.tripbook.service.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar

// Define states for UI feedback
sealed class ReminderUiState {
    object Idle : ReminderUiState()
    object Loading : ReminderUiState()
    data class Success(val message: String) : ReminderUiState()
    data class Error(val message: String) : ReminderUiState()
}

// Assume ReminderRepository is injectable (e.g., using Hilt or manual injection)
class ReminderViewModel( // Or integrate into an existing ViewModel
    private val reminderRepository: ReminderRepository,
    private val applicationContext: Context // Need context for scheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReminderUiState>(ReminderUiState.Idle)
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    // Example: LiveData or StateFlow to hold the list of reminders for display
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    init {
        loadAllReminders()
    }

    private fun loadAllReminders() {
        viewModelScope.launch {
            reminderRepository.getAllReminders()
                .catch { e ->
                    _uiState.value = ReminderUiState.Error("Failed to load reminders: ${e.message}")
                }
                .collect { result ->
                    result.onSuccess { list -> _reminders.value = list }
                    result.onFailure { e ->
                         _uiState.value = ReminderUiState.Error("Error loading reminders: ${e.message}")
                    }
                }
        }
    }

    fun setReminder(year: Int, month: Int, day: Int, hour: Int, minute: Int, message: String, relatedItemId: String? = null) {
        _uiState.value = ReminderUiState.Loading
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month) // Calendar month is 0-based
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val reminderTimeMillis = calendar.timeInMillis

            // Basic validation: Ensure time is in the future
            if (reminderTimeMillis <= System.currentTimeMillis()) {
                _uiState.value = ReminderUiState.Error("Reminder time must be in the future.")
                return@launch
            }

            val newReminder = Reminder(
                relatedItemId = relatedItemId,
                reminderTimeMillis = reminderTimeMillis,
                message = message.ifBlank { "TripBook Reminder" } // Default message if empty
            )

            val addResult = reminderRepository.addReminder(newReminder)

            addResult.onSuccess { addedReminder ->
                try {
                    ReminderScheduler.scheduleReminder(applicationContext, addedReminder)
                    // Optionally update the reminder in the repo to mark as scheduled
                    reminderRepository.updateReminder(addedReminder.copy(isScheduled = true))
                    _uiState.value = ReminderUiState.Success("Reminder set successfully!")
                    // Reload reminders to reflect the new one
                    // loadAllReminders() // Or just update the local list
                } catch (e: Exception) {
                    _uiState.value = ReminderUiState.Error("Failed to schedule reminder: ${e.message}")
                    // Attempt to clean up - delete the reminder from repo if scheduling failed
                    reminderRepository.deleteReminder(addedReminder.id)
                }
            }
            addResult.onFailure {
                _uiState.value = ReminderUiState.Error("Failed to save reminder: ${it.message}")
            }
        }
    }

    fun deleteReminder(reminderId: Long) {
        _uiState.value = ReminderUiState.Loading
        viewModelScope.launch {
            val deleteResult = reminderRepository.deleteReminder(reminderId)
            deleteResult.onSuccess {
                ReminderScheduler.cancelReminder(applicationContext, reminderId)
                _uiState.value = ReminderUiState.Success("Reminder deleted.")
                 // loadAllReminders() // Or just update the local list
            }
            deleteResult.onFailure {
                _uiState.value = ReminderUiState.Error("Failed to delete reminder: ${it.message}")
            }
        }
    }

    // Function to reset UI state after displaying a message
    fun resetUiState() {
        _uiState.value = ReminderUiState.Idle
    }
}
