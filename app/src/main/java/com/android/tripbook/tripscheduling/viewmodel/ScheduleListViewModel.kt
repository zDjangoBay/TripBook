package com.android.tripbook.tripscheduling.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.tripbook.tripscheduling.data.model.Schedule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleListViewModel : ViewModel() {
    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules

    init {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        _schedules.value = listOf(
            Schedule(
                id = "1",
                title = "Visit the Eiffel Tower",
                dateTime = LocalDateTime.parse("2025-07-01 10:00", formatter),
                lat = 48.8584,
                lng = 2.2945
            ),
            Schedule(
                id = "2",
                title = "Louvre Museum Tour",
                dateTime = LocalDateTime.parse("2025-07-02 14:30", formatter),
                lat = 48.8606,
                lng = 2.3376
            ),
            Schedule(
                id = "3",
                title = "Seine River Cruise",
                dateTime = LocalDateTime.parse("2025-07-03 18:00", formatter),
                lat = 48.8600,
                lng = 2.3000
            )
        )
    }


    fun refreshSchedules() {
        // TODO: fetch new schedules from repository and post to _schedules
    }
}
