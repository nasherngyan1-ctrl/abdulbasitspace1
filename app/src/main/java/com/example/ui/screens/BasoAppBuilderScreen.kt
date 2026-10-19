package com.example.ui.screens

import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ai.BasoAiConfigMode
import com.example.ai.BasoAiResult
import com.example.builder.BasoBuilderAiEngine
import com.example.builder.BasoBuilderAttachment
import com.example.builder.BasoBuilderFile
import com.example.builder.BasoBuilderLifecycleStatus
import com.example.builder.BasoBuilderProject
import com.example.builder.BasoBuilderProjectType
import com.example.builder.BasoBuilderPromptTurn
import com.example.builder.BasoBuilderValidatorAndExporter
import com.example.builder.BasoBuilderVersionSnapshot
import com.example.builder.BasoBuilderViewportMode
import com.example.builder.BasoBuilderWorkspaceTab
import com.example.ui.components.BasoBrandEmblem
import com.example.ui.components.BasoCard
import com.example.ui.components.BasoConfirmationDialog
import com.example.ui.components.BasoInputField
import com.example.ui.components.BasoPrimaryButton
import com.example.ui.components.BasoSecondaryButton
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.BasoBorder
import com.example.ui.theme.BasoCyanPill
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class BasoBuilderUiStrings(
  val studioBrandTitle: String,
  val newProjectAction: String,
  val openProjectAction: String,
  val renameProjectAction: String,
  val saveProjectAction: String,
  val savedBadge: String,
  val unsavedBadge: String,
  val deleteProjectAction: String,
  val settingsAction: String,
  val validateProjectAction: String,
  val exportZipAction: String,
  val restorePreviousVersionAction: String,
  // Tabs
  val tabPromptBuild: String,
  val tabLivePreview: String,
  val tabCodeFiles: String,
  // Project modes
  val modeAndroidApp: String,
  val modeWebsite: String,
  val modeWebApp: String,
  // Status labels
  val statusProjectCreated: String,
  val statusGenerating: String,
  val statusFilesGenerated: String,
  val statusValidationCompleted: String,
  val statusPreviewRunning: String,
  val statusExportCompleted: String,
  val statusBuildCompleted: String,
  val statusError: String,
  val providerReadyLabel: String,
  val providerUnconfiguredLabel: String,
  val providerUnconfiguredDetail: String,
  // Prompt & Iteration
  val promptLabelInitial: String,
  val promptLabelFollowUp: String,
  val promptPlaceholder: String,
  val buildButtonLabel: String,
  val applyFollowUpButtonLabel: String,
  val stopGenerationLabel: String,
  val attachImageLabel: String,
  val attachFileLabel: String,
  val starterPromptsTitle: String,
  val followUpChipsTitle: String,
  val promptHistoryTitle: String,
  val emptyHistoryHint: String,
  val retryAction: String,
  val externalSetupTitle: String,
  // Preview
  val viewportMobile: String,
  val viewportDesktop: String,
  val refreshPreview: String,
  val previewLoadingLabel: String,
  val previewUnavailableTitle: String,
  val previewUnavailableEmptyBody: String,
  val previewMissingHtmlError: String,
  val androidPreviewNoticeTitle: String,
  val androidPreviewNoticeBody: String,
  // Code & File Explorer
  val fileExplorerTitle: String,
  val noFilesGeneratedYet: String,
  val fileGeneratedBadge: String,
  val fileEditedBadge: String,
  val viewModeLabel: String,
  val editModeLabel: String,
  val copyCodeAction: String,
  val saveFileEditsAction: String,
  val regenerateFileAction: String,
  val regenerateFileHint: String,
  val newFileAction: String,
  val newFilePathLabel: String,
  val createFileConfirmAction: String,
  // Dialogs
  val deleteDialogTitle: String,
  val deleteDialogMessage: String,
  val restoreDialogTitle: String,
  val restoreDialogMessage: String,
  val confirmYes: String,
  val confirmCancel: String,
  val copiedFeedback: String,
)

fun builderStringsForLanguage(language: AppLanguage): BasoBuilderUiStrings =
  when (language) {
    AppLanguage.KURDISH_SORANI ->
      BasoBuilderUiStrings(
        studioBrandTitle = "BASO AI Studio • دروستکەری ئەپ",
        newProjectAction = "پڕۆژەی نوێ",
        openProjectAction = "کردنەوەی پڕۆژە",
        renameProjectAction = "گۆڕینی ناوی پڕۆژە",
        saveProjectAction = "پاشەکەوتکردن",
        savedBadge = "پاشەکەوتکراوە",
        unsavedBadge = "گۆڕانکاری نوێ",
        deleteProjectAction = "سڕینەوەی پڕۆژە",
        settingsAction = "ڕێکخستنەکان",
        validateProjectAction = "پشکنینی پڕۆژە",
        exportZipAction = "هەناردەکردنی ZIP",
        restorePreviousVersionAction = "گەڕاندنەوەی وەشانی پێشوو",
        tabPromptBuild = "پڕۆمپت و دروستکردن",
        tabLivePreview = "پێشبینینی زیندوو",
        tabCodeFiles = "فایل و کۆد",
        modeAndroidApp = "ئەپی ئەندرۆید",
        modeWebsite = "وێبسایت",
        modeWebApp = "وێب ئەپ (Web App)",
        statusProjectCreated = "پڕۆژە دروستکرا",
        statusGenerating = "لە حاڵەتی دروستکردن بە AI...",
        statusFilesGenerated = "فایلەکان دروستکران",
        statusValidationCompleted = "پشکنین تەواوبوو",
        statusPreviewRunning = "پێشبینین کاردەکات",
        statusExportCompleted = "هەناردەی ZIP تەواوبوو",
        statusBuildCompleted = "پاکێجی سەرچاوە ئامادەیە",
        statusError = "هەڵە لە دروستکردن یان پشکنین",
        providerReadyLabel = "پەیوەندیی سێرڤەری HTTPS AI چالاکە",
        providerUnconfiguredLabel = "ڕێکخستنی خزمەتگوزاریی AI پێویستە",
        providerUnconfiguredDetail =
          "هیچ خزمەتگوزارییەکی AI ڕێکنەخراوە. بۆ دروستکردنی پڕۆژەی ڕاستەقینە بە زیرەکی دەستکرد بێ دانانی کلیل لەناو APK، بەستەری سێرڤەری HTTPS AI یان مۆدێلی گونجاو پەیوەست بکە. تەواوی ژینگەی کارکردن، دەستکاریکردنی کۆد و هەناردەی ZIP بەردەستە.",
        promptLabelInitial = "وەسفی ئەپ یان وێبسایتەکەت بکە",
        promptLabelFollowUp = "داواکاریی گۆڕانکاری لەسەر پڕۆژەی ئێستا (Follow-up)",
        promptPlaceholder =
          "بۆ نموونە: فرۆشگایەکی سەرهێڵی کوردی دروست بکە بە هاوپۆلی کاڵاکان، گەڕان، سەبەتەی کڕین و پانێڵی بەڕێوەبردن...",
        buildButtonLabel = "دروستکردنی پڕۆژە",
        applyFollowUpButtonLabel = "جێبەجێکردنی گۆڕانکاری",
        stopGenerationLabel = "وەستاندن",
        attachImageLabel = "هاوپێچکردنی وێنە",
        attachFileLabel = "هاوپێچکردنی فایل",
        starterPromptsTitle = "نموونەی داواکارییەکان",
        followUpChipsTitle = "گۆڕانکاریی خێرا لەسەر پڕۆژەکە",
        promptHistoryTitle = "مێژووی پڕۆمپت و گۆڕانکارییەکان",
        emptyHistoryHint = "وەسفی بەرنامەکەت لە سەرەوە بنووسە بۆ دروستکردنی فایلە ڕاستەقینەکانی پڕۆژە.",
        retryAction = "دووبارە هەوڵدانەوە",
        externalSetupTitle = "پێداویستییەکانی ڕێکخستنی دەرەکی",
        viewportMobile = "مۆبایل",
        viewportDesktop = "کۆمپیوتەر",
        refreshPreview = "نوێکردنەوەی پێشبینین",
        previewLoadingLabel = "ئامادەکردنی پێشبینینی زیندوو...",
        previewUnavailableTitle = "پێشبینین هێشتا ئامادە نییە",
        previewUnavailableEmptyBody = "سەرەتا پڕۆژەکە دروست بکە یان فایلی index.html زیاد بکە بۆ بینینی پێشبینینی ڕاستەقینە.",
        previewMissingHtmlError = "فایلی index.html لەم پڕۆژەیەدا نەدۆزرایەوە.",
        androidPreviewNoticeTitle = "پشکنەری پڕۆژەی ئەندرۆید (Source Project Inspector)",
        androidPreviewNoticeBody =
          "ئەم پڕۆژەیە وەک کۆدی سەرچاوەی تەواوی ئەندرۆید (Kotlin & Jetpack Compose) دروست کراوە. وەرگێڕانی ڕاستەوخۆی APK لەسەر ئامێرەکە لە قۆناغی داهاتوودا کارا دەکرێت؛ لێرەدا دەتوانیت هەموو فایلەکان بپشکنیت، دەستکاری بکەیت و وەک ZIP هەناردەی بکەیت.",
        fileExplorerTitle = "درەختی فایلەکانی پڕۆژە",
        noFilesGeneratedYet = "هیچ فایلێک هێشتا لەم پڕۆژەیەدا نییە. لە بەشی پڕۆمپتەوە دەست پێ بکە یان فایلێکی نوێ زیاد بکە.",
        fileGeneratedBadge = "دروستکراو",
        fileEditedBadge = "دەستکاریکراو",
        viewModeLabel = "پیشاندان",
        editModeLabel = "دەستکاری",
        copyCodeAction = "کۆپیکردنی کۆد",
        saveFileEditsAction = "پاشەکەوتکردنی فایل",
        regenerateFileAction = "دووبارە دروستکردنەوەی فایل بە AI",
        regenerateFileHint = "تێبینی بۆ چاککردن یان نوێکردنەوەی ئەم فایلە بنووسە...",
        newFileAction = "فایلی نوێ",
        newFilePathLabel = "ڕێڕەوی فایل (بۆ نموونە: src/helpers.js)",
        createFileConfirmAction = "زیادکردنی فایل",
        deleteDialogTitle = "سڕینەوەی پڕۆژە",
        deleteDialogMessage = "ئایا دڵنیایت لە سڕینەوەی ئەم پڕۆژەیە؟ ئەم کردارە ناگەڕێتەوە.",
        restoreDialogTitle = "گەڕاندنەوەی وەشانی پێشوو",
        restoreDialogMessage = "ئایا دەتەوێت فایلەکانی پڕۆژەکە بگەڕێنیتەوە بۆ وەشانی پێش دوایین گۆڕانکاری؟",
        confirmYes = "بەڵێ، جێبەجێی بکە",
        confirmCancel = "پاشگەزبوونەوە",
        copiedFeedback = "کۆدەکە کۆپی کرا بۆ کلیپبۆرد",
      )
    AppLanguage.ARABIC ->
      BasoBuilderUiStrings(
        studioBrandTitle = "BASO AI Studio • منشئ التطبيقات",
        newProjectAction = "مشروع جديد",
        openProjectAction = "فتح مشروع",
        renameProjectAction = "إعادة تسمية المشروع",
        saveProjectAction = "حفظ",
        savedBadge = "محفوظ",
        unsavedBadge = "غير محفوظ",
        deleteProjectAction = "حذف المشروع",
        settingsAction = "الإعدادات",
        validateProjectAction = "فحص المشروع",
        exportZipAction = "تصدير ZIP",
        restorePreviousVersionAction = "استعادة النسخة السابقة",
        tabPromptBuild = "الوصف والبناء",
        tabLivePreview = "المعاينة الحية",
        tabCodeFiles = "الملفات والكود",
        modeAndroidApp = "تطبيق أندرويد",
        modeWebsite = "موقع ويب",
        modeWebApp = "تطبيق ويب (Web App)",
        statusProjectCreated = "تم إنشاء المشروع",
        statusGenerating = "جارٍ التوليد بواسطة الذكاء الاصطناعي...",
        statusFilesGenerated = "تم توليد الملفات",
        statusValidationCompleted = "اكتمل التحقق",
        statusPreviewRunning = "المعاينة قيد التشغيل",
        statusExportCompleted = "اكتمل تصدير ZIP",
        statusBuildCompleted = "اكتمل تجهيز حزمة المصدر",
        statusError = "خطأ في التوليد أو التحقق",
        providerReadyLabel = "اتصال خدمة HTTPS AI جاهز",
        providerUnconfiguredLabel = "يلزم إعداد خدمة الذكاء الاصطناعي (AI)",
        providerUnconfiguredDetail =
          "لإجراء توليد حقيقي عبر الذكاء الاصطناعي دون تضمين مفاتيح خاصة داخل APK، يرجى ربط نقطة نهاية خادم HTTPS AI متوافق. تظل بيئة تحرير الملفات والمعاينة وتصدير ZIP متاحة بالكامل.",
        promptLabelInitial = "صف التطبيق أو الموقع الذي تريد بناءه",
        promptLabelFollowUp = "تعديل إضافي على المشروع الحالي (Follow-up)",
        promptPlaceholder =
          "مثال: أنشئ متجراً إلكترونياً احترافياً مع أقسام المنتجات، البحث، سلة التسوق ولوحة تحكم...",
        buildButtonLabel = "بناء المشروع",
        applyFollowUpButtonLabel = "تطبيق التعديل",
        stopGenerationLabel = "إيقاف",
        attachImageLabel = "إرفاق صورة مرجعية",
        attachFileLabel = "إرفاق ملف",
        starterPromptsTitle = "أمثلة سريعة للبناء",
        followUpChipsTitle = "تعديلات سريعة على المشروع الحالي",
        promptHistoryTitle = "سجل الأوامر والتحديثات",
        emptyHistoryHint = "اكتب وصف التطبيق أعلاه لتوليد ملفات المشروع الفعلية.",
        retryAction = "إعادة المحاولة",
        externalSetupTitle = "متطلبات الإعداد الخارجي",
        viewportMobile = "جوال",
        viewportDesktop = "سطح المكتب",
        refreshPreview = "تحديث المعاينة",
        previewLoadingLabel = "جارٍ تحميل المعاينة الحية...",
        previewUnavailableTitle = "المعاينة غير متاحة بعد",
        previewUnavailableEmptyBody = "قم ببناء المشروع أولاً أو أضف ملف index.html لتشغيل المعاينة الفعلية.",
        previewMissingHtmlError = "لم يتم العثور على ملف index.html في هذا المشروع.",
        androidPreviewNoticeTitle = "فاحص مشروع أندرويد المصدري (Android Source Inspector)",
        androidPreviewNoticeBody =
          "تم توليد هذا المشروع ككود مصدري كامل لأندرويد (Kotlin & Jetpack Compose). لا يتم تزييف لقطة شاشة لتطبيق يعمل؛ يمكنك فحص جميع الملفات وتعديلها وتصديرها كملف ZIP.",
        fileExplorerTitle = "شجرة ملفات المشروع",
        noFilesGeneratedYet = "لا توجد ملفات في هذا المشروع بعد.",
        fileGeneratedBadge = "مُولّد",
        fileEditedBadge = "مُعدّل",
        viewModeLabel = "عرض",
        editModeLabel = "تحرير",
        copyCodeAction = "نسخ الكود",
        saveFileEditsAction = "حفظ التعديلات",
        regenerateFileAction = "إعادة توليد الملف بالذكاء الاصطناعي",
        regenerateFileHint = "اكتب تعليمات لإصلاح أو تحديث هذا الملف...",
        newFileAction = "ملف جديد",
        newFilePathLabel = "مسار الملف النسبي (مثال: src/helpers.js)",
        createFileConfirmAction = "إضافة الملف",
        deleteDialogTitle = "حذف المشروع",
        deleteDialogMessage = "هل أنت متأكد من حذف هذا المشروع؟",
        restoreDialogTitle = "استعادة النسخة السابقة",
        restoreDialogMessage = "هل تريد استعادة ملفات المشروع إلى النسخة المحفوظة السابقة؟",
        confirmYes = "نعم، تأكيد",
        confirmCancel = "إلغاء",
        copiedFeedback = "تم نسخ الكود إلى الحافظة",
      )
    AppLanguage.ENGLISH ->
      BasoBuilderUiStrings(
        studioBrandTitle = "BASO AI Studio • App Builder",
        newProjectAction = "New Project",
        openProjectAction = "Open Project",
        renameProjectAction = "Rename Project",
        saveProjectAction = "Save",
        savedBadge = "Saved",
        unsavedBadge = "Unsaved",
        deleteProjectAction = "Delete Project",
        settingsAction = "Settings",
        validateProjectAction = "Validate Project",
        exportZipAction = "Export ZIP",
        restorePreviousVersionAction = "Restore Previous Version",
        tabPromptBuild = "Prompt & Build",
        tabLivePreview = "Live Preview",
        tabCodeFiles = "Code & Files",
        modeAndroidApp = "Android App",
        modeWebsite = "Website",
        modeWebApp = "Web App",
        statusProjectCreated = "Project created",
        statusGenerating = "AI generation in progress",
        statusFilesGenerated = "Files generated",
        statusValidationCompleted = "Validation completed",
        statusPreviewRunning = "Preview running",
        statusExportCompleted = "Export completed",
        statusBuildCompleted = "Build completed",
        statusError = "Generation or validation error",
        providerReadyLabel = "HTTPS AI Endpoint Ready",
        providerUnconfiguredLabel = "AI Service Setup Required",
        providerUnconfiguredDetail =
          "Connect a compatible HTTPS AI backend endpoint and model to run live AI builds without embedding private API keys in the APK. The workspace, code editor, validator, and ZIP exporter remain fully functional.",
        promptLabelInitial = "Describe the application you want to build",
        promptLabelFollowUp = "Follow-up instruction to modify current project",
        promptPlaceholder =
          "Create a professional Kurdish online store with product categories, search, product details, a shopping cart, and an admin dashboard.",
        buildButtonLabel = "Build Project",
        applyFollowUpButtonLabel = "Apply Follow-Up",
        stopGenerationLabel = "Stop",
        attachImageLabel = "Attach Image",
        attachFileLabel = "Attach File",
        starterPromptsTitle = "Starter Build Prompts",
        followUpChipsTitle = "Follow-Up Project Iterations",
        promptHistoryTitle = "Prompt & Iteration History",
        emptyHistoryHint = "Describe your app above to generate real project source files.",
        retryAction = "Retry",
        externalSetupTitle = "External Service Setup Requirements",
        viewportMobile = "Mobile",
        viewportDesktop = "Desktop",
        refreshPreview = "Refresh Preview",
        previewLoadingLabel = "Loading isolated live preview...",
        previewUnavailableTitle = "Live Preview Not Ready Yet",
        previewUnavailableEmptyBody = "Generate project files first (or add index.html) to run the live application preview.",
        previewMissingHtmlError = "No index.html entry file found in this project.",
        androidPreviewNoticeTitle = "Android Source Project Inspector",
        androidPreviewNoticeBody =
          "This Android project contains organized Kotlin & Jetpack Compose source files. Direct on-device APK compilation is reserved for a subsequent phase; inspect, edit, validate, and export the complete Android source archive below.",
        fileExplorerTitle = "Project File Explorer",
        noFilesGeneratedYet = "No files generated yet. Submit a build prompt or create a file manually.",
        fileGeneratedBadge = "Generated",
        fileEditedBadge = "Edited",
        viewModeLabel = "View",
        editModeLabel = "Edit",
        copyCodeAction = "Copy Code",
        saveFileEditsAction = "Save Edits",
        regenerateFileAction = "Regenerate File with AI",
        regenerateFileHint = "Optional instruction to fix or update this file...",
        newFileAction = "New File",
        newFilePathLabel = "Relative file path (e.g. src/helpers.js)",
        createFileConfirmAction = "Create File",
        deleteDialogTitle = "Delete Project",
        deleteDialogMessage = "Are you sure you want to delete this project and its files?",
        restoreDialogTitle = "Restore Previous Version",
        restoreDialogMessage = "Restore the project files to the previous saved snapshot?",
        confirmYes = "Confirm",
        confirmCancel = "Cancel",
        copiedFeedback = "Copied code to clipboard",
      )
  }

/**
 * PHASE 4: Google AI Studio-Style App Builder Workspace inside BASO AI.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BasoAppBuilderScreen(
  builderEngine: BasoBuilderAiEngine,
  currentLanguage: AppLanguage,
  projects: List<BasoBuilderProject>,
  activeProjectId: String?,
  onSelectProject: (String) -> Unit,
  onUpdateProject: (BasoBuilderProject) -> Unit,
  onCreateNewProject: (name: String, type: BasoBuilderProjectType) -> Unit,
  onDeleteProject: (String) -> Unit,
  onOpenSettings: () -> Unit,
  onShowFeedback: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()
  val strings = remember(currentLanguage) { builderStringsForLanguage(currentLanguage) }

  val activeProject =
    remember(projects, activeProjectId) {
      projects.firstOrNull { it.id == activeProjectId }
        ?: projects.firstOrNull()
        ?: BasoBuilderProject(
          id = "proj_builder_default",
          name = "فرۆشگای سەرهێڵی کوردی",
          projectType = BasoBuilderProjectType.WEB_APP,
        )
    }

  var activeTab by rememberSaveable { mutableStateOf(BasoBuilderWorkspaceTab.PROMPT) }
  var viewportMode by rememberSaveable { mutableStateOf(BasoBuilderViewportMode.MOBILE) }
  var selectedFilePath by rememberSaveable(activeProject.id) {
    mutableStateOf(activeProject.files.firstOrNull()?.path ?: activeProject.projectType.defaultEntryPath)
  }

  var promptInput by rememberSaveable { mutableStateOf("") }
  var activeAttachment by remember { mutableStateOf<BasoBuilderAttachment?>(null) }
  var activeGenerationJob by remember { mutableStateOf<Job?>(null) }
  var lastErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }
  var lastFailedPrompt by rememberSaveable { mutableStateOf<String?>(null) }

  // Dialog states
  var showProjectSwitcherDialog by rememberSaveable { mutableStateOf(false) }
  var showNewProjectDialog by rememberSaveable { mutableStateOf(false) }
  var showRenameDialog by rememberSaveable { mutableStateOf(false) }
  var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }
  var showRestoreConfirmDialog by rememberSaveable { mutableStateOf(false) }

  val isGenerating = activeProject.status == BasoBuilderLifecycleStatus.AI_GENERATION_IN_PROGRESS

  // Zero-permission Photo Picker for attaching a reference UI image
  val pickImageLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      if (uri != null) {
        try {
          val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
          if (bytes != null && bytes.isNotEmpty()) {
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
            activeAttachment =
              BasoBuilderAttachment(
                name = "reference_image.jpg",
                mimeType = mime,
                base64Data = base64,
              )
          }
        } catch (_: Exception) {
          onShowFeedback("Could not read reference image.")
        }
      }
    }

  // Document picker for attaching a text/JSON specification file
  val pickSpecFileLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
      if (uri != null) {
        try {
          val text =
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
          if (!text.isNullOrBlank()) {
            activeAttachment =
              BasoBuilderAttachment(
                name = "project_spec.txt",
                mimeType = "text/plain",
                textContent = text.take(4000),
              )
          }
        } catch (_: Exception) {
          onShowFeedback("Could not read attached file.")
        }
      }
    }

  val executeBuildPrompt: (String) -> Unit = { rawPrompt ->
    val trimmed = rawPrompt.trim()
    if (trimmed.isNotEmpty() && !isGenerating) {
      lastErrorMessage = null
      lastFailedPrompt = null
      val isFollowUp = activeProject.files.isNotEmpty()
      val preservedSnapshots =
        if (isFollowUp) {
          (
            activeProject.previousVersions +
              BasoBuilderVersionSnapshot(
                versionNumber = activeProject.previousVersions.size + 1,
                timestampLabel = "v${activeProject.previousVersions.size + 1}",
                reasonSummary = trimmed.take(50),
                files = activeProject.files,
              )
            ).takeLast(5)
        } else {
          activeProject.previousVersions
        }

      val generatingProject =
        activeProject.copy(
          status = BasoBuilderLifecycleStatus.AI_GENERATION_IN_PROGRESS,
          previousVersions = preservedSnapshots,
          isSaved = false,
        )
      onUpdateProject(generatingProject)

      val attachmentToSend = activeAttachment
      val turnId = "turn_${System.currentTimeMillis()}"

      activeGenerationJob =
        coroutineScope.launch {
          val result =
            builderEngine.buildOrModifyProject(
              project = generatingProject,
              userPrompt = trimmed,
              attachment = attachmentToSend,
              appLanguage = currentLanguage,
              turnId = turnId,
            )

          when (result) {
            is BasoAiResult.Success -> {
              val outcome = result.data
              val providerTag = if (result.isFailover) "${result.providerUsed} (Failover)" else result.providerUsed
              val newTurn =
                BasoBuilderPromptTurn(
                  id = turnId,
                  prompt = trimmed,
                  attachmentName = attachmentToSend?.name,
                  timestampLabel = "$providerTag • ${outcome.modelUsed}",
                  summaryResponse = outcome.summary,
                  architectureNotes = outcome.architectureNotes,
                  changedFiles = outcome.changedFilePaths,
                  isFollowUp = isFollowUp,
                  isError = false,
                  modelUsed = outcome.modelUsed,
                )
              val updatedProject =
                generatingProject.copy(
                  name =
                    if (!isFollowUp && outcome.suggestedProjectName.isNotBlank()) {
                      outcome.suggestedProjectName
                    } else {
                      generatingProject.name
                    },
                  status = BasoBuilderLifecycleStatus.FILES_GENERATED,
                  files = outcome.mergedFiles,
                  promptHistory = generatingProject.promptHistory + newTurn,
                  externalSetupNotes = outcome.externalSetupNotes,
                  isSaved = true,
                  lastSavedTimestamp = "ئێستا",
                )
              onUpdateProject(updatedProject)
              selectedFilePath =
                outcome.changedFilePaths.firstOrNull()
                  ?: outcome.mergedFiles.firstOrNull()?.path
                  ?: selectedFilePath
              promptInput = ""
              activeAttachment = null
            }

            is BasoAiResult.Failure -> {
              lastFailedPrompt = trimmed
              lastErrorMessage = result.safeMessage
              val errorTurn =
                BasoBuilderPromptTurn(
                  id = turnId,
                  prompt = trimmed,
                  attachmentName = attachmentToSend?.name,
                  timestampLabel = "Error",
                  summaryResponse = result.safeMessage,
                  isFollowUp = isFollowUp,
                  isError = true,
                )
              onUpdateProject(
                generatingProject.copy(
                  status = BasoBuilderLifecycleStatus.ERROR,
                  promptHistory = generatingProject.promptHistory + errorTurn,
                ),
              )
            }
          }
        }
    }
  }

  val stopGeneration: () -> Unit = {
    activeGenerationJob?.cancel()
    activeGenerationJob = null
    onUpdateProject(
      activeProject.copy(
        status =
          if (activeProject.files.isNotEmpty()) {
            BasoBuilderLifecycleStatus.FILES_GENERATED
          } else {
            BasoBuilderLifecycleStatus.PROJECT_CREATED
          },
      ),
    )
  }

  val triggerValidation: () -> Unit = {
    val report =
      BasoBuilderValidatorAndExporter.validateProjectFiles(
        projectType = activeProject.projectType,
        files = activeProject.files,
      )
    val annotated =
      activeProject.files.map { file ->
        file.copy(validationErrors = report.fileErrorsMap[file.path] ?: emptyList())
      }
    onUpdateProject(
      activeProject.copy(
        files = annotated,
        status =
          if (report.isValid) {
            BasoBuilderLifecycleStatus.VALIDATION_COMPLETED
          } else {
            BasoBuilderLifecycleStatus.ERROR
          },
        lastValidationSummary = report.summaryMessage,
        isSaved = true,
      ),
    )
    onShowFeedback(report.summaryMessage)
  }

  val triggerZipExport: () -> Unit = {
    if (activeProject.files.isEmpty()) {
      onShowFeedback(strings.noFilesGeneratedYet)
    } else {
      val exportedFile = BasoBuilderValidatorAndExporter.exportProjectToZipFile(context, activeProject)
      onUpdateProject(
        activeProject.copy(
          status = BasoBuilderLifecycleStatus.EXPORT_COMPLETED,
          lastExportPath = exportedFile.absolutePath,
          isSaved = true,
        ),
      )
      onShowFeedback("ZIP: ${exportedFile.name} (${activeProject.files.size} files)")
    }
  }

  Column(
    modifier =
      modifier
        .fillMaxSize()
        .background(BasoPrimaryBackground)
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .testTag("baso_app_builder_screen"),
  ) {
    // A. Top Application Bar
    BasoBuilderTopBar(
      project = activeProject,
      strings = strings,
      onBack = onBack,
      onNewProjectClick = { showNewProjectDialog = true },
      onOpenProjectSwitcher = { showProjectSwitcherDialog = true },
      onRenameClick = { showRenameDialog = true },
      onSaveClick = {
        onUpdateProject(
          activeProject.copy(
            isSaved = true,
            lastSavedTimestamp = "ئێستا",
            status =
              if (activeProject.files.isNotEmpty()) {
                BasoBuilderLifecycleStatus.BUILD_COMPLETED
              } else {
                activeProject.status
              },
          ),
        )
        onShowFeedback(strings.savedBadge)
      },
      onValidateClick = triggerValidation,
      onExportZipClick = triggerZipExport,
      onRestoreVersionClick = {
        if (activeProject.previousVersions.isNotEmpty()) {
          showRestoreConfirmDialog = true
        }
      },
      onDeleteProjectClick = { showDeleteConfirmDialog = true },
      onOpenSettings = onOpenSettings,
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Connection & Separated Lifecycle Status Bar
    BuilderStatusBanner(
      configMode = builderEngine.configurationMode(),
      project = activeProject,
      strings = strings,
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Workspace Section Switcher: Prompt & Build | Live Preview | Code & Files
    val workspaceTabs =
      listOf(
        Triple(BasoBuilderWorkspaceTab.PROMPT, strings.tabPromptBuild, Icons.Default.AutoAwesome),
        Triple(BasoBuilderWorkspaceTab.PREVIEW, strings.tabLivePreview, Icons.Default.PlayArrow),
        Triple(
          BasoBuilderWorkspaceTab.CODE,
          "${strings.tabCodeFiles} (${activeProject.files.size})",
          Icons.Default.Code,
        ),
      )

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      workspaceTabs.forEach { (tab, label, icon) ->
        val selected = activeTab == tab
        Surface(
          color = if (selected) BasoCyanPill else BasoSurfaceCard,
          shape = RoundedCornerShape(12.dp),
          border =
            BorderStroke(
              width = 1.dp,
              color = if (selected) BasoNeonTurquoise else BasoBorder,
            ),
          modifier =
            Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { activeTab = tab }
              .testTag("builder_tab_${tab.name.lowercase()}"),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
          ) {
            Icon(
              imageVector = icon,
              contentDescription = label,
              tint = if (selected) BasoPrimaryBackground else BasoNeonTurquoise,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = label,
              style =
                MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                ),
              color = if (selected) BasoPrimaryBackground else BasoPrimaryText,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Active Workspace Panel
    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
      when (activeTab) {
        BasoBuilderWorkspaceTab.PROMPT ->
          BuilderPromptAndBuildPanel(
            project = activeProject,
            strings = strings,
            promptInput = promptInput,
            onPromptInputChange = { promptInput = it },
            activeAttachment = activeAttachment,
            onAttachImage = {
              pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
              )
            },
            onAttachFile = { pickSpecFileLauncher.launch("*/*") },
            onClearAttachment = { activeAttachment = null },
            onChangeProjectType = { newType ->
              onUpdateProject(activeProject.copy(projectType = newType, isSaved = false))
            },
            isGenerating = isGenerating,
            onSubmitPrompt = { executeBuildPrompt(promptInput) },
            onStopGeneration = stopGeneration,
            lastErrorMessage = lastErrorMessage,
            onRetryFailedPrompt =
              if (lastFailedPrompt != null) {
                { executeBuildPrompt(lastFailedPrompt!!) }
              } else {
                null
              },
            onRequestRestoreVersion = { showRestoreConfirmDialog = true },
            onOpenFileInCodeTab = { path ->
              selectedFilePath = path
              activeTab = BasoBuilderWorkspaceTab.CODE
            },
          )

        BasoBuilderWorkspaceTab.PREVIEW ->
          BuilderLivePreviewPanel(
            project = activeProject,
            strings = strings,
            viewportMode = viewportMode,
            onSelectViewport = { viewportMode = it },
            onPreviewStarted = {
              if (activeProject.status != BasoBuilderLifecycleStatus.PREVIEW_RUNNING) {
                onUpdateProject(activeProject.copy(status = BasoBuilderLifecycleStatus.PREVIEW_RUNNING))
              }
            },
            onOpenFileInCodeTab = { path ->
              selectedFilePath = path
              activeTab = BasoBuilderWorkspaceTab.CODE
            },
            onExportZip = triggerZipExport,
          )

        BasoBuilderWorkspaceTab.CODE ->
          BuilderCodeAndFilesPanel(
            project = activeProject,
            selectedFilePath = selectedFilePath,
            onSelectFilePath = { selectedFilePath = it },
            onSaveFileContent = { path, updatedContent ->
              val updatedFiles =
                activeProject.files.map { file ->
                  if (file.path == path) {
                    file.copy(content = updatedContent, isModifiedByUser = true)
                  } else {
                    file
                  }
                }
              val validation =
                BasoBuilderValidatorAndExporter.validateProjectFiles(
                  projectType = activeProject.projectType,
                  files = updatedFiles,
                )
              val annotated =
                updatedFiles.map { f ->
                  f.copy(validationErrors = validation.fileErrorsMap[f.path] ?: emptyList())
                }
              onUpdateProject(
                activeProject.copy(
                  files = annotated,
                  isSaved = true,
                  lastSavedTimestamp = "ئێستا",
                  lastValidationSummary = validation.summaryMessage,
                ),
              )
              onShowFeedback(strings.savedBadge)
            },
            onAddNewFile = { safePath, initialContent ->
              if (activeProject.files.none { it.path == safePath }) {
                val newFile =
                  BasoBuilderFile(
                    path = safePath,
                    content = initialContent,
                    isModifiedByUser = true,
                  )
                onUpdateProject(
                  activeProject.copy(
                    files = activeProject.files + newFile,
                    status = BasoBuilderLifecycleStatus.FILES_GENERATED,
                    isSaved = true,
                  ),
                )
                selectedFilePath = safePath
              }
            },
            onRegenerateFile = { path, instruction ->
              if (!isGenerating) {
                onUpdateProject(
                  activeProject.copy(status = BasoBuilderLifecycleStatus.AI_GENERATION_IN_PROGRESS),
                )
                coroutineScope.launch {
                  when (
                    val res =
                      builderEngine.regenerateSingleFile(
                        project = activeProject,
                        targetFilePath = path,
                        customInstruction = instruction,
                        appLanguage = currentLanguage,
                      )
                  ) {
                    is BasoAiResult.Success -> {
                      onUpdateProject(
                        activeProject.copy(
                          files = res.data.mergedFiles,
                          status = BasoBuilderLifecycleStatus.FILES_GENERATED,
                          isSaved = true,
                        ),
                      )
                      onShowFeedback(res.data.summary)
                    }
                    is BasoAiResult.Failure -> {
                      lastErrorMessage = res.safeMessage
                      onUpdateProject(activeProject.copy(status = BasoBuilderLifecycleStatus.ERROR))
                      onShowFeedback(res.safeMessage)
                    }
                  }
                }
              }
            },
            onValidateProject = triggerValidation,
            onExportZip = triggerZipExport,
            onCopyText = { text ->
              clipboardManager.setText(AnnotatedString(text))
              onShowFeedback(strings.copiedFeedback)
            },
            isGenerating = isGenerating,
            strings = strings,
          )
      }
    }
  }

  // Dialog: Create New Project
  if (showNewProjectDialog) {
    NewBuilderProjectDialog(
      strings = strings,
      onConfirm = { name, type ->
        showNewProjectDialog = false
        onCreateNewProject(name, type)
      },
      onDismiss = { showNewProjectDialog = false },
    )
  }

  // Dialog: Switch / Open Project
  if (showProjectSwitcherDialog) {
    OpenBuilderProjectDialog(
      projects = projects,
      activeProjectId = activeProject.id,
      strings = strings,
      onSelect = { id ->
        showProjectSwitcherDialog = false
        onSelectProject(id)
      },
      onDismiss = { showProjectSwitcherDialog = false },
    )
  }

  // Dialog: Rename Project
  if (showRenameDialog) {
    RenameBuilderProjectDialog(
      currentName = activeProject.name,
      strings = strings,
      onConfirm = { newName ->
        showRenameDialog = false
        onUpdateProject(activeProject.copy(name = newName, isSaved = true))
      },
      onDismiss = { showRenameDialog = false },
    )
  }

  // Dialog: Delete Project Confirmation
  if (showDeleteConfirmDialog) {
    BasoConfirmationDialog(
      title = strings.deleteDialogTitle,
      message = strings.deleteDialogMessage,
      confirmLabel = strings.confirmYes,
      cancelLabel = strings.confirmCancel,
      onConfirm = {
        showDeleteConfirmDialog = false
        onDeleteProject(activeProject.id)
      },
      onDismiss = { showDeleteConfirmDialog = false },
    )
  }

  // Dialog: Restore Previous Snapshot Confirmation
  if (showRestoreConfirmDialog && activeProject.previousVersions.isNotEmpty()) {
    val latestSnapshot = activeProject.previousVersions.last()
    BasoConfirmationDialog(
      title = strings.restoreDialogTitle,
      message = "${strings.restoreDialogMessage} (${latestSnapshot.timestampLabel})",
      confirmLabel = strings.confirmYes,
      cancelLabel = strings.confirmCancel,
      onConfirm = {
        showRestoreConfirmDialog = false
        onUpdateProject(
          activeProject.copy(
            files = latestSnapshot.files,
            previousVersions = activeProject.previousVersions.dropLast(1),
            status = BasoBuilderLifecycleStatus.FILES_GENERATED,
            isSaved = true,
          ),
        )
      },
      onDismiss = { showRestoreConfirmDialog = false },
    )
  }
}

@Composable
private fun BasoBuilderTopBar(
  project: BasoBuilderProject,
  strings: BasoBuilderUiStrings,
  onBack: () -> Unit,
  onNewProjectClick: () -> Unit,
  onOpenProjectSwitcher: () -> Unit,
  onRenameClick: () -> Unit,
  onSaveClick: () -> Unit,
  onValidateClick: () -> Unit,
  onExportZipClick: () -> Unit,
  onRestoreVersionClick: () -> Unit,
  onDeleteProjectClick: () -> Unit,
  onOpenSettings: () -> Unit,
) {
  var menuExpanded by remember { mutableStateOf(false) }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.weight(1f),
    ) {
      IconButton(
        onClick = onBack,
        modifier =
          Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(BasoSurfaceCard)
            .testTag("builder_back_button"),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = BasoPrimaryText,
          modifier = Modifier.size(18.dp),
        )
      }

      BasoBrandEmblem(size = 28.dp)

      Column(
        modifier =
          Modifier
            .weight(1f)
            .clickable(onClick = onRenameClick),
      ) {
        Text(
          text = strings.studioBrandTitle,
          style = MaterialTheme.typography.labelMedium,
          color = BasoNeonTurquoise,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = project.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = BasoPrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("builder_project_name_label"),
          )
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = strings.renameProjectAction,
            tint = BasoSecondaryText,
            modifier = Modifier.size(14.dp),
          )
        }
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      // Save / Saved Status Indicator Pill
      Surface(
        color = if (project.isSaved) BasoSuccessSubtleBg else BasoYellowSubtleGlow,
        shape = RoundedCornerShape(10.dp),
        border =
          BorderStroke(
            width = 1.dp,
            color = if (project.isSaved) BasoSuccess.copy(alpha = 0.6f) else BasoNeonYellow,
          ),
        modifier =
          Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onSaveClick)
            .testTag("builder_save_status_pill"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
        ) {
          Icon(
            imageVector = if (project.isSaved) Icons.Default.CheckCircle else Icons.Default.Save,
            contentDescription = null,
            tint = if (project.isSaved) BasoSuccess else BasoNeonYellow,
            modifier = Modifier.size(13.dp),
          )
          Text(
            text = if (project.isSaved) strings.savedBadge else strings.saveProjectAction,
            style = MaterialTheme.typography.labelMedium,
            color = BasoPrimaryText,
          )
        }
      }

      // New Project quick button
      IconButton(
        onClick = onNewProjectClick,
        modifier =
          Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(BasoNeonYellow)
            .testTag("builder_new_project_button"),
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = strings.newProjectAction,
          tint = BasoPrimaryBackground,
          modifier = Modifier.size(18.dp),
        )
      }

      // Project Menu (Open, Rename, Validate, Export ZIP, Restore, Delete, Settings)
      Box {
        IconButton(
          onClick = { menuExpanded = true },
          modifier =
            Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(BasoSurfaceCard)
              .testTag("builder_project_menu_button"),
        ) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Project Menu",
            tint = BasoPrimaryText,
            modifier = Modifier.size(18.dp),
          )
        }

        DropdownMenu(
          expanded = menuExpanded,
          onDismissRequest = { menuExpanded = false },
          modifier =
            Modifier
              .background(BasoSurfaceCard)
              .border(1.dp, BasoBorder, RoundedCornerShape(12.dp)),
        ) {
          DropdownMenuItem(
            text = { Text(strings.openProjectAction, color = BasoPrimaryText) },
            leadingIcon = { Icon(Icons.Default.FolderOpen, null, tint = BasoNeonTurquoise) },
            onClick = {
              menuExpanded = false
              onOpenProjectSwitcher()
            },
            modifier = Modifier.testTag("builder_menu_open_project"),
          )
          DropdownMenuItem(
            text = { Text(strings.renameProjectAction, color = BasoPrimaryText) },
            leadingIcon = { Icon(Icons.Default.Edit, null, tint = BasoNeonYellow) },
            onClick = {
              menuExpanded = false
              onRenameClick()
            },
            modifier = Modifier.testTag("builder_menu_rename_project"),
          )
          DropdownMenuItem(
            text = { Text(strings.validateProjectAction, color = BasoPrimaryText) },
            leadingIcon = { Icon(Icons.Default.Verified, null, tint = BasoNeonTurquoise) },
            onClick = {
              menuExpanded = false
              onValidateClick()
            },
            modifier = Modifier.testTag("builder_menu_validate_project"),
          )
          DropdownMenuItem(
            text = { Text(strings.exportZipAction, color = BasoPrimaryText) },
            leadingIcon = { Icon(Icons.Default.FolderZip, null, tint = BasoNeonYellow) },
            onClick = {
              menuExpanded = false
              onExportZipClick()
            },
            modifier = Modifier.testTag("builder_menu_export_zip"),
          )
          if (project.previousVersions.isNotEmpty()) {
            DropdownMenuItem(
              text = { Text(strings.restorePreviousVersionAction, color = BasoNeonTurquoise) },
              leadingIcon = { Icon(Icons.Default.History, null, tint = BasoNeonTurquoise) },
              onClick = {
                menuExpanded = false
                onRestoreVersionClick()
              },
              modifier = Modifier.testTag("builder_menu_restore_version"),
            )
          }
          DropdownMenuItem(
            text = { Text(strings.settingsAction, color = BasoPrimaryText) },
            leadingIcon = { Icon(Icons.Default.Settings, null, tint = BasoSecondaryText) },
            onClick = {
              menuExpanded = false
              onOpenSettings()
            },
            modifier = Modifier.testTag("builder_menu_settings"),
          )
          DropdownMenuItem(
            text = { Text(strings.deleteProjectAction, color = BasoError) },
            leadingIcon = { Icon(Icons.Default.DeleteOutline, null, tint = BasoError) },
            onClick = {
              menuExpanded = false
              onDeleteProjectClick()
            },
            modifier = Modifier.testTag("builder_menu_delete_project"),
          )
        }
      }
    }
  }
}

@Composable
private fun BuilderStatusBanner(
  configMode: BasoAiConfigMode,
  project: BasoBuilderProject,
  strings: BasoBuilderUiStrings,
) {
  val providerReady = configMode != BasoAiConfigMode.UNCONFIGURED
  val lifecycleText =
    when (project.status) {
      BasoBuilderLifecycleStatus.PROJECT_CREATED -> strings.statusProjectCreated
      BasoBuilderLifecycleStatus.AI_GENERATION_IN_PROGRESS -> strings.statusGenerating
      BasoBuilderLifecycleStatus.FILES_GENERATED -> strings.statusFilesGenerated
      BasoBuilderLifecycleStatus.VALIDATION_COMPLETED -> strings.statusValidationCompleted
      BasoBuilderLifecycleStatus.PREVIEW_RUNNING -> strings.statusPreviewRunning
      BasoBuilderLifecycleStatus.EXPORT_COMPLETED -> strings.statusExportCompleted
      BasoBuilderLifecycleStatus.BUILD_COMPLETED -> strings.statusBuildCompleted
      BasoBuilderLifecycleStatus.ERROR -> strings.statusError
    }

  val statusColor =
    when (project.status) {
      BasoBuilderLifecycleStatus.ERROR -> BasoError
      BasoBuilderLifecycleStatus.AI_GENERATION_IN_PROGRESS -> BasoNeonYellow
      BasoBuilderLifecycleStatus.FILES_GENERATED,
      BasoBuilderLifecycleStatus.VALIDATION_COMPLETED,
      BasoBuilderLifecycleStatus.PREVIEW_RUNNING,
      BasoBuilderLifecycleStatus.EXPORT_COMPLETED,
      BasoBuilderLifecycleStatus.BUILD_COMPLETED -> BasoSuccess
      BasoBuilderLifecycleStatus.PROJECT_CREATED -> BasoNeonTurquoise
    }

  Surface(
    color = BasoSecondaryBackground,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.55f)),
    modifier = Modifier.fillMaxWidth().testTag("builder_status_banner"),
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Box(
            modifier =
              Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(statusColor),
          )
          Text(
            text = lifecycleText,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = statusColor,
            modifier = Modifier.testTag("builder_lifecycle_status_text"),
          )
        }

        Text(
          text = if (providerReady) strings.providerReadyLabel else strings.providerUnconfiguredLabel,
          style = MaterialTheme.typography.labelMedium,
          color = if (providerReady) BasoSuccess else BasoNeonYellow,
        )
      }

      if (!providerReady) {
        Text(
          text = strings.providerUnconfiguredDetail,
          style = MaterialTheme.typography.labelMedium,
          color = BasoSecondaryText,
        )
      }
      project.lastExportPath?.let { exportPath ->
        Text(
          text = "ZIP: ${exportPath.substringAfterLast('/')}",
          style = MaterialTheme.typography.labelMedium,
          color = BasoNeonTurquoise,
          modifier = Modifier.testTag("builder_last_export_label"),
        )
      }
    }
  }
}

/**
 * Main Prompt and Build Area (Section 2B & 2E).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BuilderPromptAndBuildPanel(
  project: BasoBuilderProject,
  strings: BasoBuilderUiStrings,
  promptInput: String,
  onPromptInputChange: (String) -> Unit,
  activeAttachment: BasoBuilderAttachment?,
  onAttachImage: () -> Unit,
  onAttachFile: () -> Unit,
  onClearAttachment: () -> Unit,
  onChangeProjectType: (BasoBuilderProjectType) -> Unit,
  isGenerating: Boolean,
  onSubmitPrompt: () -> Unit,
  onStopGeneration: () -> Unit,
  lastErrorMessage: String?,
  onRetryFailedPrompt: (() -> Unit)?,
  onRequestRestoreVersion: () -> Unit,
  onOpenFileInCodeTab: (String) -> Unit,
) {
  val hasExistingFiles = project.files.isNotEmpty()

  val starterPrompts =
    listOf(
      "Create a professional Kurdish online store with product categories, search, product details, a shopping cart, and an admin dashboard.",
      "فرۆشگایەکی سەرهێڵی کوردی دروست بکە بە هاوپۆلی کاڵاکان، گەڕان، سەبەتەی کڕین و پانێڵی بەڕێوەبردن",
      "ئەپێکی بەڕێوەبردنی ئەرک و پڕۆژەی ستۆدیۆ بە ڕووکاری تاریک و فلتەرکردن دروست بکە",
    )

  val followUpSuggestions =
    listOf(
      "Change the interface to a dark theme.",
      "Add user registration and login.",
      "Make the layout responsive.",
      "Add Kurdish, Arabic, and English localization.",
      "Fix the error in this file.",
      "Add a product search feature.",
    )

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize().testTag("builder_prompt_panel"),
  ) {
    // 3 Project Type Creation Modes: Android App | Website | Web App
    item {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        val modes =
          listOf(
            Triple(BasoBuilderProjectType.ANDROID_APP, strings.modeAndroidApp, Icons.Default.Android),
            Triple(BasoBuilderProjectType.WEBSITE, strings.modeWebsite, Icons.Default.Web),
            Triple(BasoBuilderProjectType.WEB_APP, strings.modeWebApp, Icons.Default.Code),
          )
        modes.forEach { (type, label, icon) ->
          val selected = project.projectType == type
          Surface(
            color = if (selected) BasoSecondaryBackground else BasoSurfaceCard,
            shape = RoundedCornerShape(12.dp),
            border =
              BorderStroke(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) BasoNeonYellow else BasoBorder,
              ),
            modifier =
              Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onChangeProjectType(type) }
                .testTag("builder_mode_${type.id}"),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
            ) {
              Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) BasoNeonYellow else BasoSecondaryText,
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = label,
                style =
                  MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                  ),
                color = if (selected) BasoNeonYellow else BasoPrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }
      }
    }

    // Multiline Prompt Composer Card
    item {
      BasoCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          BasoInputField(
            value = promptInput,
            onValueChange = onPromptInputChange,
            label = if (hasExistingFiles) strings.promptLabelFollowUp else strings.promptLabelInitial,
            placeholder = strings.promptPlaceholder,
            singleLine = false,
            minLines = 3,
            testTag = "builder_prompt_input",
          )

          // Active Attachment Chip
          activeAttachment?.let { att ->
            Surface(
              color = BasoSecondaryBackground,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, BasoNeonTurquoise),
              modifier = Modifier.testTag("builder_active_attachment_chip"),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              ) {
                Icon(
                  imageVector = Icons.Default.AttachFile,
                  contentDescription = null,
                  tint = BasoNeonTurquoise,
                  modifier = Modifier.size(15.dp),
                )
                Text(
                  text = att.name,
                  style = MaterialTheme.typography.labelMedium,
                  color = BasoPrimaryText,
                )
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Remove attachment",
                  tint = BasoError,
                  modifier =
                    Modifier
                      .size(16.dp)
                      .clickable(onClick = onClearAttachment),
                )
              }
            }
          }

          // Attachment Buttons + Send/Build or Stop Generation CTA
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Surface(
                color = BasoSecondaryBackground,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BasoBorder),
                modifier =
                  Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onAttachImage)
                    .testTag("builder_attach_image_button"),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                ) {
                  Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = strings.attachImageLabel,
                    tint = BasoNeonTurquoise,
                    modifier = Modifier.size(15.dp),
                  )
                  Text(
                    text = strings.attachImageLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = BasoPrimaryText,
                  )
                }
              }

              Surface(
                color = BasoSecondaryBackground,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BasoBorder),
                modifier =
                  Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onAttachFile)
                    .testTag("builder_attach_file_button"),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                ) {
                  Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = strings.attachFileLabel,
                    tint = BasoNeonYellow,
                    modifier = Modifier.size(15.dp),
                  )
                  Text(
                    text = strings.attachFileLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = BasoPrimaryText,
                  )
                }
              }
            }

            if (isGenerating) {
              Surface(
                color = BasoError,
                shape = RoundedCornerShape(14.dp),
                modifier =
                  Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onStopGeneration)
                    .testTag("builder_stop_button"),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                  Icon(
                    imageVector = Icons.Default.StopCircle,
                    contentDescription = strings.stopGenerationLabel,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                  )
                  Text(
                    text = strings.stopGenerationLabel,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                  )
                }
              }
            } else {
              BasoPrimaryButton(
                text = if (hasExistingFiles) strings.applyFollowUpButtonLabel else strings.buildButtonLabel,
                onClick = onSubmitPrompt,
                enabled = promptInput.isNotBlank(),
                leadingIcon = Icons.AutoMirrored.Filled.Send,
                testTag = "builder_send_button",
              )
            }
          }
        }
      }
    }

    // Starter Prompts or Follow-Up Iteration Chips
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = if (hasExistingFiles) strings.followUpChipsTitle else strings.starterPromptsTitle,
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = BasoNeonTurquoise,
        )
        val chipsToShow = if (hasExistingFiles) followUpSuggestions else starterPrompts
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        ) {
          chipsToShow.forEachIndexed { idx, suggestion ->
            Surface(
              color = BasoSurfaceCard,
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, BasoBorder),
              modifier =
                Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { onPromptInputChange(suggestion) }
                  .testTag("builder_suggestion_chip_$idx"),
            ) {
              Text(
                text = suggestion,
                style = MaterialTheme.typography.labelMedium,
                color = BasoPrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              )
            }
          }
        }
      }
    }

    // Active Generation Progress Indicator
    if (isGenerating) {
      item {
        Surface(
          color = BasoSurfaceCard,
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, BasoNeonYellow),
          modifier = Modifier.fillMaxWidth().testTag("builder_generating_progress_card"),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(14.dp),
          ) {
            CircularProgressIndicator(
              color = BasoNeonYellow,
              strokeWidth = 2.5.dp,
              modifier = Modifier.size(22.dp),
            )
            Text(
              text = strings.statusGenerating,
              style = MaterialTheme.typography.bodyMedium,
              color = BasoPrimaryText,
            )
          }
        }
      }
    }

    // Error Card with Retry Action
    if (lastErrorMessage != null) {
      item {
        Surface(
          color = BasoErrorSubtleBg,
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, BasoError),
          modifier = Modifier.fillMaxWidth().testTag("builder_error_card"),
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(14.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = BasoError,
                modifier = Modifier.size(20.dp),
              )
              Text(
                text = lastErrorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = BasoPrimaryText,
                modifier = Modifier.weight(1f),
              )
            }
            if (onRetryFailedPrompt != null) {
              BasoSecondaryButton(
                text = strings.retryAction,
                onClick = onRetryFailedPrompt,
                leadingIcon = Icons.Default.Refresh,
                highlightTurquoise = true,
                testTag = "builder_retry_button",
              )
            }
          }
        }
      }
    }

    // Version Snapshot Restore Banner when previous versions exist
    if (project.previousVersions.isNotEmpty()) {
      item {
        Surface(
          color = BasoSecondaryBackground,
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, BasoBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          ) {
            Text(
              text =
                "${strings.restorePreviousVersionAction} (${project.previousVersions.last().timestampLabel})",
              style = MaterialTheme.typography.labelMedium,
              color = BasoSecondaryText,
            )
            Text(
              text = strings.restorePreviousVersionAction,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoNeonTurquoise,
              modifier =
                Modifier
                  .clickable(onClick = onRequestRestoreVersion)
                  .testTag("builder_restore_snapshot_button"),
            )
          }
        }
      }
    }

    // External Service Setup Notes (when authentication, DB, payments, or backend are needed)
    if (project.externalSetupNotes.isNotEmpty()) {
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("builder_external_setup_card")) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = strings.externalSetupTitle,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoNeonYellow,
            )
            project.externalSetupNotes.forEach { note ->
              Text(
                text = "• $note",
                style = MaterialTheme.typography.bodyMedium,
                color = BasoSecondaryText,
              )
            }
          }
        }
      }
    }

    // Prompt & Iteration History Section
    item {
      Text(
        text = "${strings.promptHistoryTitle} (${project.promptHistory.size})",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = BasoPrimaryText,
      )
    }

    if (project.promptHistory.isEmpty()) {
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("builder_empty_history_card")) {
          Text(
            text = strings.emptyHistoryHint,
            style = MaterialTheme.typography.bodyMedium,
            color = BasoSecondaryText,
          )
        }
      }
    } else {
      items(project.promptHistory.reversed(), key = { it.id }) { turn ->
        BasoCard(
          modifier = Modifier.fillMaxWidth().testTag("builder_history_turn_${turn.id}"),
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = if (turn.isFollowUp) "Follow-Up Iteration" else "Initial Build",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (turn.isError) BasoError else BasoNeonYellow,
              )
              Text(
                text = turn.timestampLabel,
                style = MaterialTheme.typography.labelMedium,
                color = BasoSecondaryText,
              )
            }

            Text(
              text = turn.prompt,
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
            )

            Text(
              text = turn.summaryResponse,
              style = MaterialTheme.typography.bodyMedium,
              color = if (turn.isError) BasoError else BasoSecondaryText,
            )

            if (turn.changedFiles.isNotEmpty()) {
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                turn.changedFiles.forEach { filePath ->
                  Surface(
                    color = BasoSecondaryBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BasoNeonTurquoise.copy(alpha = 0.5f)),
                    modifier =
                      Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenFileInCodeTab(filePath) }
                        .testTag("builder_turn_file_badge_$filePath"),
                  ) {
                    Text(
                      text = filePath,
                      style = MaterialTheme.typography.labelMedium,
                      color = BasoNeonTurquoise,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun NewBuilderProjectDialog(
  strings: BasoBuilderUiStrings,
  onConfirm: (name: String, type: BasoBuilderProjectType) -> Unit,
  onDismiss: () -> Unit,
) {
  var nameInput by rememberSaveable { mutableStateOf("") }
  var selectedType by rememberSaveable { mutableStateOf(BasoBuilderProjectType.WEB_APP) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BasoSurfaceCard,
    titleContentColor = BasoPrimaryText,
    title = { Text(strings.newProjectAction) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = nameInput,
          onValueChange = { nameInput = it },
          placeholder = { Text("Kurdish Online Store") },
          singleLine = true,
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BasoNeonYellow,
              unfocusedBorderColor = BasoBorder,
              focusedTextColor = BasoPrimaryText,
              unfocusedTextColor = BasoPrimaryText,
            ),
          modifier = Modifier.fillMaxWidth().testTag("builder_new_project_name_input"),
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          BasoBuilderProjectType.entries.forEach { type ->
            val selected = selectedType == type
            Surface(
              color = if (selected) BasoNeonYellow else BasoSecondaryBackground,
              shape = RoundedCornerShape(8.dp),
              modifier =
                Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { selectedType = type }
                  .testTag("builder_dialog_type_${type.id}"),
            ) {
              Text(
                text = type.id.replace('_', ' '),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) BasoPrimaryBackground else BasoPrimaryText,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
              )
            }
          }
        }
      }
    },
    confirmButton = {
      BasoPrimaryButton(
        text = strings.confirmYes,
        onClick = {
          val finalName = nameInput.trim().ifBlank { "پڕۆژەی نوێی BASO" }
          onConfirm(finalName, selectedType)
        },
        testTag = "builder_new_project_confirm_button",
      )
    },
    dismissButton = {
      BasoSecondaryButton(
        text = strings.confirmCancel,
        onClick = onDismiss,
      )
    },
  )
}

@Composable
private fun OpenBuilderProjectDialog(
  projects: List<BasoBuilderProject>,
  activeProjectId: String,
  strings: BasoBuilderUiStrings,
  onSelect: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BasoSurfaceCard,
    titleContentColor = BasoPrimaryText,
    title = { Text(strings.openProjectAction) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        projects.forEach { proj ->
          val isCurrent = proj.id == activeProjectId
          Surface(
            color = if (isCurrent) BasoSecondaryBackground else BasoPrimaryBackground,
            shape = RoundedCornerShape(10.dp),
            border =
              BorderStroke(
                width = 1.dp,
                color = if (isCurrent) BasoNeonTurquoise else BasoBorder,
              ),
            modifier =
              Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { onSelect(proj.id) }
                .testTag("builder_switch_project_${proj.id}"),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.padding(12.dp),
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = proj.name,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = BasoPrimaryText,
                )
                Text(
                  text = "${proj.projectType.id} • ${proj.files.size} files",
                  style = MaterialTheme.typography.labelMedium,
                  color = BasoSecondaryText,
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      BasoSecondaryButton(text = strings.confirmCancel, onClick = onDismiss)
    },
  )
}

@Composable
private fun RenameBuilderProjectDialog(
  currentName: String,
  strings: BasoBuilderUiStrings,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  var nameInput by rememberSaveable(currentName) { mutableStateOf(currentName) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BasoSurfaceCard,
    titleContentColor = BasoPrimaryText,
    title = { Text(strings.renameProjectAction) },
    text = {
      OutlinedTextField(
        value = nameInput,
        onValueChange = { nameInput = it },
        singleLine = true,
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BasoNeonTurquoise,
            unfocusedBorderColor = BasoBorder,
            focusedTextColor = BasoPrimaryText,
            unfocusedTextColor = BasoPrimaryText,
          ),
        modifier = Modifier.fillMaxWidth().testTag("builder_rename_project_input"),
      )
    },
    confirmButton = {
      BasoPrimaryButton(
        text = strings.confirmYes,
        onClick = {
          if (nameInput.isNotBlank()) onConfirm(nameInput.trim())
        },
        testTag = "builder_rename_confirm_button",
      )
    },
    dismissButton = {
      BasoSecondaryButton(text = strings.confirmCancel, onClick = onDismiss)
    },
  )
}
