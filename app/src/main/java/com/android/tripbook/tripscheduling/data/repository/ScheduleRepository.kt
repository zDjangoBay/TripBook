package com.android.tripbook.tripscheduling.data.repository

import com.android.tripbook.tripscheduling.data.model.Schedule

interface ScheduleRepository {
    suspend fun addSchedule(schedule: Schedule)
}
