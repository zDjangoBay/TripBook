package com.android.tripbook.tripscheduling.GhislainChe.domain.entities

enum class ScheduleType {
    FLEXIBLE,
    SEMI_FIXED,
    FIXED
}

enum class DeparturePolicy {
    ON_FULL_CAPACITY,
    SCHEDULED_WITH_FLEXIBILITY,
    STRICT_SCHEDULE
}

enum class ServiceCategory(
    val scheduleType: ScheduleType,
    val departurePolicy: DeparturePolicy
) {
    REGULAR(ScheduleType.FLEXIBLE, DeparturePolicy.ON_FULL_CAPACITY),
    VIP(ScheduleType.SEMI_FIXED, DeparturePolicy.SCHEDULED_WITH_FLEXIBILITY),
    PREMIUM(ScheduleType.FIXED, DeparturePolicy.STRICT_SCHEDULE)
}