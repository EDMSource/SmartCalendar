package com.example.smartcalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary             = Blue40,
    onPrimary           = Neutral99,
    primaryContainer    = Blue90,
    onPrimaryContainer  = Blue10,
    secondary           = Indigo40,
    onSecondary         = Neutral99,
    secondaryContainer  = Indigo80,
    onSecondaryContainer = Blue10,
    tertiary            = Teal40,
    onTertiary          = Neutral99,
    tertiaryContainer   = Teal80,
    onTertiaryContainer = Blue10,
    background          = Neutral99,
    onBackground        = Neutral10,
    surface             = Neutral99,
    onSurface           = Neutral10,
    surfaceVariant      = Blue90,
    onSurfaceVariant    = Blue10,
    error               = ErrorRed,
    onError             = Neutral99,
    errorContainer      = ErrorRedLight,
    onErrorContainer    = ErrorRed,
)

private val DarkColorScheme = darkColorScheme(
    primary             = Blue80,
    onPrimary           = Blue20,
    primaryContainer    = Blue10,
    onPrimaryContainer  = Blue90,
    secondary           = Indigo80,
    onSecondary         = Indigo40,
    secondaryContainer  = Blue20,
    onSecondaryContainer = Indigo80,
    tertiary            = Teal80,
    onTertiary          = Teal40,
    background          = Neutral10,
    onBackground        = Neutral90,
    surface             = Neutral10,
    onSurface           = Neutral90,
    surfaceVariant      = Blue10,
    onSurfaceVariant    = Blue80,
    error               = ErrorRedLight,
    onError             = ErrorRed,
)

@Composable
fun SmartCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme // выбор схемы по теме системы

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}