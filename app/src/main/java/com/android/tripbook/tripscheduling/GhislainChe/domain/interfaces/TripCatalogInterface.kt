package com.android.tripbook.tripscheduling.GhislainChe.domain.interfaces

interface TripCatalogInterface {
    suspend fun getRouteInfo(routeId: String): RouteInfo
    suspend fun getVehicleInfo(vehicleId: String): VehicleInfo
}

// Placeholder data classes
data class RouteInfo(
    val routeId: String,
    val origin: String,
    val destination: String,
    val distanceKm: Double
)

data class VehicleInfo(
    val vehicleId: String,
    val model: String,
    val seatCount: Int
)