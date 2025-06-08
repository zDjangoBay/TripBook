// UserProfileRoutes.kt
package com.android.tripbook.userprofile.routes

import com.android.tripbook.userprofile.model.UserProfile
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

fun Route.userProfileRoutes(service: UserProfileService) {

    route("/userprofiles") {

        post {
            val request = call.receive<CreateUserProfileRequest>()
            val result = service.createUserProfile(request)
            call.respond(result ?: HttpStatusCode.BadRequest)
        }

        get("/{user_id}") {
            val userId = call.parameters["user_id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val result = service.getUserProfileById(userId)
            call.respond(result ?: HttpStatusCode.NotFound)
        }

        put("/{user_id}") {
            val userId = call.parameters["user_id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<UpdateUserProfileRequest>()
            val result = service.updateUserProfile(userId, request)
            call.respond(result ?: HttpStatusCode.NotFound)
        }

        delete("/{user_id}") {
            val userId = call.parameters["user_id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val success = service.deleteUserProfile(userId)
            if (success) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
        }

        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            val results = service.getAllUserProfiles(page, size)
            call.respond(results)
        }
    }
}
