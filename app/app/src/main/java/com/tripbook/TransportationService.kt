package com.tripbook

import android.util.Log

class TransportationService {
    // Mock data for Yaounde → Buea buses
    fun getBusSchedules(): List<BusSchedule> {
        return listOf(
            BusSchedule(
                id = "1",
                operator = "Guarantee Express",
                departureTime = "06:00 AM",
                arrivalTime = "11:30 AM",
                price = "XAF 5,000",
                duration = "5h 30m"
            ),
            BusSchedule(
                id = "2",
                operator = "Amour Mezam",
                departureTime = "07:30 AM",
                arrivalTime = "01:00 PM",
                price = "XAF 4,500",
                duration = "5h 30m"
            )
        )
    }

    // Mock booking confirmation
    fun bookTicket(busId: String): Boolean {
        Log.d("TransportationService", "Booked ticket for bus $busId")
        return true
    }
}

data class BusSchedule(
    val id: String,
    val operator: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: String,
    val duration: String
)