// UserProfileRoutes.kt
package com.tripbook.routes

import com.tripbook.models.UserProfile
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.serialization.json.Json

val dummyUsers = listOf(
    UserProfile("1", "rawlings", "raw@trip.com", 25, "M", "Cameroon", 12, 4.5, listOf("Paris", "Yaoundé", "Dubai")),
    UserProfile("2", "ailee", "ailee@trip.com", 21, "F", "Cameroon", 3, 4.0, listOf("Yaoundé", "Douala")),
    UserProfile("3", "kofi", "kofi@trip.com", 30, "M", "Ghana", 25, 4.9, listOf("Cape Town", "Nairobi", "Accra"))
)

fun Route.userProfileRoutes() {
    route("/userprofiles") {

        get("/") {
            call.respond(dummyUsers)
        }

        get("/{id}") {
            val id = call.parameters["id"]
            val user = dummyUsers.find { it.id == id }
            if (user != null) {
                call.respond(user)
            } else {
                call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }

        get("/{id}/category") {
            val id = call.parameters["id"]
            val user = dummyUsers.find { it.id == id }
            if (user != null) {
                call.respond(mapOf("category" to user.travelCategory()))
            } else {
                call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }

        get("/{id}/topregion") {
            val id = call.parameters["id"]
            val user = dummyUsers.find { it.id == id }
            if (user != null) {
                call.respond(mapOf("topRegion" to user.topRegion()))
            } else {
                call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
