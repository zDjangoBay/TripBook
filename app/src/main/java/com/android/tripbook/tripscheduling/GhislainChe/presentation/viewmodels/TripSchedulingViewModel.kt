package com.android.tripbook.tripscheduling.GhislainChe.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.DepartureSchedule
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.ServiceCategory
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.VehicleCapacity
import com.android.tripbook.tripscheduling.GhislainChe.domain.repositories.TripScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class TripSchedulingViewModel(
    private val tripScheduleRepository: TripScheduleRepository
) : ViewModel() {
    private val _schedules = MutableStateFlow<List<DepartureSchedule>>(emptyList())
    val schedules: StateFlow<List<DepartureSchedule>> = _schedules.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
    val selectedCategory: StateFlow<ServiceCategory?> = _selectedCategory.asStateFlow()

    private val _capacity = MutableStateFlow<VehicleCapacity?>(null)
    val capacity: StateFlow<VehicleCapacity?> = _capacity.asStateFlow()

    fun loadSchedules(origin: String, destination: String, date: LocalDate, category: ServiceCategory? = null) {
        viewModelScope.launch {
            val result = tripScheduleRepository.getAvailableSchedules(origin, destination, date, category)
            _schedules.value = result
        }
    }

    fun selectCategory(category: ServiceCategory) {
        _selectedCategory.value = category
    }

    fun observeCapacity(scheduleId: String) {
        viewModelScope.launch {
            tripScheduleRepository.observeScheduleCapacity(scheduleId).collect {
                _capacity.value = it
            }
        }
    }
}