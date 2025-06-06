package com.tripbook.api.controller

import com.tripbook.api.model.Transportation
import com.tripbook.api.model.TransportationStatus
import com.tripbook.api.model.TransportationType
import com.tripbook.api.service.TransportationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transportation")
class TransportationController(private val service: TransportationService) {

    @GetMapping
    fun getAllTransportation(): List<Transportation> = service.getAllTransportation()

    @GetMapping("/type/{type}")
    fun getTransportationByType(@PathVariable type: TransportationType): List<Transportation> =
        service.getTransportationByType(type)

    @GetMapping("/status/{status}")
    fun getTransportationByStatus(@PathVariable status: TransportationStatus): List<Transportation> =
        service.getTransportationByStatus(status)

    @GetMapping("/dummy/{type}")
    fun getDummyData(@PathVariable type: TransportationType): List<Transportation> =
        service.getDummyData(type)
} 