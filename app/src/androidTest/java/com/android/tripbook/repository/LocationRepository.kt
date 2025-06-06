package com.tripbook.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tripbook.data.model.Traveler
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class LocationRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun updateUserLocation(lat: Double, lon: Double) {
        val uid = auth.currentUser?.uid ?: return
        val userData = mapOf(
            "latitude" to lat,
            "longitude" to lon,
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        db.collection("travelers").document(uid).set(userData, SetOptions.merge())
    }

    suspend fun fetchNearbyTravelers(currentLat: Double, currentLon: Double): List<Traveler> {
        val radiusInKm = 10.0
        val snapshot = db.collection("travelers").get().await()

        return snapshot.documents.mapNotNull { doc ->
            val lat = doc.getDouble("latitude")
            val lon = doc.getDouble("longitude")
            val id = doc.id

            if (lat != null && lon != null) {
                val distance = calculateDistance(currentLat, currentLon, lat, lon)
                if (distance <= radiusInKm) {
                    Traveler(id, lat, lon)
                } else null
            } else null
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
