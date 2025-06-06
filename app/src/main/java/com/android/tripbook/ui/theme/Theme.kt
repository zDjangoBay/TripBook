package com.android.tripbook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4338CA),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF8B5CF6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF7C3AED),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF06B6D4),
    onTertiary = Color.White,
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFF9CA3AF),
    background = Color(0xFF111827),
    onBackground = Color(0xFFE5E7EB),
    outline = Color(0xFF6B7280),
    outlineVariant = Color(0xFF4B5563)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    secondary = Color(0xFF8B5CF6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = Color(0xFF06B6D4),
    onTertiary = Color.White,
    surface = Color.White,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFF9FAFB),
    onSurfaceVariant = Color(0xFF6B7280),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1F2937)
)

@Composable
fun TripBookTheme(
    darkTheme: Boolean = true, // Always use dark theme to match the design
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
