package com.android.companycatalog.model

import kotlinx.serialization.Serializable


@Serializable
data class UpdateCompanyRequest(
    val CompanyName: String? = null,
    val CompanyLocalisation: String? = null,
    val CompanyAgency: String? = null, 
    val CompanyStatus: CompanyStatus? = null,
    val CompanyScore: Int? = null // The company score is bound to change
)
