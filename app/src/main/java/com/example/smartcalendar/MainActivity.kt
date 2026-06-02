package com.example.smartcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.smartcalendar.screens.CalendarScreen
import com.example.smartcalendar.screens.SettingsScreen
import com.example.smartcalendar.ui.theme.SmartCalendarTheme
import com.example.smartcalendar.utils.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentThemeId by ThemeManager.getCurrentThemeFlow(this)
                .collectAsState(initial = "default")

            val isSystemDark = isSystemInDarkTheme()
            val useDarkMode by ThemeManager.getDarkModeFlow(this)
                .collectAsState(initial = isSystemDark)

            SmartCalendarTheme(themeId = currentThemeId, darkTheme = useDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(onClose = { showSettings = false })
    } else {
        CalendarScreen(onOpenSettings = { showSettings = true })
    }
}
