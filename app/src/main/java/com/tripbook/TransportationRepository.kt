
package com.tripbook

class TransportationRepository(private val service: TransportationService) {
    fun fetchBusSchedules(): List<BusSchedule> {
        return service.getBusSchedules()
    }

    fun bookBus(busId: String): Boolean {
        return service.bookTicket(busId)
    }
}