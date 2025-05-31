/*
Provide structured way to store and manage various contact details for a company
 */

package com.android.tripbook.companycatalog.model
data class CompanyContact(
    val type: String, // e.g., "Phone", "Email"
    val value: String
)

