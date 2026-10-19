package com.example.ai

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

// --- Core Error & Result Types ---

enum class BasoAiErrorType {
  MISSING_CREDENTIALS,
  RATE_LIMITED,
  TIMEOUT,
  SERVICE_UNAVAILABLE,
  EMPTY_RESPONSE,
  INVALID_REQUEST,
}

sealed class BasoAiResult<out T> {
  data class Success<T>(
    val data: T,
    val modelUsed: String,
    val providerUsed: String = "Google Gemini",
    val isFailover: Boolean = false,
  ) : BasoAiResult<T>()

  data class Failure(
    val errorType: BasoAiErrorType,
    val safeMessage: String,
    val providerAttempted: String = "",
    val failoverAttempted: Boolean = false,
  ) : BasoAiResult<Nothing>()
}

enum class BasoAiConfigMode {
  BACKEND_PROXY,
  DIRECT_GEMINI_REST,
  DIRECT_OPENAI_REST,
  DUAL_ROUTER,
  UNCONFIGURED,
}

enum class BasoProviderType(val id: String, val displayName: String) {
  GEMINI("gemini", "Google Gemini"),
  OPENAI("openai", "OpenAI"),
}

enum class BasoConnectionStatus {
  UNCONFIGURED,
  UNTESTED,
  TESTING,
  CONNECTED,
  ERROR,
}

@Serializable
data class BasoProviderConfig(
  val providerType: BasoProviderType,
  val apiKey: String = "",
  val model: String = "",
  val endpointUrl: String = "",
  val status: BasoConnectionStatus = BasoConnectionStatus.UNTESTED,
  val statusMessage: String = "",
  val lastTestedTimestamp: Long = 0L,
) {
  val isConfigured: Boolean
    get() = apiKey.isNotBlank() && apiKey != GeminiAiProvider.PLACEHOLDER_KEY && !apiKey.startsWith("YOUR_")
}

@Serializable
data class BasoAiRoutingSettings(
  val primaryProvider: BasoProviderType = BasoProviderType.GEMINI,
  val automaticFailoverEnabled: Boolean = true,
  val timeoutSeconds: Int = 30,
  val maxRetries: Int = 1,
  val geminiConfig: BasoProviderConfig = BasoProviderConfig(
    providerType = BasoProviderType.GEMINI,
    model = GeminiAiProvider.DEFAULT_MODEL,
    endpointUrl = GeminiAiProvider.DEFAULT_ENDPOINT,
  ),
  val openAiConfig: BasoProviderConfig = BasoProviderConfig(
    providerType = BasoProviderType.OPENAI,
    model = OpenAiProvider.DEFAULT_MODEL,
    endpointUrl = OpenAiProvider.DEFAULT_ENDPOINT,
  ),
)

/**
 * Backward-compatible endpoint config for existing callers and tests.
 */
@Serializable
data class BasoAiEndpointConfig(
  val endpointUrl: String = GeminiAiProvider.DEFAULT_BACKEND_PROXY_URL,
  val defaultTextModel: String = GeminiAiProvider.MODEL_BASIC_TEXT,
  val defaultCodeModel: String = GeminiAiProvider.MODEL_COMPLEX_CODE,
  val bearerToken: String = "",
)

// --- Shared Wire Models ---

@Serializable
data class GeminiGenerateContentRequest(
  val contents: List<GeminiContent>,
  val generationConfig: GeminiGenerationConfig? = null,
  val systemInstruction: GeminiContent? = null,
)

@Serializable
data class GeminiContent(
  val role: String? = null,
  val parts: List<GeminiPart>,
)

@Serializable
data class GeminiInlineData(
  val mimeType: String,
  val data: String,
)

@Serializable
data class GeminiPart(
  val text: String? = null,
  val inlineData: GeminiInlineData? = null,
)

@Serializable
data class GeminiGenerationConfig(
  val responseMimeType: String? = null,
  val responseSchema: JsonObject? = null,
  val temperature: Float? = null,
)

@Serializable
data class GeminiGenerateContentResponse(
  val candidates: List<GeminiCandidate>? = null,
)

@Serializable
data class GeminiCandidate(
  val content: GeminiContent? = null,
)

@Serializable
data class GeminiErrorEnvelope(
  val error: GeminiErrorDetail? = null,
)

@Serializable
data class GeminiErrorDetail(
  val code: Int? = null,
  val message: String? = null,
  val status: String? = null,
)

// --- OpenAI Wire Models ---

@Serializable
data class OpenAiChatRequest(
  val model: String,
  val messages: List<OpenAiMessage>,
  val temperature: Float? = null,
  @SerialName("response_format") val responseFormat: OpenAiResponseFormat? = null,
)

@Serializable
data class OpenAiMessage(
  val role: String,
  val content: String,
)

@Serializable
data class OpenAiResponseFormat(
  val type: String = "json_object",
)

@Serializable
data class OpenAiChatResponse(
  val id: String? = null,
  val choices: List<OpenAiChoice>? = null,
  val usage: OpenAiUsage? = null,
)

@Serializable
data class OpenAiChoice(
  val index: Int? = null,
  val message: OpenAiMessage? = null,
  @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
data class OpenAiUsage(
  @SerialName("prompt_tokens") val promptTokens: Int? = null,
  @SerialName("completion_tokens") val completionTokens: Int? = null,
  @SerialName("total_tokens") val totalTokens: Int? = null,
)

@Serializable
data class OpenAiErrorEnvelope(
  val error: OpenAiErrorDetail? = null,
)

@Serializable
data class OpenAiErrorDetail(
  val message: String? = null,
  val type: String? = null,
  val code: String? = null,
)

// --- Provider Interface ---

/**
 * Universal AI provider abstraction for BASO AI.
 */
interface BasoAiProvider {
  val name: String get() = "AI Provider"
  val providerType: BasoProviderType? get() = null

  fun configurationMode(): BasoAiConfigMode
  fun isConfigured(): Boolean
  suspend fun testConnection(): BasoAiResult<String> =
    BasoAiResult.Success(data = "OK", modelUsed = "test-ping", providerUsed = name)

  suspend fun generateText(
    model: String,
    systemPrompt: String,
    contents: List<GeminiContent>,
    responseMimeType: String? = null,
    responseSchema: JsonObject? = null,
    temperature: Float = 0.6f,
  ): BasoAiResult<String>
}

// --- Provider Security & Redaction Helpers ---

object BasoAiSanitizer {
  fun sanitizeSecretFromMessage(raw: String?, vararg secrets: String?): String {
    if (raw.isNullOrBlank()) return "Unexpected network or service error."
    var cleaned =
      raw
        .replace(Regex("key=[^&\\s]+"), "key=REDACTED")
        .replace(Regex("AIza[0-9A-Za-z_-]{35}"), "REDACTED_API_KEY")
        .replace(Regex("sk-[0-9A-Za-z_-]{20,}"), "REDACTED_API_KEY")
        .replace(Regex("(?i)Bearer\\s+[A-Za-z0-9._-]+"), "Bearer REDACTED")
    for (secret in secrets) {
      if (!secret.isNullOrBlank() && secret.length > 3) {
        cleaned = cleaned.replace(secret, "REDACTED")
      }
    }
    return cleaned
  }
}

// --- Google Gemini Provider Adapter ---

class GeminiAiProvider(
  private val configProvider: () -> BasoProviderConfig = {
    BasoProviderConfig(
      providerType = BasoProviderType.GEMINI,
      model = DEFAULT_MODEL,
      endpointUrl = DEFAULT_ENDPOINT,
    )
  },
  private val customHttpClient: OkHttpClient? = null,
) : BasoAiProvider {

  override val name: String = "Google Gemini"
  override val providerType: BasoProviderType = BasoProviderType.GEMINI

  private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
    isLenient = true
  }

  private val defaultClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()
  }

  private fun getClient(timeoutSeconds: Int): OkHttpClient {
    if (customHttpClient != null) return customHttpClient
    return if (timeoutSeconds == 30) {
      defaultClient
    } else {
      defaultClient.newBuilder()
        .connectTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        .writeTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        .build()
    }
  }

  fun resolveBaseUrl(): String {
    val url = configProvider().endpointUrl.trim()
    val clean = if (url.isNotEmpty()) url else DEFAULT_ENDPOINT
    return if (clean.endsWith("/")) clean else "$clean/"
  }

  fun resolveModel(requestedModel: String): String {
    val cfg = configProvider()
    val configuredModel = cfg.model.trim()
    return when {
      configuredModel.isNotEmpty() -> configuredModel
      requestedModel.isNotBlank() -> requestedModel
      else -> DEFAULT_MODEL
    }
  }

  override fun configurationMode(): BasoAiConfigMode {
    val cfg = configProvider()
    val endpoint = cfg.endpointUrl.trim()
    if (endpoint.isNotEmpty() && !endpoint.contains("generativelanguage.googleapis.com")) {
      return BasoAiConfigMode.BACKEND_PROXY
    }
    return if (cfg.isConfigured) {
      BasoAiConfigMode.DIRECT_GEMINI_REST
    } else {
      BasoAiConfigMode.UNCONFIGURED
    }
  }

  override fun isConfigured(): Boolean {
    val cfg = configProvider()
    val endpoint = cfg.endpointUrl.trim()
    if (endpoint.isNotEmpty() && !endpoint.contains("generativelanguage.googleapis.com")) {
      return true
    }
    return cfg.isConfigured
  }

  override suspend fun testConnection(): BasoAiResult<String> {
    val testContent = listOf(
      GeminiContent(role = "user", parts = listOf(GeminiPart(text = "Hello, reply with 'OK'.")))
    )
    return generateText(
      model = resolveModel(DEFAULT_MODEL),
      systemPrompt = "You are a test ping responder. Reply briefly with 'OK'.",
      contents = testContent,
      temperature = 0.1f,
    )
  }

  override suspend fun generateText(
    model: String,
    systemPrompt: String,
    contents: List<GeminiContent>,
    responseMimeType: String?,
    responseSchema: JsonObject?,
    temperature: Float,
  ): BasoAiResult<String> = withContext(Dispatchers.IO) {
    val cfg = configProvider()
    val apiKey = cfg.apiKey.trim()
    val isProxy = resolveBaseUrl() != DEFAULT_ENDPOINT && !resolveBaseUrl().contains("generativelanguage.googleapis.com")

    if (!isConfigured()) {
      return@withContext BasoAiResult.Failure(
        errorType = BasoAiErrorType.MISSING_CREDENTIALS,
        safeMessage = "کلیلی Google Gemini دانەنراوە. تکایە لە بەشی ڕێکخستنەکان کلیلی API دابنێ.",
        providerAttempted = name,
      )
    }

    if (contents.isEmpty() || contents.all { c -> c.parts.all { it.text.isNullOrBlank() && it.inlineData == null } }) {
      return@withContext BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Prompt cannot be empty.",
        providerAttempted = name,
      )
    }

    val effectiveModel = resolveModel(model)
    val requestBodyDto = GeminiGenerateContentRequest(
      contents = contents,
      systemInstruction = if (systemPrompt.isNotBlank()) {
        GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
      } else null,
      generationConfig = GeminiGenerationConfig(
        responseMimeType = responseMimeType,
        responseSchema = responseSchema,
        temperature = temperature,
      ),
    )

    val jsonPayload = json.encodeToString(GeminiGenerateContentRequest.serializer(), requestBodyDto)
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val body = jsonPayload.toRequestBody(mediaType)

    val baseUrl = resolveBaseUrl()
    // Standard Gemini endpoint: {baseUrl}v1beta/models/{model}:generateContent
    val targetUrl = if (isProxy) {
      "${baseUrl}v1beta/models/$effectiveModel:generateContent"
    } else {
      "${baseUrl}v1beta/models/$effectiveModel:generateContent?key=$apiKey"
    }

    val requestBuilder = Request.Builder()
      .url(targetUrl)
      .post(body)

    if (apiKey.isNotEmpty()) {
      requestBuilder.addHeader("x-goog-api-key", apiKey)
    }

    val client = getClient(30)
    try {
      client.newCall(requestBuilder.build()).execute().use { response ->
        val rawResponse = response.body?.string().orEmpty()
        if (!response.isSuccessful) {
          val errorDetail = parseGeminiError(rawResponse)
          return@withContext mapHttpError(response.code, errorDetail, apiKey)
        }

        val parsed = json.decodeFromString(GeminiGenerateContentResponse.serializer(), rawResponse)
        val text = parsed.candidates
          ?.firstOrNull()
          ?.content
          ?.parts
          ?.mapNotNull { it.text }
          ?.joinToString("\n")
          ?.trim()

        if (text.isNullOrBlank()) {
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.EMPTY_RESPONSE,
            safeMessage = "The Google Gemini model returned an empty response.",
            providerAttempted = name,
          )
        } else {
          BasoAiResult.Success(
            data = text,
            modelUsed = effectiveModel,
            providerUsed = name,
          )
        }
      }
    } catch (e: Exception) {
      mapExceptionSafely(e, apiKey)
    }
  }

  private fun parseGeminiError(raw: String): String {
    return try {
      val env = json.decodeFromString(GeminiErrorEnvelope.serializer(), raw)
      env.error?.message.orEmpty()
    } catch (_: Exception) {
      ""
    }
  }

  private fun mapHttpError(code: Int, errorMsg: String, apiKey: String): BasoAiResult.Failure {
    val cleanMsg = BasoAiSanitizer.sanitizeSecretFromMessage(errorMsg, apiKey)
    return when (code) {
      400 -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Google Gemini: Invalid request (HTTP 400). ${cleanMsg.ifEmpty { "Check model parameters." }}",
        providerAttempted = name,
      )
      401, 403 -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.MISSING_CREDENTIALS,
        safeMessage = "Google Gemini: Invalid or unauthorized API key (HTTP $code). ${cleanMsg.ifEmpty { "Check your Gemini API key in settings." }}",
        providerAttempted = name,
      )
      429 -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.RATE_LIMITED,
        safeMessage = "Google Gemini: Rate limit or quota exhausted (HTTP 429). ${cleanMsg.ifEmpty { "Check your plan and quota." }}",
        providerAttempted = name,
      )
      in 500..599 -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
        safeMessage = "Google Gemini service is temporarily unavailable (HTTP $code).",
        providerAttempted = name,
      )
      else -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
        safeMessage = "Google Gemini error (HTTP $code): $cleanMsg",
        providerAttempted = name,
      )
    }
  }

  private fun mapExceptionSafely(e: Exception, apiKey: String): BasoAiResult.Failure {
    return when (e) {
      is SocketTimeoutException -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.TIMEOUT,
        safeMessage = "Google Gemini request timed out.",
        providerAttempted = name,
      )
      is UnknownHostException -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
        safeMessage = "Cannot reach Google Gemini. Please check your network connection.",
        providerAttempted = name,
      )
      else -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
        safeMessage = BasoAiSanitizer.sanitizeSecretFromMessage(e.message, apiKey),
        providerAttempted = name,
      )
    }
  }

  companion object {
    const val DEFAULT_ENDPOINT = "https://generativelanguage.googleapis.com/"
    const val DEFAULT_BACKEND_PROXY_URL = ""
    const val DEFAULT_MODEL = "gemini-1.5-flash"
    const val MODEL_BASIC_TEXT = "gemini-1.5-flash"
    const val MODEL_COMPLEX_CODE = "gemini-2.0-flash"
    const val PLACEHOLDER_KEY = "MY_GEMINI_API_KEY"
  }
}

// --- OpenAI Provider Adapter ---

class OpenAiProvider(
  private val configProvider: () -> BasoProviderConfig = {
    BasoProviderConfig(
      providerType = BasoProviderType.OPENAI,
      model = DEFAULT_MODEL,
      endpointUrl = DEFAULT_ENDPOINT,
    )
  },
  private val customHttpClient: OkHttpClient? = null,
) : BasoAiProvider {

  override val name: String = "OpenAI"
  override val providerType: BasoProviderType = BasoProviderType.OPENAI

  private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
    isLenient = true
  }

  private val defaultClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()
  }

  private fun getClient(timeoutSeconds: Int): OkHttpClient {
    if (customHttpClient != null) return customHttpClient
    return if (timeoutSeconds == 30) {
      defaultClient
    } else {
      defaultClient.newBuilder()
        .connectTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        .writeTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        .build()
    }
  }

  fun resolveBaseUrl(): String {
    val url = configProvider().endpointUrl.trim()
    val clean = if (url.isNotEmpty()) url else DEFAULT_ENDPOINT
    return if (clean.endsWith("/")) clean else "$clean/"
  }

  fun resolveModel(requestedModel: String): String {
    val cfg = configProvider()
    val configuredModel = cfg.model.trim()
    return when {
      configuredModel.isNotEmpty() -> configuredModel
      requestedModel.isNotBlank() && !requestedModel.contains("gemini", ignoreCase = true) -> requestedModel
      else -> DEFAULT_MODEL
    }
  }

  override fun configurationMode(): BasoAiConfigMode {
    val cfg = configProvider()
    val endpoint = cfg.endpointUrl.trim()
    if (endpoint.isNotEmpty() && !endpoint.contains("api.openai.com")) {
      return BasoAiConfigMode.BACKEND_PROXY
    }
    return if (cfg.isConfigured) {
      BasoAiConfigMode.DIRECT_OPENAI_REST
    } else {
      BasoAiConfigMode.UNCONFIGURED
    }
  }

  override fun isConfigured(): Boolean {
    val cfg = configProvider()
    val endpoint = cfg.endpointUrl.trim()
    if (endpoint.isNotEmpty() && !endpoint.contains("api.openai.com")) {
      return true
    }
    return cfg.isConfigured
  }

  override suspend fun testConnection(): BasoAiResult<String> {
    val testContent = listOf(
      GeminiContent(role = "user", parts = listOf(GeminiPart(text = "Hello, reply with 'OK'.")))
    )
    return generateText(
      model = resolveModel(DEFAULT_MODEL),
      systemPrompt = "You are a test ping responder. Reply briefly with 'OK'.",
      contents = testContent,
      temperature = 0.1f,
    )
  }

  override suspend fun generateText(
    model: String,
    systemPrompt: String,
    contents: List<GeminiContent>,
    responseMimeType: String?,
    responseSchema: JsonObject?,
    temperature: Float,
  ): BasoAiResult<String> = withContext(Dispatchers.IO) {
    val cfg = configProvider()
    val apiKey = cfg.apiKey.trim()

    if (!isConfigured()) {
      return@withContext BasoAiResult.Failure(
        errorType = BasoAiErrorType.MISSING_CREDENTIALS,
        safeMessage = "کلیلی OpenAI دانەنراوە. تکایە لە بەشی ڕێکخستنەکان کلیلی API دابنێ.",
        providerAttempted = name,
      )
    }

    if (contents.isEmpty() || contents.all { c -> c.parts.all { it.text.isNullOrBlank() && it.inlineData == null } }) {
      return@withContext BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Prompt cannot be empty.",
        providerAttempted = name,
      )
    }

    val effectiveModel = resolveModel(model)

    // Convert contents into OpenAI messages
    val openAiMessages = buildList {
      if (systemPrompt.isNotBlank()) {
        add(OpenAiMessage(role = "system", content = systemPrompt))
      }
      for (content in contents) {
        val role = when (content.role?.lowercase()) {
          "model", "assistant" -> "assistant"
          else -> "user"
        }
        val text = content.parts.mapNotNull { it.text }.joinToString("\n").trim()
        if (text.isNotEmpty()) {
          add(OpenAiMessage(role = role, content = text))
        }
      }
    }

    val isJsonFormat = responseMimeType == "application/json" || responseSchema != null
    val requestDto = OpenAiChatRequest(
      model = effectiveModel,
      messages = openAiMessages,
      temperature = temperature,
      responseFormat = if (isJsonFormat) OpenAiResponseFormat(type = "json_object") else null,
    )

    val jsonPayload = json.encodeToString(OpenAiChatRequest.serializer(), requestDto)
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val body = jsonPayload.toRequestBody(mediaType)

    val baseUrl = resolveBaseUrl()
    val targetUrl = if (baseUrl.endsWith("chat/completions") || baseUrl.endsWith("chat/completions/")) {
      baseUrl
    } else {
      "${baseUrl}chat/completions"
    }

    val requestBuilder = Request.Builder()
      .url(targetUrl)
      .post(body)
      .addHeader("Authorization", if (apiKey.startsWith("Bearer ", ignoreCase = true)) apiKey else "Bearer $apiKey")
      .addHeader("Content-Type", "application/json")

    val client = getClient(30)
    try {
      client.newCall(requestBuilder.build()).execute().use { response ->
        val rawResponse = response.body?.string().orEmpty()
        if (!response.isSuccessful) {
          val errorDetail = parseOpenAiError(rawResponse)
          return@withContext mapHttpError(response.code, errorDetail, apiKey)
        }

        val parsed = json.decodeFromString(OpenAiChatResponse.serializer(), rawResponse)
        val text = parsed.choices?.firstOrNull()?.message?.content?.trim()

        if (text.isNullOrBlank()) {
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.EMPTY_RESPONSE,
            safeMessage = "The OpenAI model returned an empty response.",
            providerAttempted = name,
          )
        } else {
          BasoAiResult.Success(
            data = text,
            modelUsed = effectiveModel,
            providerUsed = name,
          )
        }
      }
    } catch (e: Exception) {
      mapExceptionSafely(e, apiKey)
    }
  }

  private fun parseOpenAiError(raw: String): String {
    return try {
      val env = json.decodeFromString(OpenAiErrorEnvelope.serializer(), raw)
      env.error?.message.orEmpty()
    } catch (_: Exception) {
      ""
    }
  }

  private fun mapHttpError(code: Int, errorMsg: String, apiKey: String): BasoAiResult.Failure {
    val cleanMsg = BasoAiSanitizer.sanitizeSecretFromMessage(errorMsg, apiKey)
    return when (code) {
      400 -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "OpenAI: Invalid request (HTTP 400). ${cleanMsg.ifEmpty { "Check model parameters or message structure." }}",
        providerAttempted = name,
      )
      401, 403 -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.MISSING_CREDENTIALS,
        safeMessage = "OpenAI: Invalid or unauthorized API key (HTTP $code). ${cleanMsg.ifEmpty { "Check your OpenAI API key in settings." }}",
        providerAttempted = name,
      )
      429 -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.RATE_LIMITED,
        safeMessage = "OpenAI: Rate limit or quota exhausted (HTTP 429). ${cleanMsg.ifEmpty { "Check your OpenAI account plan and credits." }}",
        providerAttempted = name,
      )
      in 500..599 -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
        safeMessage = "OpenAI service is temporarily unavailable (HTTP $code).",
        providerAttempted = name,
      )
      else -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
        safeMessage = "OpenAI error (HTTP $code): $cleanMsg",
        providerAttempted = name,
      )
    }
  }

  private fun mapExceptionSafely(e: Exception, apiKey: String): BasoAiResult.Failure {
    return when (e) {
      is SocketTimeoutException -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.TIMEOUT,
        safeMessage = "OpenAI request timed out.",
        providerAttempted = name,
      )
      is UnknownHostException -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
        safeMessage = "Cannot reach OpenAI. Please check your network connection.",
        providerAttempted = name,
      )
      else -> BasoAiResult.Failure(
        errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
        safeMessage = BasoAiSanitizer.sanitizeSecretFromMessage(e.message, apiKey),
        providerAttempted = name,
      )
    }
  }

  companion object {
    const val DEFAULT_ENDPOINT = "https://api.openai.com/v1/"
    const val DEFAULT_MODEL = "gpt-4o-mini"
    const val MODEL_BASIC_TEXT = "gpt-4o-mini"
    const val MODEL_COMPLEX_CODE = "gpt-4o"
  }
}

// --- Central AI Router with Automatic Failover ---

class BasoAiRouter(
  private val settingsProvider: () -> BasoAiRoutingSettings,
  val geminiProvider: BasoAiProvider = GeminiAiProvider(configProvider = { settingsProvider().geminiConfig }),
  val openAiProvider: BasoAiProvider = OpenAiProvider(configProvider = { settingsProvider().openAiConfig }),
) : BasoAiProvider {

  override val name: String = "BASO AI Router"

  fun getPrimaryProvider(): BasoAiProvider {
    val settings = settingsProvider()
    return when (settings.primaryProvider) {
      BasoProviderType.GEMINI -> geminiProvider
      BasoProviderType.OPENAI -> openAiProvider
    }
  }

  fun getFallbackProvider(): BasoAiProvider {
    val settings = settingsProvider()
    return when (settings.primaryProvider) {
      BasoProviderType.GEMINI -> openAiProvider
      BasoProviderType.OPENAI -> geminiProvider
    }
  }

  override fun configurationMode(): BasoAiConfigMode {
    val geminiReady = geminiProvider.isConfigured()
    val openAiReady = openAiProvider.isConfigured()
    return when {
      geminiReady && openAiReady -> BasoAiConfigMode.DUAL_ROUTER
      geminiReady -> geminiProvider.configurationMode()
      openAiReady -> openAiProvider.configurationMode()
      else -> BasoAiConfigMode.UNCONFIGURED
    }
  }

  override fun isConfigured(): Boolean {
    return geminiProvider.isConfigured() || openAiProvider.isConfigured()
  }

  override suspend fun testConnection(): BasoAiResult<String> {
    val primary = getPrimaryProvider()
    return primary.testConnection()
  }

  suspend fun testProvider(providerType: BasoProviderType): BasoAiResult<String> {
    return when (providerType) {
      BasoProviderType.GEMINI -> geminiProvider.testConnection()
      BasoProviderType.OPENAI -> openAiProvider.testConnection()
    }
  }

  override suspend fun generateText(
    model: String,
    systemPrompt: String,
    contents: List<GeminiContent>,
    responseMimeType: String?,
    responseSchema: JsonObject?,
    temperature: Float,
  ): BasoAiResult<String> {
    val settings = settingsProvider()
    val primary = getPrimaryProvider()
    val fallback = getFallbackProvider()
    val failoverEnabled = settings.automaticFailoverEnabled
    val maxRetries = settings.maxRetries.coerceIn(0, 3)

    // 1. Check if primary is configured. If not, check if fallback can serve as primary.
    if (!primary.isConfigured()) {
      if (failoverEnabled && fallback.isConfigured()) {
        // Transparently route to configured fallback provider
        val fallbackResult = fallback.generateText(
          model = model,
          systemPrompt = systemPrompt,
          contents = contents,
          responseMimeType = responseMimeType,
          responseSchema = responseSchema,
          temperature = temperature,
        )
        return when (fallbackResult) {
          is BasoAiResult.Success -> fallbackResult.copy(
            providerUsed = fallback.name,
            isFailover = true,
          )
          is BasoAiResult.Failure -> fallbackResult.copy(
            providerAttempted = "${primary.name} (unconfigured) -> ${fallback.name}",
            failoverAttempted = true,
          )
        }
      }
      return BasoAiResult.Failure(
        errorType = BasoAiErrorType.MISSING_CREDENTIALS,
        safeMessage = "خزمەتگوزاریی زیرەکی دەستکرد (AI) ڕێکنەخراوە. تکایە لە بەشی ڕێکخستنەکانی دابینکەری AI کلیلی Gemini یان OpenAI دابنێ.",
        providerAttempted = primary.name,
      )
    }

    // 2. Execute against primary provider with bounded retries for transient errors
    var primaryResult: BasoAiResult<String>? = null
    var attempts = 0
    while (attempts <= maxRetries) {
      val res = primary.generateText(
        model = model,
        systemPrompt = systemPrompt,
        contents = contents,
        responseMimeType = responseMimeType,
        responseSchema = responseSchema,
        temperature = temperature,
      )
      primaryResult = res
      if (res is BasoAiResult.Success) {
        return res.copy(providerUsed = primary.name, isFailover = false)
      }
      val failure = res as BasoAiResult.Failure
      // Only retry transient errors
      val isTransient = failure.errorType == BasoAiErrorType.TIMEOUT ||
        failure.errorType == BasoAiErrorType.RATE_LIMITED ||
        failure.errorType == BasoAiErrorType.SERVICE_UNAVAILABLE

      if (!isTransient || attempts == maxRetries) {
        break
      }
      attempts++
      delay(300L * attempts)
    }

    val primaryFailure = primaryResult as? BasoAiResult.Failure
      ?: return BasoAiResult.Failure(BasoAiErrorType.SERVICE_UNAVAILABLE, "Request failed unexpectedly.")

    // 3. Evaluate Failover Eligibility
    // Do NOT failover on invalid input (HTTP 400) or client refusal (Requirement 3.5)
    val isFatalClientError = primaryFailure.errorType == BasoAiErrorType.INVALID_REQUEST
    val canFailover = failoverEnabled && fallback.isConfigured() && !isFatalClientError

    if (!canFailover) {
      return primaryFailure.copy(
        providerAttempted = primary.name,
        failoverAttempted = false,
      )
    }

    // 4. Execute Failover Request against secondary provider
    val fallbackResult = fallback.generateText(
      model = model,
      systemPrompt = systemPrompt,
      contents = contents,
      responseMimeType = responseMimeType,
      responseSchema = responseSchema,
      temperature = temperature,
    )

    return when (fallbackResult) {
      is BasoAiResult.Success -> {
        fallbackResult.copy(
          providerUsed = fallback.name,
          isFailover = true,
        )
      }
      is BasoAiResult.Failure -> {
        BasoAiResult.Failure(
          errorType = fallbackResult.errorType,
          safeMessage = "هەردوو دابینکەر شکستیان هێنا. ${primary.name}: ${primaryFailure.safeMessage} | ${fallback.name}: ${fallbackResult.safeMessage}",
          providerAttempted = "${primary.name} -> ${fallback.name}",
          failoverAttempted = true,
        )
      }
    }
  }
}

// --- Backward-Compatible GeminiRestAiProvider Wrapper ---

class GeminiRestAiProvider(
  private val apiKeyProvider: () -> String = { GeminiAiProvider.DEFAULT_BACKEND_PROXY_URL },
  private val backendUrlProvider: () -> String = { GeminiAiProvider.DEFAULT_BACKEND_PROXY_URL },
  private val endpointConfigProvider: () -> BasoAiEndpointConfig = {
    BasoAiEndpointConfig(endpointUrl = backendUrlProvider())
  },
  customApi: Any? = null,
) : BasoAiProvider {

  override val name: String = "Google Gemini"
  override val providerType: BasoProviderType = BasoProviderType.GEMINI

  private val delegate = GeminiAiProvider(
    configProvider = {
      val cfg = endpointConfigProvider()
      BasoProviderConfig(
        providerType = BasoProviderType.GEMINI,
        apiKey = apiKeyProvider(),
        model = cfg.defaultTextModel,
        endpointUrl = cfg.endpointUrl.ifEmpty { backendUrlProvider() },
      )
    }
  )

  fun resolveBaseUrl(): String = delegate.resolveBaseUrl()

  fun resolveModelName(requestedModel: String): String {
    val cfg = endpointConfigProvider()
    return when (requestedModel) {
      MODEL_BASIC_TEXT -> cfg.defaultTextModel.trim().ifEmpty { MODEL_BASIC_TEXT }
      MODEL_COMPLEX_CODE -> cfg.defaultCodeModel.trim().ifEmpty { MODEL_COMPLEX_CODE }
      else -> requestedModel.trim().ifEmpty { MODEL_BASIC_TEXT }
    }
  }

  override fun configurationMode(): BasoAiConfigMode = delegate.configurationMode()

  override fun isConfigured(): Boolean = delegate.isConfigured()

  override suspend fun testConnection(): BasoAiResult<String> = delegate.testConnection()

  override suspend fun generateText(
    model: String,
    systemPrompt: String,
    contents: List<GeminiContent>,
    responseMimeType: String?,
    responseSchema: JsonObject?,
    temperature: Float,
  ): BasoAiResult<String> = delegate.generateText(
    model = model,
    systemPrompt = systemPrompt,
    contents = contents,
    responseMimeType = responseMimeType,
    responseSchema = responseSchema,
    temperature = temperature,
  )

  companion object {
    const val GEMINI_BASE_URL = GeminiAiProvider.DEFAULT_ENDPOINT
    const val DEFAULT_BACKEND_PROXY_URL = ""
    const val DEFAULT_NO_CLIENT_API_KEY = ""
    const val PLACEHOLDER_KEY = GeminiAiProvider.PLACEHOLDER_KEY
    const val MODEL_BASIC_TEXT = GeminiAiProvider.MODEL_BASIC_TEXT
    const val MODEL_COMPLEX_CODE = GeminiAiProvider.MODEL_COMPLEX_CODE

    fun sanitizeSecretFromMessage(raw: String?, secret: String?): String =
      BasoAiSanitizer.sanitizeSecretFromMessage(raw, secret)

    fun mapExceptionSafely(e: Throwable, secret: String?): BasoAiResult.Failure {
      val msg = e.message.orEmpty()
      return when {
        e is java.net.SocketTimeoutException ->
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.TIMEOUT,
            safeMessage = "Request timed out while waiting for the AI service.",
          )
        e is java.net.UnknownHostException ->
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
            safeMessage = "Network connection unavailable. Check your internet connection or endpoint URL.",
          )
        msg.contains("429") || msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
          msg.contains("quota", ignoreCase = true) ->
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.RATE_LIMITED,
            safeMessage = "Rate limit or quota reached. Please wait a moment and try again.",
          )
        else ->
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
            safeMessage = sanitizeSecretFromMessage(e.message, secret),
          )
      }
    }
  }
}
