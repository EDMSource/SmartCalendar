package com.example.smartcalendar.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object LogCollector {
    private val logBuffer = mutableListOf<String>() //буфер логов
    private val maxLogSize = 500 //максимум записей

    fun addLog(tag: String, message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val logLine = "$timestamp $tag: $message"
        synchronized(logBuffer) {
            logBuffer.add(logLine)
            while (logBuffer.size > maxLogSize) logBuffer.removeAt(0) //удаляем старые
        }
    }

    fun getLogs(): String = synchronized(logBuffer) { logBuffer.joinToString("\n") } //все логи строкой

    fun clearLogs() = synchronized(logBuffer) { logBuffer.clear() } //очистить

    fun exportLogsToFile(context: Context): File? {
        return synchronized(logBuffer) {
            if (logBuffer.isEmpty()) return null //нет логов
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "smartcalendar_logs_$timestamp.txt"
                val file = File(context.cacheDir, fileName)
                file.writeText(buildString {
                    appendLine("smartcalendar error report")
                    appendLine("date: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())}")
                    appendLine("logs count: ${logBuffer.size}")
                    appendLine("".padEnd(50, '='))
                    appendLine()
                    appendLine(getLogs())
                })
                file
            } catch (e: Exception) {
                null //ошибка сохранения
            }
        }
    }
}