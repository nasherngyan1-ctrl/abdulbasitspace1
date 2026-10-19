package com.example.ai

import com.example.ui.theme.AppLanguage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

enum class BasoAiToolTab {
  CHAT,
  CODE,
  WEBSITE,
  DESIGN,
  PLANNING,
}

enum class BasoCodeTaskMode {
  GENERATE,
  EXPLAIN,
  BUG_ANALYSIS,
  REFACTOR,
  EXAMPLE,
}

enum class BasoDesignTaskMode {
  CONCEPT,
  COLOR_PALETTE,
  TYPOGRAPHY_LAYOUT,
  IMAGE_PROMPT,
  GRAPHIC_BRIEF,
  SOCIAL_IDEAS,
}

@Serializable
data class BasoAiChatTurn(
  val id: String,
  val role: String, // "user" or "model"
  val text: String,
  val timestampLabel: String,
)

@Serializable
data class BasoCodeAssistantOutput(
  val summary: String,
  val language: String,
  val code: String,
  val notes: String,
)

@Serializable
data class BasoWebsiteBundleOutput(
  val summary: String,
  val indexHtml: String,
  val stylesCss: String,
  val appJs: String,
  val securityNote: String,
)

@Serializable
data class BasoDesignAssistantOutput(
  val creativeDirection: String,
  val colorPaletteHex: List<String>,
  val typographyAndLayout: String,
  val imageGenerationPrompt: String,
  val deliverablesList: List<String>,
)

@Serializable
data class BasoProjectPlanOutput(
  val projectTitle: String,
  val objectives: List<String>,
  val milestones: List<String>,
  val tasks: List<String>,
  val estimatedEffort: String,
  val priorities: List<String>,
  val recommendedCategoryIndex: Int = 3,
)

/**
 * Multi-Tool AI Service for BASO Freelance Studio.
 * Delegates all network execution to [BasoAiProvider] and parses/validates responses for the 5 tools.
 */
class BasoAiService(
  private val provider: BasoAiProvider = GeminiRestAiProvider(),
) {
  private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
  }

  fun isConfigured(): Boolean = provider.isConfigured()

  fun configurationMode(): BasoAiConfigMode = provider.configurationMode()

  private fun languageDirective(language: AppLanguage): String =
    when (language) {
      AppLanguage.KURDISH_SORANI ->
        "Respond in natural, clear Kurdish Sorani (کوردیی سۆرانی). Keep code, HTML/CSS/JS syntax, and hex codes in standard technical form."
      AppLanguage.ARABIC ->
        "Respond in natural, clear Arabic (العربية). Keep code, HTML/CSS/JS syntax, and hex codes in standard technical form."
      AppLanguage.ENGLISH ->
        "Respond in clear, professional English."
    }

  /**
   * TOOL A: AI Chat Assistant
   * Sends conversation context + latest user prompt to Gemini 3.5 Flash.
   */
  suspend fun sendChatMessage(
    history: List<BasoAiChatTurn>,
    userMessage: String,
    language: AppLanguage,
  ): BasoAiResult<String> {
    val trimmed = userMessage.trim()
    if (trimmed.isEmpty()) {
      return BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Message cannot be empty.",
      )
    }

    val recentTurns = history.takeLast(10)
    val contents =
      buildList {
        recentTurns.forEach { turn ->
          add(
            GeminiContent(
              role = if (turn.role == "model") "model" else "user",
              parts = listOf(GeminiPart(text = turn.text)),
            ),
          )
        }
        add(
          GeminiContent(
            role = "user",
            parts = listOf(GeminiPart(text = trimmed)),
          ),
        )
      }

    val systemPrompt =
      "You are the BASO Freelance Studio AI Assistant. Provide concise, accurate, helpful answers for freelancers, designers, developers, and general users. ${languageDirective(language)}"

    return provider.generateText(
      model = GeminiRestAiProvider.MODEL_BASIC_TEXT,
      systemPrompt = systemPrompt,
      contents = contents,
      temperature = 0.65f,
    )
  }

  /**
   * TOOL B: AI Code Assistant
   * Supports Code Generation, Explanation, Bug Analysis, Refactoring, and Examples using Gemini 3.1 Pro (with fallback to 3.5 Flash).
   */
  suspend fun runCodeAssistant(
    taskDescription: String,
    mode: BasoCodeTaskMode,
    programmingLanguage: String,
    appLanguage: AppLanguage,
  ): BasoAiResult<BasoCodeAssistantOutput> {
    val trimmed = taskDescription.trim()
    if (trimmed.isEmpty()) {
      return BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Code task description cannot be empty.",
      )
    }

    val modeInstruction =
      when (mode) {
        BasoCodeTaskMode.GENERATE -> "Generate clean, well-structured, production-ready $programmingLanguage code for the requested task."
        BasoCodeTaskMode.EXPLAIN -> "Explain how the provided or described $programmingLanguage code works step by step, and include a clear annotated snippet."
        BasoCodeTaskMode.BUG_ANALYSIS -> "Analyze potential bugs, edge cases, and security issues in the described $programmingLanguage code and provide the fixed version."
        BasoCodeTaskMode.REFACTOR -> "Refactor the described $programmingLanguage code for readability, performance, and maintainability."
        BasoCodeTaskMode.EXAMPLE -> "Provide a practical, self-contained $programmingLanguage example demonstrating best practices for the topic."
      }

    val schema =
      buildJsonObject {
        put("type", "OBJECT")
        putJsonObject("properties") {
          putJsonObject("summary") { put("type", "STRING") }
          putJsonObject("language") { put("type", "STRING") }
          putJsonObject("code") { put("type", "STRING") }
          putJsonObject("notes") { put("type", "STRING") }
        }
        putJsonArray("required") {
          add(kotlinx.serialization.json.JsonPrimitive("summary"))
          add(kotlinx.serialization.json.JsonPrimitive("language"))
          add(kotlinx.serialization.json.JsonPrimitive("code"))
          add(kotlinx.serialization.json.JsonPrimitive("notes"))
        }
      }

    val systemPrompt =
      "You are the BASO AI Code Assistant. $modeInstruction Do not execute code. ${languageDirective(appLanguage)}"

    val primaryAttempt =
      provider.generateText(
        model = GeminiRestAiProvider.MODEL_COMPLEX_CODE,
        systemPrompt = systemPrompt,
        contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = trimmed)))),
        responseMimeType = "application/json",
        responseSchema = schema,
        temperature = 0.35f,
      )

    val rawResult =
      when (primaryAttempt) {
        is BasoAiResult.Success -> primaryAttempt
        is BasoAiResult.Failure -> {
          if (primaryAttempt.errorType == BasoAiErrorType.MISSING_CREDENTIALS ||
            primaryAttempt.errorType == BasoAiErrorType.INVALID_REQUEST
          ) {
            primaryAttempt
          } else {
            provider.generateText(
              model = GeminiRestAiProvider.MODEL_BASIC_TEXT,
              systemPrompt = systemPrompt,
              contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = trimmed)))),
              responseMimeType = "application/json",
              responseSchema = schema,
              temperature = 0.35f,
            )
          }
        }
      }

    return when (rawResult) {
      is BasoAiResult.Failure -> rawResult
      is BasoAiResult.Success -> {
        try {
          val parsed = json.decodeFromString<BasoCodeAssistantOutput>(rawResult.data)
          if (parsed.code.isBlank()) {
            BasoAiResult.Failure(
              errorType = BasoAiErrorType.EMPTY_RESPONSE,
              safeMessage = "The model response did not contain a valid code block.",
            )
          } else {
            BasoAiResult.Success(
              data = parsed,
              modelUsed = rawResult.modelUsed,
              providerUsed = rawResult.providerUsed,
              isFailover = rawResult.isFailover,
            )
          }
        } catch (_: Exception) {
          // Graceful fallback if model returned plain markdown instead of JSON
          BasoAiResult.Success(
            data =
              BasoCodeAssistantOutput(
                summary = programmingLanguage,
                language = programmingLanguage,
                code = rawResult.data,
                notes = "",
              ),
            modelUsed = rawResult.modelUsed,
            providerUsed = rawResult.providerUsed,
            isFailover = rawResult.isFailover,
          )
        }
      }
    }
  }

  /**
   * TOOL C: AI Website Generator
   * Generates separated HTML (index.html), CSS (styles.css), and JavaScript (app.js) files and validates non-empty output.
   */
  suspend fun generateWebsiteBundle(
    websiteDescription: String,
    appLanguage: AppLanguage,
  ): BasoAiResult<BasoWebsiteBundleOutput> {
    val trimmed = websiteDescription.trim()
    if (trimmed.isEmpty()) {
      return BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Website description cannot be empty.",
      )
    }

    val schema =
      buildJsonObject {
        put("type", "OBJECT")
        putJsonObject("properties") {
          putJsonObject("summary") { put("type", "STRING") }
          putJsonObject("indexHtml") { put("type", "STRING") }
          putJsonObject("stylesCss") { put("type", "STRING") }
          putJsonObject("appJs") { put("type", "STRING") }
          putJsonObject("securityNote") { put("type", "STRING") }
        }
        putJsonArray("required") {
          add(kotlinx.serialization.json.JsonPrimitive("summary"))
          add(kotlinx.serialization.json.JsonPrimitive("indexHtml"))
          add(kotlinx.serialization.json.JsonPrimitive("stylesCss"))
          add(kotlinx.serialization.json.JsonPrimitive("appJs"))
          add(kotlinx.serialization.json.JsonPrimitive("securityNote"))
        }
      }

    val systemPrompt =
      "You are the BASO AI Website Generator. Generate a complete, modern, responsive website separated strictly into indexHtml (HTML5), stylesCss (CSS3), and appJs (vanilla JavaScript). Never include external tracking scripts. ${languageDirective(appLanguage)}"

    val result =
      provider.generateText(
        model = GeminiRestAiProvider.MODEL_BASIC_TEXT,
        systemPrompt = systemPrompt,
        contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = trimmed)))),
        responseMimeType = "application/json",
        responseSchema = schema,
        temperature = 0.4f,
      )

    return when (result) {
      is BasoAiResult.Failure -> result
      is BasoAiResult.Success -> {
        try {
          val parsed = json.decodeFromString<BasoWebsiteBundleOutput>(result.data)
          if (parsed.indexHtml.isBlank() || parsed.stylesCss.isBlank() || parsed.appJs.isBlank()) {
            BasoAiResult.Failure(
              errorType = BasoAiErrorType.EMPTY_RESPONSE,
              safeMessage = "Generated website bundle is incomplete (missing HTML, CSS, or JS).",
            )
          } else {
            BasoAiResult.Success(
              data = parsed,
              modelUsed = result.modelUsed,
              providerUsed = result.providerUsed,
              isFailover = result.isFailover,
            )
          }
        } catch (e: Exception) {
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.EMPTY_RESPONSE,
            safeMessage = "Failed to validate generated website files.",
          )
        }
      }
    }
  }

  /**
   * TOOL D: AI Design Assistant
   * Generates creative direction, color palette hex codes, typography/layout recommendations, and distinct image-generation text prompts.
   */
  suspend fun runDesignAssistant(
    designBrief: String,
    mode: BasoDesignTaskMode,
    appLanguage: AppLanguage,
  ): BasoAiResult<BasoDesignAssistantOutput> {
    val trimmed = designBrief.trim()
    if (trimmed.isEmpty()) {
      return BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Design brief cannot be empty.",
      )
    }

    val schema =
      buildJsonObject {
        put("type", "OBJECT")
        putJsonObject("properties") {
          putJsonObject("creativeDirection") { put("type", "STRING") }
          putJsonObject("colorPaletteHex") {
            put("type", "ARRAY")
            putJsonObject("items") { put("type", "STRING") }
          }
          putJsonObject("typographyAndLayout") { put("type", "STRING") }
          putJsonObject("imageGenerationPrompt") { put("type", "STRING") }
          putJsonObject("deliverablesList") {
            put("type", "ARRAY")
            putJsonObject("items") { put("type", "STRING") }
          }
        }
        putJsonArray("required") {
          add(kotlinx.serialization.json.JsonPrimitive("creativeDirection"))
          add(kotlinx.serialization.json.JsonPrimitive("colorPaletteHex"))
          add(kotlinx.serialization.json.JsonPrimitive("typographyAndLayout"))
          add(kotlinx.serialization.json.JsonPrimitive("imageGenerationPrompt"))
          add(kotlinx.serialization.json.JsonPrimitive("deliverablesList"))
        }
      }

    val focusNote =
      when (mode) {
        BasoDesignTaskMode.CONCEPT -> "Focus on overall brand concept and creative direction."
        BasoDesignTaskMode.COLOR_PALETTE -> "Focus on a cohesive 5-color hex palette (#RRGGBB) with contrast notes."
        BasoDesignTaskMode.TYPOGRAPHY_LAYOUT -> "Focus on font pairing (Kurdish/Arabic/Latin) and spatial layout hierarchy."
        BasoDesignTaskMode.IMAGE_PROMPT -> "Focus on crafting a detailed text prompt for an image generator (clearly labeled as a text prompt, not an image file)."
        BasoDesignTaskMode.GRAPHIC_BRIEF -> "Focus on a structured graphic design brief and deliverables."
        BasoDesignTaskMode.SOCIAL_IDEAS -> "Focus on social media campaign visuals and post concepts."
      }

    val systemPrompt =
      "You are the BASO AI Design Assistant. $focusNote Return 4 to 5 valid 6-digit hex color codes (e.g. #05090F, #FFD400, #00F5D4) in colorPaletteHex. Note that imageGenerationPrompt is a text prompt for image models, not a generated image file. ${languageDirective(appLanguage)}"

    val result =
      provider.generateText(
        model = GeminiRestAiProvider.MODEL_BASIC_TEXT,
        systemPrompt = systemPrompt,
        contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = trimmed)))),
        responseMimeType = "application/json",
        responseSchema = schema,
        temperature = 0.6f,
      )

    return when (result) {
      is BasoAiResult.Failure -> result
      is BasoAiResult.Success -> {
        try {
          val parsed = json.decodeFromString<BasoDesignAssistantOutput>(result.data)
          BasoAiResult.Success(
            data = parsed,
            modelUsed = result.modelUsed,
            providerUsed = result.providerUsed,
            isFailover = result.isFailover,
          )
        } catch (_: Exception) {
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.EMPTY_RESPONSE,
            safeMessage = "Could not parse design recommendations.",
          )
        }
      }
    }
  }

  /**
   * TOOL E: AI Project Planning Assistant
   * Generates a draft project plan with objectives, milestones, tasks, estimated effort (marked as an estimate), and priorities.
   */
  suspend fun generateProjectPlan(
    projectDescription: String,
    appLanguage: AppLanguage,
  ): BasoAiResult<BasoProjectPlanOutput> {
    val trimmed = projectDescription.trim()
    if (trimmed.isEmpty()) {
      return BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Project description cannot be empty.",
      )
    }

    val schema =
      buildJsonObject {
        put("type", "OBJECT")
        putJsonObject("properties") {
          putJsonObject("projectTitle") { put("type", "STRING") }
          putJsonObject("objectives") {
            put("type", "ARRAY")
            putJsonObject("items") { put("type", "STRING") }
          }
          putJsonObject("milestones") {
            put("type", "ARRAY")
            putJsonObject("items") { put("type", "STRING") }
          }
          putJsonObject("tasks") {
            put("type", "ARRAY")
            putJsonObject("items") { put("type", "STRING") }
          }
          putJsonObject("estimatedEffort") { put("type", "STRING") }
          putJsonObject("priorities") {
            put("type", "ARRAY")
            putJsonObject("items") { put("type", "STRING") }
          }
          putJsonObject("recommendedCategoryIndex") { put("type", "INTEGER") }
        }
        putJsonArray("required") {
          add(kotlinx.serialization.json.JsonPrimitive("projectTitle"))
          add(kotlinx.serialization.json.JsonPrimitive("objectives"))
          add(kotlinx.serialization.json.JsonPrimitive("milestones"))
          add(kotlinx.serialization.json.JsonPrimitive("tasks"))
          add(kotlinx.serialization.json.JsonPrimitive("estimatedEffort"))
          add(kotlinx.serialization.json.JsonPrimitive("priorities"))
          add(kotlinx.serialization.json.JsonPrimitive("recommendedCategoryIndex"))
        }
      }

    val systemPrompt =
      "You are the BASO AI Project Planning Assistant. Break down the user's project into clear objectives, milestones, task breakdowns, an estimated effort string (always explicitly prefix or mark it as an estimate / خەمڵاندن), and priorities. Set recommendedCategoryIndex to 1 for Design, 2 for App, 3 for Website, or 4 for Video. ${languageDirective(appLanguage)}"

    val result =
      provider.generateText(
        model = GeminiRestAiProvider.MODEL_BASIC_TEXT,
        systemPrompt = systemPrompt,
        contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = trimmed)))),
        responseMimeType = "application/json",
        responseSchema = schema,
        temperature = 0.5f,
      )

    return when (result) {
      is BasoAiResult.Failure -> result
      is BasoAiResult.Success -> {
        try {
          val parsed = json.decodeFromString<BasoProjectPlanOutput>(result.data)
          if (parsed.projectTitle.isBlank() || parsed.tasks.isEmpty()) {
            BasoAiResult.Failure(
              errorType = BasoAiErrorType.EMPTY_RESPONSE,
              safeMessage = "Generated project plan was incomplete.",
            )
          } else {
            BasoAiResult.Success(
              data = parsed,
              modelUsed = result.modelUsed,
              providerUsed = result.providerUsed,
              isFailover = result.isFailover,
            )
          }
        } catch (_: Exception) {
          BasoAiResult.Failure(
            errorType = BasoAiErrorType.EMPTY_RESPONSE,
            safeMessage = "Failed to parse structured project plan.",
          )
        }
      }
    }
  }
}
