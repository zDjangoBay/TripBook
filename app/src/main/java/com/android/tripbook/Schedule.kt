package com.android.tripbook

import org.threeten.bp.LocalDate

data class Schedule(
    val id: String,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destinations: List<String>,
    val activities: List<String>
)