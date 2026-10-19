package com.example.builder

import com.example.ai.BasoAiConfigMode
import com.example.ai.BasoAiErrorType
import com.example.ai.BasoAiProvider
import com.example.ai.BasoAiResult
import com.example.ai.GeminiContent
import com.example.ai.GeminiInlineData
import com.example.ai.GeminiPart
import com.example.ai.GeminiRestAiProvider
import com.example.ui.theme.AppLanguage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

@Serializable
data class BasoBuilderAiFileDto(
  val path: String,
  val content: String,
  val language: String = "",
)

@Serializable
data class BasoBuilderAiResponseDto(
  val projectName: String = "",
  val summary: String,
  val architectureNotes: String = "",
  val externalSetupNotes: List<String> = emptyList(),
  val files: List<BasoBuilderAiFileDto>,
)

data class BasoBuilderGenerationOutcome(
  val suggestedProjectName: String,
  val summary: String,
  val architectureNotes: String,
  val externalSetupNotes: List<String>,
  val mergedFiles: List<BasoBuilderFile>,
  val changedFilePaths: List<String>,
  val modelUsed: String,
)

/**
 * Google AI Studio-style AI App Builder Engine for BASO AI (Phase 4).
 *
 * - Sends real prompts, project context, and optional reference attachments to the configured AI provider.
 * - Supports initial multi-file generation, contextual follow-up iterations (modifying existing files while
 *   preserving unrelated files), and single-file regeneration.
 * - Strictly enforces safe relative paths and never simulates fake AI output when credentials are missing.
 */
class BasoBuilderAiEngine(
  private val provider: BasoAiProvider = GeminiRestAiProvider(),
) {
  private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
  }

  fun isConfigured(): Boolean = provider.isConfigured()

  fun configurationMode(): BasoAiConfigMode = provider.configurationMode()

  private fun languageInstruction(language: AppLanguage): String =
    when (language) {
      AppLanguage.KURDISH_SORANI ->
        "Write human-readable summaries, UI copy, and architecture notes in natural Kurdish Sorani (کوردیی سۆرانی) unless the user asks for another language."
      AppLanguage.ARABIC ->
        "Write human-readable summaries, UI copy, and architecture notes in natural Arabic (العربية) unless the user asks for another language."
      AppLanguage.ENGLISH ->
        "Write human-readable summaries, UI copy, and architecture notes in clear English."
    }

  private fun projectTypeSpecification(projectType: BasoBuilderProjectType): String =
    when (projectType) {
      BasoBuilderProjectType.ANDROID_APP ->
        """
        Target Project Type: ANDROID_APP (Mode 1: Android Application).
        Generate a clean, modular Android source project using Kotlin and Jetpack Compose under package `com.generated.basoapp` (never `com.example`).
        Include real source files such as:
        - `app/src/main/AndroidManifest.xml`
        - `app/build.gradle.kts`
        - `app/src/main/java/com/generated/basoapp/MainActivity.kt`
        - `app/src/main/java/com/generated/basoapp/model/Models.kt`
        - `app/src/main/java/com/generated/basoapp/ui/AppScreens.kt`
        - `app/src/test/java/com/generated/basoapp/AppLogicTest.kt`
        - `README.md`
        Ensure all curly braces are balanced and code is well-structured.
        """.trimIndent()
      BasoBuilderProjectType.WEBSITE ->
        """
        Target Project Type: WEBSITE (Mode 2: Responsive Website).
        Generate a complete, responsive, interactive website with separated files:
        - `index.html` (semantic HTML5, accessible, RTL/LTR aware)
        - `styles.css` (modern CSS3 variables, responsive layout, clean styling)
        - `app.js` (real interactive JavaScript for search, filtering, cart/modals, or dynamic views)
        - `README.md`
        """.trimIndent()
      BasoBuilderProjectType.WEB_APP ->
        """
        Target Project Type: WEB_APP (Mode 3: Structured Web Application).
        Generate a multi-module web application with state management, view routing, forms, and clear backend integration points:
        - `index.html` (application shell and root mount container)
        - `styles.css` (application theme and component styles)
        - `src/state.js` (reactive state store, persistence, and actions)
        - `src/routes.js` (view router and navigation state)
        - `src/app.js` (interactive UI components, forms, search, and event handlers)
        - `src/api-config.js` (documented integration points for auth, database, or external APIs without hardcoded secrets)
        - `README.md`
        If authentication, database, payments, or external APIs are requested, list the exact setup requirements in `externalSetupNotes`.
        """.trimIndent()
    }

  /**
   * Runs either an initial project build or a contextual follow-up modification on [project].
   */
  suspend fun buildOrModifyProject(
    project: BasoBuilderProject,
    userPrompt: String,
    attachment: BasoBuilderAttachment? = null,
    appLanguage: AppLanguage,
    turnId: String = "turn_${System.currentTimeMillis()}",
  ): BasoAiResult<BasoBuilderGenerationOutcome> {
    val trimmedPrompt = userPrompt.trim()
    if (trimmedPrompt.isEmpty()) {
      return BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Prompt cannot be empty.",
      )
    }

    if (trimmedPrompt.length > MAX_PROMPT_CHARS) {
      return BasoAiResult.Failure(
        errorType = BasoAiErrorType.INVALID_REQUEST,
        safeMessage = "Prompt exceeds the maximum allowed length ($MAX_PROMPT_CHARS characters).",
      )
    }

    val isFollowUp = project.files.isNotEmpty()
    val existingContextBlock =
      if (isFollowUp) {
        val manifestSummary =
          project.files.joinToString("\n\n") { file ->
            val clippedContent = file.content.take(MAX_FILE_CONTEXT_CHARS)
            "--- FILE: ${file.path} ---\n$clippedContent"
          }
        val recentPrompts =
          project.promptHistory.takeLast(4).joinToString("\n") { "- ${it.prompt} -> ${it.summaryResponse}" }
        """
        EXISTING PROJECT CONTEXT (Follow-Up Iteration Mode):
        Project Name: ${project.name}
        Recent History:
        $recentPrompts

        Current Project Files:
        $manifestSummary

        IMPORTANT FOLLOW-UP RULE:
        Modify or add only the files needed to fulfill the user's follow-up instruction.
        Return the complete updated content for every modified or newly added file in `files`.
        Unrelated existing files will be preserved automatically.
        """.trimIndent()
      } else {
        "INITIAL BUILD MODE: Generate a complete, multi-file project from scratch for the requested application."
      }

    val systemPrompt =
      """
      You are the BASO AI Studio App Builder engine.
      ${projectTypeSpecification(project.projectType)}
      ${languageInstruction(appLanguage)}
      Never include private API keys or credentials in generated files.
      Always return valid JSON matching the requested schema.
      """.trimIndent()

    val userParts = mutableListOf<GeminiPart>()
    val fullUserText =
      buildString {
        appendLine(existingContextBlock)
        if (attachment != null && attachment.textContent.isNotBlank()) {
          appendLine("\nATTACHED FILE (${attachment.name}):")
          appendLine(attachment.textContent.take(4000))
        }
        appendLine("\nUSER BUILD INSTRUCTION:")
        appendLine(trimmedPrompt)
      }
    userParts.add(GeminiPart(text = fullUserText))

    if (attachment != null && attachment.base64Data.isNotBlank() && attachment.mimeType.startsWith("image/")) {
      userParts.add(
        GeminiPart(
          inlineData =
            GeminiInlineData(
              mimeType = attachment.mimeType,
              data = attachment.base64Data,
            ),
        ),
      )
    }

    val schema = buildResponseJsonSchema()
    val rawResult =
      callModelWithFallback(
        systemPrompt = systemPrompt,
        contents = listOf(GeminiContent(role = "user", parts = userParts)),
        responseSchema = schema,
      )

    return when (rawResult) {
      is BasoAiResult.Failure -> rawResult
      is BasoAiResult.Success ->
        parseAndMergeProjectFiles(
          rawJson = rawResult.data,
          modelUsed = rawResult.modelUsed,
          existingProject = project,
          turnId = turnId,
          providerUsed = rawResult.providerUsed,
          isFailover = rawResult.isFailover,
        )
    }
  }

  /**
   * Regenerates or repairs a single selected file within the context of [project].
   */
  suspend fun regenerateSingleFile(
    project: BasoBuilderProject,
    targetFilePath: String,
    customInstruction: String,
    appLanguage: AppLanguage,
    turnId: String = "regen_${System.currentTimeMillis()}",
  ): BasoAiResult<BasoBuilderGenerationOutcome> {
    val safeTarget =
      BasoBuilderValidatorAndExporter.sanitizeAndValidateRelativePath(targetFilePath)
        ?: return BasoAiResult.Failure(
          errorType = BasoAiErrorType.INVALID_REQUEST,
          safeMessage = "Invalid target file path: $targetFilePath",
        )

    val currentFile = project.files.firstOrNull { it.path == safeTarget }
    val otherFilesManifest =
      project.files
        .filter { it.path != safeTarget }
        .joinToString("\n") { "- ${it.path} (${it.language})" }

    val prompt =
      buildString {
        appendLine("Regenerate and improve ONLY the file `$safeTarget` for project `${project.name}` (${project.projectType.id}).")
        if (customInstruction.isNotBlank()) {
          appendLine("Instruction: $customInstruction")
        }
        if (currentFile != null) {
          if (currentFile.validationErrors.isNotEmpty()) {
            appendLine("Fix these validation errors: ${currentFile.validationErrors.joinToString("; ")}")
          }
          appendLine("\nCurrent content of `$safeTarget`:")
          appendLine(currentFile.content.take(MAX_FILE_CONTEXT_CHARS))
        }
        if (otherFilesManifest.isNotBlank()) {
          appendLine("\nOther files in project:")
          appendLine(otherFilesManifest)
        }
      }

    val systemPrompt =
      """
      You are the BASO AI Studio File Regeneration Engine.
      Return a JSON response containing the updated file `$safeTarget` inside `files`.
      ${languageInstruction(appLanguage)}
      """.trimIndent()

    val rawResult =
      callModelWithFallback(
        systemPrompt = systemPrompt,
        contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))),
        responseSchema = buildResponseJsonSchema(),
      )

    return when (rawResult) {
      is BasoAiResult.Failure -> rawResult
      is BasoAiResult.Success ->
        parseAndMergeProjectFiles(
          rawJson = rawResult.data,
          modelUsed = rawResult.modelUsed,
          existingProject = project,
          turnId = turnId,
          providerUsed = rawResult.providerUsed,
          isFailover = rawResult.isFailover,
        )
    }
  }

  private suspend fun callModelWithFallback(
    systemPrompt: String,
    contents: List<GeminiContent>,
    responseSchema: kotlinx.serialization.json.JsonObject,
  ): BasoAiResult<String> {
    val primary =
      provider.generateText(
        model = GeminiRestAiProvider.MODEL_COMPLEX_CODE,
        systemPrompt = systemPrompt,
        contents = contents,
        responseMimeType = "application/json",
        responseSchema = responseSchema,
        temperature = 0.35f,
      )
    return when (primary) {
      is BasoAiResult.Success -> primary
      is BasoAiResult.Failure -> {
        if (primary.errorType == BasoAiErrorType.MISSING_CREDENTIALS ||
          primary.errorType == BasoAiErrorType.INVALID_REQUEST
        ) {
          primary
        } else {
          provider.generateText(
            model = GeminiRestAiProvider.MODEL_BASIC_TEXT,
            systemPrompt = systemPrompt,
            contents = contents,
            responseMimeType = "application/json",
            responseSchema = responseSchema,
            temperature = 0.35f,
          )
        }
      }
    }
  }

  private fun parseAndMergeProjectFiles(
    rawJson: String,
    modelUsed: String,
    existingProject: BasoBuilderProject,
    turnId: String,
    providerUsed: String = "AI Provider",
    isFailover: Boolean = false,
  ): BasoAiResult<BasoBuilderGenerationOutcome> {
    return try {
      val dto = json.decodeFromString<BasoBuilderAiResponseDto>(rawJson)
      val sanitizedIncoming =
        dto.files.mapNotNull { fileDto ->
          val safePath =
            BasoBuilderValidatorAndExporter.sanitizeAndValidateRelativePath(fileDto.path)
              ?: return@mapNotNull null
          if (fileDto.content.isBlank()) return@mapNotNull null
          val cleanContent = BasoBuilderValidatorAndExporter.redactEmbeddedSecrets(fileDto.content)
          BasoBuilderFile(
            path = safePath,
            content = cleanContent,
            language =
              fileDto.language.ifBlank {
                BasoBuilderFile.inferLanguageFromPath(safePath)
              },
            isModifiedByUser = false,
            lastUpdatedTurnId = turnId,
          )
        }

      if (sanitizedIncoming.isEmpty()) {
        return BasoAiResult.Failure(
          errorType = BasoAiErrorType.EMPTY_RESPONSE,
          safeMessage = "AI response did not contain any valid, safe project files.",
        )
      }

      // Merge incoming files with existing project files so follow-up prompts preserve unrelated files
      val incomingByPath = sanitizedIncoming.associateBy { it.path }
      val mergedMap = LinkedHashMap<String, BasoBuilderFile>()
      existingProject.files.forEach { existingFile ->
        mergedMap[existingFile.path] = existingFile
      }
      incomingByPath.forEach { (path, updatedFile) ->
        mergedMap[path] = updatedFile
      }

      val mergedList = mergedMap.values.toList()
      val validationReport =
        BasoBuilderValidatorAndExporter.validateProjectFiles(
          projectType = existingProject.projectType,
          files = mergedList,
        )

      val annotatedFiles =
        mergedList.map { file ->
          file.copy(validationErrors = validationReport.fileErrorsMap[file.path] ?: emptyList())
        }

      BasoAiResult.Success(
        data =
          BasoBuilderGenerationOutcome(
            suggestedProjectName = dto.projectName.ifBlank { existingProject.name },
            summary = dto.summary.ifBlank { "Updated ${sanitizedIncoming.size} file(s)." },
            architectureNotes = dto.architectureNotes,
            externalSetupNotes =
              (existingProject.externalSetupNotes + dto.externalSetupNotes).distinct(),
            mergedFiles = annotatedFiles,
            changedFilePaths = sanitizedIncoming.map { it.path },
            modelUsed = modelUsed,
          ),
        modelUsed = modelUsed,
        providerUsed = providerUsed,
        isFailover = isFailover,
      )
    } catch (_: Exception) {
      BasoAiResult.Failure(
        errorType = BasoAiErrorType.EMPTY_RESPONSE,
        safeMessage = "Failed to parse structured project files from the AI model response.",
      )
    }
  }

  private fun buildResponseJsonSchema() =
    buildJsonObject {
      put("type", "OBJECT")
      putJsonObject("properties") {
        putJsonObject("projectName") { put("type", "STRING") }
        putJsonObject("summary") { put("type", "STRING") }
        putJsonObject("architectureNotes") { put("type", "STRING") }
        putJsonObject("externalSetupNotes") {
          put("type", "ARRAY")
          putJsonObject("items") { put("type", "STRING") }
        }
        putJsonObject("files") {
          put("type", "ARRAY")
          putJsonObject("items") {
            put("type", "OBJECT")
            putJsonObject("properties") {
              putJsonObject("path") { put("type", "STRING") }
              putJsonObject("content") { put("type", "STRING") }
              putJsonObject("language") { put("type", "STRING") }
            }
            putJsonArray("required") {
              add(JsonPrimitive("path"))
              add(JsonPrimitive("content"))
              add(JsonPrimitive("language"))
            }
          }
        }
      }
      putJsonArray("required") {
        add(JsonPrimitive("projectName"))
        add(JsonPrimitive("summary"))
        add(JsonPrimitive("architectureNotes"))
        add(JsonPrimitive("externalSetupNotes"))
        add(JsonPrimitive("files"))
      }
    }

  companion object {
    const val MAX_PROMPT_CHARS = 8000
    const val MAX_FILE_CONTEXT_CHARS = 3500
  }
}
