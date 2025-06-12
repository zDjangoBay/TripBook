package com.android.Tripbook.Datamining.modules.data.reservations.routes

import com.android.Tripbook.Datamining.modules.data.reservations.model.* // Import models, DTOs, Service
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.time.format.DateTimeParseException


fun ApplicationCall.getAuthenticatedUserId(): String? {

    return request.headers["X-User-ID"]
}


fun Route.ReservationRoutes(reservationService: ReservationsService) {

    route("/reservations") {


        post {
            val userId = call.getAuthenticatedUserId() ?: return@post call.respond(HttpStatusCode.Unauthorized, "User ID missing or not authenticated")
            try {
                val request = call.receive<CreateReservationRequest>()
                val reservation = reservationService.createReservation(userId, request)
                if (reservation != null) {
                    call.respond(HttpStatusCode.Created, reservation)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Failed to create reservation. Check input data (e.g., date format, end date before start date).")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            }
        }


        get {
            val userId = call.getAuthenticatedUserId() ?: return@get call.respond(HttpStatusCode.Unauthorized, "User ID missing or not authenticated")

            try {
                val queryParams = call.request.queryParameters
                val statusStrings = queryParams.getAll("status")
                val statuses = statusStrings?.mapNotNull {
                    try { ReservationStatus.valueOf(it.uppercase()) } catch (e: IllegalArgumentException) { null }
                }?.toSet()

                val filter = ReservationFilter(
                    status = statuses,
                    destination = queryParams["destination"],
                    startDateFrom = queryParams["startDateFrom"]?.let { try { LocalDateTime.parse(it) } catch (e: DateTimeParseException) { null } },
                    startDateTo = queryParams["startDateTo"]?.let { try { LocalDateTime.parse(it) } catch (e: DateTimeParseException) { null } },
                    priceMin = queryParams["priceMin"]?.toDoubleOrNull(),
                    priceMax = queryParams["priceMax"]?.toDoubleOrNull(),
                    searchQuery = queryParams["searchQuery"]
                )
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["size"]?.toIntOrNull() ?: 20

                val reservations = reservationService.getUserReservations(userId, filter, page, pageSize)
                call.respond(HttpStatusCode.OK, reservations)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving reservations: ${e.message}")
            }
        }


        get("/{id}") {
            val userId = call.getAuthenticatedUserId() ?: return@get call.respond(HttpStatusCode.Unauthorized, "User ID missing or not authenticated")
            val reservationId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing reservation id parameter")

            val reservation = reservationService.getReservationById(reservationId, userId)
            if (reservation != null) {
                call.respond(HttpStatusCode.OK, reservation)
            } else {
                call.respond(HttpStatusCode.NotFound, "Reservation not found or access denied")
            }
        }


        put("/{id}") {
            val userId = call.getAuthenticatedUserId() ?: return@put call.respond(HttpStatusCode.Unauthorized, "User ID missing or not authenticated")
            val reservationId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing reservation id parameter")

            try {
                val request = call.receive<UpdateReservationRequest>()
                val updatedReservation = reservationService.updateReservation(reservationId, userId, request)
                if (updatedReservation != null) {
                    call.respond(HttpStatusCode.OK, updatedReservation)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Reservation not found, update failed, or access denied.")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            }
        }


        patch("/{id}/status") {
            val userId = call.getAuthenticatedUserId() ?: return@patch call.respond(HttpStatusCode.Unauthorized, "User ID missing or not authenticated")
            val reservationId = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing reservation id parameter")

            try {
                val request = call.receive<UpdateReservationStatusRequest>()
                val updatedReservation = reservationService.updateReservationStatus(reservationId, userId, request.status)
                if (updatedReservation != null) {
                    call.respond(HttpStatusCode.OK, updatedReservation)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Reservation not found, status update failed, or access denied.")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid status value in request body. Valid values are: ${ReservationStatus.values().joinToString()}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            }
        }


        delete("/{id}") {
            val userId = call.getAuthenticatedUserId() ?: return@delete call.respond(HttpStatusCode.Unauthorized, "User ID missing or not authenticated")
            val reservationId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing reservation id parameter")

            val success = reservationService.deleteReservation(reservationId, userId)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Reservation not found or delete failed")
            }
        }
    }
}