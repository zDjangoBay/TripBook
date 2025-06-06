package com.tripbook.api.service

import com.tripbook.api.model.*
import com.tripbook.api.repository.TransportationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransportationService(private val repository: TransportationRepository) {

    fun getAllTransportation(): List<Transportation> = repository.findAll()

    fun getTransportationByType(type: TransportationType): List<Transportation> =
        repository.findByType(type)

    fun getTransportationByStatus(status: TransportationStatus): List<Transportation> =
        repository.findByStatus(status)

    fun getDummyData(type: TransportationType): List<Transportation> {
        return when (type) {
            TransportationType.PLANE -> listOf(
                Plane(
                    id = "1",
                    name = "Boeing 737",
                    capacity = 150,
                    status = TransportationStatus.AVAILABLE,
                    manufacturer = "Boeing",
                    model = "737-800",
                    yearOfManufacture = 2020,
                    lastMaintenanceDate = LocalDateTime.now().minusDays(30),
                    nextMaintenanceDate = LocalDateTime.now().plusDays(150),
                    flightRange = 5000,
                    maxAltitude = 12000,
                    fuelCapacity = 25000
                ),
                Plane(
                    id = "2",
                    name = "Airbus A320",
                    capacity = 180,
                    status = TransportationStatus.IN_USE,
                    manufacturer = "Airbus",
                    model = "A320",
                    yearOfManufacture = 2021,
                    lastMaintenanceDate = LocalDateTime.now().minusDays(60),
                    nextMaintenanceDate = LocalDateTime.now().plusDays(120),
                    flightRange = 6000,
                    maxAltitude = 13000,
                    fuelCapacity = 28000
                )
            )
            TransportationType.CAR -> listOf(
                Car(
                    id = "1",
                    name = "Toyota Camry",
                    capacity = 5,
                    status = TransportationStatus.AVAILABLE,
                    manufacturer = "Toyota",
                    model = "Camry",
                    yearOfManufacture = 2022,
                    lastMaintenanceDate = LocalDateTime.now().minusDays(15),
                    nextMaintenanceDate = LocalDateTime.now().plusDays(75),
                    fuelType = FuelType.PETROL,
                    transmissionType = TransmissionType.AUTOMATIC,
                    mileage = 15000
                ),
                Car(
                    id = "2",
                    name = "Tesla Model 3",
                    capacity = 5,
                    status = TransportationStatus.IN_USE,
                    manufacturer = "Tesla",
                    model = "Model 3",
                    yearOfManufacture = 2023,
                    lastMaintenanceDate = LocalDateTime.now().minusDays(30),
                    nextMaintenanceDate = LocalDateTime.now().plusDays(60),
                    fuelType = FuelType.ELECTRIC,
                    transmissionType = TransmissionType.AUTOMATIC,
                    mileage = 8000
                )
            )
            TransportationType.SHIP -> listOf(
                Ship(
                    id = "1",
                    name = "Cargo Ship Alpha",
                    capacity = 1000,
                    status = TransportationStatus.AVAILABLE,
                    manufacturer = "Maritime Co",
                    model = "Cargo-1000",
                    yearOfManufacture = 2020,
                    lastMaintenanceDate = LocalDateTime.now().minusDays(90),
                    nextMaintenanceDate = LocalDateTime.now().plusDays(270),
                    length = 200.0,
                    beam = 32.0,
                    draft = 12.0,
                    maxSpeed = 25
                ),
                Ship(
                    id = "2",
                    name = "Cruise Ship Beta",
                    capacity = 2000,
                    status = TransportationStatus.IN_USE,
                    manufacturer = "Ocean Cruises",
                    model = "Luxury-2000",
                    yearOfManufacture = 2021,
                    lastMaintenanceDate = LocalDateTime.now().minusDays(60),
                    nextMaintenanceDate = LocalDateTime.now().plusDays(300),
                    length = 300.0,
                    beam = 40.0,
                    draft = 15.0,
                    maxSpeed = 30
                )
            )
        }
    }
} 