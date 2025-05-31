package com.tripbook.api.service

import com.tripbook.api.model.*
import com.tripbook.api.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TransportationService(
    private val transportationRepository: TransportationRepository,
    private val planeRepository: PlaneRepository,
    private val carRepository: CarRepository,
    private val shipRepository: ShipRepository
) {
    // Generic transportation methods
    fun getAllTransportation(): List<Transportation> = transportationRepository.findAll()
    
    fun getTransportationById(id: Long): Transportation? = transportationRepository.findById(id).orElse(null)
    
    fun getTransportationByStatus(status: TransportationStatus): List<Transportation> =
        transportationRepository.findByStatus(status)

    // Plane specific methods
    fun getAllPlanes(): List<Plane> = planeRepository.findAll()
    
    fun getPlaneById(id: Long): Plane? = planeRepository.findById(id).orElse(null)
    
    fun getPlanesByStatus(status: TransportationStatus): List<Plane> =
        planeRepository.findByStatus(status)
    
    fun getPlanesByFlightRange(minRange: Int): List<Plane> =
        planeRepository.findByFlightRangeGreaterThanEqual(minRange)

    // Car specific methods
    fun getAllCars(): List<Car> = carRepository.findAll()
    
    fun getCarById(id: Long): Car? = carRepository.findById(id).orElse(null)
    
    fun getCarsByStatus(status: TransportationStatus): List<Car> =
        carRepository.findByStatus(status)
    
    fun getCarsByFuelType(fuelType: FuelType): List<Car> =
        carRepository.findByFuelType(fuelType)
    
    fun getCarsByTransmissionType(transmissionType: TransmissionType): List<Car> =
        carRepository.findByTransmissionType(transmissionType)

    // Ship specific methods
    fun getAllShips(): List<Ship> = shipRepository.findAll()
    
    fun getShipById(id: Long): Ship? = shipRepository.findById(id).orElse(null)
    
    fun getShipsByStatus(status: TransportationStatus): List<Ship> =
        shipRepository.findByStatus(status)
    
    fun getShipsByMinSpeed(minSpeed: Int): List<Ship> =
        shipRepository.findByMaxSpeedGreaterThanEqual(minSpeed)

    // Create methods
    @Transactional
    fun createPlane(plane: Plane): Plane = planeRepository.save(plane)
    
    @Transactional
    fun createCar(car: Car): Car = carRepository.save(car)
    
    @Transactional
    fun createShip(ship: Ship): Ship = shipRepository.save(ship)

    // Update methods
    @Transactional
    fun updatePlane(id: Long, plane: Plane): Plane? {
        return if (planeRepository.existsById(id)) {
            planeRepository.save(plane.copy(id = id))
        } else null
    }
    
    @Transactional
    fun updateCar(id: Long, car: Car): Car? {
        return if (carRepository.existsById(id)) {
            carRepository.save(car.copy(id = id))
        } else null
    }
    
    @Transactional
    fun updateShip(id: Long, ship: Ship): Ship? {
        return if (shipRepository.existsById(id)) {
            shipRepository.save(ship.copy(id = id))
        } else null
    }

    // Delete methods
    @Transactional
    fun deletePlane(id: Long) = planeRepository.deleteById(id)
    
    @Transactional
    fun deleteCar(id: Long) = carRepository.deleteById(id)
    
    @Transactional
    fun deleteShip(id: Long) = shipRepository.deleteById(id)

    // Maintenance methods
    @Transactional
    fun updateMaintenanceStatus(id: Long, status: TransportationStatus) {
        getTransportationById(id)?.let { transportation ->
            when (transportation) {
                is Plane -> planeRepository.save(transportation.copy(
                    status = status,
                    lastMaintenanceDate = LocalDateTime.now(),
                    nextMaintenanceDate = LocalDateTime.now().plusMonths(6)
                ))
                is Car -> carRepository.save(transportation.copy(
                    status = status,
                    lastMaintenanceDate = LocalDateTime.now(),
                    nextMaintenanceDate = LocalDateTime.now().plusMonths(3)
                ))
                is Ship -> shipRepository.save(transportation.copy(
                    status = status,
                    lastMaintenanceDate = LocalDateTime.now(),
                    nextMaintenanceDate = LocalDateTime.now().plusMonths(12)
                ))
            }
        }
    }
} 