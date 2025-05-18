package com.tripbook.catalog

import com.tripbook.catalog.api.CompanyController
import com.tripbook.catalog.api.ReviewController
import com.tripbook.catalog.repository.CompanyRepository
import com.tripbook.catalog.repository.ReviewRepository
import com.tripbook.catalog.repository.impl.CompanyRepositoryImpl
import com.tripbook.catalog.repository.impl.ReviewRepositoryImpl
import com.tripbook.catalog.service.CompanyService
import com.tripbook.catalog.service.ReviewService
import com.tripbook.catalog.service.impl.CompanyServiceImpl
import com.tripbook.catalog.service.impl.ReviewServiceImpl
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

/**
 * Main application entry point for the TripBook Company Catalog backend.
 */
fun main() {
    // Start the Ktor server
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Install necessary Ktor features
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        
        // Configure CORS
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            // Allow requests from the frontend (Flutter app)
            anyHost()
        }
        
        // Set up dependency injection
        val companyRepository: CompanyRepository = CompanyRepositoryImpl()
        val reviewRepository: ReviewRepository = ReviewRepositoryImpl()
        
        val companyService: CompanyService = CompanyServiceImpl(companyRepository)
        val reviewService: ReviewService = ReviewServiceImpl(reviewRepository, companyRepository)
        
        val companyController = CompanyController(companyService)
        val reviewController = ReviewController(reviewService)
        
        // Configure routing
        routing {
            // Register controllers
            companyController.configureRoutes(this)
            reviewController.configureRoutes(this)
            
            // Simple route for health check
            get("/health") {
                call.respondText("TripBook Company Catalog Service is running!")
            }
        }
    }.start(wait = true)
}