package com.example.smartcalendar.utils



import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



import android.util.Log

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Расширение для Context, создающее DataStore с именем "settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private val SHOW_HOLIDAYS_KEY = booleanPreferencesKey("show_holidays")

private val NOTES_KEY = stringPreferencesKey("notes_json")

@Stable
data class RewardTheme(
    val id: String,
    val name: String,
    val emoji: String,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val todayColor: Color,
    val noteIndicator: Color,
    val surfaceVariant: Color
)

object ThemeManager {
    // Ключи для DataStore
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    private val CURRENT_THEME_ID_KEY = stringPreferencesKey("current_theme_id")
    private val UNLOCKED_THEMES_KEY = stringSetPreferencesKey("unlocked_themes")
    private val SHOW_HOLIDAYS_KEY = booleanPreferencesKey("show_holidays")


    // Предопределённые темы (награды)
    val defaultTheme = RewardTheme(
        id = "default",
        name = "Стандартная",
        emoji = "🎨",
        primary = Color(0xFF6200EE),
        primaryContainer = Color(0xFFEADDFF),
        onPrimaryContainer = Color(0xFF1D192B),
        todayColor = Color(0xFF03DAC6),
        noteIndicator = Color(0xFF018786),
        surfaceVariant = Color(0xFFE7E0EC)
    )

    val sakuraTheme = RewardTheme(
        id = "sakura",
        name = "Сакура",
        emoji = "🌸",
        primary = Color(0xFFE91E8C),
        primaryContainer = Color(0xFFFFB7C5),
        onPrimaryContainer = Color(0xFF5A0D3A),
        todayColor = Color(0xFFFF80A5),
        noteIndicator = Color(0xFFC2185B),
        surfaceVariant = Color(0xFFFFE0E6)
    )

    val mintTheme = RewardTheme(
        id = "mint",
        name = "Мятный градиент",
        emoji = "🌿",
        primary = Color(0xFF4CAF93),
        primaryContainer = Color(0xFFB2DFDB),
        onPrimaryContainer = Color(0xFF004D40),
        todayColor = Color(0xFF80CBC4),
        noteIndicator = Color(0xFF00796B),
        surfaceVariant = Color(0xFFE0F2F1)
    )

    val lavenderTheme = RewardTheme(
        id = "lavender",
        name = "Лавандовый туман",
        emoji = "💜",
        primary = Color(0xFF9C7BB8),
        primaryContainer = Color(0xFFE8DAEF),
        onPrimaryContainer = Color(0xFF311B45),
        todayColor = Color(0xFFB49CC8),
        noteIndicator = Color(0xFF6A1B9A),
        surfaceVariant = Color(0xFFF3E5F5)
    )

    val sunsetTheme = RewardTheme(
        id = "sunset",
        name = "Закат на море",
        emoji = "🌊",
        primary = Color(0xFFFF7043),
        primaryContainer = Color(0xFFFFCCBC),
        onPrimaryContainer = Color(0xFF4E1A00),
        todayColor = Color(0xFFFF8A65),
        noteIndicator = Color(0xFFBF360C),
        surfaceVariant = Color(0xFFFFF3E0)
    )
    val sandTheme = RewardTheme(
        id = "sand",
        name = "Песочный фон",
        emoji = "🏜️",
        primary = Color(0xFFD4A96A),
        primaryContainer = Color(0xFFF5E6CC),
        onPrimaryContainer = Color(0xFF3E2A00),
        todayColor = Color(0xFFE8C07A),
        noteIndicator = Color(0xFF8D6E3A),
        surfaceVariant = Color(0xFFFFF8EE)
    )

    val grayPearlTheme = RewardTheme(
        id = "gray_pearl",
        name = "Серый жемчуг",
        emoji = "🩶",
        primary = Color(0xFF9E9E9E),
        primaryContainer = Color(0xFFE0E0E0),
        onPrimaryContainer = Color(0xFF212121),
        todayColor = Color(0xFFBDBDBD),
        noteIndicator = Color(0xFF616161),
        surfaceVariant = Color(0xFFF5F5F5)
    )

    val skyTheme = RewardTheme(
        id = "sky",
        name = "Утреннее небо",
        emoji = "🌅",
        primary = Color(0xFF5B9BD5),
        primaryContainer = Color(0xFFBBDEFB),
        onPrimaryContainer = Color(0xFF0D2B4E),
        todayColor = Color(0xFF90CAF9),
        noteIndicator = Color(0xFF1976D2),
        surfaceVariant = Color(0xFFE3F2FD)
    )

    val riceTheme = RewardTheme(
        id = "rice",
        name = "Рисовая бумага",
        emoji = "📜",
        primary = Color(0xFFBFA07A),
        primaryContainer = Color(0xFFF5E6CC),
        onPrimaryContainer = Color(0xFF3E2A00),
        todayColor = Color(0xFFD4B896),
        noteIndicator = Color(0xFF795548),
        surfaceVariant = Color(0xFFFAF3E0)
    )

    val fogForestTheme = RewardTheme(
        id = "fog_forest",
        name = "Туманный лес",
        emoji = "🌲",
        primary = Color(0xFF5C8A6B),
        primaryContainer = Color(0xFFC8E6C9),
        onPrimaryContainer = Color(0xFF1B3A28),
        todayColor = Color(0xFF81C784),
        noteIndicator = Color(0xFF388E3C),
        surfaceVariant = Color(0xFFE8F5E9)
    )

    val auroraTheme = RewardTheme(
        id = "aurora",
        name = "Северное сияние",
        emoji = "🌌",
        primary = Color(0xFF26C6DA),
        primaryContainer = Color(0xFFB2EBF2),
        onPrimaryContainer = Color(0xFF003E47),
        todayColor = Color(0xFF80DEEA),
        noteIndicator = Color(0xFF00838F),
        surfaceVariant = Color(0xFFE0F7FA)
    )

    val marbleTheme = RewardTheme(
        id = "marble",
        name = "Мрамор",
        emoji = "🪨",
        primary = Color(0xFF9E9E9E),
        primaryContainer = Color(0xFFF5F5F5),
        onPrimaryContainer = Color(0xFF1A1A1A),
        todayColor = Color(0xFFE0E0E0),
        noteIndicator = Color(0xFF757575),
        surfaceVariant = Color(0xFFFAFAFA)
    )

    val spaceTheme = RewardTheme(
        id = "space",
        name = "Космос",
        emoji = "🚀",
        primary = Color(0xFF7986CB),
        primaryContainer = Color(0xFF3F3F7A),
        onPrimaryContainer = Color(0xFFE8EAF6),
        todayColor = Color(0xFF9FA8DA),
        noteIndicator = Color(0xFF5C6BC0),
        surfaceVariant = Color(0xFF2A2A5A)
    )

    val mountainTheme = RewardTheme(
        id = "mountain",
        name = "Дымка над горами",
        emoji = "⛰️",
        primary = Color(0xFF78909C),
        primaryContainer = Color(0xFFCFD8DC),
        onPrimaryContainer = Color(0xFF1C2E35),
        todayColor = Color(0xFFB0BEC5),
        noteIndicator = Color(0xFF455A64),
        surfaceVariant = Color(0xFFECEFF1)
    )

    val liveGradientTheme = RewardTheme(
        id = "live_gradient",
        name = "Живой градиент",
        emoji = "✨",
        primary = Color(0xFFAB47BC),
        primaryContainer = Color(0xFFE1BEE7),
        onPrimaryContainer = Color(0xFF2A003E),
        todayColor = Color(0xFFCE93D8),
        noteIndicator = Color(0xFF7B1FA2),
        surfaceVariant = Color(0xFFF3E5F5)
    )




    val allThemes = listOf(
        defaultTheme, sakuraTheme, mintTheme, lavenderTheme, sunsetTheme,
        sandTheme, grayPearlTheme, skyTheme, riceTheme, fogForestTheme,
        auroraTheme, marbleTheme, spaceTheme, mountainTheme,
        liveGradientTheme,
    )


    fun getThemeById(id: String): RewardTheme = allThemes.find { it.id == id } ?: defaultTheme

    // Сохранение состояния тёмной темы

    suspend fun saveNotes(context: Context, notes: Map<String, List<String>>) {
        Log.d("ThemeManager", "saveNotes: $notes")
        val json = Gson().toJson(notes)
        context.dataStore.edit { prefs ->
            prefs[NOTES_KEY] = json
        }
    }

    fun getNotesFlow(context: Context): Flow<Map<String, List<String>>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[NOTES_KEY] ?: return@map emptyMap()
            try {
                val type = object : TypeToken<Map<String, List<String>>>() {}.type
                val result: Map<String, List<String>> = Gson().fromJson(json, type)
                result ?: emptyMap()
            } catch (e: Exception) {
                Log.e("ThemeManager", "Ошибка загрузки заметок", e)
                emptyMap()
            }
        }

    suspend fun saveDarkMode(context: Context, isDark: Boolean) {
        Log.d("ThemeManager", "saveDarkMode: $isDark")
        LogCollector.addLog("ThemeManager", "saveDarkMode: $isDark")
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDark
        }
    }


    fun getDarkModeFlow(context: Context): Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    // Сохранение выбранной темы оформления
    suspend fun saveCurrentTheme(context: Context, themeId: String) {
        Log.d("ThemeManager", "saveCurrentTheme: $themeId")
        LogCollector.addLog("ThemeManager", "saveCurrentTheme: $themeId")
        context.dataStore.edit { prefs ->
            prefs[CURRENT_THEME_ID_KEY] = themeId
        }
    }

    fun getCurrentThemeFlow(context: Context): Flow<String> = context.dataStore.data
        .map { prefs -> prefs[CURRENT_THEME_ID_KEY] ?: "default" }

    // Добавление выигранной темы (если ещё не разблокирована)
    suspend fun unlockTheme(context: Context, themeId: String) {
        Log.d("ThemeManager", "unlockTheme: $themeId")
        LogCollector.addLog("ThemeManager", "unlockTheme: $themeId")
        context.dataStore.edit { prefs ->
            val currentSet = prefs[UNLOCKED_THEMES_KEY]?.toMutableSet() ?: mutableSetOf()
            if (currentSet.add(themeId)) {
                prefs[UNLOCKED_THEMES_KEY] = currentSet
                Log.d("ThemeManager", "Theme $themeId unlocked")
                LogCollector.addLog("ThemeManager", "Theme $themeId unlocked")
            } else {
                Log.d("ThemeManager", "Theme $themeId already unlocked")
                LogCollector.addLog("ThemeManager", "Theme $themeId already unlocked")
            }
        }
    }

    suspend fun saveShowHolidays(context: Context, show: Boolean) {
        Log.d("ThemeManager", "saveShowHolidays: $show")
        LogCollector.addLog("ThemeManager", "saveShowHolidays: $show")
        context.dataStore.edit { prefs ->
            prefs[SHOW_HOLIDAYS_KEY] = show
        }
    }

    fun getUnlockedThemesFlow(context: Context): Flow<Set<String>> = context.dataStore.data
        .map { prefs -> prefs[UNLOCKED_THEMES_KEY] ?: emptySet() }

    fun getShowHolidaysFlow(context: Context): Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SHOW_HOLIDAYS_KEY] ?: true }
}