package com.example.smartcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
<<<<<<< HEAD
import androidx.compose.runtime.*
=======
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
>>>>>>> 3a25146faef926a66f560b189c2c33352113cbb6
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
            var currentThemeId by remember { mutableStateOf("default") }
            LaunchedEffect(Unit) {
                ThemeManager.getCurrentThemeFlow(this@MainActivity).collect { newId ->
                    currentThemeId = newId
                }
            }
            SmartCalendarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
<<<<<<< HEAD
                    key(currentThemeId) {
                        AppContent()
                    }
=======
                    AppContent()
>>>>>>> 3a25146faef926a66f560b189c2c33352113cbb6
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    var showSettings by remember { mutableStateOf(false) }
<<<<<<< HEAD
=======

>>>>>>> 3a25146faef926a66f560b189c2c33352113cbb6
    if (showSettings) {
        SettingsScreen(onClose = { showSettings = false })
    } else {
        CalendarScreen(onOpenSettings = { showSettings = true })
    }
}