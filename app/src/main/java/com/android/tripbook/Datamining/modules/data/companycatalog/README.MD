# Datamining - Company Catalog Module

<p>This document provides a comprehensive overview of the Company Catalog Module. This module is responsible for managing information about travel and transportation companies, including their details, operational status, locations, and user-generated scores. It utilizes a caching layer with Redis for performance and persistent storage via MongoDB.</p>

## Table of Contents

1.  [Module Overview](#module-overview)
    * [Key Functionalities](#key-functionalities)
    * [Technologies Used](#technologies-used)
2.  [Data Models](#data-models)
    * [Company](#company-kt)
    * [CompanyStatus](#companystatus-kt)
    * [CreateCompanyRequest DTO](#createcompanyrequest-dto)
    * [UpdateCompanyRequest DTO](#updatecompanyrequest-dto)
    * [UpdateCompanyStatusRequest DTO](#updatecompanystatusrequest-dto)
3.  [Service Layer](#service-layer)
    * [CompanyCatalogService (Interface)](#companycatalogservice-interface)
    * [CompanyCatalogServiceImpl (Implementation)](#companycatalogserviceimpl-implementation)
        * [Dependencies](#dependencies)
        * [MongoDB Collection](#mongodb-collection)
        * [Caching Strategy](#caching-strategy)
        * [Method Descriptions](#method-descriptions)
4.  [API Routes (Conceptual)](#api-routes-conceptual)

---

## 1. Module Overview

The Company Catalog Module serves as a centralized repository for managing all data related to travel and transportation companies within the TripBook application. It allows for efficient storage, retrieval, and updating of company profiles, their operational statuses, and other relevant details.

### Key Functionalities

* **CRUD Operations:** Full support for creating, reading, updating, and managing company profiles.
* **Status Management:** Tracks whether a company is `RUNNING`, `SUSPENDED`, or `CLOSED`.
* **Location & Agency Information:** Stores details about a company's main identifiers, specific localizations, and a description of its agency network.
* **Scoring System:** Accommodates a nullable `CompanyScore` which can be derived from user reviews.
* **Efficient Data Retrieval:** Employs Redis caching for frequently accessed company data to reduce latency and database load.
* **Persistent Storage:** Utilizes MongoDB for robust and scalable storage of company information.
* **Querying Capabilities:** Allows fetching companies based on various criteria like ID, main business ID, status, and agency information.

### Technologies Used

* **Kotlin:** Primary programming language.
* **Ktor:** Framework used for building the server and API routes.
* **MongoDB:** NoSQL database for persistent storage of company data.
* **Redis:** In-memory data store for caching company data.
* **Kotlinx Serialization:** For serializing and deserializing Kotlin objects to/from JSON.

## 2. Data Models

The module uses the following data classes to represent company information and request payloads.

### `Company`

This data class defines the structure of a company object.

```kotlin

@Serializable
data class Company(
    val Company_Id:String=UUID.randomUUID().toString(),
    val CompanyName:String,
    val CompanyMain_id:String, // Code of the Main company, e.g., "UnitedVoy458"
    val CompanyLocalisation:String, // Specific location, e.g., "Nyom" or "Mvan"
    val CompanyAgemcy:String, // Illustrates agency presence, e.g., "Douala, Garoua, Youade and Bertoua"
    val CompanyScore:Int?, // Score given by users, compiled from reviews
    val CompanyStatus: CompanyStatus
)
```

### `CompanyStatus`

This data class defines the possible operational statuses for a company.

```kotlin
@Serializable
enum class CompanyStatus { // Helps define if the company was suspended by MINTRANSPORT, is running, or closed
    SUSPENDED,
    RUNNING,
    CLOSED
}
```

### `CreateCompanyRequest`

This data class is a DTO(Data Transfert Object ) , that in our case intervene in the company creation process . 

```kotlin
@Serializable
data class CreateCompanyRequest(
    val CompanyName: String,
    val CompanyMain_id: String,
    val CompanyLocalisation: String,
    val CompanyAgency: String, // Corresponds to Company.CompanyAgemcy
    val CompanyStatus: CompanyStatus = CompanyStatus.RUNNING
)
```

### `UpdateCompantRequest`

This is a ata Transfer Object for updating an existing company's information. Fields are nullable, allowing partial updates

```kotlin
@Serializable
data class UpdateCompanyRequest(
    val CompanyName: String? = null,
    val CompanyLocalisation: String? = null,
    val CompanyAgency: String? = null, // Corresponds to Company.CompanyAgemcy
    val CompanyStatus: CompanyStatus? = null,
    val CompanyScore: Int? = null
)
```

### `UpdateCompanyStatusRequest`

This is a Data Transfer Object specifically for updating a company's status.

```kotlin
@Serializable
data class UpdateCompanyStatusRequest(
    val CompanyStatus: CompanyStatus
)
```


## Service Layer 

The service layer handles the core business logic, data manipulation, and coordination between the database and cache.

### `CompanyCatalogService (Interface)`

Defines the contract for operations related to the company catalog.

```kotlin
interface CompanyCatalogService {
    suspend fun createCompany(request: CreateCompanyRequest): Company?
    suspend fun getCompanyById(companyId: String): Company?
    suspend fun getCompanyByMainId(mainId: String): Company?
    suspend fun getCompaniesByStatus(status: CompanyStatus, page: Int = 1, pageSize: Int = 20): List<Company>
    suspend fun getCompaniesByAgency(agencyQuery: String, page: Int = 1, pageSize: Int = 20): List<Company>
    suspend fun updateCompany(companyId: String, request: UpdateCompanyRequest): Company?
    suspend fun updateCompanyStatus(companyId: String, newStatus: CompanyStatus): Company?
}
```

### `CompanyCatalog (Implementation)`

Provides the concrete implementation for `CompanyCatalogService`.

#### Dependencies

* **CoroutineDatabase (MongoDB):** KMongo coroutine database instance.
* **Jedis (Redis):** Jedis client instance for Redis interaction.
* **Json (Kotlinx Serialization):** For JSON serialization/deserialization.

#### MongoDB Collection

Company data is stored in a MongoDB collection named `"CompanyCatalog"`. 

#### Caching Strategy

* **Cache Keys:** Managed by `CompanyCacheKeys` object for consistency (e.g., `company:<id>`, `company_main:<main_id>`, `companies_status:...`).
* **TTL:** A default `COMPANY_CACHE_TTL_SECONDS` of 3600 seconds (1 hour) is applied to cache entries.

##### Read Operations:

1. Attempt to retrieve data from Redis.
2. On cache hit, return deserialized data.
3. On cache miss, query MongoDB.
4. Cache the MongoDB result in Redis before returning.

##### Write Operations:

1. Perform the operation in MongoDB.
2. Update or invalidate relevant Redis cache entries .

#### Method 

* ##### `createCompany(request: CreateCompanyRequest):`


* ##### `getCompanyById(companyId: String):`

* ##### `getCompanyByMainId(mainId: String):`


* ##### `getCompaniesByStatus(status: CompanyStatus, page: Int, pageSize: Int):`

* ##### `getCompaniesByAgency(agencyQuery: String, page: Int, pageSize: Int):`


* ##### `updateCompany(companyId: String, request: UpdateCompanyRequest):`

* ##### `updateCompanyStatus(companyId: String, newStatus: CompanyStatus):`



## API Route 

The service methods are exposed through Ktor routes defined in `CompanyCatalogRoutes.kt`:

| Route | Method | Description | Service Call |
|-------|--------|-------------|--------------|
| `/companies` | POST | Creates new company | `createCompany()` |
| `/companies/{company_id}` | GET | Gets company by UUID | `getCompanyById()` |
| `/companies/main/{main_id}` | GET | Gets company by main ID | `getCompanyByMainId()` |
| `/companies` | GET | Lists companies with filters:<br>- `status` (RUNNING/SUSPENDED/CLOSED)<br>- `agency` (location query)<br>- `page`, `size` (pagination)<br>Defaults to RUNNING companies | `getCompaniesByStatus()`<br>`getCompaniesByAgency()` |
| `/companies/{company_id}` | PUT | Updates company details | `updateCompany()` |
| `/companies/{company_id}/status` | PATCH | Updates company status only | `updateCompanyStatus()` |
| `/companies/agency/{agency_query}` | GET | Alternative agency search route | `getCompaniesByAgency()` |

>I improved the design of the api routes ✅