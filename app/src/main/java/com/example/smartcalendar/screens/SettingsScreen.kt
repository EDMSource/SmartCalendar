package com.example.smartcalendar.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcalendar.roulette.RouletteDialog

@Composable
fun SettingsScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    var clickCount by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "← назад",
            modifier = Modifier.clickable { onClose() },
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "настройки", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "тема: светлая (пока не меняется)", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(48.dp))

        Text(text = "версия 1.0.0-dev", fontSize = 14.sp)

        Text(
            text = "[ секретное казино ]",
            fontSize = 12.sp,
            modifier = Modifier.clickable {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > 1000) {
                    clickCount = 0
                }
                lastClickTime = currentTime
                clickCount++
                if (clickCount >= 3) {
                    clickCount = 0
                    RouletteDialog(context).show()
                }
            }
        )
    }
}