package com.example.smartcalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.smartcalendar.utils.ThemeManager

private val LocalTodayColor = staticCompositionLocalOf { Color.Unspecified }
private val LocalNoteIndicator = staticCompositionLocalOf { Color.Unspecified }

@Composable
fun SmartCalendarTheme(
    themeId: String = "default",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val theme = ThemeManager.getThemeById(themeId)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = theme.primary,
            primaryContainer = theme.primaryContainer.copy(alpha = 0.8f),
            onPrimaryContainer = theme.onPrimaryContainer,
            surfaceVariant = theme.surfaceVariant.copy(alpha = 0.2f)
        )
    } else {
        lightColorScheme(
            primary = theme.primary,
            primaryContainer = theme.primaryContainer,
            onPrimaryContainer = theme.onPrimaryContainer,
            surfaceVariant = theme.surfaceVariant
        )
    }

    CompositionLocalProvider(
        LocalTodayColor provides theme.todayColor,
        LocalNoteIndicator provides theme.noteIndicator
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(),
            content = content
        )
    }
}

val ColorScheme.todayColor: Color
    @Composable
    get() = LocalTodayColor.current

val ColorScheme.noteIndicator: Color
    @Composable
    get() = LocalNoteIndicator.current
