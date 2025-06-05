package com.android.Tripbook.Datamining.modules.data.tripscheduling.model



import java.time.LocalDateTime
import java.util.UUID
import com.android.Tripbook.Datamining.modules.data.*

// This class we define is a more particular instance of a trip , let's say for example
// while a trip might be "Depart Buca Prestige en partance de Douala pour Yaounde "
// a trip schedule should be more "Le depart BUCA238 du 30 Novembre 2025 en partance de Douala pour Yaoundé "

data class TripSchedule(

    val TripSchedule_id: String = UUID.randomUUID().toString(),

    val trip_id:String, // This refers to the trip object , no join but just refer to a particular trip
    val tripTitle:String, // This refers to the title of the trip , let's say for instance ,
    val tripDescription:String,// A complete description of a trip schedule
    val DepartureTime: LocalDateTime,
    val arrivalDateTime: LocalDateTime,// user should be able to reference

    val totalCapabilities:Int,// This refers to let's say the number of seats the user can see
    val availableSeats:Int, // This refers to the number of seats of the aggglumentaion
    val vehicleType:String?,//This refers to the type of transport mean
    val company_id:String, // This refers to the company id
    val tripStatus:TripScheduleStatus,
    val isBookable:Boolean=true // In order to know if this TripSchedule is still bok
)
