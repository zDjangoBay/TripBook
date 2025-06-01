package com.tripbook.api.controller

import com.tripbook.api.model.*
import com.tripbook.api.service.TransportationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transportation")
class TransportationController(private val transportationService: TransportationService) {

    // Generic transportation endpoints
    @GetMapping
    fun getAllTransportation(): List<Transportation> = transportationService.getAllTransportation()

    @GetMapping("/{id}")
    fun getTransportationById(@PathVariable id: Long): ResponseEntity<Transportation> =
        transportationService.getTransportationById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @GetMapping("/status/{status}")
    fun getTransportationByStatus(@PathVariable status: TransportationStatus): List<Transportation> =
        transportationService.getTransportationByStatus(status)

    // Plane endpoints
    @GetMapping("/planes")
    fun getAllPlanes(): List<Plane> = transportationService.getAllPlanes()

    @GetMapping("/planes/{id}")
    fun getPlaneById(@PathVariable id: Long): ResponseEntity<Plane> =
        transportationService.getPlaneById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @GetMapping("/planes/status/{status}")
    fun getPlanesByStatus(@PathVariable status: TransportationStatus): List<Plane> =
        transportationService.getPlanesByStatus(status)

    @GetMapping("/planes/range/{minRange}")
    fun getPlanesByFlightRange(@PathVariable minRange: Int): List<Plane> =
        transportationService.getPlanesByFlightRange(minRange)

    @PostMapping("/planes")
    fun createPlane(@RequestBody plane: Plane): Plane = transportationService.createPlane(plane)

    @PutMapping("/planes/{id}")
    fun updatePlane(@PathVariable id: Long, @RequestBody plane: Plane): ResponseEntity<Plane> =
        transportationService.updatePlane(id, plane)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @DeleteMapping("/planes/{id}")
    fun deletePlane(@PathVariable id: Long): ResponseEntity<Unit> {
        transportationService.deletePlane(id)
        return ResponseEntity.ok().build()
    }

    // Car endpoints
    @GetMapping("/cars")
    fun getAllCars(): List<Car> = transportationService.getAllCars()

    @GetMapping("/cars/{id}")
    fun getCarById(@PathVariable id: Long): ResponseEntity<Car> =
        transportationService.getCarById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @GetMapping("/cars/status/{status}")
    fun getCarsByStatus(@PathVariable status: TransportationStatus): List<Car> =
        transportationService.getCarsByStatus(status)

    @GetMapping("/cars/fuel/{fuelType}")
    fun getCarsByFuelType(@PathVariable fuelType: FuelType): List<Car> =
        transportationService.getCarsByFuelType(fuelType)

    @GetMapping("/cars/transmission/{transmissionType}")
    fun getCarsByTransmissionType(@PathVariable transmissionType: TransmissionType): List<Car> =
        transportationService.getCarsByTransmissionType(transmissionType)

    @PostMapping("/cars")
    fun createCar(@RequestBody car: Car): Car = transportationService.createCar(car)

    @PutMapping("/cars/{id}")
    fun updateCar(@PathVariable id: Long, @RequestBody car: Car): ResponseEntity<Car> =
        transportationService.updateCar(id, car)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @DeleteMapping("/cars/{id}")
    fun deleteCar(@PathVariable id: Long): ResponseEntity<Unit> {
        transportationService.deleteCar(id)
        return ResponseEntity.ok().build()
    }

    // Ship endpoints
    @GetMapping("/ships")
    fun getAllShips(): List<Ship> = transportationService.getAllShips()

    @GetMapping("/ships/{id}")
    fun getShipById(@PathVariable id: Long): ResponseEntity<Ship> =
        transportationService.getShipById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @GetMapping("/ships/status/{status}")
    fun getShipsByStatus(@PathVariable status: TransportationStatus): List<Ship> =
        transportationService.getShipsByStatus(status)

    @GetMapping("/ships/speed/{minSpeed}")
    fun getShipsByMinSpeed(@PathVariable minSpeed: Int): List<Ship> =
        transportationService.getShipsByMinSpeed(minSpeed)

    @PostMapping("/ships")
    fun createShip(@RequestBody ship: Ship): Ship = transportationService.createShip(ship)

    @PutMapping("/ships/{id}")
    fun updateShip(@PathVariable id: Long, @RequestBody ship: Ship): ResponseEntity<Ship> =
        transportationService.updateShip(id, ship)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @DeleteMapping("/ships/{id}")
    fun deleteShip(@PathVariable id: Long): ResponseEntity<Unit> {
        transportationService.deleteShip(id)
        return ResponseEntity.ok().build()
    }

    // Maintenance endpoint
    @PutMapping("/{id}/maintenance")
    fun updateMaintenanceStatus(
        @PathVariable id: Long,
        @RequestParam status: TransportationStatus
    ): ResponseEntity<Unit> {
        transportationService.updateMaintenanceStatus(id, status)
        return ResponseEntity.ok().build()
    }
} 