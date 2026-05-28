package com.example.smartcalendar.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
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
fun CalendarScreen(onOpenSettings: () -> Unit = {}) {
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery      by remember { mutableStateOf("") }
    var showDayPicker    by remember { mutableStateOf(false) }
    var pickedDay        by remember { mutableStateOf<Int?>(null) }
    var managingDay      by remember { mutableStateOf<Int?>(null) }
    var currentDate      by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val notes            = remember { mutableStateMapOf<String, MutableList<String>>() } // заметки в памяти

    val today          = LocalDate.now()
    val daysInMonth    = currentDate.lengthOfMonth()
    val firstDayOfWeek = currentDate.dayOfWeek.value // 1=Пн, 7=Вс
    val monthName      = currentDate.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("ru")))

    val holidays = remember {
        mapOf(
            Pair(1, 1)   to "НГ",   // новый год
            Pair(12, 31) to "НГ",
            Pair(1, 7)   to "Рож",  // рождество
            Pair(2, 23)  to "23Ф",  // день защитника
            Pair(3, 8)   to "8М",   // женский день
            Pair(5, 1)   to "1М",   // праздник труда
            Pair(5, 9)   to "9М",   // день победы
            Pair(6, 12)  to "12И",  // день россии
            Pair(11, 4)  to "4Н"    // день народного единства
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // навигация: стрелки + название месяца + поиск + настройки
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
                    Text("🔍", fontSize = 20.sp)
                }
            }

            Row {
                Button(
                    onClick = { currentDate = currentDate.plusMonths(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("›", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "настройки")
                }
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

            // дни предыдущего месяца для заполнения начала сетки
            val daysFromPrevMonth = if (firstDayOfWeek > 1) {
                val daysInPrev = prevMonth.lengthOfMonth()
                (daysInPrev - (firstDayOfWeek - 2)..daysInPrev).toList()
            } else emptyList()

            val daysFromCurrentMonth = (1..daysInMonth).toList()

            // дни следующего месяца для заполнения до 42 ячеек
            val filledCount = daysFromPrevMonth.size + daysFromCurrentMonth.size
            val daysFromNextMonth = (1..(42 - filledCount)).toList()

            data class CalendarDay(val day: Int, val monthValue: Int, val year: Int, val isCurrentMonth: Boolean)

            val allDays = buildList {
                daysFromPrevMonth.forEach { add(CalendarDay(it, prevMonth.monthValue, prevMonth.year, false)) }
                daysFromCurrentMonth.forEach { add(CalendarDay(it, currentDate.monthValue, currentDate.year, true)) }
                daysFromNextMonth.forEach { add(CalendarDay(it, nextMonth.monthValue, nextMonth.year, false)) }
            }

            items(allDays.size) { index ->
                val calDay = allDays[index]
                val isToday = calDay.isCurrentMonth &&
                        calDay.year == today.year &&
                        calDay.monthValue == today.monthValue &&
                        calDay.day == today.dayOfMonth
                val key = "${calDay.year}-${calDay.monthValue}-${calDay.day}"
                val holiday = holidays[Pair(calDay.monthValue, calDay.day)]

                if (calDay.isCurrentMonth) {
                    DayCell(
                        day         = calDay.day,
                        isToday     = isToday,
                        hasNote     = notes[key]?.isNotEmpty() == true,
                        onClick     = { managingDay = calDay.day },
                        holidayName = holiday
                    )
                } else {
                    InactiveDayCell(day = calDay.day, holidayName = holiday)
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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "+ Добавить заметку",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // диалог выбора дня
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

    // диалог управления заметками дня
    if (managingDay != null) {
        val dateKey  = "${currentDate.year}-${currentDate.monthValue}-${managingDay!!}"
        val dayNotes = notes[dateKey] ?: mutableListOf()
        var newNoteText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { managingDay = null },
            title = { Text("Заметки на $managingDay ${monthName.replaceFirstChar { it.uppercase() }}") },
            text = {
                Column {
                    // список существующих заметок с кнопкой удаления
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
                    // поле новой заметки
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
                                val list = notes.getOrPut(dateKey) { mutableListOf() }
                                list.add(newNoteText)
                                newNoteText = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Добавить") }
                }
            },
            confirmButton = {
                TextButton(onClick = { managingDay = null }) { Text("Готово") }
            }
        )
    }

    // диалог поиска заметок
    if (showSearchDialog) {
        var results by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

        LaunchedEffect(searchQuery) {
            results = if (searchQuery.isNotBlank()) {
                notes.flatMap { (dateKey, list) ->
                    list.filter { it.contains(searchQuery, ignoreCase = true) }
                        .map { dateKey to it }
                }
            } else emptyList()
        }

        AlertDialog(
            onDismissRequest = { showSearchDialog = false; searchQuery = "" },
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
                            results.forEach { (dateKey, noteText) ->
                                val parts = dateKey.split("-")
                                val date  = LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                                val formatted = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru")))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showSearchDialog = false
                                            searchQuery = ""
                                            currentDate = date.withDayOfMonth(1)
                                            managingDay = date.dayOfMonth
                                        }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = noteText, fontWeight = FontWeight.Medium)
                                        Text(text = formatted, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchDialog = false; searchQuery = "" }) { Text("Закрыть") }
            }
        )
    }
}

// ячейка текущего месяца
@Composable
fun DayCell(
    day: Int,
    isToday: Boolean,
    hasNote: Boolean,
    onClick: () -> Unit,
    holidayName: String? = null // название праздника если есть
) {
    val bgColor  = when {
        holidayName != null -> Color.Yellow // праздник — жёлтый
        isToday             -> TodayColor
        else                -> MaterialTheme.colorScheme.primaryContainer
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
            if (hasNote) { // точка-индикатор заметки
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (isToday) TodayOnColor else NoteIndicator)
                )
            }
            if (holidayName != null) { // подпись праздника
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

// ячейка соседнего месяца (серая, некликабельная)
@Composable
fun InactiveDayCell(
    day: Int,
    holidayName: String? = null
) {
    val bgColor  = if (holidayName != null) Color.Yellow
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
            Text(text = "$day", fontSize = 15.sp, color = txtColor)
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