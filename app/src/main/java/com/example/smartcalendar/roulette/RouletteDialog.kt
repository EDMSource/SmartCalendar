package com.example.smartcalendar.roulette

import android.app.AlertDialog
import android.content.Context
import android.widget.TextView
import kotlin.random.Random

class RouletteDialog(private val context: Context) {

    private val rewards = listOf(
        "мятный градиент", "песочный фон", "серый жемчуг", "лавандовый туман",
        "утреннее небо", "рисовая бумага", "закат на море", "туманный лес",
        "северное сияние", "мрамор", "космос", "цветущая сакура",
        "дымка над горами", "живой градиент", "карточка локации", "анимация старта"
    )

    fun show() {
        val textView = TextView(context)
        textView.text = "нажмите 'крутить'"
        textView.textSize = 18f
        textView.setPadding(50, 50, 50, 50)

        val dialog = AlertDialog.Builder(context)
            .setTitle("колесо фортуны")
            .setView(textView)
            .setPositiveButton("крутить") { _, _ ->
                val reward = rewards.random()
                textView.text = "вы выиграли: $reward"
            }
            .setNegativeButton("закрыть", null)
            .create()

        dialog.show()
    }
}