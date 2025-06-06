package com.android.tripbook.model

data class MapRegion(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val latitudeDelta: Double,
    val longitudeDelta: Double,
    val zoomLevel: Float = 10f
) {
    companion object {
        fun defaultRegion() = MapRegion(
            centerLatitude = 3.8480,
            centerLongitude = 11.5021,
            latitudeDelta = 5.0,
            longitudeDelta = 5.0,
            zoomLevel = 6f
        )

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


            val latDelta = (if (maxLat == minLat) 0.5 else maxLat - minLat) * 1.2
            val lngDelta = (if (maxLng == minLng) 0.5 else maxLng - minLng) * 1.2


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
                maxDelta > 180 -> 1f
                maxDelta > 90 -> 2f
                maxDelta > 45 -> 3f
                maxDelta > 22 -> 4f
                maxDelta > 10 -> 5f
                maxDelta > 5  -> 6f
                maxDelta > 2  -> 7f
                maxDelta > 1  -> 8f
                maxDelta > 0.5 -> 9f
                maxDelta > 0.25 -> 10f
                maxDelta > 0.1 -> 11f
                maxDelta > 0.05 -> 12f
                maxDelta > 0.02 -> 13f
                maxDelta > 0.01 -> 14f
                else -> 15f
            }
        }
    }
}