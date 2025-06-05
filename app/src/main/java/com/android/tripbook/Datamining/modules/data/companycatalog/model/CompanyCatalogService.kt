package com.android.Tripbook.Datamining.modules.data.companycatalog.model



interface CompanyCatalogService {
    suspend fun createCompany(request: CreateCompanyRequest): Company?
    suspend fun getCompanyById(companyId: String): Company?
    suspend fun getCompanyByMainId(mainId: String): Company?
    suspend fun getCompaniesByStatus(status: CompanyStatus, page: Int = 1, pageSize: Int = 20): List<Company>
    suspend fun getCompaniesByAgency(agencyQuery: String, page: Int = 1, pageSize: Int = 20): List<Company>
    suspend fun updateCompany(companyId: String, request: UpdateCompanyRequest): Company?
    suspend fun updateCompanyStatus(companyId: String, newStatus: CompanyStatus): Company?
}