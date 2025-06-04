/*
This class is to model a company with its attributes essential to handle
company details, user interactions and organizing related information.
 */
package com.android.tripbook.companycatalog.model
data class Company(
    val id: Int,
    val name: String,
    val imageResIds: List<Int>, // List of drawable references
    val description: String,
    var likes: Int, // Changed to 'var' so it can be updated
    var isLiked: Boolean = false, // New: Added mutable isLiked state
    val stars: Int,
    val services: List<CompanyService>,
    val contacts: List<CompanyContact>
)

