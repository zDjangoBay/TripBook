package com.tripbook.catalog.service

import com.tripbook.catalog.model.Company
import com.tripbook.catalog.model.ServiceCategory
import java.util.UUID

/**
 * Service interface for business logic related to company management.
 * Defines methods for CRUD operations and queries related to companies.
 */
interface CompanyService {
    /**
     * Retrieves all companies.
     * 
     * @return List of all companies
     */
    suspend fun getAllCompanies(): List<Company>
    
    /**
     * Retrieves a single company by ID.
     * 
     * @param id The company ID
     * @return The company if found, null otherwise
     */
    suspend fun getCompanyById(id: UUID): Company?
    
    /**
     * Creates a new company.
     * 
     * @param company The company to create
     * @return The created company
     */
    suspend fun createCompany(company: Company): Company
    
    /**
     * Updates an existing company.
     * 
     * @param id The ID of the company to update
     * @param company The updated company data
     * @return The updated company if found, null otherwise
     */
    suspend fun updateCompany(id: UUID, company: Company): Company?
    
    /**
     * Deletes a company by ID.
     * 
     * @param id The ID of the company to delete
     * @return true if deleted, false if not found
     */
    suspend fun deleteCompany(id: UUID): Boolean
    
    /**
     * Searches for companies by name.
     * 
     * @param query The search query string
     * @return List of matching companies
     */
    suspend fun searchCompanies(query: String): List<Company>
    
    /**
     * Gets companies by location.
     * 
     * @param country Optional country filter
     * @param city Optional city filter
     * @return List of companies matching the location criteria
     */
    suspend fun getCompaniesByLocation(country: String? = null, city: String? = null): List<Company>
    
    /**
     * Gets companies that offer a specific service category.
     * 
     * @param category The service category to filter by
     * @return List of matching companies
     */
    suspend fun getCompaniesByServiceCategory(category: ServiceCategory): List<Company>
    
    /**
     * Gets featured companies.
     * 
     * @param limit Maximum number of companies to return
     * @return List of featured companies
     */
    suspend fun getFeaturedCompanies(limit: Int = 10): List<Company>
    
    /**
     * Gets top-rated companies.
     * 
     * @param minReviews Minimum number of reviews required
     * @param limit Maximum number of companies to return
     * @return List of top-rated companies
     */
    suspend fun getTopRatedCompanies(minReviews: Int = 5, limit: Int = 10): List<Company>
    
    /**
     * Marks a company as verified.
     * 
     * @param id The ID of the company to verify
     * @return The updated company if found, null otherwise
     */
    suspend fun verifyCompany(id: UUID): Company?
    
    /**
     * Marks a company as featured or removes featured status.
     * 
     * @param id The ID of the company
     * @param featured Whether the company should be featured
     * @return The updated company if found, null otherwise
     */
    suspend fun setCompanyFeaturedStatus(id: UUID, featured: Boolean): Company?
}