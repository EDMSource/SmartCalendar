package com.example.smartcalendar.utils

import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object CloudflareReporter {
    private const val WORKER_URL = "https://telegram-report-proxy.nikitaivanin822.workers.dev/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun sendBugReport(
        context: Context,
        comment: String,
        logFile: File?
    ): ReportResult = withContext(Dispatchers.IO) {
        try {
            if (logFile == null || !logFile.exists()) {
                return@withContext ReportResult.Error("нет логов для отправки")
            }

            val logs = logFile.readText().take(3000) //обрезаем чтобы не было слишком длинно
            val device = "${Build.MANUFACTURER} ${Build.MODEL}"
            val androidVersion = "${Build.VERSION.RELEASE} (api ${Build.VERSION.SDK_INT})"
            val appVersion = getAppVersion(context)
            val timestamp = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())

            val json = JSONObject().apply {
                put("timestamp", timestamp)
                put("device", "$device, android $androidVersion")
                put("version", appVersion)
                put("comment", comment)
                put("logs", logs)
            }

            val request = Request.Builder()
                .url(WORKER_URL)
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            Log.d("CloudflareReporter", "response code: ${response.code}")
            Log.d("CloudflareReporter", "response body: $responseBody")

            if (response.isSuccessful) {
                logFile.delete()
                ReportResult.Success
            } else {
                ReportResult.Error("ошибка: ${response.code}")
            }

        } catch (e: Exception) {
            Log.e("CloudflareReporter", "ошибка отправки", e)
            ReportResult.Error(e.message ?: "неизвестная ошибка")
        }
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.longVersionCode})"
        } catch (e: Exception) {
            "unknown"
        }
    }

    sealed class ReportResult {
        object Success : ReportResult()
        data class Error(val message: String) : ReportResult()
    }
}