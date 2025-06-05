package com.android.Tripbook.Datamining.modules.data.companycatalog.model

import kotlinx.serialization.Serializable


@Serializable
data class UpdateCompanyStatusRequest(
    val CompanyStatus: CompanyStatus
)