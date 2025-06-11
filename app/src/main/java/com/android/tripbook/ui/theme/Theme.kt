package com.android.tripbook.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng

// Original colors (preserved)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val Purple700 = Color(0xFF5C26D5)

// New location colors (added at bottom of existing color.kt)
val TropicalPrimary = Color(0xFF2E7D32)
val ArcticPrimary = Color(0xFF0288D1)
val UrbanPrimary = Color(0xFF424242)

enum class LocationTheme { DEFAULT, TROPICAL, ARCTIC, URBAN }

// Location detection extension (added to Theme.kt)
private fun LatLng.determineTheme(): LocationTheme = when {
    latitude in -23.5..23.5 -> LocationTheme.TROPICAL
    latitude > 66.5 || latitude < -66.5 -> LocationTheme.ARCTIC
    else -> LocationTheme.URBAN
}

@Composable
fun TripBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    currentLocation: LatLng? = null,  // New optional parameter
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val locationTheme = currentLocation?.determineTheme() ?: LocationTheme.DEFAULT

    val baseScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80
        )
        else -> lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = Pink40
        )
    }

    val finalScheme = when (locationTheme) {
        LocationTheme.TROPICAL -> baseScheme.copy(primary = TropicalPrimary)
        LocationTheme.ARCTIC -> baseScheme.copy(primary = ArcticPrimary)
        LocationTheme.URBAN -> baseScheme.copy(primary = UrbanPrimary)
        LocationTheme.DEFAULT -> baseScheme
    }

    MaterialTheme(
        colorScheme = finalScheme,
        typography = Typography,
        content = content
    )
}