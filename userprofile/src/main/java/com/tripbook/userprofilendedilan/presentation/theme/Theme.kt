package com.tripbook.userprofilendedilan.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)
//
//@Composable
//fun TripBookTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}


private val OrangeTheme = lightColorScheme(
    primary = Color(0xFFFF5722), // Orange vibrant
    secondary = Color(0xFFFF9800),
    tertiary = Color(0xFFFFEB3B),

    // Couleurs de surface avec teintes orangées
    surface = Color(0xFFFFF8F6),
    surfaceVariant = Color(0xFFFFEDE8),

    // Autres couleurs
    background = Color(0xFFFFFBFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1B1B1B)
)

@Composable
fun TripBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = OrangeTheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
