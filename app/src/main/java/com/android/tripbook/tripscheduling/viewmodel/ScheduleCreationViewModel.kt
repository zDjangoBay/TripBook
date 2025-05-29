package com.android.tripbook.tripscheduling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.android.tripbook.tripscheduling.data.model.Schedule
import com.android.tripbook.tripscheduling.data.repository.ScheduleRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ScheduleCreationViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    fun createSchedule(title: String, date: LocalDate, lat: Double, lng: Double) {
        val newSchedule = Schedule(
            id = UUID.randomUUID().toString(),
            title = title,
            dateTime = date.atStartOfDay(),
            lat = lat,
            lng = lng
        )

        viewModelScope.launch {
            repository.addSchedule(newSchedule)
        }
    }

}
