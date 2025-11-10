package com.example.itew3_midterm_case_study.ui.theme

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark Color Scheme for Dark Mode
private val DarkColorScheme = darkColorScheme(
    primary = DarkCoralPink,
    secondary = DarkMediumPink,
    tertiary = DarkPink,
    background = DarkCream,
    surface = DarkPink,
    surfaceVariant = DarkMediumPink,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE8E8E8),
    onSurface = Color(0xFFE8E8E8),
    primaryContainer = DarkMediumPink,
    onPrimaryContainer = Color.White,
    secondaryContainer = DarkPink,
    onSecondaryContainer = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

// Light Color Scheme for Light Mode
private val LightColorScheme = lightColorScheme(
    primary = CoralPink,
    secondary = MediumPink,
    tertiary = LightPink,
    background = Cream,
    surface = Color.White,
    surfaceVariant = LightPink,
    onPrimary = Color.White,
    onSecondary = Color(0xFF5A4040),
    onTertiary = Color(0xFF5A4040),
    onBackground = Color(0xFF3A3530),
    onSurface = Color(0xFF3A3530),
    primaryContainer = LightPink,
    onPrimaryContainer = Color(0xFF5A4040),
    secondaryContainer = MediumPink,
    onSecondaryContainer = Color.White,
    outline = MediumPink,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun AttendanceTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled by default to use custom colors
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
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Legacy theme function for compatibility
@Composable
fun ITEW3_MIDTERM_CASE_STUDYTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AttendanceTrackerTheme(darkTheme, dynamicColor, content)
}

