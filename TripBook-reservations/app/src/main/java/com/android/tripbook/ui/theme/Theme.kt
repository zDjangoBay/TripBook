package com.android.tripbook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = ErrorColor,
    onPrimary = TextOnPrimary,
    onSecondary = TextOnPrimary,
    onTertiary = TextOnPrimary,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = TextOnPrimary,
    primaryContainer = PurpleDark,
    secondaryContainer = PurpleMedium,
    onPrimaryContainer = TextOnPrimary,
    onSecondaryContainer = TextOnPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = ErrorColor,
    onPrimary = TextOnPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextOnPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = TextOnPrimary,
    primaryContainer = PurpleLavender,
    secondaryContainer = Secondary,
    onPrimaryContainer = TextPrimary,
    onSecondaryContainer = TextPrimary,
    surfaceVariant = ButtonSurface,
    onSurfaceVariant = TextSecondary
)

@Composable
fun TripBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}