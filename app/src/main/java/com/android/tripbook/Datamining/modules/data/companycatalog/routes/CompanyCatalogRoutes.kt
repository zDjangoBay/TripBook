package com.android.companycatalog.routes

import com.android.companycatalog.model.* // Importer Company, DTOs, Service
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.CompanyCatalogRoutes(companyCatalogService: CompanyCatalogService) { // Renommé pour la cohérence

    route("/companies") {

        /*
        *   The route to create or insert a new company into the database using the CreateCompanyRequest DTO
        * */
        post {
            try {

                val request = call.receive<CreateCompanyRequest>()

                  val company = companyCatalogService.createCompany(request)

                if (company != null) {
                    call.respond(HttpStatusCode.Created, company)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create company")
                }
            } catch (e: ContentTransformationException) { // Erreur de désérialisation
                call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            }
        }

        /*
        *   This route allows to find a company using the company_id
        * */
        get("/{company_id}") {
            val companyId = call.parameters["company_id"]
            if (companyId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing company_id parameter")
                return@get
            }
            val company = companyCatalogService.getCompanyById(companyId)
            if (company != null) {
                call.respond(HttpStatusCode.OK, company)
            } else {
                call.respond(HttpStatusCode.NotFound, "Company not found with ID: $companyId")
            }
        }

        /*
        *   This route allows to find Main company using the Main company id
        * */

        get("/main/{main_id}") {
            val mainId = call.parameters["main_id"]
            if (mainId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing main_id parameter")
                return@get
            }
            val company = companyCatalogService.getCompanyByMainId(mainId)
            if (company != null) {
                call.respond(HttpStatusCode.OK, company)
            } else {
                call.respond(HttpStatusCode.NotFound, "Company not found with Main ID: $mainId")
            }
        }

        /*
        *   Allow to list companies using filter such as the status  and agency
        * */

        get {
            val statusParam = call.request.queryParameters["status"]
            val agencyQuery = call.request.queryParameters["agency"]
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

            val companies: List<Company> = when {
                statusParam != null -> {
                    try {
                        val status = CompanyStatus.valueOf(statusParam.uppercase())
                        companyCatalogService.getCompaniesByStatus(status, page, pageSize)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid status value: $statusParam. Valid values are: ${CompanyStatus.values().joinToString()}")
                        return@get
                    }
                }
                agencyQuery != null -> {
                    companyCatalogService.getCompaniesByAgency(agencyQuery, page, pageSize)
                }
                else -> {

                    companyCatalogService.getCompaniesByStatus(CompanyStatus.RUN, page, pageSize)
                }
            }
            call.respond(HttpStatusCode.OK, companies)
        }


        /*
        *   This route allow for the update of the informations of a company
        * */
        put("/{company_id}") {
            val companyId = call.parameters["company_id"]
            if (companyId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing company_id parameter")
                return@put
            }
            try {
                val request = call.receive<UpdateCompanyRequest>()
                val updatedCompany = companyCatalogService.updateCompany(companyId, request)
                if (updatedCompany != null) {
                    call.respond(HttpStatusCode.OK, updatedCompany)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Company not found or update failed for ID: $companyId")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            }
        }

        /*
        *   This route allow the update of the status information of a company
        * */

        patch("/{company_id}/status") {
            val companyId = call.parameters["company_id"]
            if (companyId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing company_id parameter")
                return@patch
            }
            try {
                val request = call.receive<UpdateCompanyStatusRequest>() // Un DTO spécifique pour le statut
                val updatedCompany = companyCatalogService.updateCompanyStatus(companyId, request.CompanyStatus)
                if (updatedCompany != null) {
                    call.respond(HttpStatusCode.OK, updatedCompany)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Company not found or status update failed for ID: $companyId")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
            } catch (e: IllegalArgumentException) { // Si CompanyStatus n'est pas valide dans le JSON
                call.respond(HttpStatusCode.BadRequest, "Invalid status value in request body. Valid values are: ${CompanyStatus.values().joinToString()}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            }
        }


        get("/agency/{agency_name_query}") {
            val agencyQuery = call.parameters["agency_name_query"]
            if (agencyQuery == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing agency_name_query parameter")
                return@get
            }
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

            val companies = companyCatalogService.getCompaniesByAgency(agencyQuery, page, pageSize)
            call.respond(HttpStatusCode.OK, companies)
        }

    }
}