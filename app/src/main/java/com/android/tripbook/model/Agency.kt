package com.android.tripbook.model
// attributes for the agencies
import kotlinx.serialization.Serializable

@Serializable
data class Agency(
    val agencyId: Int = 0,
    val agencyName: String = "",
    val agencyDescription: String? = null,
    val agencyAddress: String? = null,
    val contactPhone: String? = null,
    val website: String? = null,
    val isActive: Boolean = true
)
