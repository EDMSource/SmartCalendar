package com.example.smartcalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.smartcalendar.utils.ThemeManager
import kotlinx.coroutines.flow.first

@Composable
fun SmartCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var darkModePref by remember { mutableStateOf(false) }
    var currentThemeId by remember { mutableStateOf("default") }

    LaunchedEffect(Unit) {
        darkModePref = ThemeManager.getDarkModeFlow(context).first()
        ThemeManager.getDarkModeFlow(context).collect { darkModePref = it }
    }
    LaunchedEffect(Unit) {
        currentThemeId = ThemeManager.getCurrentThemeFlow(context).first()
        ThemeManager.getCurrentThemeFlow(context).collect { currentThemeId = it }
    }

    val useDarkTheme = darkModePref
    val theme = ThemeManager.getThemeById(currentThemeId)

    val colorScheme = if (useDarkTheme) {
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

// Extension properties для ColorScheme
val ColorScheme.todayColor: Color
    @Composable
    get() {
        val context = LocalContext.current
        var themeId by remember { mutableStateOf("default") }
        LaunchedEffect(Unit) {
            themeId = ThemeManager.getCurrentThemeFlow(context).first()
            ThemeManager.getCurrentThemeFlow(context).collect { themeId = it }
        }
        return ThemeManager.getThemeById(themeId).todayColor
    }

val ColorScheme.noteIndicator: Color
    @Composable
    get() {
        val context = LocalContext.current
        var themeId by remember { mutableStateOf("default") }
        LaunchedEffect(Unit) {
            themeId = ThemeManager.getCurrentThemeFlow(context).first()
            ThemeManager.getCurrentThemeFlow(context).collect { themeId = it }
        }
        return ThemeManager.getThemeById(themeId).noteIndicator
    }