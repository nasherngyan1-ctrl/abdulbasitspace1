package com.example.builder

import kotlinx.serialization.Serializable

/**
 * Three supported project creation modes in BASO AI Studio App Builder (Phase 4).
 */
@Serializable
enum class BasoBuilderProjectType(
  val id: String,
  val defaultEntryPath: String,
) {
  ANDROID_APP(
    id = "ANDROID_APP",
    defaultEntryPath = "app/src/main/java/com/generated/basoapp/MainActivity.kt",
  ),
  WEBSITE(
    id = "WEBSITE",
    defaultEntryPath = "index.html",
  ),
  WEB_APP(
    id = "WEB_APP",
    defaultEntryPath = "index.html",
  ),
}

/**
 * Strictly separated lifecycle statuses for the App Builder workspace.
 * A status is only set after its corresponding operation genuinely succeeds.
 */
@Serializable
enum class BasoBuilderLifecycleStatus {
  PROJECT_CREATED,
  AI_GENERATION_IN_PROGRESS,
  FILES_GENERATED,
  VALIDATION_COMPLETED,
  PREVIEW_RUNNING,
  EXPORT_COMPLETED,
  BUILD_COMPLETED,
  ERROR,
}

@Serializable
enum class BasoBuilderWorkspaceTab {
  PROMPT,
  PREVIEW,
  CODE,
}

@Serializable
enum class BasoBuilderViewportMode {
  MOBILE,
  DESKTOP,
}

/**
 * Optional reference attachment (image or text/JSON specification file) attached to a builder prompt.
 */
@Serializable
data class BasoBuilderAttachment(
  val name: String,
  val mimeType: String,
  val base64Data: String = "",
  val textContent: String = "",
)

/**
 * Represents an actual source or configuration file inside a generated BASO AI Builder project.
 */
@Serializable
data class BasoBuilderFile(
  val path: String,
  val content: String,
  val language: String = inferLanguageFromPath(path),
  val isModifiedByUser: Boolean = false,
  val lastUpdatedTurnId: String = "",
  val validationErrors: List<String> = emptyList(),
) {
  val fileName: String
    get() = path.substringAfterLast('/')

  val parentFolder: String
    get() = if (path.contains('/')) path.substringBeforeLast('/') else "/"

  companion object {
    fun inferLanguageFromPath(path: String): String {
      val lower = path.lowercase()
      return when {
        lower.endsWith(".kt") || lower.endsWith(".kts") -> "kotlin"
        lower.endsWith(".xml") -> "xml"
        lower.endsWith(".html") || lower.endsWith(".htm") -> "html"
        lower.endsWith(".css") -> "css"
        lower.endsWith(".js") || lower.endsWith(".mjs") -> "javascript"
        lower.endsWith(".ts") || lower.endsWith(".tsx") -> "typescript"
        lower.endsWith(".json") -> "json"
        lower.endsWith(".md") -> "markdown"
        else -> "text"
      }
    }
  }
}

/**
 * Single prompt/iteration turn in a project's build history.
 */
@Serializable
data class BasoBuilderPromptTurn(
  val id: String,
  val prompt: String,
  val attachmentName: String? = null,
  val timestampLabel: String,
  val summaryResponse: String,
  val architectureNotes: String = "",
  val changedFiles: List<String> = emptyList(),
  val isFollowUp: Boolean = false,
  val isError: Boolean = false,
  val modelUsed: String = "",
)

/**
 * Preserved snapshot of a project's files prior to a follow-up modification or destructive operation.
 */
@Serializable
data class BasoBuilderVersionSnapshot(
  val versionNumber: Int,
  val timestampLabel: String,
  val reasonSummary: String,
  val files: List<BasoBuilderFile>,
)

/**
 * Complete persistent state for a BASO AI Studio App Builder project.
 */
@Serializable
data class BasoBuilderProject(
  val id: String,
  val name: String,
  val projectType: BasoBuilderProjectType,
  val status: BasoBuilderLifecycleStatus = BasoBuilderLifecycleStatus.PROJECT_CREATED,
  val files: List<BasoBuilderFile> = emptyList(),
  val promptHistory: List<BasoBuilderPromptTurn> = emptyList(),
  val previousVersions: List<BasoBuilderVersionSnapshot> = emptyList(),
  val externalSetupNotes: List<String> = emptyList(),
  val isSaved: Boolean = true,
  val lastSavedTimestamp: String = "ئێستا",
  val lastExportPath: String? = null,
  val lastValidationSummary: String? = null,
)
