package com.android.companycatalog.model

import kotlinx.serialization.Serializable

@Serializable
enum class CompanyStatus { // This will help us define the status of the company whether it was suspended by the MINTRANSPORT or not or even if it was closed
    SUSPENDED,
    RUN,
    CLOSED
}