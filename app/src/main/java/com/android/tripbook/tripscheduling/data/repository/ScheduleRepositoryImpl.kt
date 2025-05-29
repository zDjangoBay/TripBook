package com.android.tripbook.tripscheduling.data.repository

import com.android.tripbook.tripscheduling.data.model.Schedule
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    // TODO: Inject my DAO/data source here
) : ScheduleRepository {

    override suspend fun addSchedule(schedule: Schedule) {
        // TODO: Add logic to save the schedule (use: scheduleDao.insert(schedule))
    }
}
