package com.android.tripbook.model

data class MapRegion(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val latitudeDelta: Double,
    val longitudeDelta: Double,
    val zoomLevel: Float = 10f
) {
    companion object {
        // Default region (can be user's location or world view)
        fun defaultRegion() = MapRegion(
            centerLatitude = 3.8480, // Yaoundé, Cameroon
            centerLongitude = 11.5021, // Yaoundé, Cameroon
            latitudeDelta = 5.0, // A bit wider view
            longitudeDelta = 5.0,
            zoomLevel = 6f // A more zoomed-in view of the region
        )

        // Create region to fit multiple trips
        fun fromTrips(trips: List<Trip>): MapRegion {
            if (trips.isEmpty()) return defaultRegion()

            val latitudes = trips.map { it.latitude }
            val longitudes = trips.map { it.longitude }

            val minLat = latitudes.minOrNull() ?: 0.0
            val maxLat = latitudes.maxOrNull() ?: 0.0
            val minLng = longitudes.minOrNull() ?: 0.0
            val maxLng = longitudes.maxOrNull() ?: 0.0

            val centerLat = (minLat + maxLat) / 2
            val centerLng = (minLng + maxLng) / 2

            val latDelta = maxOf(maxLat - minLat, 0.1) * 1.2 // 20% padding
            val lngDelta = maxOf(maxLng - minLng, 0.1) * 1.2

            return MapRegion(
                centerLatitude = centerLat,
                centerLongitude = centerLng,
                latitudeDelta = latDelta,
                longitudeDelta = lngDelta,
                zoomLevel = calculateZoomLevel(latDelta, lngDelta)
            )
        }

        private fun calculateZoomLevel(latDelta: Double, lngDelta: Double): Float {
            val maxDelta = maxOf(latDelta, lngDelta)
            return when {
                maxDelta > 90 -> 2f
                maxDelta > 45 -> 4f
                maxDelta > 20 -> 6f
                maxDelta > 10 -> 8f
                maxDelta > 5 -> 10f
                maxDelta > 2 -> 12f
                else -> 14f
            }
        }
    }
}