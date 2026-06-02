package com.example.smartcalendar.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcalendar.roulette.RouletteScreen
import com.example.smartcalendar.utils.ThemeManager

import com.example.smartcalendar.utils.LogCollector

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private const val REPORT_EMAIL = "your-email@example.com"   // замените на реальный email

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var darkMode by remember { mutableStateOf(false) }
    var currentThemeId by remember { mutableStateOf("default") }
    var unlockedThemes by remember { mutableStateOf(setOf<String>()) }
    var showHolidays by remember { mutableStateOf(true) }

    var showErrorReportDialog by remember { mutableStateOf(false) }
    var errorReportText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        launch {
            ThemeManager.getDarkModeFlow(context).collect {
                darkMode = it; Log.d(
                "Settings",
                "darkMode = $it"
            ); LogCollector.addLog("Settings", "darkMode = $it")
            }
        }
        launch {
            ThemeManager.getCurrentThemeFlow(context).collect {
                currentThemeId = it; Log.d(
                "Settings",
                "currentThemeId = $it"
            ); LogCollector.addLog("Settings", "currentThemeId = $it")
            }
        }
        launch {
            ThemeManager.getUnlockedThemesFlow(context).collect {
                unlockedThemes = it; Log.d(
                "Settings",
                "unlockedThemes = $it"
            ); LogCollector.addLog("Settings", "unlockedThemes = $it")
            }
        }
        launch {
            ThemeManager.getShowHolidaysFlow(context).collect {
                showHolidays = it; Log.d(
                "Settings",
                "showHolidays = $it"
            ); LogCollector.addLog("Settings", "showHolidays = $it")
            }
        }
    }

    var showRoulette by remember { mutableStateOf(false) }
    var secretClicks by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }

    if (showRoulette) {
        RouletteScreen(onClose = {
            showRoulette = false
            scope.launch {
                val fresh = ThemeManager.getUnlockedThemesFlow(context).first()
                unlockedThemes = fresh
                Log.d("Settings", "Обновлено после рулетки: $fresh")
            }
        })
        return
    }

    if (showErrorReportDialog) {
        AlertDialog(
            onDismissRequest = { showErrorReportDialog = false; errorReportText = "" },
            title = { Text("Сообщить об ошибке") },
            text = {
                Column {
                    Text(
                        "Отправляется на почту: $REPORT_EMAIL",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Опишите проблему:")
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = errorReportText,
                        onValueChange = { errorReportText = it },
                        label = { Text("Ваш комментарий") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Логи приложения:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val logs = LogCollector.getLogs()
                        if (logs.isEmpty()) {
                            Text("Логи отсутствуют", fontSize = 10.sp, color = Color.Gray)
                        } else {
                            Text(
                                text = logs,
                                fontSize = 9.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val logs = LogCollector.getLogs()
                        val fullMessage = """
                        Комментарий пользователя:
                        $errorReportText
                        
                        Логи приложения:
                        $logs
                    """.trimIndent()
                        sendEmail(
                            context,
                            REPORT_EMAIL,
                            "Отчёт об ошибке SmartCalendar",
                            fullMessage
                        )
                        showErrorReportDialog = false
                        errorReportText = ""
                    }
                ) {
                    Text("Отправить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showErrorReportDialog = false; errorReportText = "" }) {
                    Text("Отмена")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader("Внешний вид")

            SettingsItem(
                icon = Icons.Default.Brightness6,
                title = "Тёмная тема",
                subtitle = if (darkMode) "Включена" else "Выключена",
                onClick = {
                    darkMode = !darkMode
                    LogCollector.addLog("Settings", "Тёмная тема переключена на $darkMode")
                    scope.launch { ThemeManager.saveDarkMode(context, darkMode) }
                }
            ) { Switch(checked = darkMode, onCheckedChange = {}) }



            SectionHeader("Оформление")

            val availableThemes = remember(unlockedThemes) {
                ThemeManager.allThemes.filter { it.id == "default" || it.id in unlockedThemes }
            }

            availableThemes.forEach { theme ->
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = theme.name,
                    subtitle = if (currentThemeId == theme.id) "✓ Активна" else "Нажмите, чтобы применить",
                    onClick = {
                        if (currentThemeId != theme.id) {
                            Log.d("Settings", "Применяем тему: ${theme.id}")
                            currentThemeId = theme.id
                            scope.launch { ThemeManager.saveCurrentTheme(context, theme.id) }
                        }
                    }
                ) {
                    Text(text = theme.emoji, fontSize = 20.sp)
                }
            }

            if (availableThemes.size == 1) {
                SettingsItem(
                    icon = Icons.Default.Celebration,
                    title = "Нет выигранных тем",
                    subtitle = "Покрутите рулетку в разделе «О приложении» → пасхалка",
                    onClick = {}
                )
            }

            SectionHeader("Календарь")

            SettingsItem(
                icon = Icons.Default.Celebration,
                title = "Показывать праздники",
                subtitle = if (showHolidays) "Включено" else "Выключено",
                onClick = {
                    showHolidays = !showHolidays
                    scope.launch { ThemeManager.saveShowHolidays(context, showHolidays) }
                }
            ) {
                Switch(checked = showHolidays, onCheckedChange = {})
            }

            SettingsItem(
                icon = Icons.Default.Language,
                title = "Начало недели",
                subtitle = "Понедельник",
                onClick = {}
            )

            SectionHeader("Заметки")
            SettingsItem(
                icon = Icons.Default.Save,
                title = "Сохранение данных",
                subtitle = "Только в памяти — данные не сохраняются",
                onClick = {},
                enabled = false
            )

            SectionHeader("О приложении")
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Версия",
                subtitle = "1.0.0-dev",
                onClick = {}
            )
            SettingsItem(
                icon = Icons.Default.BugReport,
                title = "Сообщить об ошибке",
                subtitle = "Отправить лог и комментарий",
                onClick = { showErrorReportDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SmartCalendar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        val now = System.currentTimeMillis()
                        if (now - lastClickTime > 1200) secretClicks = 0
                        lastClickTime = now
                        secretClicks++
                        if (secretClicks >= 3) {
                            secretClicks = 0
                            showRoulette = true
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun sendEmail(context: Context, recipient: String, subject: String, body: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Установите почтовый клиент", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (enabled) 0.5f else 0.25f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            if (trailing != null) {
                trailing()
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}