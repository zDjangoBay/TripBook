package com.tripbook.catalog.repository.impl

import com.tripbook.catalog.model.Company
import com.tripbook.catalog.model.ServiceCategory
import com.tripbook.catalog.repository.CompanyRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of the CompanyRepository interface.
 * Uses Exposed SQL framework for database operations.
 */
class CompanyRepositoryImpl : CompanyRepository {

    /**
     * In-memory data store for development and testing.
     * This would be replaced with actual database operations in production.
     */
    private val companies = mutableListOf<Company>()

    override suspend fun getAllCompanies(): List<Company> = withContext(Dispatchers.IO) {
        // In a real implementation, this would query the database
        return@withContext companies.toList()
    }

    override suspend fun getCompanyById(id: UUID): Company? = withContext(Dispatchers.IO) {
        // In a real implementation, this would query the database by ID
        return@withContext companies.find { it.id == id }
    }

    override suspend fun saveCompany(company: Company): Company = withContext(Dispatchers.IO) {
        // In a real implementation, this would insert or update in the database
        val index = companies.indexOfFirst { it.id == company.id }
        if (index >= 0) {
            companies[index] = company
        } else {
            companies.add(company)
        }
        return@withContext company
    }

    override suspend fun deleteCompany(id: UUID): Boolean = withContext(Dispatchers.IO) {
        // In a real implementation, this would delete from the database
        val size = companies.size
        companies.removeIf { it.id == id }
        return@withContext companies.size < size
    }

    override suspend fun searchCompaniesByName(query: String): List<Company> = withContext(Dispatchers.IO) {
        // In a real implementation, this would use SQL LIKE or a full-text search
        return@withContext companies.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
    }

    override suspend fun getCompaniesByLocation(country: String?, city: String?): List<Company> = withContext(Dispatchers.IO) {
        // In a real implementation, this would filter by country and/or city in SQL
        return@withContext companies.filter { company ->
            (country == null || company.location.country.equals(country, ignoreCase = true)) &&
            (city == null || company.location.city.equals(city, ignoreCase = true))
        }
    }

    override suspend fun getCompaniesByServiceCategory(category: ServiceCategory): List<Company> = withContext(Dispatchers.IO) {
        // In a real implementation, this would join with services and filter
        return@withContext companies.filter { company ->
            company.services.any { service -> service.category == category }
        }
    }

    override suspend fun getFeaturedCompanies(limit: Int): List<Company> = withContext(Dispatchers.IO) {
        // In a real implementation, this would filter by featured flag in SQL
        return@withContext companies
            .filter { it.featured }
            .take(limit)
    }

    override suspend fun getTopRatedCompanies(minReviews: Int, limit: Int): List<Company> = withContext(Dispatchers.IO) {
        // In a real implementation, this would order by rating and filter by review count
        return@withContext companies
            .filter { it.reviewCount >= minReviews }
            .sortedByDescending { it.rating }
            .take(limit)
    }
}