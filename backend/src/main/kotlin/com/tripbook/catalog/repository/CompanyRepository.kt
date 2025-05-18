package com.tripbook.catalog.repository

import com.tripbook.catalog.model.Company
import com.tripbook.catalog.model.ServiceCategory
import java.util.UUID

/**
 * Repository interface for Company data operations.
 * Defines methods for CRUD operations and various queries related to companies.
 */
interface CompanyRepository {
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
     * Saves a new company or updates an existing one.
     * 
     * @param company The company to save
     * @return The saved company with any auto-generated fields populated
     */
    suspend fun saveCompany(company: Company): Company
    
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
    suspend fun searchCompaniesByName(query: String): List<Company>
    
    /**
     * Filters companies by location (country and/or city).
     * 
     * @param country Optional country filter
     * @param city Optional city filter
     * @return List of companies matching the location criteria
     */
    suspend fun getCompaniesByLocation(country: String? = null, city: String? = null): List<Company>
    
    /**
     * Filters companies by service category.
     * 
     * @param category The service category to filter by
     * @return List of companies offering services in the specified category
     */
    suspend fun getCompaniesByServiceCategory(category: ServiceCategory): List<Company>
    
    /**
     * Gets featured companies for highlighting on the platform.
     * 
     * @param limit Maximum number of companies to return
     * @return List of featured companies
     */
    suspend fun getFeaturedCompanies(limit: Int = 10): List<Company>
    
    /**
     * Gets top-rated companies based on user reviews.
     * 
     * @param minReviews Minimum number of reviews required
     * @param limit Maximum number of companies to return
     * @return List of top-rated companies
     */
    suspend fun getTopRatedCompanies(minReviews: Int = 5, limit: Int = 10): List<Company>
}