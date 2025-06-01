package com.tripbook.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB2FFF4),
    onSecondaryContainer = Color(0xFF00201D),
    tertiary = Color(0xFF6200EE),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8DEF8),
    onTertiaryContainer = Color(0xFF21005D),
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFCDAD7),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1976D2),
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF03DAC6),
    onSecondaryContainer = Color(0xFFB2FFF4),
    tertiary = Color(0xFFBB86FC),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF6200EE),
    onTertiaryContainer = Color(0xFFE8DEF8),
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color(0xFFFCDAD7),
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFE7E0EC)
)

@Composable
fun TripBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
} 