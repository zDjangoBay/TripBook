package com.android.tripbook.model

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}
class TripViewModel : ViewModel() {

    var tripInfo by mutableStateOf(
        Trip(
            name = "",
            startDate = LocalDate.now().toString(),
            destination = "",
            travelers = 1,
            budget = 0,
            status = TripStatus.PLANNED,
            time = LocalTime.now().toString()
        )
    )

    fun saveData(
        trip:Trip,
        context: Context
    ) = CoroutineScope(Dispatchers.IO).launch {
        val fireStoreRef = Firebase.firestore
            .collection("Trip")
            .document(trip.name)

        try{
            fireStoreRef.set(trip)
                .addOnSuccessListener{
                    Toast.makeText(context,"successfully saved", Toast.LENGTH_SHORT).show()
                }

        }catch (e: Exception) {
            Toast.makeText(context,e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val _tripList = mutableStateOf<List<Trip>>(emptyList())
    var tripList: State<List<Trip>> = _tripList

    private var isLoading by mutableStateOf(true)
    private var errorMessage by mutableStateOf<String?>(null)


    init {
        listenToTrips()
    }

    private fun listenToTrips() {
        Firebase.firestore.collection("Trip")
            .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                isLoading = false
                if (error != null) {
                    errorMessage = "Failed to load trips: ${error.message}"
                    return@addSnapshotListener
                }

                snapshot?.let {
                    try {
                        it.documents.mapNotNull { doc ->
                            doc.toObject(Trip::class.java)
                        }.also { tripList }
                    } catch (e: Exception) {
                        errorMessage = "Parsing error: ${e.message}"
                    }
                }
            }
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

    var budgetInput by mutableStateOf("1") // default as string
        private set

    fun updateBudget(value: String) {
        budgetInput = value
        tripInfo = tripInfo.copy(budget = value.toIntOrNull() ?: 0)
    }

    fun updateTime(localTime: LocalTime) {
        tripInfo = tripInfo.copy(time = localTime.toString())
    }

    fun updateDate(localDate: LocalDate) {
        tripInfo = tripInfo.copy(startDate = localDate.toString())
    }

    fun getLocalDate(): LocalDate {
        return try {
            LocalDate.parse(tripInfo.startDate)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    fun getLocalTime(): LocalTime {
        return try {
            LocalTime.parse(tripInfo.time)
        } catch (e: Exception) {
            LocalTime.now()
        }
    }
}