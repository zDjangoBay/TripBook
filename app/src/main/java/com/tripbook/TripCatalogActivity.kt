// android/app/src/main/java/com/tripbook/TripCatalogActivity.kt
package com.tripbook

import android.app.Activity
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise

class TripCatalogActivity(reactContext: ReactApplicationContext) : 
    ReactContextBaseJavaModule(reactContext) {

    private val repository = TransportationRepository(TransportationService())

    override fun getName() = "TripCatalogActivity"

    // Expose bus schedules to React Native
    @ReactMethod
    fun getBusSchedules(promise: Promise) {
        try {
            val schedules = repository.fetchBusSchedules()
            promise.resolve(schedules.toWritableArray()) // Convert to React Native WritableArray
        } catch (e: Exception) {
            promise.reject("FETCH_ERROR", e.message)
        }
    }

    // Expose booking function to React Native
    @ReactMethod
    fun bookBusTicket(busId: String, promise: Promise) {
        try {
            val success = repository.bookBus(busId)
            promise.resolve(success)
        } catch (e: Exception) {
            promise.reject("BOOKING_ERROR", e.message)
        }
    }
}