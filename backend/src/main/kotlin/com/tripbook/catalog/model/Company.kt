package com.tripbook.catalog.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Data class representing a travel company in the TripBook platform.
 * 
 * @property id The unique identifier for the company
 * @property name The name of the company
 * @property description A detailed description of the company
 * @property logo URL to the company's logo image
 * @property coverImage URL to the company's cover image
 * @property location The physical location of the company
 * @property contactInfo Contact information for the company
 * @property services List of services offered by the company
 * @property rating Current average rating of the company
 * @property reviewCount Number of reviews the company has received
 * @property verified Whether the company is verified by TripBook
 * @property featured Whether the company is featured by TripBook
 * @property createdAt When the company was added to the platform
 * @property updatedAt When the company information was last updated
 */
data class Company(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val logo: String,
    val coverImage: String,
    val location: Location,
    val contactInfo: ContactInfo,
    val services: List<Service>,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val verified: Boolean = false,
    val featured: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Data class representing a physical location.
 * 
 * @property address The street address
 * @property city The city name
 * @property country The country name
 * @property latitude The geographic latitude
 * @property longitude The geographic longitude
 */
data class Location(
    val address: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * Data class representing contact information for a company.
 * 
 * @property email The company's contact email
 * @property phone The company's phone number
 * @property website The company's website URL
 * @property socialMedia A map of social media platform names to profile URLs
 */
data class ContactInfo(
    val email: String,
    val phone: String,
    val website: String? = null,
    val socialMedia: Map<String, String> = mapOf()
)

/**
 * Data class representing a service offered by a company.
 * 
 * @property id The unique identifier for the service
 * @property name The name of the service
 * @property description A detailed description of the service
 * @property category The category this service belongs to
 * @property price The base price for this service (nullable if variable pricing)
 * @property currencyCode The ISO currency code for the price
 */
data class Service(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val category: ServiceCategory,
    val price: Double? = null,
    val currencyCode: String? = null
)

/**
 * Enum representing categories of services that travel companies can offer.
 */
enum class ServiceCategory {
    ACCOMMODATION,
    TRANSPORTATION,
    TOURS,
    ACTIVITIES,
    FOOD_AND_DINING,
    GUIDE_SERVICES,
    EQUIPMENT_RENTAL,
    PHOTOGRAPHY,
    OTHER
}