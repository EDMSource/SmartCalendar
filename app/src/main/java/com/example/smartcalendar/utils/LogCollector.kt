package com.example.smartcalendar.utils

import android.util.Log
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
}