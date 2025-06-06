package com.android.tripbook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.notifications.TripNotificationService
import com.android.tripbook.notifications.NotificationTester
import com.android.tripbook.repository.SupabaseTripRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TripViewModel(
    application: Application,
    private val repository: SupabaseTripRepository = SupabaseTripRepository.getInstance()
) : AndroidViewModel(application) {

    private val notificationService = TripNotificationService.getInstance(application)

    val trips: StateFlow<List<Trip>> = repository.trips
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    init {
        // Load trips when ViewModel is created
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            repository.loadTrips()
        }
    }

    fun refreshTrips() {
        loadTrips()
    }

    fun createTrip(tripCreationState: TripCreationState) {
        viewModelScope.launch {
            val trip = tripCreationState.toTrip()
            val result = repository.addTrip(trip)

            if (result.isSuccess) {
                // Schedule notifications for the new trip
                result.getOrNull()?.let { createdTrip ->
                    notificationService.onTripCreated(createdTrip)
                }
            } else {
                // Error is already handled in repository
                result.exceptionOrNull()?.let { exception ->
                    // Additional error handling if needed
                }
            }
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            val result = repository.updateTrip(trip)

            if (result.isSuccess) {
                // Update notifications for the modified trip
                notificationService.onTripUpdated(trip)
            } else {
                // Error is already handled in repository
                result.exceptionOrNull()?.let { exception ->
                    // Additional error handling if needed
                }
            }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            val result = repository.deleteTrip(tripId)

            if (result.isSuccess) {
                // Cancel notifications for the deleted trip
                notificationService.onTripDeleted(tripId)
            } else {
                // Error is already handled in repository
                result.exceptionOrNull()?.let { exception ->
                    // Additional error handling if needed
                }
            }
        }
    }

    fun getTripById(tripId: String): Trip? {
        return repository.getTripById(tripId)
    }

    fun clearError() {
        repository.clearError()
    }

    /**
     * Send a test notification to verify the notification system is working
     */
    fun sendTestNotification() {
        notificationService.sendTestNotification()
    }

    /**
     * Run comprehensive notification tests
     */
    fun runNotificationTests() {
        NotificationTester.runNotificationTests(getApplication())
    }

    /**
     * Test immediate notifications for debugging
     */
    fun testImmediateNotifications() {
        NotificationTester.testImmediateNotifications(getApplication())
    }

    /**
     * Get notification system status for debugging
     */
    fun getNotificationStatus() = notificationService.getNotificationStatus()

    /**
     * Get test summary
     */
    fun getTestSummary() = NotificationTester.getTestSummary()
}
