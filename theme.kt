package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4F46E5),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF10B981),
    onSecondary = Color.White,
    tertiary = Color(0xFFF59E0B),
    onTertiary = Color.White,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE2E8F0),
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF3730A3),
    secondary = Color(0xFF10B981),
    onSecondary = Color.White,
    tertiary = Color(0xFFF59E0B),
    onTertiary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1F2937),
    surface = Color.White,
    onSurface = Color(0xFF1F2937),
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun PasswordRecoveryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
