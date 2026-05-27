package com.example.smartcalendar.screens


import androidx.compose.ui.graphics.Color

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

    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var currentDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val notes = remember { mutableStateMapOf<String, MutableList<String>>() } // заметки в памяти

    var managingDay by remember { mutableStateOf<Int?>(null) }
    var showDayPicker by remember { mutableStateOf(false) }
    var pickedDay by remember { mutableStateOf<Int?>(null) }

    val today = LocalDate.now()
    val daysInMonth = currentDate.lengthOfMonth()
    val firstDayOfWeek = currentDate.dayOfWeek.value // 1=Пн, 7=Вс
    val monthName = currentDate.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru")))

    val holidays = remember {
        mapOf(
            // 1 января - Новый год
            Pair(1, 1) to "НГ",
            // 31 декабря - Новый год (подпись тоже "НГ")
            Pair(12, 31) to "НГ",
            // 7 января - Рождество
            Pair(1, 7) to "Рож",
            // 23 февраля - День защитника Отечества
            Pair(2, 23) to "23Ф",
            // 8 марта - Международный женский день
            Pair(3, 8) to "8М",
            // 1 мая - Праздник Весны и Труда
            Pair(5, 1) to "1М",
            // 9 мая - День Победы
            Pair(5, 9) to "9М",
            // 12 июня - День России
            Pair(6, 12) to "12И",
            // 4 ноября - День народного единства
            Pair(11, 4) to "4Н"
        )
    }

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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = monthName.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { showSearchDialog = true }) {
                    Text("Поиск заметок", fontSize = 20.sp)
                }
            }

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

            val prevMonth = currentDate.minusMonths(1)
            val nextMonth = currentDate.plusMonths(1)

// Дни предыдущего месяца, которые попадают в сетку
            val daysFromPrevMonth = (firstDayOfWeek - 1).let { offset ->
                if (offset > 0) {
                    val daysInPrev = prevMonth.lengthOfMonth()
                    (daysInPrev - offset + 1..daysInPrev).map { it }
                } else emptyList()
            }

// Дни текущего месяца
            val daysFromCurrentMonth = (1..daysInMonth).toList()

// Сколько дней следующего месяца нужно, чтобы заполнить 42 ячейки (6 строк × 7 дней)
            val totalCellsNeeded = 42
            val filledCount = daysFromPrevMonth.size + daysFromCurrentMonth.size
            val daysFromNextMonth = (1..(totalCellsNeeded - filledCount)).toList()

            // Собираем все дни с флагом "isCurrentMonth"
            data class CalendarDay(val day: Int, val monthValue: Int, val year: Int, val isCurrentMonth: Boolean)

            val allDays = mutableListOf<CalendarDay>()

            daysFromPrevMonth.forEach { day ->
                allDays.add(CalendarDay(day, prevMonth.monthValue, prevMonth.year, false))
            }
            daysFromCurrentMonth.forEach { day ->
                allDays.add(CalendarDay(day, currentDate.monthValue, currentDate.year, true))
            }
            daysFromNextMonth.forEach { day ->
                allDays.add(CalendarDay(day, nextMonth.monthValue, nextMonth.year, false))
            }

// Отображаем все 42 ячейки
            items(allDays.size) { index ->
                val calDay = allDays[index]
                val day = calDay.day
                val isToday = calDay.isCurrentMonth &&
                        calDay.year == today.year &&
                        calDay.monthValue == today.monthValue &&
                        day == today.dayOfMonth

                val key = if (calDay.isCurrentMonth) {
                    "${currentDate.year}-${currentDate.monthValue}-$day"
                } else null

                val holiday = holidays[Pair(calDay.monthValue, calDay.day)]

                if (calDay.isCurrentMonth) {
                    DayCell(
                        day = day,
                        isToday = isToday,
                        hasNote = notes[key]?.isNotEmpty() == true,
                        onClick = { managingDay = day },
                        holidayName = holiday
                    )
                } else {
                    InactiveDayCell(
                        day = day,
                        isToday = false,
                        holidayName = holiday
                    )
                }
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        // кнопка добавления заметки
        Button(
            onClick = { showDayPicker = true },
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

    if (showDayPicker) {
        AlertDialog(
            onDismissRequest = { showDayPicker = false; pickedDay = null },
            title = { Text("Выберите день") },
            text = {
                OutlinedTextField(
                    value = pickedDay?.toString() ?: "",
                    onValueChange = { pickedDay = it.toIntOrNull() },
                    label = { Text("День от 1 до $daysInMonth") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pickedDay != null && pickedDay!! in 1..daysInMonth) {
                        managingDay = pickedDay
                        showDayPicker = false
                        pickedDay = null
                    }
                }) { Text("Далее") }
            },
            dismissButton = {
                TextButton(onClick = { showDayPicker = false; pickedDay = null }) { Text("Отмена") }
            }
        )
    }

// диалог управления заметками (добавление/удаление/просмотр)
    if (managingDay != null) {
        val dateKey = "${currentDate.year}-${currentDate.monthValue}-${managingDay!!}"
        val dayNotes = notes[dateKey] ?: mutableListOf()
        var newNoteText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { managingDay = null },
            title = { Text("Заметки на $managingDay ${monthName.replaceFirstChar { it.uppercase() }}") },
            text = {
                Column {
                    // список существующих заметок
                    dayNotes.forEachIndexed { idx, note ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = note, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                val updated = dayNotes.toMutableList().apply { removeAt(idx) }
                                if (updated.isEmpty()) notes.remove(dateKey)
                                else notes[dateKey] = updated
                            }) {
                                Text("Удалить", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // поле для новой заметки
                    OutlinedTextField(
                        value = newNoteText,
                        onValueChange = { newNoteText = it },
                        label = { Text("Новая заметка") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (newNoteText.isNotBlank()) {
                                val currentList = notes[dateKey] ?: mutableListOf()
                                currentList.add(newNoteText)
                                notes[dateKey] = currentList
                                newNoteText = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { managingDay = null }) { Text("Готово") }
            }
        )

    }

    // Диалог поиска заметок — теперь снаружи, на одном уровне
    if (showSearchDialog) {
        var results by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

        LaunchedEffect(searchQuery) {
            if (searchQuery.isNotBlank()) {
                results = notes.flatMap { (dateKey, notesList) ->
                    notesList.filter { note -> note.contains(searchQuery, ignoreCase = true) }
                        .map { note -> dateKey to note }
                }
            } else {
                results = emptyList()
            }
        }

        AlertDialog(
            onDismissRequest = {
                showSearchDialog = false
                searchQuery = ""
            },
            title = { Text("Поиск заметок") },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Найти заметку") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (searchQuery.isNotBlank()) {
                        if (results.isEmpty()) {
                            Text("Ничего не найдено")
                        } else {
                            Column {
                                results.forEach { (dateKey, noteText) ->
                                    val parts = dateKey.split("-")
                                    val year = parts[0].toInt()
                                    val month = parts[1].toInt()
                                    val day = parts[2].toInt()
                                    val date = LocalDate.of(year, month, day)
                                    val formattedDate = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru")))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                showSearchDialog = false
                                                searchQuery = ""
                                                currentDate = LocalDate.of(year, month, 1)
                                                managingDay = day
                                            }
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = noteText, fontWeight = FontWeight.Medium)
                                            Text(text = formattedDate, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchDialog = false; searchQuery = "" }) {
                    Text("Закрыть")
                }
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
    onClick: () -> Unit,
    holidayName: String? = null   // <-- добавить эту строку
) {
    // Определяем цвет фона: если праздник – жёлтый, иначе стандартный
    val bgColor = when {
        holidayName != null -> Color.Yellow
        isToday -> TodayColor
        else -> MaterialTheme.colorScheme.primaryContainer
    }
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
            if (hasNote) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (isToday) TodayOnColor else NoteIndicator)
                )
            }
            // Отображаем подпись праздника под цифрой
            if (holidayName != null) {
                Text(
                    text = holidayName,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = txtColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun InactiveDayCell(
    day: Int,
    isToday: Boolean,
    holidayName: String? = null   // <-- добавить
) {
    // Фон: если праздник – жёлтый, иначе полупрозрачный серый
    val bgColor = if (holidayName != null) Color.Yellow
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val txtColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .padding(3.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$day",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = txtColor
            )
            if (holidayName != null) {
                Text(
                    text = holidayName,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = txtColor.copy(alpha = 0.8f)
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