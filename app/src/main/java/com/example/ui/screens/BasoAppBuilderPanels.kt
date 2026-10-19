package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.builder.BasoBuilderFile
import com.example.builder.BasoBuilderProject
import com.example.builder.BasoBuilderProjectType
import com.example.builder.BasoBuilderValidatorAndExporter
import com.example.builder.BasoBuilderViewportMode
import com.example.ui.components.BasoCard
import com.example.ui.components.BasoPrimaryButton
import com.example.ui.components.BasoSecondaryButton
import com.example.ui.theme.BasoBorder
import com.example.ui.theme.BasoError
import com.example.ui.theme.BasoErrorSubtleBg
import com.example.ui.theme.BasoNeonTurquoise
import com.example.ui.theme.BasoNeonYellow
import com.example.ui.theme.BasoPrimaryBackground
import com.example.ui.theme.BasoPrimaryText
import com.example.ui.theme.BasoSecondaryBackground
import com.example.ui.theme.BasoSecondaryText
import com.example.ui.theme.BasoSuccess
import com.example.ui.theme.BasoSuccessSubtleBg
import com.example.ui.theme.BasoSurfaceCard
import com.example.ui.theme.BasoTurquoiseSubtleGlow
import com.example.ui.theme.BasoYellowSubtleGlow

/**
 * Live Application Preview Panel for BASO AI App Builder (Section 2C).
 * - For WEBSITE and WEB_APP: runs the actual generated HTML/CSS/JS inside an isolated WebView
 *   with Refresh, Mobile/Desktop viewport toggle, and loading/error states.
 * - For ANDROID_APP: honestly presents the Android source structure, manifest, and Gradle summary
 *   without faking a running APK screenshot.
 */
@Composable
fun BuilderLivePreviewPanel(
  project: BasoBuilderProject,
  strings: BasoBuilderUiStrings,
  viewportMode: BasoBuilderViewportMode,
  onSelectViewport: (BasoBuilderViewportMode) -> Unit,
  onPreviewStarted: () -> Unit,
  onOpenFileInCodeTab: (String) -> Unit,
  onExportZip: () -> Unit,
) {
  var refreshNonce by rememberSaveable { mutableIntStateOf(0) }
  var previewLoading by remember { mutableStateOf(false) }
  var previewError by remember { mutableStateOf<String?>(null) }

  Column(
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxSize().testTag("builder_preview_panel"),
  ) {
    // Preview Controls Bar (Viewport Toggle + Refresh + Export)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        ViewportToggleButton(
          label = strings.viewportMobile,
          icon = Icons.Default.PhoneAndroid,
          selected = viewportMode == BasoBuilderViewportMode.MOBILE,
          onClick = { onSelectViewport(BasoBuilderViewportMode.MOBILE) },
          testTag = "builder_viewport_mobile_button",
        )
        ViewportToggleButton(
          label = strings.viewportDesktop,
          icon = Icons.Default.DesktopWindows,
          selected = viewportMode == BasoBuilderViewportMode.DESKTOP,
          onClick = { onSelectViewport(BasoBuilderViewportMode.DESKTOP) },
          testTag = "builder_viewport_desktop_button",
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        IconButton(
          onClick = {
            previewError = null
            refreshNonce++
          },
          modifier =
            Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(BasoSurfaceCard)
              .border(1.dp, BasoBorder, RoundedCornerShape(10.dp))
              .testTag("builder_preview_refresh_button"),
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = strings.refreshPreview,
            tint = BasoNeonTurquoise,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }

    if (project.files.isEmpty()) {
      BasoCard(modifier = Modifier.fillMaxWidth().testTag("builder_preview_empty_card")) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = strings.previewUnavailableTitle,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = BasoNeonYellow,
          )
          Text(
            text = strings.previewUnavailableEmptyBody,
            style = MaterialTheme.typography.bodyMedium,
            color = BasoSecondaryText,
          )
        }
      }
      return
    }

    when (project.projectType) {
      BasoBuilderProjectType.WEBSITE, BasoBuilderProjectType.WEB_APP -> {
        val bundledHtml =
          remember(project.files, refreshNonce) {
            BasoBuilderValidatorAndExporter.buildSelfContainedWebPreviewHtml(project.files)
          }

        if (bundledHtml == null) {
          BasoCard(modifier = Modifier.fillMaxWidth().testTag("builder_preview_missing_html_card")) {
            Text(
              text = strings.previewMissingHtmlError,
              style = MaterialTheme.typography.bodyMedium,
              color = BasoError,
            )
          }
        } else {
          LaunchedEffect(bundledHtml, refreshNonce) {
            onPreviewStarted()
          }

          if (previewLoading) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              CircularProgressIndicator(
                color = BasoNeonTurquoise,
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp),
              )
              Text(
                text = strings.previewLoadingLabel,
                style = MaterialTheme.typography.labelMedium,
                color = BasoNeonTurquoise,
              )
            }
          }

          previewError?.let { err ->
            Surface(
              color = BasoErrorSubtleBg,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, BasoError),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = err,
                style = MaterialTheme.typography.labelMedium,
                color = BasoPrimaryText,
                modifier = Modifier.padding(10.dp),
              )
            }
          }

          Box(
            contentAlignment = Alignment.TopCenter,
            modifier =
              Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BasoSecondaryBackground)
                .border(1.dp, BasoBorder, RoundedCornerShape(16.dp))
                .padding(if (viewportMode == BasoBuilderViewportMode.MOBILE) 10.dp else 2.dp),
          ) {
            val frameModifier =
              if (viewportMode == BasoBuilderViewportMode.MOBILE) {
                Modifier.width(360.dp).fillMaxSize()
              } else {
                Modifier.fillMaxSize()
              }

            IsolatedWebPreviewView(
              htmlContent = bundledHtml,
              refreshKey = refreshNonce,
              onLoadingChange = { previewLoading = it },
              onError = { previewError = it },
              modifier =
                frameModifier
                  .clip(RoundedCornerShape(12.dp))
                  .border(1.dp, BasoNeonTurquoise.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                  .testTag("builder_live_webview"),
            )
          }
        }
      }

      BasoBuilderProjectType.ANDROID_APP -> {
        AndroidSourceInspectorPreview(
          project = project,
          strings = strings,
          onOpenFileInCodeTab = onOpenFileInCodeTab,
          onExportZip = onExportZip,
          modifier = Modifier.weight(1f).fillMaxWidth(),
        )
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun IsolatedWebPreviewView(
  htmlContent: String,
  refreshKey: Int,
  onLoadingChange: (Boolean) -> Unit,
  onError: (String?) -> Unit,
  modifier: Modifier = Modifier,
) {
  AndroidView(
    factory = { ctx ->
      WebView(ctx).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = false
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        webViewClient =
          object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
              onLoadingChange(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              onLoadingChange(false)
            }

            override fun onReceivedError(
              view: WebView?,
              request: WebResourceRequest?,
              error: WebResourceError?,
            ) {
              if (request?.isForMainFrame == true) {
                onLoadingChange(false)
                onError(error?.description?.toString())
              }
            }
          }
      }
    },
    update = { webView ->
      val taggedHtml = "<!-- refresh:$refreshKey -->\n$htmlContent"
      webView.loadDataWithBaseURL(
        "https://preview.baso.local/",
        taggedHtml,
        "text/html",
        "UTF-8",
        null,
      )
    },
    modifier = modifier,
  )
}

@Composable
private fun AndroidSourceInspectorPreview(
  project: BasoBuilderProject,
  strings: BasoBuilderUiStrings,
  onOpenFileInCodeTab: (String) -> Unit,
  onExportZip: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val manifestFile = project.files.firstOrNull { it.path.endsWith("AndroidManifest.xml", ignoreCase = true) }
  val gradleFile = project.files.firstOrNull { it.path.endsWith("build.gradle.kts", ignoreCase = true) }
  val kotlinFiles = project.files.filter { it.path.endsWith(".kt") }

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier.testTag("builder_android_source_inspector"),
  ) {
    item {
      Surface(
        color = BasoYellowSubtleGlow,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BasoNeonYellow.copy(alpha = 0.65f)),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.padding(14.dp),
        ) {
          Icon(
            imageVector = Icons.Default.Android,
            contentDescription = null,
            tint = BasoNeonYellow,
            modifier = Modifier.size(22.dp),
          )
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = strings.androidPreviewNoticeTitle,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
            )
            Text(
              text = strings.androidPreviewNoticeBody,
              style = MaterialTheme.typography.bodyMedium,
              color = BasoSecondaryText,
            )
          }
        }
      }
    }

    item {
      BasoCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "${project.name} • Android / Jetpack Compose",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = BasoNeonTurquoise,
          )
          Text(
            text =
              "Manifest: ${manifestFile?.path ?: "N/A"} • Gradle: ${gradleFile?.path ?: "N/A"} • Kotlin Files: ${kotlinFiles.size}",
            style = MaterialTheme.typography.labelMedium,
            color = BasoSecondaryText,
          )
          BasoPrimaryButton(
            text = strings.exportZipAction,
            onClick = onExportZip,
            leadingIcon = Icons.Default.FolderZip,
            modifier = Modifier.fillMaxWidth(),
            testTag = "builder_android_inspector_export_button",
          )
        }
      }
    }

    items(project.files, key = { it.path }) { file ->
      Surface(
        color = BasoSurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BasoBorder),
        modifier =
          Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onOpenFileInCodeTab(file.path) }
            .testTag("builder_inspector_file_${file.fileName}"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.padding(14.dp),
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = file.path,
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
            )
            Text(
              text = "${file.language} • ${file.content.lines().size} lines",
              style = MaterialTheme.typography.labelMedium,
              color = BasoSecondaryText,
            )
          }
          Icon(
            imageVector = Icons.Default.Code,
            contentDescription = null,
            tint = BasoNeonTurquoise,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

/**
 * Code and File Explorer Panel for BASO AI App Builder (Section 2D).
 * Displays the real project folder/file tree, file status indicators, syntax-highlighted/editable
 * code view, save edits, copy code, file-level AI regeneration, project validation, and ZIP export.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BuilderCodeAndFilesPanel(
  project: BasoBuilderProject,
  selectedFilePath: String,
  onSelectFilePath: (String) -> Unit,
  onSaveFileContent: (path: String, newContent: String) -> Unit,
  onAddNewFile: (path: String, initialContent: String) -> Unit,
  onRegenerateFile: (path: String, instruction: String) -> Unit,
  onValidateProject: () -> Unit,
  onExportZip: () -> Unit,
  onCopyText: (String) -> Unit,
  isGenerating: Boolean,
  strings: BasoBuilderUiStrings,
) {
  val activeFile =
    project.files.firstOrNull { it.path == selectedFilePath } ?: project.files.firstOrNull()

  var editorBuffer by remember(activeFile?.path, activeFile?.content) {
    mutableStateOf(activeFile?.content ?: "")
  }
  var isEditingMode by rememberSaveable { mutableStateOf(false) }
  var showNewFileInlineForm by rememberSaveable { mutableStateOf(false) }
  var newFilePathInput by rememberSaveable { mutableStateOf("") }
  var regenInstruction by rememberSaveable { mutableStateOf("") }

  val hasUnsavedEditorChanges = activeFile != null && editorBuffer != activeFile.content

  // Group files by folder so the tree reflects actual project directories
  val groupedByFolder =
    remember(project.files) {
      project.files.groupBy { it.parentFolder }
    }

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize().testTag("builder_code_panel"),
  ) {
    // Top Project Action Toolbar (Validate, Export ZIP, + Add File)
    item {
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        BasoSecondaryButton(
          text = strings.validateProjectAction,
          onClick = onValidateProject,
          leadingIcon = Icons.Default.Verified,
          highlightTurquoise = true,
          testTag = "builder_validate_project_button",
        )
        BasoSecondaryButton(
          text = strings.exportZipAction,
          onClick = onExportZip,
          leadingIcon = Icons.Default.FolderZip,
          testTag = "builder_export_zip_button",
        )
        BasoSecondaryButton(
          text = strings.newFileAction,
          onClick = { showNewFileInlineForm = !showNewFileInlineForm },
          leadingIcon = Icons.Default.Add,
          testTag = "builder_add_file_button",
        )
      }
    }

    if (showNewFileInlineForm) {
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("builder_new_file_card")) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newFilePathInput,
              onValueChange = { newFilePathInput = it },
              label = { Text(strings.newFilePathLabel) },
              placeholder = { Text("src/utils.js") },
              singleLine = true,
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BasoNeonTurquoise,
                  unfocusedBorderColor = BasoBorder,
                  focusedTextColor = BasoPrimaryText,
                  unfocusedTextColor = BasoPrimaryText,
                ),
              modifier = Modifier.fillMaxWidth().testTag("builder_new_file_path_input"),
            )
            BasoPrimaryButton(
              text = strings.createFileConfirmAction,
              onClick = {
                val safe =
                  BasoBuilderValidatorAndExporter.sanitizeAndValidateRelativePath(newFilePathInput)
                if (safe != null) {
                  onAddNewFile(safe, "// $safe\n")
                  newFilePathInput = ""
                  showNewFileInlineForm = false
                }
              },
              testTag = "builder_new_file_confirm_button",
            )
          }
        }
      }
    }

    if (project.files.isEmpty()) {
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("builder_code_empty_card")) {
          Text(
            text = strings.noFilesGeneratedYet,
            style = MaterialTheme.typography.bodyMedium,
            color = BasoSecondaryText,
          )
        }
      }
    } else {
      // Real Project Folder & File Tree
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("builder_file_tree")) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "${strings.fileExplorerTitle} (${project.files.size})",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoNeonTurquoise,
            )

            groupedByFolder.forEach { (folder, folderFiles) ->
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Default.Folder,
                  contentDescription = null,
                  tint = BasoNeonYellow,
                  modifier = Modifier.size(16.dp),
                )
                Text(
                  text = folder,
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = BasoSecondaryText,
                )
              }

              folderFiles.forEach { file ->
                val isSelected = file.path == activeFile?.path
                val hasError = file.validationErrors.isNotEmpty()
                Surface(
                  color = if (isSelected) BasoSecondaryBackground else Color.Transparent,
                  shape = RoundedCornerShape(10.dp),
                  border =
                    BorderStroke(
                      width = 1.dp,
                      color =
                        when {
                          hasError -> BasoError
                          isSelected -> BasoNeonTurquoise
                          else -> BasoBorder.copy(alpha = 0.5f)
                        },
                    ),
                  modifier =
                    Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(10.dp))
                      .clickable { onSelectFilePath(file.path) }
                      .testTag("builder_file_item_${file.path}"),
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      modifier = Modifier.weight(1f),
                    ) {
                      Icon(
                        imageVector =
                          when (file.language) {
                            "html", "css" -> Icons.Default.Web
                            "kotlin" -> Icons.Default.Android
                            else -> Icons.Default.Description
                          },
                        contentDescription = null,
                        tint = if (isSelected) BasoNeonTurquoise else BasoSecondaryText,
                        modifier = Modifier.size(16.dp),
                      )
                      Text(
                        text = file.fileName,
                        style =
                          MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                          ),
                        color = BasoPrimaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                      )
                    }

                    // File status pill (Validation Error / Edited / Generated)
                    val statusLabel =
                      when {
                        hasError -> "Error (${file.validationErrors.size})"
                        file.isModifiedByUser -> strings.fileEditedBadge
                        else -> strings.fileGeneratedBadge
                      }
                    val statusColor =
                      when {
                        hasError -> BasoError
                        file.isModifiedByUser -> BasoNeonYellow
                        else -> BasoSuccess
                      }
                    Text(
                      text = statusLabel,
                      style = MaterialTheme.typography.labelMedium,
                      color = statusColor,
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Active File Code Viewer / Editor + File Regeneration
      activeFile?.let { file ->
        item {
          BasoCard(modifier = Modifier.fillMaxWidth().testTag("builder_active_file_editor_card")) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = file.path,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BasoNeonYellow,
                  )
                  Text(
                    text = "${file.language.uppercase()} • ${editorBuffer.lines().size} lines",
                    style = MaterialTheme.typography.labelMedium,
                    color = BasoSecondaryText,
                  )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Surface(
                    color = if (isEditingMode) BasoNeonTurquoise else BasoSecondaryBackground,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BasoNeonTurquoise),
                    modifier =
                      Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isEditingMode = !isEditingMode }
                        .testTag("builder_toggle_edit_mode_button"),
                  ) {
                    Text(
                      text = if (isEditingMode) strings.viewModeLabel else strings.editModeLabel,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = if (isEditingMode) BasoPrimaryBackground else BasoNeonTurquoise,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                  }

                  IconButton(
                    onClick = { onCopyText(editorBuffer) },
                    modifier = Modifier.size(34.dp).testTag("builder_copy_code_button"),
                  ) {
                    Icon(
                      imageVector = Icons.Default.ContentCopy,
                      contentDescription = strings.copyCodeAction,
                      tint = BasoNeonTurquoise,
                      modifier = Modifier.size(18.dp),
                    )
                  }
                }
              }

              // Validation Errors Banner for this file
              if (file.validationErrors.isNotEmpty()) {
                Surface(
                  color = BasoErrorSubtleBg,
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, BasoError),
                  modifier = Modifier.fillMaxWidth().testTag("builder_file_validation_errors"),
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    file.validationErrors.forEach { err ->
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                      ) {
                        Icon(
                          imageVector = Icons.Default.ErrorOutline,
                          contentDescription = null,
                          tint = BasoError,
                          modifier = Modifier.size(16.dp),
                        )
                        Text(
                          text = err,
                          style = MaterialTheme.typography.labelMedium,
                          color = BasoPrimaryText,
                        )
                      }
                    }
                  }
                }
              }

              // Code View / Editor
              if (isEditingMode) {
                OutlinedTextField(
                  value = editorBuffer,
                  onValueChange = { editorBuffer = it },
                  textStyle =
                    MaterialTheme.typography.bodyMedium.copy(
                      fontFamily = FontFamily.Monospace,
                      lineHeight = 20.sp,
                    ),
                  minLines = 8,
                  colors =
                    OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = BasoPrimaryBackground,
                      unfocusedContainerColor = BasoPrimaryBackground,
                      focusedBorderColor = BasoNeonYellow,
                      unfocusedBorderColor = BasoBorder,
                      focusedTextColor = BasoPrimaryText,
                      unfocusedTextColor = BasoPrimaryText,
                    ),
                  modifier = Modifier.fillMaxWidth().testTag("builder_code_editor_input"),
                )
              } else {
                Surface(
                  color = BasoPrimaryBackground,
                  shape = RoundedCornerShape(12.dp),
                  border = BorderStroke(1.dp, BasoBorder),
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  SelectionContainer {
                    Text(
                      text = highlightSyntax(editorBuffer),
                      style =
                        MaterialTheme.typography.bodyMedium.copy(
                          fontFamily = FontFamily.Monospace,
                          lineHeight = 20.sp,
                        ),
                      modifier =
                        Modifier
                          .fillMaxWidth()
                          .horizontalScroll(rememberScrollState())
                          .padding(14.dp)
                          .testTag("builder_code_syntax_view"),
                    )
                  }
                }
              }

              // Save File Edits + File-Level Regeneration Controls
              Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
              ) {
                BasoPrimaryButton(
                  text = strings.saveFileEditsAction,
                  onClick = { onSaveFileContent(file.path, editorBuffer) },
                  enabled = hasUnsavedEditorChanges || isEditingMode,
                  leadingIcon = Icons.Default.Save,
                  modifier = Modifier.weight(1f),
                  testTag = "builder_save_file_button",
                )
                BasoSecondaryButton(
                  text = strings.regenerateFileAction,
                  onClick = { onRegenerateFile(file.path, regenInstruction) },
                  leadingIcon = Icons.Default.Refresh,
                  highlightTurquoise = true,
                  modifier = Modifier.weight(1f),
                  testTag = "builder_regenerate_file_button",
                )
              }

              OutlinedTextField(
                value = regenInstruction,
                onValueChange = { regenInstruction = it },
                placeholder = { Text(strings.regenerateFileHint) },
                singleLine = true,
                colors =
                  OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BasoNeonTurquoise,
                    unfocusedBorderColor = BasoBorder,
                    focusedTextColor = BasoPrimaryText,
                    unfocusedTextColor = BasoPrimaryText,
                  ),
                modifier = Modifier.fillMaxWidth().testTag("builder_regenerate_file_instruction_input"),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ViewportToggleButton(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  selected: Boolean,
  onClick: () -> Unit,
  testTag: String,
) {
  Surface(
    color = if (selected) BasoTurquoiseSubtleGlow else BasoSurfaceCard,
    shape = RoundedCornerShape(10.dp),
    border =
      BorderStroke(
        width = 1.dp,
        color = if (selected) BasoNeonTurquoise else BasoBorder,
      ),
    modifier =
      Modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable(onClick = onClick)
        .testTag(testTag),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) BasoNeonTurquoise else BasoSecondaryText,
        modifier = Modifier.size(15.dp),
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = if (selected) BasoNeonTurquoise else BasoPrimaryText,
      )
    }
  }
}

/**
 * Lightweight syntax highlighter for Kotlin, HTML, CSS, JS, and JSON in the Code Viewer.
 */
private fun highlightSyntax(code: String): AnnotatedString =
  buildAnnotatedString {
    val keywords =
      setOf(
        "package",
        "import",
        "class",
        "data",
        "fun",
        "val",
        "var",
        "return",
        "if",
        "else",
        "when",
        "for",
        "object",
        "interface",
        "const",
        "function",
        "let",
        "async",
        "await",
        "export",
        "default",
      )
    val lines = code.lines()
    lines.forEachIndexed { idx, line ->
      val lineNo = "${(idx + 1).toString().padStart(3, ' ')} │ "
      withStyle(SpanStyle(color = Color(0xFF5A7186))) {
        append(lineNo)
      }
      val trimmed = line.trimStart()
      if (trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("<!--") || trimmed.startsWith("#")) {
        withStyle(SpanStyle(color = Color(0xFF37D99A))) {
          append(line)
        }
      } else {
        val tokens = line.split(Regex("(?=[\\s(){}.,:;\"'])|(?<=[\\s(){}.,:;\"'])"))
        tokens.forEach { token ->
          val color =
            when {
              token in keywords -> Color(0xFFFFD400)
              token.startsWith("@") || token.startsWith("<") || token.startsWith("</") -> Color(0xFF00F5D4)
              token.all { it.isDigit() } && token.isNotEmpty() -> Color(0xFF7AD7F0)
              else -> Color(0xFFF5F7FA)
            }
          withStyle(SpanStyle(color = color)) {
            append(token)
          }
        }
      }
      if (idx < lines.lastIndex) append("\n")
    }
  }
