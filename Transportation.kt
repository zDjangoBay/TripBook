package com.tripbook.api.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transport_type")
abstract class Transportation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val name: String,
    val capacity: Int,
    val status: TransportationStatus,
    val manufacturer: String,
    val model: String,
    val yearOfManufacture: Int,
    val lastMaintenanceDate: LocalDateTime,
    val nextMaintenanceDate: LocalDateTime
)

@Entity
@DiscriminatorValue("PLANE")
class Plane(
    id: Long = 0,
    name: String,
    capacity: Int,
    status: TransportationStatus,
    manufacturer: String,
    model: String,
    yearOfManufacture: Int,
    lastMaintenanceDate: LocalDateTime,
    nextMaintenanceDate: LocalDateTime,
    val flightRange: Int, // in kilometers
    val maxAltitude: Int, // in meters
    val fuelCapacity: Int // in liters
) : Transportation(id, name, capacity, status, manufacturer, model, yearOfManufacture, lastMaintenanceDate, nextMaintenanceDate)

@Entity
@DiscriminatorValue("CAR")
class Car(
    id: Long = 0,
    name: String,
    capacity: Int,
    status: TransportationStatus,
    manufacturer: String,
    model: String,
    yearOfManufacture: Int,
    lastMaintenanceDate: LocalDateTime,
    nextMaintenanceDate: LocalDateTime,
    val fuelType: FuelType,
    val transmissionType: TransmissionType,
    val mileage: Int // in kilometers
) : Transportation(id, name, capacity, status, manufacturer, model, yearOfManufacture, lastMaintenanceDate, nextMaintenanceDate)

@Entity
@DiscriminatorValue("SHIP")
class Ship(
    id: Long = 0,
    name: String,
    capacity: Int,
    status: TransportationStatus,
    manufacturer: String,
    model: String,
    yearOfManufacture: Int,
    lastMaintenanceDate: LocalDateTime,
    nextMaintenanceDate: LocalDateTime,
    val length: Double, // in meters
    val beam: Double, // in meters
    val draft: Double, // in meters
    val maxSpeed: Int // in knots
) : Transportation(id, name, capacity, status, manufacturer, model, yearOfManufacture, lastMaintenanceDate, nextMaintenanceDate)

enum class TransportationStatus {
    AVAILABLE,
    IN_USE,
    MAINTENANCE,
    OUT_OF_SERVICE
}

enum class FuelType {
    PETROL,
    DIESEL,
    ELECTRIC,
    HYBRID
}

enum class TransmissionType {
    MANUAL,
    AUTOMATIC
} 