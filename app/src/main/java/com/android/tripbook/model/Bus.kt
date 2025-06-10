package com.android.tripbook.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Bus(
    val busId: Int,
    val busName: String,
    val timeOfDeparture: String, // Store as ISO string for serialization
    val agencyId: Int,
    val destinationId: Int,
    val destinationName: String = "",
    val agencyName: String = ""
) {
    // Helper functions for LocalDateTime conversion
    fun getDepartureDateTime(): LocalDateTime {
        return LocalDateTime.parse(timeOfDeparture, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    companion object {
        fun create(
            busId: Int,
            busName: String,
            departureTime: LocalDateTime,
            agencyId: Int,
            destinationId: Int,
            destinationName: String = "",
            agencyName: String = ""
        ): Bus {
            return Bus(
                busId = busId,
                busName = busName,
                timeOfDeparture = departureTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                agencyId = agencyId,
                destinationId = destinationId,
                destinationName = destinationName,
                agencyName = agencyName
            )
        }
    }
}