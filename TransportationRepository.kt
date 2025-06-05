package com.tripbook.api.repository

import com.tripbook.api.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransportationRepository : JpaRepository<Transportation, Long> {
    fun findByStatus(status: TransportationStatus): List<Transportation>
}

@Repository
interface PlaneRepository : JpaRepository<Plane, Long> {
    fun findByStatus(status: TransportationStatus): List<Plane>
    fun findByFlightRangeGreaterThanEqual(range: Int): List<Plane>
}

@Repository
interface CarRepository : JpaRepository<Car, Long> {
    fun findByStatus(status: TransportationStatus): List<Car>
    fun findByFuelType(fuelType: FuelType): List<Car>
    fun findByTransmissionType(transmissionType: TransmissionType): List<Car>
}

@Repository
interface ShipRepository : JpaRepository<Ship, Long> {
    fun findByStatus(status: TransportationStatus): List<Ship>
    fun findByMaxSpeedGreaterThanEqual(speed: Int): List<Ship>
} 