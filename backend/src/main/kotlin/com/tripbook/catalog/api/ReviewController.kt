package com.tripbook.catalog.api

import com.tripbook.catalog.model.Review
import com.tripbook.catalog.service.ReviewService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

/**
 * Controller class for handling Review-related HTTP endpoints.
 */
class ReviewController(
    private val reviewService: ReviewService
) {
    /**
     * Configures all routes related to review operations.
     */
    fun configureRoutes(routing: Routing) {
        routing.route("/api/reviews") {
            // Get reviews by company
            get("/company/{companyId}") {
                val companyId = call.parameters["companyId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid company ID format")
                
                val reviews = reviewService.getReviewsByCompany(companyId)
                call.respond(reviews)
            }
            
            // Get review by ID
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val review = reviewService.getReviewById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Review not found")
                
                call.respond(review)
            }
            
            // Create a new review
            post {
                val review = call.receive<Review>()
                
                try {
                    val createdReview = reviewService.createReview(review)
                    call.respond(HttpStatusCode.Created, createdReview)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid review data")
                }
            }
            
            // Update review
            put("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val review = call.receive<Review>()
                
                try {
                    val updatedReview = reviewService.updateReview(id, review)
                        ?: return@put call.respond(HttpStatusCode.NotFound, "Review not found")
                    
                    call.respond(updatedReview)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid review data")
                }
            }
            
            // Delete review
            delete("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val deleted = reviewService.deleteReview(id)
                
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Review not found")
                }
            }
            
            // Mark review as helpful
            post("/{id}/helpful") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val userId = call.request.queryParameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "User ID is required")
                
                val marked = reviewService.markReviewAsHelpful(id, userId)
                
                if (marked) {
                    call.respond(HttpStatusCode.OK, mapOf("marked" to true))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Could not mark review as helpful")
                }
            }
            
            // Get review summary for company
            get("/summary/{companyId}") {
                val companyId = call.parameters["companyId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid company ID format")
                
                val summary = reviewService.getReviewSummaryForCompany(companyId)
                call.respond(summary)
            }
            
            // Get reviews by user
            get("/user/{userId}") {
                val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")
                
                val reviews = reviewService.getReviewsByUser(userId)
                call.respond(reviews)
            }
        }
    }
}