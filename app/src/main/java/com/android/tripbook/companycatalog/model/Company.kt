
/*
This class is to model a company with its attributes essential to handle
company details, user interactions and organizing related information.
 */
package com.android.tripbook.companycatalog.model

data class Company(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val location: String,
    val rating: Float,
    val reviewCount: Int,
    val imageUrl: String,
    val contact: CompanyContact,
    val services: List<CompanyService>,
    var isLiked: Boolean = false,
    val priceRange: String,
    val website: String? = null,
    val established: Int? = null,
    val specialties: List<String> = emptyList()
)
