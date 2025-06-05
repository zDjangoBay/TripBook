package com.android.tripbook.model

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}
class TripViewModel : ViewModel() {
    private val _tripList = mutableStateListOf<Trip>()
    val tripList: List<Trip> = _tripList

    private val _List = mutableStateListOf<Activity>()
    val List: List<Activity> = _List

    var tripInfo by mutableStateOf(
        Trip(
//            id = "",
            name = "",
            startDate = LocalDate.now(),
//            endDate =  LocalDate.now(),
            destination = "",
            travelers = 1,
            budget = 0,
//            status = TripStatus.PLANNED,
//            type = "",
//            description = "",
        )
    )

    var tripTime by mutableStateOf(
        Activity(
            time = LocalTime.now(),
            date = LocalDate.now(),
            status = TripStatus.PLANNED,
            title = "",
            location = "",
            description = ""
        )
    )

    fun saveData(
        Trip:Trip,
        context: Context
    ) = CoroutineScope(Dispatchers.IO).launch {
        val fireStoreRef = Firebase.firestore
            .collection("Trip")
            .document(Trip.name)

        try{
            fireStoreRef.set(Trip)
                .addOnSuccessListener{
                    Toast.makeText(context,"successfully saved", Toast.LENGTH_SHORT).show()
                }

        }catch (e: Exception) {
            Toast.makeText(context,e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun retrieveData(
        name: String,
        context: Context,
        data: (Trip) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        val fireStoreRef = Firebase.firestore
            .collection("Trip")
            .document(name)

        try{
            fireStoreRef.get()
                .addOnSuccessListener{
                    if(it.exists()){
                        val Trip = it.toObject<Trip>()!!
                        data(Trip)
                    }else{
                        Toast.makeText(context,"No Trip Data Found", Toast.LENGTH_SHORT).show()
                    }
                }

        }catch (e: Exception) {
            Toast.makeText(context,e.message, Toast.LENGTH_SHORT).show()
        }
    }
//
//    fun addTrip() {
//        updateStatus()
//        _tripList.add(tripInfo)
//    }
//
//    var travelersInput by mutableStateOf("1") // default as string
//        private set
//
//    fun updateTravelers(value: String) {
//        travelersInput = value
//        tripInfo = tripInfo.copy(travelers = value.toIntOrNull() ?: 1)
//    }
//
//    fun updateName(value: String) {
//        tripInfo = tripInfo.copy(name = value)
//    }
//
//    fun updateDestination(value: String) {
//        tripInfo = tripInfo.copy(destination = value)
//    }
//
//    fun updateDescription(value: String) {
//        tripInfo = tripInfo.copy(description = value)
//    }
//
//    var budgetInput by mutableStateOf("1") // default as string
//        private set
//
//    fun updateBudget(value: String) {
//        budgetInput = value
//        tripInfo = tripInfo.copy(budget = value.toIntOrNull() ?: 0)
//    }
//
//    fun updateTime(value: LocalTime) {
//        tripTime = tripTime.copy(time = value)
//    }
//
//    fun updateDate(value: LocalDate) {
//        tripInfo = tripInfo.copy(startDate = value)
//    }
//
//    private fun updateStatus() {
//        val now = LocalDate.now()
//        tripInfo = tripInfo.copy(
//            status = when {
//                now.isBefore(tripInfo.startDate) -> TripStatus.PLANNED
//                now.isEqual(tripInfo.startDate) -> TripStatus.ACTIVE
//                else -> TripStatus.COMPLETED
//            }
//        )
//    }
}