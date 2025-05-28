package com.android.tripbook

import com.android.tripbook.Schedule

interface TripScheduleRepository {
    suspend fun getAllSchedules(): List<Schedule>
    suspend fun getScheduleById(id: String): Schedule?
}