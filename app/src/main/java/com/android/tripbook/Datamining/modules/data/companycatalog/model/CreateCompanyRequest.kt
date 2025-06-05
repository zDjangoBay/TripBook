package com.android.Tripbook.Datamining.modules.data.companycatalog.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateCompanyRequest(
    val CompanyName: String,
    val CompanyMain_id: String,
    val CompanyLocalisation: String,
    val CompanyAgency: String,
    val CompanyStatus: CompanyStatus = CompanyStatus.RUNNING
)