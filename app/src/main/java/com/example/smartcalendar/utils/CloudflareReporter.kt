package com.example.smartcalendar.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object CloudflareReporter {
    private const val WORKER_URL = "https://telegram-report-proxy.nikitaivanin822.workers.dev" //url воркера

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) //таймаут подключения
        .writeTimeout(30, TimeUnit.SECONDS) //таймаут отправки
        .readTimeout(30, TimeUnit.SECONDS) //таймаут чтения
        .build()

    suspend fun sendBugReport(context: Context, comment: String, logFile: File?): Boolean = withContext(Dispatchers.IO) {
        if (logFile == null || !logFile.exists()) return@withContext false //нет файла

        try {
            val logs = logFile.readText().take(3000) //читаем логи, обрезаем
            val device = "${Build.MANUFACTURER} ${Build.MODEL}" //модель телефона
            val androidVersion = "${Build.VERSION.RELEASE} (api ${Build.VERSION.SDK_INT})" //версия андроид
            val appVersion = getAppVersion(context) //версия приложения
            val timestamp = System.currentTimeMillis().toString() //время

            val json = JSONObject().apply {
                put("device", "$device, android $androidVersion")
                put("version", appVersion)
                put("comment", comment)
                put("logs", logs)
                put("timestamp", timestamp)
            }

            val request = Request.Builder()
                .url(WORKER_URL)
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val success = response.isSuccessful
            if (success) logFile.delete() //удаляем файл после отправки
            response.close()
            return@withContext success
        } catch (e: Exception) {
            return@withContext false //ошибка отправки
        }
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val pm = context.packageManager
            val packageName = context.packageName
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }
            info.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }
}