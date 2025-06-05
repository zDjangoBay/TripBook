package com.android.tripbook.model
import java.io.Serializable

data class Agency(
    val id: Int,
    val name: String,
    val price: String,  // Formatted price (e.g., "10,000 XAF")
    val rating: Float,  // 1-5 scale
    val reviewCount: Int,
    val perks: List<String>,  // e.g., ["Free cancellation", "24/7 support"]
    val imageUrl: String,
    val description: String,
) : Serializable
