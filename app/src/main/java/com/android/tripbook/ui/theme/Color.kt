package com.android.tripbook.ui.theme

import androidx.compose.ui.graphics.Color

// Original color palette (keep all these)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Your additional purple
val Purple700 = Color(0xFF5C26D5)

// ▼▼▼ New Location-Based Color System ▼▼▼
// Tropical Theme Colors (lush greens)
val TropicalPrimary = Color(0xFF2E7D32)  // Deep green
val TropicalSecondary = Color(0xFF4CAF50) // Medium green
val TropicalTertiary = Color(0xFF8BC34A)  // Lime green

// Arctic Theme Colors (cool blues)
val ArcticPrimary = Color(0xFF0288D1)    // Deep blue
val ArcticSecondary = Color(0xFF81D4FA)  // Sky blue
val ArcticTertiary = Color(0xFFE1F5FE)   // Ice blue

// Urban Theme Colors (modern grays)
val UrbanPrimary = Color(0xFF424242)     // Dark gray
val UrbanSecondary = Color(0xFF757575)   // Medium gray
val UrbanTertiary = Color(0xFFBDBDBD)    // Light gray

// Theme type selector
enum class LocationTheme {
    DEFAULT,    // Uses original purple palette
    TROPICAL,   // Green palette
    ARCTIC,     // Blue palette
    URBAN       // Gray palette
}