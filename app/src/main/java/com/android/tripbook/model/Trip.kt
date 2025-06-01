package com.android.tripbook.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}

data class Trip(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int,
    val status: TripStatus,
    val type: String = "",
    val description: String = "",
    val activities: List<Activity> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val travelersList: List<Traveler> = emptyList()
)

data class Activity(
    val date: LocalDate,
    val time: LocalTime,
    val title: String,
    val location: String,
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

class TripViewModel : ViewModel() {
    private val _tripList = mutableStateListOf<Trip>()
    val tripList: List<Trip> = _tripList

    var tripInfo by mutableStateOf(
        Trip(
            id = "",
            name = "",
            startDate = LocalDate.now(),
            endDate =  LocalDate.now(),
            destination = "",
            travelers = 1,
            budget = 0,
            status = TripStatus.PLANNED,
            type = "",
            description = "",
        )
    )

    var tripTime by mutableStateOf(
        Activity(
            time = LocalTime.now(),
            date = LocalDate.now(),
            title = "",
            location = "",
            description = ""
        )
    )

    fun addTrip() {
        updateStatus()
        _tripList.add(tripInfo)
    }

    var travelersInput by mutableStateOf("1") // default as string
        private set

    fun updateTravelers(value: String) {
        travelersInput = value
        tripInfo = tripInfo.copy(travelers = value.toIntOrNull() ?: 1)
    }

    fun updateName(value: String) {
        tripInfo = tripInfo.copy(name = value)
    }

    fun updateDestination(value: String) {
        tripInfo = tripInfo.copy(destination = value)
    }

    fun updateDescription(value: String) {
        tripInfo = tripInfo.copy(description = value)
    }

    var budgetInput by mutableStateOf("1") // default as string
        private set

    fun updateBudget(value: String) {
        budgetInput = value
        tripInfo = tripInfo.copy(budget = value.toIntOrNull() ?: 0)
    }

    fun updateTime(value: LocalTime) {
        tripTime = tripTime.copy(time = value)
    }

    fun updateDate(value: LocalDate) {
        tripInfo = tripInfo.copy(startDate = value)
    }

    fun updateStatus() {
        val now = LocalDate.now()
        tripInfo = tripInfo.copy(
            status = when {
                now.isBefore(tripInfo.startDate) -> TripStatus.PLANNED
                now.isEqual(tripInfo.startDate) -> TripStatus.ACTIVE
                else -> TripStatus.COMPLETED
            }
        )
    }
}