package com.android.tripbook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF7C3AED),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF9575CD),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF673AB7),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFBA68C8),
    onTertiary = Color.White,
    surface = Color(0xFFF5F3FF),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFEDE9FE),
    onSurfaceVariant = Color(0xFF6B7280),
    background = Color(0xFFFAF9FF),
    onBackground = Color(0xFF1F2937),
    outline = Color(0xFFD1D5DB),
    outlineVariant = Color(0xFFE5E7EB)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B5CF6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF4C1D95),
    secondary = Color(0xFF9575CD),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = Color(0xFFBA68C8),
    onTertiary = Color.White,
    surface = Color(0xFFF5F3FF),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFEDE9FE),
    onSurfaceVariant = Color(0xFF6B7280),
    background = Color(0xFFFAF9FF),
    onBackground = Color(0xFF1F2937)
)

@Composable
fun TripBookTheme(
    darkTheme: Boolean = false, // Use light theme to match the design
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
