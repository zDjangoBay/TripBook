package com.android.tripbook.model // Assuming this is your package


data class MapRegion(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val latitudeDelta: Double,  // Span of latitude
    val longitudeDelta: Double, // Span of longitude
    val zoomLevel: Float = 10f  // Default zoom, can be overridden
) {
    companion object {
        fun defaultRegion() = MapRegion(
            centerLatitude = 3.8480, // Yaoundé, Cameroon
            centerLongitude = 11.5021,
            latitudeDelta = 5.0,
            longitudeDelta = 5.0,
            zoomLevel = 6f // More zoomed out for a region
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

            // Ensure delta is not too small, add padding
            val latDelta = (if (maxLat == minLat) 0.5 else maxLat - minLat) * 1.2
            val lngDelta = (if (maxLng == minLng) 0.5 else maxLng - minLng) * 1.2


            return MapRegion(
                centerLatitude = centerLat,
                centerLongitude = centerLng,
                latitudeDelta = latDelta,
                longitudeDelta = lngDelta,
                // Use the calculated zoom or a default if calculation is complex
                zoomLevel = calculateZoomLevel(latDelta, lngDelta)
            )
        }

        // This function calculates a zoom level based on the largest delta (latitude or longitude span).
        // The values are heuristics and may need adjustment for your specific map provider/desired appearance.
        // Google Maps zoom levels typically range from ~1 (world) to ~21 (individual buildings).
        private fun calculateZoomLevel(latDelta: Double, lngDelta: Double): Float {
            val maxDelta = maxOf(latDelta, lngDelta)
            // This is a simplified heuristic. Real zoom calculation from bounds is more complex.
            // The GoogleMap composable often handles this better with newLatLngBounds(bounds, padding).
            // However, since your MapRegion includes zoomLevel, we'll use this.
            return when {
                maxDelta > 180 -> 1f   // World
                maxDelta > 90 -> 2f    // Continent
                maxDelta > 45 -> 3f
                maxDelta > 22 -> 4f    // Large country
                maxDelta > 10 -> 5f    // Country / Large Region
                maxDelta > 5  -> 6f    // Region
                maxDelta > 2  -> 7f
                maxDelta > 1  -> 8f    // Large City / Metro Area
                maxDelta > 0.5 -> 9f   // City
                maxDelta > 0.25 -> 10f
                maxDelta > 0.1 -> 11f  // Town / District
                maxDelta > 0.05 -> 12f
                maxDelta > 0.02 -> 13f // Neighborhood
                maxDelta > 0.01 -> 14f
                else -> 15f           // Streets
            }
        }
    }
}