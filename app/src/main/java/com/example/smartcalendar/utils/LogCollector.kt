package com.example.smartcalendar.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object LogCollector {
    private val logBuffer = mutableListOf<String>()
    private val maxLogSize = 500

    fun addLog(tag: String, message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val logLine = "$timestamp $tag: $message"
        synchronized(logBuffer) {
            logBuffer.add(logLine)
            while (logBuffer.size > maxLogSize) logBuffer.removeAt(0)
        }
        Log.d("LogCollector", "Добавлено: $tag: $message")
    }

    fun getLogs(): String = synchronized(logBuffer) { logBuffer.joinToString("\n") }

    fun clearLogs() = synchronized(logBuffer) { logBuffer.clear() }

    fun exportLogsToFile(context: Context): File? {
        return synchronized(logBuffer) {
            if (logBuffer.isEmpty()) return null
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
                Log.d("LogCollector", "logs exported to ${file.absolutePath}")
                file
            } catch (e: Exception) {
                Log.e("LogCollector", "failed to export logs", e)
                null
            }
        }
    }
}