package com.android.tripbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class SchedulingViewModel(
    private val tripScheduleRepository: TripScheduleRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    // UI States using sealed classes
    sealed class ScheduleUiState {
        data object Loading : ScheduleUiState()
        data class Success(val schedules: List<Schedule>) : ScheduleUiState()
        data class Error(val message: String) : ScheduleUiState()
    }

    // Shared state for the entire module
    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    // Shared error handling
    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(dispatcher) {
            _uiState.value = ScheduleUiState.Loading
            try {
                val schedules = tripScheduleRepository.getAllSchedules()
                _uiState.value = ScheduleUiState.Success(schedules)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    suspend fun getScheduleById(scheduleId: String): Schedule? {
        return try {
            tripScheduleRepository.getScheduleById(scheduleId)
        } catch (e: Exception) {
            handleError(e)
            null
        }
    }

    fun refreshData() {
        loadInitialData()
    }

    fun validateScheduleDates(startDate: LocalDate, endDate: LocalDate): Boolean {
        return startDate.isBefore(endDate) || startDate.isEqual(endDate)
    }

    private fun handleError(e: Exception) {
        val errorMessage = ViewModelErrorHandler.handleError(e)
        _uiState.value = ScheduleUiState.Error(errorMessage)
        _errorEvents.tryEmit(errorMessage)
    }
}