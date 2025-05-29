package com.android.tripbook.tripscheduling.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatForDisplay(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a")
    return this.format(formatter)
}
