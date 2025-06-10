package com.android.companycatalog.model


import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import org.litote.kmongo.regex
import org.litote.kmongo.replaceOne
import org.litote.kmongo.updateOne
import redis.clients.jedis.Jedis
import java.util.UUID

// Keys and Cache TTL , no need to change anything here when testing it 
object CompanyCacheKeys {
    fun companyById(companyId: String) = "company:$companyId"
    fun companyByMainId(mainId: String) = "company_main:$mainId"
    fun companiesByStatus(status: CompanyStatus, page: Int, pageSize: Int) = "companies_status:${status.name}:page:$page:size:$pageSize"
    fun companiesByAgency(agencyQuery: String, page: Int, pageSize: Int) = "companies_agency:${agencyQuery.replace("\\s+".toRegex(), "_")}:page:$page:size:$pageSize"
}

const val COMPANY_CACHE_TTL_SECONDS: Long = 3600

class CompanyCatalogServiceImpl(
    private val mongoDb: CoroutineDatabase,
    private val redis: Jedis,
    private val jsonMapper: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true; prettyPrint = false }
) : CompanyCatalogService {

    
    private val companiesCollection: CoroutineCollection<Company> = mongoDb.getCollection("CompanyCatalog")

   
    override suspend fun createCompany(request: CreateCompanyRequest): Company? {
        val newCompany = Company(
            Company_Id = UUID.randomUUID().toString(),
            CompanyName = request.CompanyName,
            CompanyMain_id = request.CompanyMain_id,
            CompanyLocalisation = request.CompanyLocalisation,
            CompanyAgency = request.CompanyAgency,
            CompanyStatus = request.CompanyStatus,
            CompanyScore = null
        )

        try {
            val insertResult = companiesCollection.insertOne(newCompany)
            if (!insertResult.wasAcknowledged()) {
                println("MongoDB insert failed for company: ${newCompany.CompanyName}")
                return null
            }

            val companyJson = jsonMapper.encodeToString(newCompany)
            redis.setex(CompanyCacheKeys.companyById(newCompany.Company_Id), COMPANY_CACHE_TTL_SECONDS, companyJson)
            redis.setex(CompanyCacheKeys.companyByMainId(newCompany.CompanyMain_id), COMPANY_CACHE_TTL_SECONDS, companyJson)

            redis.del(CompanyCacheKeys.companiesByStatus(newCompany.CompanyStatus, 1, 20))

            return newCompany
        } catch (e: Exception) {
            println("Error creating company ${newCompany.CompanyName}: ${e.message}")
            return null
        }
    }

   
    override suspend fun getCompanyById(companyId: String): Company? {
        val cacheKey = CompanyCacheKeys.companyById(companyId)
        try {
            val cachedCompanyJson = redis.get(cacheKey)
            if (cachedCompanyJson != null) {
                return jsonMapper.decodeFromString<Company>(cachedCompanyJson)
            }

            val dbCompany = companiesCollection.findOne(Company::Company_Id eq companyId)
            if (dbCompany != null) {
                redis.setex(cacheKey, COMPANY_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbCompany))
            }
            return dbCompany
        } catch (e: Exception) {
            println("Error getting company by ID $companyId: ${e.message}")
            return null
        }
    }

   
    override suspend fun getCompanyByMainId(mainId: String): Company? {
        val cacheKey = CompanyCacheKeys.companyByMainId(mainId)
        try {
            val cachedCompanyJson = redis.get(cacheKey)
            if (cachedCompanyJson != null) {
                return jsonMapper.decodeFromString<Company>(cachedCompanyJson)
            }

            val dbCompany = companiesCollection.findOne(Company::CompanyMain_id eq mainId)
            if (dbCompany != null) {
                redis.setex(CompanyCacheKeys.companyById(dbCompany.Company_Id), COMPANY_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbCompany))
                redis.setex(cacheKey, COMPANY_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbCompany))
            }
            return dbCompany
        } catch (e: Exception) {
            println("Error getting company by Main ID $mainId: ${e.message}")
            return null
        }
    }


    override suspend fun getCompaniesByStatus(status: CompanyStatus, page: Int, pageSize: Int): List<Company> {
        val cacheKey = CompanyCacheKeys.companiesByStatus(status, page, pageSize)
        try {
            val cachedListJson = redis.get(cacheKey)
            if (cachedListJson != null) {
                return jsonMapper.decodeFromString<List<Company>>(cachedListJson)
            }

            val skip = (page - 1) * pageSize
            val dbCompanies = companiesCollection.find(Company::CompanyStatus eq status)
                .skip(skip)
                .limit(pageSize)
                .toList()

            if (dbCompanies.isNotEmpty()) {
                redis.setex(cacheKey, COMPANY_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbCompanies))
            }
            return dbCompanies
        } catch (e: Exception) {
            println("Error getting companies by status $status: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getCompaniesByAgency(agencyQuery: String, page: Int, pageSize: Int): List<Company> {
        val cacheKey = CompanyCacheKeys.companiesByAgency(agencyQuery, page, pageSize)
        try {
            val cachedListJson = redis.get(cacheKey)
            if (cachedListJson != null) {
                return jsonMapper.decodeFromString<List<Company>>(cachedListJson)
            }

            val skip = (page - 1) * pageSize


            val dbCompanies = companiesCollection.find(Company::CompanyAgency regex agencyQuery )
                .limit(pageSize)
                .toList()

            if (dbCompanies.isNotEmpty()) {
                redis.setex(cacheKey, COMPANY_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbCompanies))
            }
            return dbCompanies
        } catch (e: Exception) {
            println("Error getting companies by agency query '$agencyQuery': ${e.message}")
            return emptyList()
        }
    }

    override suspend fun updateCompany(companyId: String, request: UpdateCompanyRequest): Company? {
        try {
            val currentCompany = companiesCollection.findOne(Company::Company_Id eq companyId) ?: return null

            val updatedCompany = currentCompany.copy(
                CompanyName = request.CompanyName ?: currentCompany.CompanyName,
                CompanyLocalisation = request.CompanyLocalisation ?: currentCompany.CompanyLocalisation,
                CompanyAgency = request.CompanyAgency ?: currentCompany.CompanyAgency,
                CompanyStatus = request.CompanyStatus ?: currentCompany.CompanyStatus,
                CompanyScore = request.CompanyScore ?: currentCompany.CompanyScore
            )

            // I used replaceOne to update the document 
            val updateResult = companiesCollection.replaceOne(Company::Company_Id eq companyId, updatedCompany)

           
            if (updateResult.modifiedCount == 0L && updateResult.upsertedId == null) {
                if (currentCompany == updatedCompany) return currentCompany
                println("Company update failed or no changes for ID $companyId")
                return null
            }

            val freshlyUpdatedCompany = companiesCollection.findOne(Company::Company_Id eq companyId) ?: return null

            val companyJson = jsonMapper.encodeToString(freshlyUpdatedCompany)
            redis.setex(CompanyCacheKeys.companyById(companyId), COMPANY_CACHE_TTL_SECONDS, companyJson)
            redis.setex(CompanyCacheKeys.companyByMainId(freshlyUpdatedCompany.CompanyMain_id), COMPANY_CACHE_TTL_SECONDS, companyJson)

            if (currentCompany.CompanyStatus != freshlyUpdatedCompany.CompanyStatus) {
                redis.del(CompanyCacheKeys.companiesByStatus(currentCompany.CompanyStatus, 1, 20))
                redis.del(CompanyCacheKeys.companiesByStatus(freshlyUpdatedCompany.CompanyStatus, 1, 20))
            }

            return freshlyUpdatedCompany
        } catch (e: Exception) {
            println("Error updating company $companyId: ${e.message}")
            return null
        }
    }

    override suspend fun updateCompanyStatus(companyId: String, newStatus: CompanyStatus): Company? {
        try {
            val currentCompany = companiesCollection.findOne(Company::Company_Id eq companyId) ?: return null

            if (currentCompany.CompanyStatus == newStatus) {
                return currentCompany
            }

            
            val updateResult = companiesCollection.updateOne(
                Company::Company_Id eq companyId,
                setValue(Company::CompanyStatus, newStatus)
            )

            if (updateResult.modifiedCount == 0L) {
                println("Company status update failed for ID $companyId")
                return null
            }

            val updatedCompany = companiesCollection.findOne(Company::Company_Id eq companyId) ?: return null

            val companyJson = jsonMapper.encodeToString(updatedCompany)
            redis.setex(CompanyCacheKeys.companyById(companyId), COMPANY_CACHE_TTL_SECONDS, companyJson)
            redis.setex(CompanyCacheKeys.companyByMainId(updatedCompany.CompanyMain_id), COMPANY_CACHE_TTL_SECONDS, companyJson)

            redis.del(CompanyCacheKeys.companiesByStatus(currentCompany.CompanyStatus, 1, 20))
            redis.del(CompanyCacheKeys.companiesByStatus(newStatus, 1, 20))

            return updatedCompany
        } catch (e: Exception) {
            println("Error updating company status for $companyId: ${e.message}")
            return null
        }
    }
}
