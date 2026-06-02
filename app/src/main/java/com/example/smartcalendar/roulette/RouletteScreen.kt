package com.example.smartcalendar.roulette

<<<<<<< HEAD




import android.util.Log





import com.example.smartcalendar.utils.ThemeManager

import com.example.smartcalendar.utils.LogCollector

import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

=======
>>>>>>> 3a25146faef926a66f560b189c2c33352113cbb6
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

//данные одной награды
private data class Reward(
    val name: String,
    val emoji: String,
    val color: Color
)

//список всех наград
private val rewards = listOf(
    Reward("Мятный градиент",   "🌿", Color(0xFF4CAF93)),
    Reward("Песочный фон",      "🏜️", Color(0xFFD4A96A)),
    Reward("Серый жемчуг",      "🩶", Color(0xFF9E9E9E)),
    Reward("Лавандовый туман",  "💜", Color(0xFF9C7BB8)),
    Reward("Утреннее небо",     "🌅", Color(0xFF5B9BD5)),
    Reward("Рисовая бумага",    "📜", Color(0xFFE8D5B0)),
    Reward("Закат на море",     "🌊", Color(0xFFFF7043)),
    Reward("Туманный лес",      "🌲", Color(0xFF5C8A6B)),
    Reward("Северное сияние",   "🌌", Color(0xFF26C6DA)),
    Reward("Мрамор",            "🪨", Color(0xFFBDBDBD)),
    Reward("Космос",            "🚀", Color(0xFF3F3F7A)),
    Reward("Цветущая сакура",   "🌸", Color(0xFFE91E8C)),
    Reward("Дымка над горами",  "⛰️", Color(0xFF78909C)),
    Reward("Живой градиент",    "✨", Color(0xFFAB47BC)),
    Reward("Карточка локации",  "📍", Color(0xFF26A69A)),
    Reward("Анимация старта",   "🎬", Color(0xFFEF5350)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteScreen(onClose: () -> Unit) {
<<<<<<< HEAD

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

=======
>>>>>>> 3a25146faef926a66f560b189c2c33352113cbb6
    var isSpinning by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<Reward?>(null) } //null пока не крутили
    var spinCount by remember { mutableStateOf(0) }
    var shownIndex by remember { mutableStateOf(0) } //текущий индекс при прокрутке

    //бесконечная анимация вращения (0 -> 360 по кругу)
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing)
        ),
        label = "rotation"
    )

    //запускается когда isSpinning меняется на true
    LaunchedEffect(isSpinning) {
        if (!isSpinning) return@LaunchedEffect

        val totalTicks = 20 + Random.nextInt(15) //случайное кол-во шагов
        repeat(totalTicks) { tick ->
            shownIndex = (shownIndex + 1) % rewards.size
            //в конце замедляемся: задержка растёт
            val pause = if (tick < totalTicks - 5) 80L else 80L + tick * 20L
            delay(pause)
        }

<<<<<<< HEAD
        val wonReward = rewards[shownIndex]
        result = wonReward
        Log.d("Roulette", "Выиграна награда: ${wonReward.name}")
        LogCollector.addLog("Roulette", "Выиграна награда: ${wonReward.name}")
        coroutineScope.launch {
            val themeId = when (wonReward.name) {
                "Цветущая сакура" -> "sakura"
                "Мятный градиент" -> "mint"
                "Лавандовый туман" -> "lavender"
                "Закат на море" -> "sunset"
                else -> null
            }
            Log.d("Roulette", "Соответствующий themeId = $themeId")
            LogCollector.addLog("Roulette", "Соответствующий themeId = $themeId")
            themeId?.let { ThemeManager.unlockTheme(context, it) }
        }
        isSpinning = false
        spinCount++

=======
        result = rewards[shownIndex] //фиксируем итоговую награду
        isSpinning = false
        spinCount++
>>>>>>> 3a25146faef926a66f560b189c2c33352113cbb6
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Колесо фортуны", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            //круглый барабан
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    )
                    .border(
                        width = 3.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.primary
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isSpinning -> {
                        //во время прокрутки: emoji крутится
                        Text(
                            text = rewards[shownIndex].emoji,
                            fontSize = 64.sp,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                    result != null -> {
                        //результат: emoji + название
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = result!!.emoji, fontSize = 52.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = result!!.name,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    else -> {
                        //начальное состояние до первого спина
                        Text(text = "🎰", fontSize = 72.sp)
                    }
                }
            }

            //информационная плашка под барабаном
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isSpinning -> Text(
                        text = "Крутим барабан...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    result != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Вы выиграли тему:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result!!.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = result!!.color //цвет самой награды
                        )
                        //счётчик показываем только со второго спина
                        if (spinCount > 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Прокрутов: $spinCount",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                            )
                        }
                    }
                    else -> Text(
                        text = "Нажмите «Крутить» — и удача улыбнётся!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            //кнопка запуска
            Button(
                onClick = {
                    result = null
                    isSpinning = true
                },
                enabled = !isSpinning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isSpinning) "Крутится..." else "🎲  Крутить",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("Закрыть")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
