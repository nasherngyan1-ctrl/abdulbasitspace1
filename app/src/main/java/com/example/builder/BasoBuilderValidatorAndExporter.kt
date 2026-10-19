package com.example.builder

import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class BasoProjectValidationReport(
  val isValid: Boolean,
  val totalFilesChecked: Int,
  val globalErrors: List<String>,
  val fileErrorsMap: Map<String, List<String>>,
  val summaryMessage: String,
)

/**
 * Handles file-path security sanitization, project-level and file-level validation,
 * isolated HTML/CSS/JS live preview bundling, and real ZIP archive export with README generation.
 */
object BasoBuilderValidatorAndExporter {

  private val FORBIDDEN_HOST_PREFIXES =
    listOf(
      "app/src/main/java/com/example/",
      "com/example/MainActivity.kt",
      "com/example/ai/",
      "com/example/data/",
      "com/example/ui/",
      "com/example/builder/",
    )

  private val EMBEDDED_GOOGLE_KEY_REGEX = Regex("AIza[0-9A-Za-z_-]{35}")
  private val EMBEDDED_PROVIDER_KEY_REGEX = Regex("sk-[0-9A-Za-z_-]{20,}")
  private val EMBEDDED_KEY_ASSIGNMENT_REGEX =
    Regex(
      "(?i)(?:GEMINI_API_KEY|OPENAI_API_KEY|PRIVATE_API_KEY)\\s*[:=]\\s*[\"'](?!YOUR_|MY_|REDACTED|process\\.env|import\\.meta)[^\"'\\s]{12,}[\"']",
    )

  /**
   * Returns true if [content] contains a hardcoded private API key pattern
   * that should never be embedded in browser JavaScript, HTML, or client source files.
   */
  fun containsEmbeddedPrivateSecret(content: String): Boolean =
    EMBEDDED_GOOGLE_KEY_REGEX.containsMatchIn(content) ||
      EMBEDDED_PROVIDER_KEY_REGEX.containsMatchIn(content) ||
      EMBEDDED_KEY_ASSIGNMENT_REGEX.containsMatchIn(content)

  /**
   * Redacts any accidentally embedded private API key literals from generated source files
   * before saving or exporting to ZIP.
   */
  fun redactEmbeddedSecrets(content: String): String =
    content
      .replace(EMBEDDED_GOOGLE_KEY_REGEX, "REDACTED_SERVER_SIDE_KEY_REQUIRED")
      .replace(EMBEDDED_PROVIDER_KEY_REGEX, "REDACTED_SERVER_SIDE_KEY_REQUIRED")
      .replace(EMBEDDED_KEY_ASSIGNMENT_REGEX) { match ->
        val keyName = match.value.substringBefore("=").substringBefore(":").trim()
        "$keyName = \"REDACTED_USE_SERVER_BACKEND\""
      }

  /**
   * Validates and normalizes a generated file path.
   * Rejects path traversal (".."), absolute paths, drive letters, null bytes,
   * and paths that attempt to target BASO AI's own host package files.
   */
  fun sanitizeAndValidateRelativePath(rawPath: String): String? {
    val trimmed = rawPath.trim().replace('\\', '/')
    if (trimmed.isEmpty()) return null
    if (trimmed.contains('\u0000')) return null
    if (trimmed.startsWith("/") || trimmed.startsWith("~")) return null
    if (Regex("^[a-zA-Z]:").containsMatchIn(trimmed)) return null

    val segments = trimmed.split('/').filter { it.isNotEmpty() }
    if (segments.isEmpty()) return null
    if (segments.any { it == "." || it == ".." }) return null
    if (segments.any { seg -> seg.any { ch -> ch < ' ' } }) return null

    val normalized = segments.joinToString("/")
    if (FORBIDDEN_HOST_PREFIXES.any { normalized.startsWith(it, ignoreCase = true) }) {
      // Rewrite generated Android package paths if they accidentally collide with com/example/
      return normalized.replaceFirst(
        Regex("^app/src/main/java/com/example/", RegexOption.IGNORE_CASE),
        "app/src/main/java/com/generated/basoapp/",
      ).takeIf { safe ->
        FORBIDDEN_HOST_PREFIXES.none { safe.startsWith(it, ignoreCase = true) }
      }
    }
    return normalized
  }

  /**
   * Validates all files in a project according to its [BasoBuilderProjectType].
   */
  fun validateProjectFiles(
    projectType: BasoBuilderProjectType,
    files: List<BasoBuilderFile>,
  ): BasoProjectValidationReport {
    if (files.isEmpty()) {
      return BasoProjectValidationReport(
        isValid = false,
        totalFilesChecked = 0,
        globalErrors = listOf("Project has no generated files yet."),
        fileErrorsMap = emptyMap(),
        summaryMessage = "Validation failed: project contains 0 files.",
      )
    }

    val globalErrors = mutableListOf<String>()
    val fileErrorsMap = mutableMapOf<String, List<String>>()

    files.forEach { file ->
      val errors = mutableListOf<String>()
      val safePath = sanitizeAndValidateRelativePath(file.path)
      if (safePath == null) {
        errors.add("Unsafe or invalid relative path: ${file.path}")
      }
      if (file.content.isBlank()) {
        errors.add("File content is empty.")
      } else {
        when (file.language) {
          "kotlin", "javascript", "typescript", "css" -> {
            val openBraces = file.content.count { it == '{' }
            val closeBraces = file.content.count { it == '}' }
            if (openBraces != closeBraces) {
              errors.add("Unbalanced curly braces ('{' = $openBraces, '}' = $closeBraces).")
            }
          }
          "html" -> {
            val lower = file.content.lowercase()
            if (!lower.contains("<html") && !lower.contains("<!doctype") && !lower.contains("<body") && !lower.contains("<div")) {
              errors.add("HTML file does not contain recognizable HTML markup.")
            }
          }
          "xml" -> {
            if (!file.content.trim().startsWith("<")) {
              errors.add("XML file must start with a valid XML tag.")
            }
          }
        }
        if (containsEmbeddedPrivateSecret(file.content)) {
          errors.add(
            "Security violation: Hardcoded private API key detected in ${file.path}. Keep private provider keys on the server.",
          )
        }
      }
      if (errors.isNotEmpty()) {
        fileErrorsMap[file.path] = errors
      }
    }

    val pathSet = files.map { it.path.lowercase() }.toSet()
    when (projectType) {
      BasoBuilderProjectType.WEBSITE -> {
        if (pathSet.none { it == "index.html" || it.endsWith("/index.html") }) {
          globalErrors.add("Website project requires an index.html entry file.")
        }
      }
      BasoBuilderProjectType.WEB_APP -> {
        if (pathSet.none { it == "index.html" || it.endsWith("/index.html") }) {
          globalErrors.add("Web App project requires an index.html entry file.")
        }
        if (files.none { it.language == "javascript" || it.language == "typescript" }) {
          globalErrors.add("Web App project requires at least one JavaScript/TypeScript logic module.")
        }
      }
      BasoBuilderProjectType.ANDROID_APP -> {
        if (pathSet.none { it.endsWith("androidmanifest.xml") }) {
          globalErrors.add("Android project requires an AndroidManifest.xml file.")
        }
        if (files.none { it.path.endsWith(".kt") }) {
          globalErrors.add("Android project requires at least one Kotlin (.kt) source file.")
        }
      }
    }

    val isValid = globalErrors.isEmpty() && fileErrorsMap.isEmpty()
    val totalIssueCount = globalErrors.size + fileErrorsMap.values.sumOf { it.size }
    val summary =
      if (isValid) {
        "Validation passed (${files.size} files verified)."
      } else {
        "Validation found $totalIssueCount issue(s) across ${files.size} files."
      }

    return BasoProjectValidationReport(
      isValid = isValid,
      totalFilesChecked = files.size,
      globalErrors = globalErrors,
      fileErrorsMap = fileErrorsMap,
      summaryMessage = summary,
    )
  }

  /**
   * Builds a self-contained HTML document combining the generated project's HTML, CSS, and JS files
   * so it can run inside an isolated local WebView preview.
   */
  fun buildSelfContainedWebPreviewHtml(files: List<BasoBuilderFile>): String? {
    val htmlFile =
      files.firstOrNull { it.path.equals("index.html", ignoreCase = true) }
        ?: files.firstOrNull { it.path.endsWith("/index.html", ignoreCase = true) }
        ?: files.firstOrNull { it.language == "html" }
        ?: return null

    val cssContents =
      files
        .filter { it.language == "css" }
        .joinToString("\n\n") { "/* ${it.path} */\n${it.content}" }

    val jsContents =
      files
        .filter { it.language == "javascript" }
        .sortedBy {
          when {
            it.path.contains("state", ignoreCase = true) -> 0
            it.path.contains("route", ignoreCase = true) -> 1
            it.path.contains("component", ignoreCase = true) -> 2
            else -> 3
          }
        }
        .joinToString("\n\n") { "// ${it.path}\n${it.content}" }

    var combined = htmlFile.content
    // Strip external local stylesheet link tags and script src tags that point to bundled local files
    combined =
      combined.replace(
        Regex("<link[^>]+rel=[\"']stylesheet[\"'][^>]*>", RegexOption.IGNORE_CASE),
        "",
      )
    combined =
      combined.replace(
        Regex("<script[^>]+src=[\"'](?!https?:)[^\"']+[\"'][^>]*>\\s*</script>", RegexOption.IGNORE_CASE),
        "",
      )

    val styleBlock = if (cssContents.isNotBlank()) "<style>\n$cssContents\n</style>" else ""
    val scriptBlock = if (jsContents.isNotBlank()) "<script>\n$jsContents\n</script>" else ""

    return if (combined.contains("</head>", ignoreCase = true)) {
      val withStyles = combined.replaceFirst(Regex("</head>", RegexOption.IGNORE_CASE), "$styleBlock\n</head>")
      if (withStyles.contains("</body>", ignoreCase = true)) {
        withStyles.replaceFirst(Regex("</body>", RegexOption.IGNORE_CASE), "$scriptBlock\n</body>")
      } else {
        "$withStyles\n$scriptBlock"
      }
    } else {
      """
      <!DOCTYPE html>
      <html dir="auto">
      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        $styleBlock
      </head>
      <body>
        $combined
        $scriptBlock
      </body>
      </html>
      """.trimIndent()
    }
  }

  /**
   * Generates a complete README.md for the exported ZIP archive if the project does not already include one.
   */
  fun generateReadmeForProject(project: BasoBuilderProject): String {
    val fileList = project.files.joinToString("\n") { "- `${it.path}` (${it.language})" }
    val setupSection =
      if (project.externalSetupNotes.isNotEmpty()) {
        "\n## External Services & Configuration Notes\n" +
          project.externalSetupNotes.joinToString("\n") { "- $it" }
      } else {
        ""
      }
    val typeNote =
      when (project.projectType) {
        BasoBuilderProjectType.ANDROID_APP ->
          "Android Source Project (Kotlin & Jetpack Compose). Open in Android Studio or build with Gradle (`./gradlew assembleDebug`). Note: This export contains verified source code; compile with Android SDK to produce an APK."
        BasoBuilderProjectType.WEBSITE ->
          "Responsive Static Website (HTML5, CSS3, JavaScript). Open `index.html` in any modern browser."
        BasoBuilderProjectType.WEB_APP ->
          "Structured Web Application with modular state, routing, and UI components. Open `index.html` or serve via a local HTTP server."
      }

    return """
      # ${project.name}

      Generated and managed with **BASO AI App Builder**.

      ## Project Type
      - **Mode**: `${project.projectType.id}`
      - **Overview**: $typeNote

      ## Included Files (${project.files.size})
      $fileList
      $setupSection
    """.trimIndent()
  }

  /**
   * Creates a real ZIP archive in memory containing all validated project files and a README.md.
   */
  fun exportProjectToZipBytes(project: BasoBuilderProject): ByteArray {
    val baos = ByteArrayOutputStream()
    ZipOutputStream(baos).use { zos ->
      val seenPaths = mutableSetOf<String>()
      project.files.forEach { file ->
        val safePath = sanitizeAndValidateRelativePath(file.path) ?: return@forEach
        if (seenPaths.add(safePath)) {
          val entry = ZipEntry(safePath)
          zos.putNextEntry(entry)
          val safeContent = redactEmbeddedSecrets(file.content)
          zos.write(safeContent.toByteArray(Charsets.UTF_8))
          zos.closeEntry()
        }
      }

      if (seenPaths.none { it.equals("README.md", ignoreCase = true) }) {
        val readmeEntry = ZipEntry("README.md")
        zos.putNextEntry(readmeEntry)
        zos.write(generateReadmeForProject(project).toByteArray(Charsets.UTF_8))
        zos.closeEntry()
      }
    }
    return baos.toByteArray()
  }

  /**
   * Writes the project's real ZIP archive to the app's sandboxed export directory and returns the [File].
   */
  fun exportProjectToZipFile(context: Context, project: BasoBuilderProject): File {
    val exportDir = File(context.cacheDir, "baso_builder_exports").apply { mkdirs() }
    val slug =
      project.name
        .lowercase()
        .replace(Regex("[^a-z0-9\\u0600-\\u06FF]+"), "_")
        .trim('_')
        .ifBlank { "baso_project" }
    val outFile = File(exportDir, "${slug}_${project.id.takeLast(6)}.zip")
    val zipBytes = exportProjectToZipBytes(project)
    FileOutputStream(outFile).use { fos ->
      fos.write(zipBytes)
      fos.flush()
    }
    return outFile
  }
}
