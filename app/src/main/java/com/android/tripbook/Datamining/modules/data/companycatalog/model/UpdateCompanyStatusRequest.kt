package com.android.companycatalog.model

import kotlinx.serialization.Serializable


@Serializable
data class UpdateCompanyStatusRequest(
    val CompanyStatus: CompanyStatus
)