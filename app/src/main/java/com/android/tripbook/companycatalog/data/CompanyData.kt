
package com.android.tripbook.companycatalog.data

import androidx.annotation.DrawableRes

/*
 * A data class representing a company listed in the catalog.
 */

data class CompanyData(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val contactInfo: Map<String, String>,
    val location: Pair<Double, Double>,
    val servicesOffered: List<String>,
    @DrawableRes val logoResId: Int,
    val averageRating: Float,
    val numberOfReviews: Int,
    val socialMediaLinks: Map<String, String>? = null
)
