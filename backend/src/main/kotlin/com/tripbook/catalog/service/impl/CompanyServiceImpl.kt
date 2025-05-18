package com.tripbook.catalog.service.impl

import com.tripbook.catalog.model.Company
import com.tripbook.catalog.model.ServiceCategory
import com.tripbook.catalog.repository.CompanyRepository
import com.tripbook.catalog.service.CompanyService
import java.time.LocalDateTime
import java.util.UUID

/**
 * Implementation of the CompanyService interface.
 * Contains business logic for company-related operations.
 */
class CompanyServiceImpl(
    private val companyRepository: CompanyRepository
) : CompanyService {

    override suspend fun getAllCompanies(): List<Company> {
        return companyRepository.getAllCompanies()
    }

    override suspend fun getCompanyById(id: UUID): Company? {
        return companyRepository.getCompanyById(id)
    }

    override suspend fun createCompany(company: Company): Company {
        // Ensure a new ID is generated
        val newCompany = company.copy(
            id = UUID.randomUUID(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return companyRepository.saveCompany(newCompany)
    }

    override suspend fun updateCompany(id: UUID, company: Company): Company? {
        // Check if company exists
        val existingCompany = companyRepository.getCompanyById(id) ?: return null
        
        // Update with new data while preserving creation date and ID
        val updatedCompany = company.copy(
            id = existingCompany.id,
            createdAt = existingCompany.createdAt,
            updatedAt = LocalDateTime.now()
        )
        
        return companyRepository.saveCompany(updatedCompany)
    }

    override suspend fun deleteCompany(id: UUID): Boolean {
        return companyRepository.deleteCompany(id)
    }

    override suspend fun searchCompanies(query: String): List<Company> {
        return companyRepository.searchCompaniesByName(query)
    }

    override suspend fun getCompaniesByLocation(country: String?, city: String?): List<Company> {
        return companyRepository.getCompaniesByLocation(country, city)
    }

    override suspend fun getCompaniesByServiceCategory(category: ServiceCategory): List<Company> {
        return companyRepository.getCompaniesByServiceCategory(category)
    }

    override suspend fun getFeaturedCompanies(limit: Int): List<Company> {
        return companyRepository.getFeaturedCompanies(limit)
    }

    override suspend fun getTopRatedCompanies(minReviews: Int, limit: Int): List<Company> {
        return companyRepository.getTopRatedCompanies(minReviews, limit)
    }

    override suspend fun verifyCompany(id: UUID): Company? {
        val company = companyRepository.getCompanyById(id) ?: return null
        val updatedCompany = company.copy(
            verified = true,
            updatedAt = LocalDateTime.now()
        )
        return companyRepository.saveCompany(updatedCompany)
    }

    override suspend fun setCompanyFeaturedStatus(id: UUID, featured: Boolean): Company? {
        val company = companyRepository.getCompanyById(id) ?: return null
        val updatedCompany = company.copy(
            featured = featured,
            updatedAt = LocalDateTime.now()
        )
        return companyRepository.saveCompany(updatedCompany)
    }
}