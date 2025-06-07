package com.android.Tripbook.Datamining.modules.data.tripscheduling.model

enum class TripScheduleStatus {
     // A field which will help us know if the TripSchedule is open for booking or not
    ACTIVE,         // Schedule is open for bookings
    // This help to know if the trip has been postponed due to x or y circumstances
    DELAYED,
    // The Trip schedule is not open for booking, there is no way to book a trip
    CANCELLED,
    // The trip has been achieved , the transport mean reached its destination
    COMPLETED,
    // The trip has departed
    DEPARTED,
    // This helps set the status of the Trip when passenger are entering the bus
    BOARDING
}