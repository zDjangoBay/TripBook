package com.android.companycatalog.model

import kotlinx.serialization.Serializable
import com.android.companycatalog.model.CompanyStatus.RUN

@Serializable
data class CreateCompanyRequest(
    val CompanyName: String,
    val CompanyMain_id: String,
    val CompanyLocalisation: String,
    val CompanyAgency: String,
    val CompanyStatus: CompanyStatus
)