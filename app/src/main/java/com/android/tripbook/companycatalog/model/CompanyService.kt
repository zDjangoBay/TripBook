
/*
- This class is used to define and store the different services
  that the company provides.
 */

package com.android.tripbook.companycatalog.model

data class CompanyService(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val duration: String? = null,
    val isPopular: Boolean = false,
    val availability: String = "Available"
)
