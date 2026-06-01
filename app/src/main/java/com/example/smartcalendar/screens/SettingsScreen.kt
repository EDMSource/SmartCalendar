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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcalendar.roulette.RouletteScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onClose: () -> Unit) {
    var showRoulette by remember { mutableStateOf(false) }
    var secretClicks by remember { mutableStateOf(0) } //счётчик тайных кликов
    var lastClickTime by remember { mutableStateOf(0L) } //время последнего клика

    //если открыли рулетку - показываем её вместо настроек
    if (showRoulette) {
        RouletteScreen(onClose = { showRoulette = false })
        return
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

            //раздел внешний вид
            SectionHeader("Внешний вид")

            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Тема",
                subtitle = "Системная (авто)",
                onClick = { /*TODO*/ }
            ) {
                //маленький чип-бейдж справа
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "Авто",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            //раздел календарь
            SectionHeader("Календарь")

            SettingsItem(
                icon = Icons.Default.Language,
                title = "Начало недели",
                subtitle = "Понедельник",
                onClick = { /*TODO*/ }
            )

            SettingsItem(
                icon = Icons.Default.Celebration,
                title = "Показывать праздники",
                subtitle = "Государственные праздники РФ",
                onClick = { /*TODO*/ }
            ) {
                var checked by remember { mutableStateOf(true) }
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
            }

            //раздел заметки
            SectionHeader("Заметки")

            SettingsItem(
                icon = Icons.Default.Save,
                title = "Сохранение данных",
                subtitle = "Только в памяти — данные не сохраняются",
                onClick = { /*TODO: добавить DataStore или Room*/ },
                enabled = false //пока не реализовано
            )

            //раздел о приложении
            SectionHeader("О приложении")

            SettingsItem(
                icon = Icons.Default.Info,
                title = "Версия",
                subtitle = "1.0.0-dev",
                onClick = { }
            )

            SettingsItem(
                icon = Icons.Default.BugReport,
                title = "Сообщить об ошибке",
                subtitle = "Открыть форму обратной связи",
                onClick = { /*TODO*/ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            //пасхалка: три быстрых клика на подпись открывают рулетку
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
                        indication = null //убираем стандартную рябь
                    ) {
                        val now = System.currentTimeMillis()
                        //если пауза больше 1.2с — сброс счётчика
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

//заголовок раздела настроек
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

//одна строка в списке настроек
@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null //элемент справа: стрелка, свитч, чип
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(
            alpha = if (enabled) 0.5f else 0.25f //серее если недоступно
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //иконка в круглом фоне
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

            //текст название и подпись
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (enabled) 1f else 0.4f
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            //справа либо переданный элемент, либо стрелка по умолчанию
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