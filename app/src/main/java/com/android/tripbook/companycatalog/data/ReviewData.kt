package com.android.tripbook.companycatalog.data

import androidx.annotation.DrawableRes

/**
 * Holds info for a single user review on a company.
 */
data class ReviewData(
    val id: String,
    val companyId: String,
    val reviewerId: String,
    val reviewerName: String,
    @DrawableRes val reviewerAvatarResId: Int,
    val rating: Int,
    val comment: String,
    val timestamp: Long
)
