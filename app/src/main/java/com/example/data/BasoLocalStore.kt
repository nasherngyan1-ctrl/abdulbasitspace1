package com.example.data

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.BasoProjectRecord
import com.example.ai.BasoAiChatTurn
import com.example.ai.BasoAiEndpointConfig
import com.example.ai.BasoAiRoutingSettings
import com.example.ai.BasoKeyCipher
import com.example.ai.BasoProviderType
import com.example.builder.BasoBuilderProject
import com.example.ui.screens.BasoChatMessage
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.BasoIconBlue
import com.example.ui.theme.BasoIconIndigo
import com.example.ui.theme.BasoIconOrange
import com.example.ui.theme.BasoIconPurple
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

/**
 * Lightweight device-local persistence store backed by Android SharedPreferences.
 * Persists user-created projects, chat messages, AI assistant conversation turns,
 * and language/display settings across screen navigation, Activity recreation, and full restarts.
 */
class BasoLocalStore(context: Context) {
  private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
  }

  fun loadLanguage(): AppLanguage {
    val code = prefs.getString(KEY_LANGUAGE, AppLanguage.KURDISH_SORANI.code)
    return AppLanguage.entries.firstOrNull { it.code == code } ?: AppLanguage.KURDISH_SORANI
  }

  fun saveLanguage(language: AppLanguage) {
    prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
  }

  fun loadHighContrast(): Boolean = prefs.getBoolean(KEY_HIGH_CONTRAST, false)

  fun saveHighContrast(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
  }

  fun loadSpaciousDensity(): Boolean = prefs.getBoolean(KEY_SPACIOUS_DENSITY, true)

  fun saveSpaciousDensity(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_SPACIOUS_DENSITY, enabled).apply()
  }

  fun loadCustomProjects(): List<BasoProjectRecord> {
    val raw = prefs.getString(KEY_CUSTOM_PROJECTS, null) ?: return emptyList()
    return try {
      val array = JSONArray(raw)
      buildList {
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          val categoryIndex = obj.optInt("categoryIndex", 3)
          val (icon, color) = categoryVisualsForIndex(categoryIndex)
          add(
            BasoProjectRecord(
              id = obj.optString("id", "custom_$i"),
              title = obj.optString("title", ""),
              subtitle = obj.optString("subtitle", ""),
              statusType = obj.optString("statusType", "IN_PROGRESS"),
              progressPercent = obj.optInt("progressPercent", 15),
              categoryIndex = categoryIndex,
              icon = icon,
              iconColor = color,
            ),
          )
        }
      }
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun saveCustomProjects(projects: List<BasoProjectRecord>) {
    val array = JSONArray()
    projects.forEach { proj ->
      val obj =
        JSONObject().apply {
          put("id", proj.id)
          put("title", proj.title)
          put("subtitle", proj.subtitle)
          put("statusType", proj.statusType)
          put("progressPercent", proj.progressPercent)
          put("categoryIndex", proj.categoryIndex)
        }
      array.put(obj)
    }
    prefs.edit().putString(KEY_CUSTOM_PROJECTS, array.toString()).apply()
  }

  fun loadChatMessages(): List<BasoChatMessage> {
    val raw = prefs.getString(KEY_CHAT_MESSAGES, null) ?: return defaultChatMessages()
    return try {
      val array = JSONArray(raw)
      if (array.length() == 0) return defaultChatMessages()
      buildList {
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          add(
            BasoChatMessage(
              id = obj.optString("id", "m_$i"),
              text = obj.optString("text", ""),
              time = obj.optString("time", "10:26"),
              isOutgoing = obj.optBoolean("isOutgoing", true),
            ),
          )
        }
      }
    } catch (_: Exception) {
      defaultChatMessages()
    }
  }

  fun saveChatMessages(messages: List<BasoChatMessage>) {
    val array = JSONArray()
    messages.forEach { msg ->
      val obj =
        JSONObject().apply {
          put("id", msg.id)
          put("text", msg.text)
          put("time", msg.time)
          put("isOutgoing", msg.isOutgoing)
        }
      array.put(obj)
    }
    prefs.edit().putString(KEY_CHAT_MESSAGES, array.toString()).apply()
  }

  fun loadAiChatHistory(): List<BasoAiChatTurn> {
    val raw = prefs.getString(KEY_AI_CHAT_HISTORY, null) ?: return emptyList()
    return try {
      val array = JSONArray(raw)
      buildList {
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          add(
            BasoAiChatTurn(
              id = obj.optString("id", "ai_$i"),
              role = obj.optString("role", "user"),
              text = obj.optString("text", ""),
              timestampLabel = obj.optString("timestampLabel", "ئێستا"),
            ),
          )
        }
      }
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun saveAiChatHistory(turns: List<BasoAiChatTurn>) {
    val array = JSONArray()
    turns.forEach { turn ->
      val obj =
        JSONObject().apply {
          put("id", turn.id)
          put("role", turn.role)
          put("text", turn.text)
          put("timestampLabel", turn.timestampLabel)
        }
      array.put(obj)
    }
    prefs.edit().putString(KEY_AI_CHAT_HISTORY, array.toString()).apply()
  }

  fun clearAiChatHistory() {
    prefs.edit().remove(KEY_AI_CHAT_HISTORY).apply()
  }

  fun loadBuilderProjects(): List<BasoBuilderProject> {
    val raw = prefs.getString(KEY_BUILDER_PROJECTS, null) ?: return emptyList()
    return try {
      json.decodeFromString<List<BasoBuilderProject>>(raw)
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun saveBuilderProjects(projects: List<BasoBuilderProject>) {
    try {
      val encoded = json.encodeToString(projects)
      prefs.edit().putString(KEY_BUILDER_PROJECTS, encoded).apply()
    } catch (_: Exception) {
      // Ignore serialization error
    }
  }

  fun loadActiveBuilderProjectId(): String? = prefs.getString(KEY_ACTIVE_BUILDER_PROJECT_ID, null)

  fun saveActiveBuilderProjectId(projectId: String?) {
    prefs.edit().putString(KEY_ACTIVE_BUILDER_PROJECT_ID, projectId).apply()
  }

  fun loadAiEndpointConfig(): BasoAiEndpointConfig {
    val raw = prefs.getString(KEY_AI_ENDPOINT_CONFIG, null) ?: return BasoAiEndpointConfig()
    return try {
      json.decodeFromString<BasoAiEndpointConfig>(raw)
    } catch (_: Exception) {
      BasoAiEndpointConfig()
    }
  }

  fun saveAiEndpointConfig(config: BasoAiEndpointConfig) {
    try {
      val encoded = json.encodeToString(config)
      prefs.edit().putString(KEY_AI_ENDPOINT_CONFIG, encoded).apply()
    } catch (_: Exception) {
      // Ignore serialization error
    }
  }

  fun loadAiRoutingSettings(): BasoAiRoutingSettings {
    val raw = prefs.getString(KEY_AI_ROUTING_SETTINGS, null)
    if (raw.isNullOrBlank()) {
      return BasoAiRoutingSettings()
    }
    return try {
      val encryptedSettings = json.decodeFromString<BasoAiRoutingSettings>(raw)
      // Decrypt stored provider API keys
      encryptedSettings.copy(
        geminiConfig = encryptedSettings.geminiConfig.copy(
          apiKey = BasoKeyCipher.decrypt(encryptedSettings.geminiConfig.apiKey)
        ),
        openAiConfig = encryptedSettings.openAiConfig.copy(
          apiKey = BasoKeyCipher.decrypt(encryptedSettings.openAiConfig.apiKey)
        )
      )
    } catch (_: Exception) {
      BasoAiRoutingSettings()
    }
  }

  fun saveAiRoutingSettings(settings: BasoAiRoutingSettings) {
    try {
      // Encrypt sensitive provider API keys before persisting
      val secured = settings.copy(
        geminiConfig = settings.geminiConfig.copy(
          apiKey = BasoKeyCipher.encrypt(settings.geminiConfig.apiKey)
        ),
        openAiConfig = settings.openAiConfig.copy(
          apiKey = BasoKeyCipher.encrypt(settings.openAiConfig.apiKey)
        )
      )
      val encoded = json.encodeToString(secured)
      prefs.edit().putString(KEY_AI_ROUTING_SETTINGS, encoded).apply()

      // Keep legacy endpoint config in sync with active primary provider
      val primaryConfig = when (settings.primaryProvider) {
        BasoProviderType.GEMINI -> settings.geminiConfig
        BasoProviderType.OPENAI -> settings.openAiConfig
      }
      saveAiEndpointConfig(
        BasoAiEndpointConfig(
          endpointUrl = primaryConfig.endpointUrl,
          defaultTextModel = primaryConfig.model,
          defaultCodeModel = primaryConfig.model,
          bearerToken = primaryConfig.apiKey,
        )
      )
    } catch (_: Exception) {
      // Ignore serialization error
    }
  }

  fun resetSettingsToDefault() {
    prefs
      .edit()
      .putString(KEY_LANGUAGE, AppLanguage.KURDISH_SORANI.code)
      .putBoolean(KEY_HIGH_CONTRAST, false)
      .putBoolean(KEY_SPACIOUS_DENSITY, true)
      .apply()
  }

  fun clearAllForTesting() {
    prefs.edit().clear().commit()
  }

  companion object {
    const val PREFS_NAME = "baso_local_store"
    private const val KEY_LANGUAGE = "language_code"
    private const val KEY_HIGH_CONTRAST = "high_contrast_borders"
    private const val KEY_SPACIOUS_DENSITY = "spacious_density"
    private const val KEY_CUSTOM_PROJECTS = "custom_projects_json"
    private const val KEY_CHAT_MESSAGES = "chat_messages_json"
    private const val KEY_AI_CHAT_HISTORY = "ai_chat_history_json"
    private const val KEY_BUILDER_PROJECTS = "builder_projects_json"
    private const val KEY_ACTIVE_BUILDER_PROJECT_ID = "active_builder_project_id"
    private const val KEY_AI_ENDPOINT_CONFIG = "ai_endpoint_config_json"
    private const val KEY_AI_ROUTING_SETTINGS = "ai_routing_settings_json"

    fun defaultChatMessages(): List<BasoChatMessage> =
      listOf(
        BasoChatMessage(
          id = "m1",
          text = "سڵاو.. دەتوانیت بۆم وێبسایتێکی سادە و پێشکەوتوو دروست بکەیت؟",
          time = "10:24",
          isOutgoing = false,
        ),
        BasoChatMessage(
          id = "m2",
          text = "بەدڵنیایی.. دەمەوێت وێبسایتێکی پێشکەوتوو و سادە بۆت دروست بکەم.",
          time = "10:26",
          isOutgoing = true,
        ),
      )

    fun categoryVisualsForIndex(categoryIndex: Int): Pair<ImageVector, Color> =
      when (categoryIndex) {
        1 -> Icons.Default.Brush to BasoIconOrange
        2 -> Icons.Default.Smartphone to BasoIconIndigo
        3 -> Icons.Default.Language to BasoIconBlue
        4 -> Icons.Default.Videocam to BasoIconPurple
        else -> Icons.Default.Language to BasoIconBlue
      }
  }
}
