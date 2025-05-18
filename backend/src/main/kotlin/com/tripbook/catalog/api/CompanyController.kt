package com.tripbook.catalog.api

import com.tripbook.catalog.model.Company
import com.tripbook.catalog.model.ServiceCategory
import com.tripbook.catalog.service.CompanyService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

/**
 * Controller class for handling Company-related HTTP endpoints.
 */
class CompanyController(
    private val companyService: CompanyService
) {
    /**
     * Configures all routes related to company operations.
     */
    fun configureRoutes(routing: Routing) {
        routing.route("/api/companies") {
            // Get all companies
            get {
                val companies = companyService.getAllCompanies()
                call.respond(companies)
            }
            
            // Create a new company
            post {
                val company = call.receive<Company>()
                val createdCompany = companyService.createCompany(company)
                call.respond(HttpStatusCode.Created, createdCompany)
            }
            
            // Get company by ID
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val company = companyService.getCompanyById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Company not found")
                
                call.respond(company)
            }
            
            // Update company
            put("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val company = call.receive<Company>()
                
                val updatedCompany = companyService.updateCompany(id, company)
                    ?: return@put call.respond(HttpStatusCode.NotFound, "Company not found")
                
                call.respond(updatedCompany)
            }
            
            // Delete company
            delete("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val deleted = companyService.deleteCompany(id)
                
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Company not found")
                }
            }
            
            // Search companies by name
            get("/search") {
                val query = call.request.queryParameters["q"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Query parameter 'q' is required")
                
                val companies = companyService.searchCompanies(query)
                call.respond(companies)
            }
            
            // Get companies by location
            get("/location") {
                val country = call.request.queryParameters["country"]
                val city = call.request.queryParameters["city"]
                
                if (country == null && city == null) {
                    return@get call.respond(HttpStatusCode.BadRequest, "At least one of 'country' or 'city' parameters is required")
                }
                
                val companies = companyService.getCompaniesByLocation(country, city)
                call.respond(companies)
            }
            
            // Get companies by service category
            get("/category/{category}") {
                val categoryStr = call.parameters["category"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Category parameter is required")
                
                val category = try {
                    ServiceCategory.valueOf(categoryStr.uppercase())
                } catch (e: IllegalArgumentException) {
                    return@get call.respond(HttpStatusCode.BadRequest, "Invalid category")
                }
                
                val companies = companyService.getCompaniesByServiceCategory(category)
                call.respond(companies)
            }
            
            // Get featured companies
            get("/featured") {
                val limitStr = call.request.queryParameters["limit"]
                val limit = limitStr?.toIntOrNull() ?: 10
                
                val companies = companyService.getFeaturedCompanies(limit)
                call.respond(companies)
            }
            
            // Get top-rated companies
            get("/top-rated") {
                val limitStr = call.request.queryParameters["limit"]
                val limit = limitStr?.toIntOrNull() ?: 10
                
                val minReviewsStr = call.request.queryParameters["minReviews"]
                val minReviews = minReviewsStr?.toIntOrNull() ?: 5
                
                val companies = companyService.getTopRatedCompanies(minReviews, limit)
                call.respond(companies)
            }
            
            // Verify a company
            post("/{id}/verify") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val company = companyService.verifyCompany(id)
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Company not found")
                
                call.respond(company)
            }
            
            // Set company featured status
            post("/{id}/featured") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val featured = call.receive<Map<String, Boolean>>()["featured"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Featured status is required")
                
                val company = companyService.setCompanyFeaturedStatus(id, featured)
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Company not found")
                
                call.respond(company)
            }
        }
    }
}