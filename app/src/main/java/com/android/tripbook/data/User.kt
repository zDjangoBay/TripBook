package com.android.tripbook.data

data class User(
    val id: String,
    val name: String,
    val destination: String,
    val profileImageUrl: String? = null
)
