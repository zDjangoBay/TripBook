package com.android.tripbook.tripschedulling.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Trip

val tripStorage = mutableListOf<Trip>()
fun Route.tripRoutes() {
    route("/trips") {
        post {
            val trip = call.receive<Trip>()
            tripStorage.add(trip)
            call.respond(trip)
        }
        get {
                call.respond(tripStorage)
            }
            get("{id}") {
                val id + call.parameters["id"]
                ?: return@get call.respondText("Missing ID", status = HttpStatusCode.BadRequest)
                val trip = tripStorage.find { it.id == id}
                ?: return@get call.respondText("Not found", status = HttpStatusCode.NotFound)
                call.respond(Trip)
            }
    }
}