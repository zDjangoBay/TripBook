package com.tripbook

import android.app.Application
import com.google.firebase.FirebaseApp

class MainActivity : Application() {
    fun checkNearby(currentLat: Double, currentLon: Double, context: Context) {
        val dbRef = FirebaseDatabase.getInstance().getReference("locations")
        dbRef.get().addOnSuccessListener {
            it.children.forEach { snap ->
                val lat = snap.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = snap.child("longitude").getValue(Double::class.java) ?: return@forEach
                val results = FloatArray(1)
                Location.distanceBetween(currentLat, currentLon, lat, lon, results)
                if (results[0] <= 500) {
                    Toast.makeText(context, "Traveler nearby: ${snap.key}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
