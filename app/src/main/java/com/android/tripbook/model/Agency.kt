package com.android.tripbook.model
import java.io.Serializable

data class Agency(
    val id: Int,
    val title: String,
    val caption: String,
    val description: String,
    val imageUrl: List<String>
) : Serializable
