package com.android.Tripbook.Datamining.modules.data.tripcatalog.models

data class Review(
    val tripId: Int,
    val username: String,
    val comment: String,
    val images: List<String>
)
