
/*
Provide structured way to store and manage various contact details for a company
 */

package com.android.tripbook.companycatalog.model

data class CompanyContact(
    val phone: String,
    val email: String,
    val address: String,
    val city: String,
    val region: String,
    val postalCode: String? = null,
    val whatsapp: String? = null,
    val facebook: String? = null,
    val instagram: String? = null
)
