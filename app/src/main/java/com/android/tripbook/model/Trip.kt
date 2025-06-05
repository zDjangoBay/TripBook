package com.android.tripbook.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime

//enum class TripStatus {
//    PLANNED, ACTIVE, COMPLETED
//}

data class Trip(
//    val id: String,
    val name: String,
    val startDate: LocalDate,
//    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int,
//    val type: String = "",
//    val description: String = "",
//    val activities: List<Activity> = emptyList(),
//    val expenses: List<Expense> = emptyList(),
//    val travelersList: List<Traveler> = emptyList()
)

data class Activity(
    val date: LocalDate,
    val time: LocalTime,
    val title: String,
    val location: String,
    val status: TripStatus,
    val description: String = ""
)

data class Expense(
    val category: String,
    val description: String,
    val amount: Int
)

data class Traveler(
    val name: String,
    val isLeader: Boolean = false
)