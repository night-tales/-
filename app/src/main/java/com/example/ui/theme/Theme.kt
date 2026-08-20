package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HakayatDarkColors = darkColorScheme(
    primary = Color(0xFFF9C74F), // Gold/Yellow Accent
    secondary = Color(0xFF2E86AB), // Distinct Blue
    tertiary = Color(0xFF7BE495), // Keeping a green for success states

    background = Color(0xFF0D1B2A), // Deep Background Blue
    surface = Color(0xFF1B263B), // Surface / Cards Blue
    surfaceVariant = Color(0xFF23324C), // Slightly lighter surface for elements

    onPrimary = Color(0xFF0D1B2A),
    onSecondary = Color.White,
    onTertiary = Color(0xFF0B0B14),

    onBackground = Color(0xFFFFFFEF), // Off-white text
    onSurface = Color(0xFFFFFFEF),
    onSurfaceVariant = Color(0xFFA6A6B3)
)

@Composable
fun HakayatTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = HakayatDarkColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
