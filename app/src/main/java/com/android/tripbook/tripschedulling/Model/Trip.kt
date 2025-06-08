package com.android.tripbook.tripschedulling.Model

import kotlinx.serialization.Serializable
import java.util

@Serializable
data class Trip(
     val id: String = UUID.randomUUID().toString(),
     val destination: String,
     val startdate: String,
     val endDate: String,
     val participants: List<String> = emptylist()
)
