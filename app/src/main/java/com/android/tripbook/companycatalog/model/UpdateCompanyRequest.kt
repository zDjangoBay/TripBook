package com.android.companycatalog.model

import kotlinx.serialization.Serializable


@Serializable
data class UpdateCompanyRequest(
    val CompanyName: String? = null,
    val CompanyLocalisation: String? = null,
    val CompanyAgency: String? = null, // ou List<String>
    val CompanyStatus: CompanyStatus? = null,
    val CompanyScore: Int? = null // Le score pourrait être mis à jour par un autre module
)