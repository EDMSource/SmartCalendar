package com.example.smartcalendar.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.example.smartcalendar.roulette.RouletteScreen
import com.example.smartcalendar.utils.ThemeManager
import com.example.smartcalendar.utils.LogCollector
import com.example.smartcalendar.utils.CloudflareReporter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
                darkMode = it
                Log.d("Settings", "darkMode = $it")
                LogCollector.addLog("Settings", "darkMode = $it")
            }
        }
        launch {
            ThemeManager.getCurrentThemeFlow(context).collect {
                currentThemeId = it
                Log.d("Settings", "currentThemeId = $it")
                LogCollector.addLog("Settings", "currentThemeId = $it")
            }
        }
        launch {
            ThemeManager.getUnlockedThemesFlow(context).collect {
                unlockedThemes = it
                Log.d("Settings", "unlockedThemes = $it")
                LogCollector.addLog("Settings", "unlockedThemes = $it")
            }
        }
        launch {
            ThemeManager.getShowHolidaysFlow(context).collect {
                showHolidays = it
                Log.d("Settings", "showHolidays = $it")
                LogCollector.addLog("Settings", "showHolidays = $it")
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
                Log.d("Settings", "обновлено после рулетки: $fresh")
            }
        })
        return
    }

    if (showErrorReportDialog) {
        var isSending by remember { mutableStateOf(false) }
        var sendResult by remember { mutableStateOf<String?>(null) }
        val logsCount = LogCollector.getLogs().split("\n").filter { it.isNotBlank() }.size

        AlertDialog(
            onDismissRequest = {
                if (!isSending) {
                    showErrorReportDialog = false
                    errorReportText = ""
                    sendResult = null
                }
            },
            title = { Text("сообщить об ошибке") },
            text = {
                Column {
                    if (sendResult != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (sendResult == "отправлено! спасибо!")
                                Color(0xFF4CAF50).copy(alpha = 0.15f)
                            else
                                Color(0xFFF44336).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = sendResult!!,
                                modifier = Modifier.padding(12.dp),
                                color = if (sendResult == "отправлено! спасибо!")
                                    Color(0xFF4CAF50)
                                else
                                    Color(0xFFF44336)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "отправляется анонимно в telegram\nлоги будут отправлены файлом .txt",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("опишите проблему (необязательно):")
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = errorReportText,
                        onValueChange = { errorReportText = it },
                        label = { Text("что случилось?") },
                        minLines = 2,
                        enabled = !isSending,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("логов в файле: $logsCount", fontSize = 12.sp)
                        Text("формат: .txt", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "файл будет отправлен в telegram после нажатия кнопки",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                if (!isSending && sendResult == null) {
                    Button(
                        onClick = {
                            isSending = true
                            scope.launch {
                                val logFile = LogCollector.exportLogsToFile(context) //сохраняем логи в файл

                                val success = CloudflareReporter.sendBugReport(
                                    context = context,
                                    comment = errorReportText,
                                    logFile = logFile
                                ) //отправляем

                                sendResult = if (success) {
                                    "отправлено! спасибо!" //успех
                                } else {
                                    "ошибка: не удалось отправить" //неудача
                                }

                                isSending = false

                                delay(2000) //ждём 2 секунды
                                if (sendResult == "отправлено! спасибо!") {
                                    showErrorReportDialog = false //закрываем диалог
                                    errorReportText = ""
                                    sendResult = null
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSending
                    ) {
                        if (isSending) {
                            Row(horizontalArrangement = Arrangement.Center) {
                                Text("отправка...")
                                Spacer(modifier = Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        } else {
                            Text("отправить файл с логами")
                        }
                    }
                } else if (sendResult == "отправлено! спасибо!") {
                    TextButton(onClick = {
                        showErrorReportDialog = false
                        errorReportText = ""
                        sendResult = null
                    }) {
                        Text("закрыть")
                    }
                }
            },
            dismissButton = {
                if (!isSending && sendResult == null) {
                    TextButton(onClick = {
                        showErrorReportDialog = false
                        errorReportText = ""
                        sendResult = null
                    }) {
                        Text("отмена")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("настройки", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "назад")
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
            SectionHeader("внешний вид")

            SettingsItem(
                icon = Icons.Default.Brightness6,
                title = "тёмная тема",
                subtitle = if (darkMode) "включена" else "выключена",
                onClick = {
                    darkMode = !darkMode
                    LogCollector.addLog("Settings", "тёмная тема переключена на $darkMode")
                    scope.launch { ThemeManager.saveDarkMode(context, darkMode) }
                }
            ) { Switch(checked = darkMode, onCheckedChange = {}) }

            SectionHeader("оформление")

            val availableThemes = remember(unlockedThemes) {
                ThemeManager.allThemes.filter { it.id == "default" || it.id in unlockedThemes }
            }

            availableThemes.forEach { theme ->
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = theme.name,
                    subtitle = if (currentThemeId == theme.id) "активна" else "нажмите чтобы применить",
                    onClick = {
                        if (currentThemeId != theme.id) {
                            Log.d("Settings", "применяем тему: ${theme.id}")
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
                    title = "нет выигранных тем",
                    subtitle = "покрутите рулетку в разделе «о приложении» → пасхалка",
                    onClick = {}
                )
            }

            SectionHeader("календарь")

            SettingsItem(
                icon = Icons.Default.Celebration,
                title = "показывать праздники",
                subtitle = if (showHolidays) "включено" else "выключено",
                onClick = {
                    showHolidays = !showHolidays
                    scope.launch { ThemeManager.saveShowHolidays(context, showHolidays) }
                }
            ) {
                Switch(checked = showHolidays, onCheckedChange = {})
            }

            SettingsItem(
                icon = Icons.Default.Language,
                title = "начало недели",
                subtitle = "понедельник",
                onClick = {}
            )

            SectionHeader("заметки")

            SettingsItem(
                icon = Icons.Default.Save,
                title = "сохранение данных",
                subtitle = "только в памяти — данные не сохраняются",
                onClick = {},
                enabled = false
            )

            SectionHeader("о приложении")

            SettingsItem(
                icon = Icons.Default.Info,
                title = "версия",
                subtitle = "1.0.0-dev",
                onClick = {}
            )

            SettingsItem(
                icon = Icons.Default.BugReport,
                title = "сообщить об ошибке",
                subtitle = "отправить логи файлом в telegram",
                onClick = { showErrorReportDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "smartcalendar",
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