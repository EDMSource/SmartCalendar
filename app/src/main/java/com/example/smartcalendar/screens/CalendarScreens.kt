package com.example.smartcalendar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcalendar.ui.theme.NoteIndicator
import com.example.smartcalendar.ui.theme.SmartCalendarTheme
import com.example.smartcalendar.ui.theme.TodayColor
import com.example.smartcalendar.ui.theme.TodayOnColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val WEEK_DAYS = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс") // заголовки столбцов

@Composable
fun CalendarScreen() {
    var showAddDialog by remember { mutableStateOf(false) }
    var tempDay       by remember { mutableStateOf<Int?>(null) }
    var viewDay       by remember { mutableStateOf<Int?>(null) }
    var currentDate   by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var selectedDay   by remember { mutableStateOf<Int?>(null) }
    var noteInput     by remember { mutableStateOf("") }
    val notes         = remember { mutableStateMapOf<String, String>() } // заметки в памяти

    val today         = LocalDate.now()
    val daysInMonth   = currentDate.lengthOfMonth()
    val firstDayOfWeek = currentDate.dayOfWeek.value // 1=Пн, 7=Вс
    val monthName     = currentDate.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru")))
    val totalCells    = (firstDayOfWeek - 1) + daysInMonth // всего ячеек в сетке

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // навигация: стрелки + название месяца
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { currentDate = currentDate.minusMonths(1) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("‹", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
            }

            Text(
                text = monthName.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = { currentDate = currentDate.plusMonths(1) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("›", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
            }
        }

        // заголовки дней недели
        Row(modifier = Modifier.fillMaxWidth()) {
            WEEK_DAYS.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // сетка дней
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // растягивается на всё свободное место
            userScrollEnabled = false
        ) {
            items(firstDayOfWeek - 1) { // пустые ячейки до первого числа
                Box(modifier = Modifier.aspectRatio(1f))
            }

            items(daysInMonth) { index ->
                val day = index + 1
                val isToday = currentDate.year == today.year
                        && currentDate.monthValue == today.monthValue
                        && day == today.dayOfMonth
                val key = "${currentDate.year}-${currentDate.monthValue}-$day"

                DayCell(
                    day     = day,
                    isToday = isToday,
                    hasNote = notes[key] != null,
                    onClick = { viewDay = day }
                )
            }

            val remainder = totalCells % 7
            if (remainder != 0) { // выравнивающие ячейки в конце строки
                items(7 - remainder) {
                    Box(modifier = Modifier.aspectRatio(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // кнопка добавления заметки
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "+ Добавить заметку",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // диалог выбора дня
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false; tempDay = null },
            title = { Text("Выберите день") },
            text = {
                OutlinedTextField(
                    value = tempDay?.toString() ?: "",
                    onValueChange = { tempDay = it.toIntOrNull() },
                    label = { Text("День от 1 до $daysInMonth") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tempDay != null && tempDay!! in 1..daysInMonth) {
                            val key = "${currentDate.year}-${currentDate.monthValue}-$tempDay"
                            noteInput = notes[key] ?: ""
                            selectedDay = tempDay
                            showAddDialog = false
                            tempDay = null
                        }
                    }
                ) { Text("Далее") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; tempDay = null }) { Text("Отмена") }
            }
        )
    }

    // диалог ввода заметки
    if (selectedDay != null) {
        AlertDialog(
            onDismissRequest = { selectedDay = null },
            title = { Text("Заметка для $selectedDay ${monthName.replaceFirstChar { it.uppercase() }}") },
            text = {
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Текст заметки") },
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (noteInput.isNotBlank()) {
                            val key = "${currentDate.year}-${currentDate.monthValue}-${selectedDay!!}"
                            notes[key] = noteInput // сохраняем заметку
                        }
                        selectedDay = null
                        noteInput = ""
                    }
                ) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { selectedDay = null; noteInput = "" }) { Text("Отмена") }
            }
        )
    }

    // диалог просмотра заметки
    if (viewDay != null) {
        val key = "${currentDate.year}-${currentDate.monthValue}-$viewDay"
        AlertDialog(
            onDismissRequest = { viewDay = null },
            title = { Text("$viewDay ${monthName.replaceFirstChar { it.uppercase() }}") },
            text = {
                Text(
                    text = notes[key] ?: "Нет заметки",
                    color = if (notes[key] != null)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                if (notes[key] != null) {
                    TextButton(onClick = {
                        noteInput = notes[key] ?: ""
                        selectedDay = viewDay
                        viewDay = null
                    }) { Text("Изменить") }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewDay = null }) { Text("Закрыть") }
            }
        )
    }
}

// ячейка одного дня
@Composable
fun DayCell(
    day: Int,
    isToday: Boolean,
    hasNote: Boolean,
    onClick: () -> Unit
) {
    val bgColor  = if (isToday) TodayColor else MaterialTheme.colorScheme.primaryContainer // фон ячейки
    val txtColor = if (isToday) TodayOnColor else MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        modifier = Modifier
            .padding(3.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$day",
                fontSize = 15.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = txtColor
            )
            if (hasNote) { // точка-индикатор заметки
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (isToday) TodayOnColor else NoteIndicator)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCalendarScreen() {
    SmartCalendarTheme {
        CalendarScreen()
    }
}