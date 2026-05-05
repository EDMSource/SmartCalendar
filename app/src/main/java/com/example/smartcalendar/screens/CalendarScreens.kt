package com.example.smartcalendar.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.smartcalendar.ui.theme.SmartCalendarTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalendarScreen() {
    var showAddDialog by remember { mutableStateOf(false) }
    var tempDay by remember { mutableStateOf<Int?>(null) }
    var viewDay by remember { mutableStateOf<Int?>(null) }
    var currentDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val monthName = currentDate.format(DateTimeFormatter.ofPattern("LLLL yyyy"))
    val daysInMonth = currentDate.lengthOfMonth()
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var noteInput by remember { mutableStateOf("") }
    val notes = remember { mutableStateMapOf<String, String>() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = monthName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp)
        ) {
            items(daysInMonth) { day ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .clickable { viewDay = day + 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${day + 1}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        val key = "${currentDate.year}-${currentDate.monthValue}-${day + 1}"
                        if (notes[key] != null) {
                            Text(
                                text = "З",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        if (selectedDay != null) {
            AlertDialog(
                onDismissRequest = { selectedDay = null },
                title = { Text("Заметка для $selectedDay") },
                text = {
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Текст") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (noteInput.isNotBlank()) {
                                val key = "${currentDate.year}-${currentDate.monthValue}-${selectedDay!!}"
                                notes[key] = noteInput
                                selectedDay = null
                                noteInput = ""
                            }
                        }
                    ) { Text("Сохранить") }
                },
                dismissButton = {
                    TextButton(onClick = { selectedDay = null }) { Text("Отмена") }
                }
            )
        }

        if (viewDay != null) {
            AlertDialog(
                onDismissRequest = { viewDay = null },
                title = { Text("Заметка для $viewDay") },
                text = {
                    val key = "${currentDate.year}-${currentDate.monthValue}-${viewDay}"
                    Text(notes[key] ?: "Нет заметки")
                },
                confirmButton = {
                    TextButton(onClick = { viewDay = null }) { Text("Закрыть") }
                }
            )
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Выберите день") },
                text = {
                    OutlinedTextField(
                        value = tempDay?.toString() ?: "",
                        onValueChange = { tempDay = it.toIntOrNull() },
                        label = { Text("День от 1 до $daysInMonth") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (tempDay != null && tempDay!! in 1..daysInMonth) {
                                showAddDialog = false
                                selectedDay = tempDay
                                val key = "${currentDate.year}-${currentDate.monthValue}-${tempDay}"
                                noteInput = notes[key] ?: ""
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

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { currentDate = currentDate.minusMonths(1) }) { Text("Назад") }
            Button(onClick = { currentDate = currentDate.plusMonths(1) }) { Text("Вперед") }
        }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить заметку")
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