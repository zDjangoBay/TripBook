package com.android.tripbook.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager as AndroidLocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.TimeUnit

/**
 * LocationManager utility class for handling location services
 * Features:
 * - Permission management
 * - Location updates
 * - Distance calculations
 * - Provider status monitoring
 */
class LocationManager(private val context: Context) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager
    private val locationChannel = Channel<Location>(Channel.UNLIMITED)
    private var locationListener: LocationListener? = null

    companion object {
        private const val TAG = "LocationManager"
        private const val MIN_TIME_BETWEEN_UPDATES = 5000L // 5 seconds
        private const val MIN_DISTANCE_CHANGE = 10f // 10 meters

        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ActivityCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if GPS is enabled
     */
    fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(AndroidLocationManager.GPS_PROVIDER)
    }

    /**
     * Check if Network location is enabled
     */
    fun isNetworkLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(AndroidLocationManager.NETWORK_PROVIDER)
    }

    /**
     * Get the best available location provider
     */
    fun getBestProvider(): String? {
        return when {
            isGpsEnabled() -> AndroidLocationManager.GPS_PROVIDER
            isNetworkLocationEnabled() -> AndroidLocationManager.NETWORK_PROVIDER
            else -> null
        }
    }

    /**
     * Get last known location
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(): Location? {
        if (!hasLocationPermissions()) {
            Log.w(TAG, "Location permissions not granted")
            return null
        }

        val providers = listOf(
            AndroidLocationManager.GPS_PROVIDER,
            AndroidLocationManager.NETWORK_PROVIDER,
            AndroidLocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null

        for (provider in providers) {
            try {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null && isBetterLocation(location, bestLocation)) {
                    bestLocation = location
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting last known location from $provider", e)
            }
        }

        return bestLocation
    }

    /**
     * Start location updates
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(): Flow<Location> {
        if (!hasLocationPermissions()) {
            Log.w(TAG, "Location permissions not granted")
            return locationChannel.receiveAsFlow()
        }

        val provider = getBestProvider()
        if (provider == null) {
            Log.w(TAG, "No location provider available")
            return locationChannel.receiveAsFlow()
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d(TAG, "Location updated: ${location.latitude}, ${location.longitude}")
                locationChannel.trySend(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                Log.d(TAG, "Location provider $provider status changed: $status")
            }

            override fun onProviderEnabled(provider: String) {
                Log.d(TAG, "Location provider $provider enabled")
            }

            override fun onProviderDisabled(provider: String) {
                Log.d(TAG, "Location provider $provider disabled")
            }
        }

        try {
            locationManager.requestLocationUpdates(
                provider,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE,
                locationListener!!
            )
            Log.d(TAG, "Started location updates with provider: $provider")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting location updates", e)
        }

        return locationChannel.receiveAsFlow()
    }

    /**
     * Stop location updates
     */
    fun stopLocationUpdates() {
        locationListener?.let { listener ->
            try {
                locationManager.removeUpdates(listener)
                locationListener = null
                Log.d(TAG, "Stopped location updates")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping location updates", e)
            }
        }
    }

    /**
     * Calculate distance between two locations in meters
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val location1 = Location("point1").apply {
            latitude = lat1
            longitude = lon1
        }
        val location2 = Location("point2").apply {
            latitude = lat2
            longitude = lon2
        }
        return location1.distanceTo(location2)
    }

    /**
     * Calculate distance between two Location objects
     */
    fun calculateDistance(location1: Location, location2: Location): Float {
        return location1.distanceTo(location2)
    }

    /**
     * Check if a location is within a certain radius of another location
     */
    fun isWithinRadius(
        centerLat: Double,
        centerLon: Double,
        targetLat: Double,
        targetLon: Double,
        radiusMeters: Float
    ): Boolean {
        val distance = calculateDistance(centerLat, centerLon, targetLat, targetLon)
        return distance <= radiusMeters
    }

    /**
     * Convert meters to appropriate distance string
     */
    fun formatDistance(distanceMeters: Float): String {
        return when {
            distanceMeters < 1000 -> "${distanceMeters.toInt()} m"
            distanceMeters < 10000 -> "${"%.1f".format(distanceMeters / 1000)} km"
            else -> "${"%.0f".format(distanceMeters / 1000)} km"
        }
    }

    /**
     * Determine if one location reading is better than the current best estimate
     */
    private fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            return true
        }

        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TimeUnit.SECONDS.toMillis(120)
        val isSignificantlyOlder = timeDelta < -TimeUnit.SECONDS.toMillis(120)
        val isNewer = timeDelta > 0

        when {
            isSignificantlyNewer -> return true
            isSignificantlyOlder -> return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = location.provider == currentBestLocation.provider

        // Determine location quality using a combination of timeliness and accuracy
        return when {
            isMoreAccurate -> true
            isNewer && !isLessAccurate -> true
            isNewer && !isSignificantlyLessAccurate && isFromSameProvider -> true
            else -> false
        }
    }

    /**
     * Get location accuracy description
     */
    fun getAccuracyDescription(accuracy: Float): String {
        return when {
            accuracy <= 5 -> "Excellent"
            accuracy <= 10 -> "Good"
            accuracy <= 20 -> "Fair"
            accuracy <= 50 -> "Poor"
            else -> "Very Poor"
        }
    }
}