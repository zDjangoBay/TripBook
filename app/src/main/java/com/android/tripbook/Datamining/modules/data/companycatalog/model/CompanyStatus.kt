package com.android.Tripbook.Datamining.modules.data.companycatalog.model

import kotlinx.serialization.Serializable

@Serializable
enum class CompanyStatus { // This will help us define the status of the company whether it was suspended by the MINTRANSPORT or not or even if it was closed
    SUSPENDED,
    RUNNING,
    CLOSED
}