package com.example.ui.screens

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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.BasoAiChatTurn
import com.example.ai.BasoAiConfigMode
import com.example.ai.BasoAiErrorType
import com.example.ai.BasoAiResult
import com.example.ai.BasoAiService
import com.example.ai.BasoAiToolTab
import com.example.ai.BasoCodeAssistantOutput
import com.example.ai.BasoCodeTaskMode
import com.example.ai.BasoDesignAssistantOutput
import com.example.ai.BasoDesignTaskMode
import com.example.ai.BasoProjectPlanOutput
import com.example.ai.BasoWebsiteBundleOutput
import com.example.ui.components.BasoCard
import com.example.ui.components.BasoConfirmationDialog
import com.example.ui.components.BasoInputField
import com.example.ui.components.BasoPrimaryButton
import com.example.ui.components.BasoSecondaryButton
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.BasoBorder
import com.example.ui.theme.BasoChatBubbleOut
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
import kotlinx.coroutines.launch

data class BasoAiUiStrings(
  val hubTitle: String,
  val hubSubtitle: String,
  val statusConfiguredDirect: String,
  val statusConfiguredBackend: String,
  val statusUnconfiguredTitle: String,
  val statusUnconfiguredBody: String,
  val tabChat: String,
  val tabCode: String,
  val tabWebsite: String,
  val tabDesign: String,
  val tabPlanning: String,
  val newChatLabel: String,
  val chatInputLabel: String,
  val chatInputPlaceholder: String,
  val sendAction: String,
  val retryAction: String,
  val copyAction: String,
  val copiedFeedback: String,
  val loadingMessage: String,
  val emptyChatHint: String,
  // Code Assistant
  val codeInputLabel: String,
  val codeInputPlaceholder: String,
  val codeModeGenerate: String,
  val codeModeExplain: String,
  val codeModeBug: String,
  val codeModeRefactor: String,
  val codeModeExample: String,
  val codeSubmitAction: String,
  val codeSafetyNotice: String,
  // Website Generator
  val webInputLabel: String,
  val webInputPlaceholder: String,
  val webSubmitAction: String,
  val webSafetyNotice: String,
  val copyAllFilesAction: String,
  // Design Assistant
  val designInputLabel: String,
  val designInputPlaceholder: String,
  val designModeConcept: String,
  val designModePalette: String,
  val designModeTypeLayout: String,
  val designModePrompt: String,
  val designModeBrief: String,
  val designModeSocial: String,
  val designSubmitAction: String,
  val designPromptLabel: String,
  val designPromptDisclaimer: String,
  // Project Planning
  val planInputLabel: String,
  val planInputPlaceholder: String,
  val planSubmitAction: String,
  val planObjectivesTitle: String,
  val planMilestonesTitle: String,
  val planTasksTitle: String,
  val planEffortTitle: String,
  val planPrioritiesTitle: String,
  val planSaveDraftButton: String,
  val planConfirmDialogTitle: String,
  val planConfirmDialogBody: String,
  val planConfirmAccept: String,
  val planConfirmCancel: String,
)

fun aiStringsForLanguage(language: AppLanguage): BasoAiUiStrings =
  when (language) {
    AppLanguage.KURDISH_SORANI ->
      BasoAiUiStrings(
        hubTitle = "ئامرازەکانی BASO AI",
        hubSubtitle = "یاریدەدەری زیرەک بە پێنج ئامرازی جیاواز بۆ چات، کۆد، وێبسایت، دیزاین و پلاندانان",
        statusConfiguredDirect = "پەیوەندیی ڕاستەوخۆی خزمەتگوزاریی HTTPS AI چالاکە",
        statusConfiguredBackend = "پەیوەندیی سێرڤەری ناوەندیی HTTPS AI چالاکە",
        statusUnconfiguredTitle = "ڕێکخستنی خزمەتگوزاریی AI پێویستە",
        statusUnconfiguredBody =
          "هیچ دابینکەرێکی AI ڕێکنەخراوە. بۆ کارپێکردنی داواکارییە ڕاستەقینەکانی زیرەکی دەستکرد بێ دانانی کلیل لەناو APK، تکایە بەستەری سێرڤەری HTTPS AI یان APIـی گونجاو پەیوەست بکە. هیچ وەڵامێکی ساختە پیشان نادرێت.",
        tabChat = "چاتی زیرەک",
        tabCode = "یاریدەدەری کۆد",
        tabWebsite = "دروستکەری وێبسایت",
        tabDesign = "یاریدەدەری دیزاین",
        tabPlanning = "پلاندانانی پڕۆژە",
        newChatLabel = "چاتی نوێ",
        chatInputLabel = "پرسیار یان داواکارییەکەت",
        chatInputPlaceholder = "پەیامێک بنووسە بۆ یاریدەدەری زیرەک...",
        sendAction = "ناردن بۆ AI",
        retryAction = "دووبارە هەوڵدانەوە",
        copyAction = "کۆپیکردن",
        copiedFeedback = "دەقەکە کۆپی کرا بۆ کلیپبۆرد",
        loadingMessage = "لە حاڵەتی وەرگرتنی وەڵامی ڕاستەقینەی مۆدێلی AI...",
        emptyChatHint = "گفتوگۆیەکی نوێ دەست پێ بکە بە زمانی کوردی، عەرەبی یان ئینگلیزی.",
        codeInputLabel = "وەسفی ئەرکی بەرنامەنووسی یان کۆد",
        codeInputPlaceholder = "بۆ نموونە: فەنکشنێکی Kotlin بنووسە بۆ پشکنینی ئیمەیڵ...",
        codeModeGenerate = "دروستکردنی کۆد",
        codeModeExplain = "ڕوونکردنەوە",
        codeModeBug = "پشکنینی هەڵە",
        codeModeRefactor = "باشترکردن",
        codeModeExample = "نموونە",
        codeSubmitAction = "ئەنجامدانی داواکاریی کۆد",
        codeSafetyNotice = "تێبینی پارێزراوی: کۆدی دروستکراو تەنها بۆ خوێندنەوە و کۆپیکردنە و بە شێوەی خۆکار لەناو بەرنامەکەدا جێبەجێ ناکرێت.",
        webInputLabel = "وەسفی وێبسایتەکەت",
        webInputPlaceholder = "بۆ نموونە: پەڕەیەکی سادەی ستۆدیۆی دیزاین بە ڕەنگی تاریک و فۆرمی پەیوەندی...",
        webSubmitAction = "دروستکردنی HTML / CSS / JS",
        webSafetyNotice = "فایلەکانی index.html و styles.css و app.js بە جیا ئامادە دەکرێن بۆ کۆپیکردن. ئەم ئامرازە وێبسایت لەسەر ئینتەرنێت بڵاو ناکاتەوە و کۆدەکە لەناو ئەپەکەدا کارپێناکات.",
        copyAllFilesAction = "کۆپیکردنی هەموو فایلەکان",
        designInputLabel = "وەسفی پڕۆژەی دیزاین یان براند",
        designInputPlaceholder = "بۆ نموونە: ناسنامەی بینراو بۆ قاوەخانەیەکی مۆدێرن لە سلێمانی...",
        designModeConcept = "بیرۆکەی دیزاین",
        designModePalette = "پالێتی ڕەنگ",
        designModeTypeLayout = "فۆنت و دابەشکردن",
        designModePrompt = "پڕۆمپتی وێنە",
        designModeBrief = "پوختەی گرافیک",
        designModeSocial = "سۆشیاڵ میدیا",
        designSubmitAction = "وەرگرتنی ڕێنمایی دیزاین",
        designPromptLabel = "پڕۆمپتی دەقی بۆ دروستکردنی وێنە (Text Prompt Only)",
        designPromptDisclaimer = "تێبینی: ئەم بەشە تەنها دەقی پڕۆمپت و ڕێنمایی دیزاین دروست دەکات، نەک فایلی وێنە.",
        planInputLabel = "بیرۆکە یان داواکاریی پڕۆژە",
        planInputPlaceholder = "بۆ نموونە: دروستکردنی ئەپێکی گەیاندنی خواردن بۆ چێشتخانەکان...",
        planSubmitAction = "دروستکردنی ڕەشنووسی پلان",
        planObjectivesTitle = "ئامانجەکانی پڕۆژە",
        planMilestonesTitle = "قۆناغە سەرەکییەکان (Milestones)",
        planTasksTitle = "دابەشکردنی ئەرکەکان",
        planEffortTitle = "خەمڵاندنی کات و هەوڵ (تەنها خەمڵاندنە)",
        planPrioritiesTitle = "کارە لەپێشینەکان",
        planSaveDraftButton = "پاشەکەوتکردنی ئەم پلانە لە پڕۆژەکان",
        planConfirmDialogTitle = "دڵنیابوونەوە لە پاشەکەوتکردنی پلانی پڕۆژە",
        planConfirmDialogBody = "ئایا دەتەوێت ئەم ڕەشنووسەی پڕۆژە زیاد بکەیت بۆ لیستی پڕۆژە پاشەکەوتکراوەکانی BASO؟",
        planConfirmAccept = "بەڵێ، زیادی بکە",
        planConfirmCancel = "پاشگەزبوونەوە",
      )
    AppLanguage.ARABIC ->
      BasoAiUiStrings(
        hubTitle = "أدوات BASO AI الذكية",
        hubSubtitle = "مساعد ذكي بخمس أدوات منفصلة للمحادثة، البرمجة، المواقع، التصميم وتخطيط المشاريع",
        statusConfiguredDirect = "اتصال خدمة HTTPS AI المباشر مفعل",
        statusConfiguredBackend = "اتصال خادم HTTPS AI الآمن مفعل",
        statusUnconfiguredTitle = "يلزم إعداد خدمة الذكاء الاصطناعي (AI)",
        statusUnconfiguredBody =
          "لم يتم إعداد مزود الذكاء الاصطناعي بعد. لإجراء طلبات حقيقية دون تضمين مفاتيح خاصة داخل التطبيق، يرجى ربط نقطة نهاية خادم HTTPS AI متوافق. لا يتم عرض أي ردود وهمية.",
        tabChat = "المحادثة الذكية",
        tabCode = "مساعد الكود",
        tabWebsite = "منشئ المواقع",
        tabDesign = "مساعد التصميم",
        tabPlanning = "تخطيط المشاريع",
        newChatLabel = "محادثة جديدة",
        chatInputLabel = "سؤالك أو طلبك",
        chatInputPlaceholder = "اكتب رسالتك للمساعد الذكي...",
        sendAction = "إرسال إلى AI",
        retryAction = "إعادة المحاولة",
        copyAction = "نسخ",
        copiedFeedback = "تم نسخ النص إلى الحافظة",
        loadingMessage = "جارٍ انتظار استجابة نموذج الذكاء الاصطناعي...",
        emptyChatHint = "ابدأ محادثة جديدة بالكردية أو العربية أو الإنجليزية.",
        codeInputLabel = "وصف المهمة البرمجية أو الكود",
        codeInputPlaceholder = "مثال: اكتب دالة بلغة Kotlin للتحقق من البريد الإلكتروني...",
        codeModeGenerate = "توليد كود",
        codeModeExplain = "شرح الكود",
        codeModeBug = "تحليل الأخطاء",
        codeModeRefactor = "تحسين الكود",
        codeModeExample = "مثال تطبيقي",
        codeSubmitAction = "تنفيذ طلب البرمجة",
        codeSafetyNotice = "ملاحظة أمان: الكود المولد مخصص للقراءة والنسخ فقط ولا يتم تنفيذه تلقائياً داخل التطبيق.",
        webInputLabel = "وصف الموقع المطلوب",
        webInputPlaceholder = "مثال: صفحة هبوط لاستوديو تصميم بواجهة داكنة ونموذج تواصل...",
        webSubmitAction = "توليد HTML / CSS / JS",
        webSafetyNotice = "يتم فصل ملفات index.html و styles.css و app.js للنسخ والمراجعة فقط دون نشر مباشر أو تشغيل غير آمن.",
        copyAllFilesAction = "نسخ جميع الملفات",
        designInputLabel = "وصف مشروع التصميم أو الهوية",
        designInputPlaceholder = "مثال: هوية بصرية لمقهى حديث...",
        designModeConcept = "فكرة التصميم",
        designModePalette = "لوحة الألوان",
        designModeTypeLayout = "الخطوط والتخطيط",
        designModePrompt = "برومبت صور",
        designModeBrief = "ملخص تصميم",
        designModeSocial = "سوشيال ميديا",
        designSubmitAction = "إنشاء توصيات التصميم",
        designPromptLabel = "وصف نصي لتوليد الصور (Text Prompt Only)",
        designPromptDisclaimer = "ملاحظة: هذه الأداة تنشئ نصوص البرومبت والتوجيهات الإبداعية فقط وليس ملفات صور.",
        planInputLabel = "فكرة أو متطلبات المشروع",
        planInputPlaceholder = "مثال: تطبيق توصيل طلبات للمطاعم المحلية...",
        planSubmitAction = "إنشاء مسودة خطة المشروع",
        planObjectivesTitle = "أهداف المشروع",
        planMilestonesTitle = "المراحل الرئيسية (Milestones)",
        planTasksTitle = "تقسيم المهام",
        planEffortTitle = "تقدير الجهد والوقت (تقدير تقريبي)",
        planPrioritiesTitle = "الأولويات المقترحة",
        planSaveDraftButton = "حفظ الخطة في المشاريع",
        planConfirmDialogTitle = "تأكيد حفظ مسودة المشروع",
        planConfirmDialogBody = "هل ترغب في إضافة مسودة المشروع هذه إلى قائمة مشاريع BASO المحفوظة؟",
        planConfirmAccept = "نعم، حفظ المشروع",
        planConfirmCancel = "إلغاء",
      )
    AppLanguage.ENGLISH ->
      BasoAiUiStrings(
        hubTitle = "BASO AI Multi-Tool Hub",
        hubSubtitle = "Five dedicated AI tools for Chat, Code, Website Generation, Design, and Project Planning",
        statusConfiguredDirect = "HTTPS AI REST Endpoint Ready",
        statusConfiguredBackend = "Secure HTTPS AI Backend Ready",
        statusUnconfiguredTitle = "AI Service Setup Required",
        statusUnconfiguredBody =
          "No AI provider is configured yet. Connect a compatible HTTPS AI backend endpoint and model to run live AI requests without embedding private API keys in the APK. No simulated responses are returned.",
        tabChat = "AI Chat",
        tabCode = "Code Assistant",
        tabWebsite = "Website Builder",
        tabDesign = "Design Assistant",
        tabPlanning = "Project Planner",
        newChatLabel = "New Chat",
        chatInputLabel = "Your Prompt",
        chatInputPlaceholder = "Ask BASO AI in Kurdish, Arabic, or English...",
        sendAction = "Send to AI",
        retryAction = "Retry",
        copyAction = "Copy",
        copiedFeedback = "Copied to clipboard",
        loadingMessage = "Waiting for live AI model response...",
        emptyChatHint = "Start a conversation in Kurdish Sorani, Arabic, or English.",
        codeInputLabel = "Programming Task or Code Snippet",
        codeInputPlaceholder = "e.g., Write a Kotlin coroutine retry helper with exponential backoff...",
        codeModeGenerate = "Generate",
        codeModeExplain = "Explain",
        codeModeBug = "Bug Analysis",
        codeModeRefactor = "Refactor",
        codeModeExample = "Example",
        codeSubmitAction = "Run Code Assistant",
        codeSafetyNotice = "Security note: Generated code is displayed for inspection and copy only; it is never executed automatically.",
        webInputLabel = "Website Specification",
        webInputPlaceholder = "e.g., Minimalist dark-mode portfolio page with project grid and contact form...",
        webSubmitAction = "Generate HTML / CSS / JS",
        webSafetyNotice = "Generated index.html, styles.css, and app.js are kept strictly separated for review and copy. Code is never executed in the app's trusted context.",
        copyAllFilesAction = "Copy All Files",
        designInputLabel = "Design Brief or Brand Concept",
        designInputPlaceholder = "e.g., Modern visual identity for a specialty coffee roaster...",
        designModeConcept = "Concept",
        designModePalette = "Color Palette",
        designModeTypeLayout = "Type & Layout",
        designModePrompt = "Image Prompt",
        designModeBrief = "Design Brief",
        designModeSocial = "Social Media",
        designSubmitAction = "Generate Design Guidance",
        designPromptLabel = "Image-Generation Text Prompt (Prompt Generation Only)",
        designPromptDisclaimer = "Note: This tool generates descriptive text prompts and design briefs, not raster image files.",
        planInputLabel = "Project Description",
        planInputPlaceholder = "e.g., Build a bilingual mobile booking app for local clinics...",
        planSubmitAction = "Generate Draft Project Plan",
        planObjectivesTitle = "Project Objectives",
        planMilestonesTitle = "Suggested Milestones",
        planTasksTitle = "Task Breakdown",
        planEffortTitle = "Estimated Effort (Estimate Only)",
        planPrioritiesTitle = "Suggested Priorities",
        planSaveDraftButton = "Save Draft to Projects",
        planConfirmDialogTitle = "Confirm Saving AI Project Plan",
        planConfirmDialogBody = "Would you like to add this reviewed project plan to your persistent BASO projects list?",
        planConfirmAccept = "Save to Projects",
        planConfirmCancel = "Cancel",
      )
  }

@Composable
fun BasoAiHubScreen(
  aiService: BasoAiService,
  currentLanguage: AppLanguage,
  persistedChatTurns: List<BasoAiChatTurn>,
  onUpdateChatTurns: (List<BasoAiChatTurn>) -> Unit,
  onClearChatTurns: () -> Unit,
  onSavePlannedProject: (title: String, categoryIndex: Int, subtitle: String) -> Unit,
  onShowFeedback: (String) -> Unit,
  onBack: () -> Unit,
  onOpenAiSettings: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  val aiStrings = remember(currentLanguage) { aiStringsForLanguage(currentLanguage) }
  var selectedTab by rememberSaveable { mutableStateOf(BasoAiToolTab.CHAT) }
  val clipboardManager = LocalClipboardManager.current

  val copyText: (String) -> Unit = { textToCopy ->
    clipboardManager.setText(AnnotatedString(textToCopy))
    onShowFeedback(aiStrings.copiedFeedback)
  }

  Column(
    modifier =
      modifier
        .fillMaxSize()
        .background(BasoPrimaryBackground)
        .padding(horizontal = 18.dp, vertical = 12.dp)
        .testTag("baso_ai_hub_screen"),
  ) {
    // Top Header with Back Button + Title + Provider Status Indicator
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      IconButton(
        onClick = onBack,
        modifier =
          Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(BasoSurfaceCard)
            .testTag("ai_hub_back_button"),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = BasoPrimaryText,
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = aiStrings.hubTitle,
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )
        Text(
          text = aiStrings.hubSubtitle,
          style = MaterialTheme.typography.labelMedium,
          color = BasoSecondaryText,
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Provider Configuration Banner (Explicitly shows whether credentials or backend are configured)
    val configMode = aiService.configurationMode()
    ProviderConfigBanner(
      configMode = configMode,
      aiStrings = aiStrings,
      onOpenSettings = onOpenAiSettings,
    )

    Spacer(modifier = Modifier.height(12.dp))

    // 5 Clearly Separated AI Tool Selector Tabs
    val tabs =
      listOf(
        Triple(BasoAiToolTab.CHAT, aiStrings.tabChat, Icons.AutoMirrored.Filled.Chat),
        Triple(BasoAiToolTab.CODE, aiStrings.tabCode, Icons.Default.Code),
        Triple(BasoAiToolTab.WEBSITE, aiStrings.tabWebsite, Icons.Default.Web),
        Triple(BasoAiToolTab.DESIGN, aiStrings.tabDesign, Icons.Default.Palette),
        Triple(BasoAiToolTab.PLANNING, aiStrings.tabPlanning, Icons.AutoMirrored.Filled.FactCheck),
      )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      tabs.forEach { (tab, label, icon) ->
        val selected = selectedTab == tab
        val shape = RoundedCornerShape(14.dp)
        Surface(
          color = if (selected) BasoCyanPill else BasoSurfaceCard,
          shape = shape,
          border =
            BorderStroke(
              width = 1.dp,
              color = if (selected) BasoNeonTurquoise else BasoBorder,
            ),
          modifier =
            Modifier
              .clip(shape)
              .clickable { selectedTab = tab }
              .testTag("ai_tool_tab_${tab.name.lowercase()}"),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          ) {
            Icon(
              imageVector = icon,
              contentDescription = label,
              tint = if (selected) BasoPrimaryBackground else BasoNeonTurquoise,
              modifier = Modifier.size(16.dp),
            )
            Text(
              text = label,
              style =
                MaterialTheme.typography.labelLarge.copy(
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                ),
              color = if (selected) BasoPrimaryBackground else BasoPrimaryText,
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Active Tool Body
    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
      when (selectedTab) {
        BasoAiToolTab.CHAT ->
          AiChatToolPanel(
            aiService = aiService,
            currentLanguage = currentLanguage,
            aiStrings = aiStrings,
            chatTurns = persistedChatTurns,
            onUpdateChatTurns = onUpdateChatTurns,
            onClearChatTurns = onClearChatTurns,
            onCopy = copyText,
          )
        BasoAiToolTab.CODE ->
          AiCodeToolPanel(
            aiService = aiService,
            currentLanguage = currentLanguage,
            aiStrings = aiStrings,
            onCopy = copyText,
          )
        BasoAiToolTab.WEBSITE ->
          AiWebsiteToolPanel(
            aiService = aiService,
            currentLanguage = currentLanguage,
            aiStrings = aiStrings,
            onCopy = copyText,
          )
        BasoAiToolTab.DESIGN ->
          AiDesignToolPanel(
            aiService = aiService,
            currentLanguage = currentLanguage,
            aiStrings = aiStrings,
            onCopy = copyText,
          )
        BasoAiToolTab.PLANNING ->
          AiProjectPlanningToolPanel(
            aiService = aiService,
            currentLanguage = currentLanguage,
            aiStrings = aiStrings,
            onSavePlannedProject = onSavePlannedProject,
            onCopy = copyText,
          )
      }
    }
  }
}

@Composable
private fun ProviderConfigBanner(
  configMode: BasoAiConfigMode,
  aiStrings: BasoAiUiStrings,
  onOpenSettings: () -> Unit = {},
) {
  val isReady = configMode != BasoAiConfigMode.UNCONFIGURED
  val borderColor = if (isReady) BasoSuccess.copy(alpha = 0.55f) else BasoNeonYellow.copy(alpha = 0.65f)
  val bgColor = if (isReady) BasoSuccessSubtleBg else BasoYellowSubtleGlow

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, borderColor),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onOpenSettings)
      .testTag("ai_provider_config_banner"),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
      Icon(
        imageVector = if (isReady) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
        contentDescription = null,
        tint = if (isReady) BasoSuccess else BasoNeonYellow,
        modifier = Modifier.size(20.dp),
      )
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text =
            when (configMode) {
              BasoAiConfigMode.DUAL_ROUTER -> "دابینکەری دووانی چالاکە (Gemini & OpenAI)"
              BasoAiConfigMode.BACKEND_PROXY -> aiStrings.statusConfiguredBackend
              BasoAiConfigMode.DIRECT_GEMINI_REST -> aiStrings.statusConfiguredDirect
              BasoAiConfigMode.DIRECT_OPENAI_REST -> "پەیوەندیی ڕاستەوخۆی OpenAI چالاکە"
              BasoAiConfigMode.UNCONFIGURED -> aiStrings.statusUnconfiguredTitle
            },
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )
        if (!isReady) {
          Text(
            text = aiStrings.statusUnconfiguredBody,
            style = MaterialTheme.typography.labelMedium,
            color = BasoSecondaryText,
          )
        }
      }
      Surface(
        color = BasoSurfaceCard,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BasoBorder),
        modifier = Modifier.clickable(onClick = onOpenSettings),
      ) {
        Text(
          text = "ڕێکخستن",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = BasoNeonTurquoise,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
      }
    }
  }
}

/**
 * TOOL A: AI Chat Assistant Panel
 */
@Composable
private fun AiChatToolPanel(
  aiService: BasoAiService,
  currentLanguage: AppLanguage,
  aiStrings: BasoAiUiStrings,
  chatTurns: List<BasoAiChatTurn>,
  onUpdateChatTurns: (List<BasoAiChatTurn>) -> Unit,
  onClearChatTurns: () -> Unit,
  onCopy: (String) -> Unit,
) {
  var promptText by rememberSaveable { mutableStateOf("") }
  var lastFailedPrompt by rememberSaveable { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
  val coroutineScope = rememberCoroutineScope()

  val sendPrompt: (String) -> Unit = { input ->
    val trimmed = input.trim()
    if (trimmed.isNotEmpty() && !isLoading) {
      val userTurn =
        BasoAiChatTurn(
          id = "u_${System.currentTimeMillis()}_${chatTurns.size + 1}",
          role = "user",
          text = trimmed,
          timestampLabel = "User",
        )
      val updatedWithUser = chatTurns + userTurn
      onUpdateChatTurns(updatedWithUser)
      promptText = ""
      errorMessage = null
      lastFailedPrompt = null
      isLoading = true

      coroutineScope.launch {
        when (val result = aiService.sendChatMessage(chatTurns, trimmed, currentLanguage)) {
          is BasoAiResult.Success -> {
            isLoading = false
            val providerTag = if (result.isFailover) "${result.providerUsed} (Failover)" else result.providerUsed
            val modelTurn =
              BasoAiChatTurn(
                id = "m_${System.currentTimeMillis()}_${updatedWithUser.size + 1}",
                role = "model",
                text = result.data,
                timestampLabel = "$providerTag • ${result.modelUsed}",
              )
            onUpdateChatTurns(updatedWithUser + modelTurn)
          }
          is BasoAiResult.Failure -> {
            isLoading = false
            lastFailedPrompt = trimmed
            errorMessage = result.safeMessage
          }
        }
      }
    }
  }

  Column(
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxSize().testTag("ai_chat_tool_panel"),
  ) {
    // New Conversation Action Row
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text = aiStrings.tabChat,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = BasoNeonTurquoise,
      )
      Surface(
        color = BasoSurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BasoBorder),
        modifier =
          Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              onClearChatTurns()
              errorMessage = null
              lastFailedPrompt = null
            }
            .testTag("ai_chat_new_session_button"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        ) {
          Icon(
            imageVector = Icons.Default.AddComment,
            contentDescription = aiStrings.newChatLabel,
            tint = BasoNeonYellow,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = aiStrings.newChatLabel,
            style = MaterialTheme.typography.labelMedium,
            color = BasoPrimaryText,
          )
        }
      }
    }

    // Conversation History List
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f).fillMaxWidth().testTag("ai_chat_messages_list"),
    ) {
      if (chatTurns.isEmpty() && !isLoading && errorMessage == null) {
        item {
          BasoCard(modifier = Modifier.fillMaxWidth().testTag("ai_chat_empty_state")) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = BasoNeonTurquoise,
                modifier = Modifier.size(24.dp),
              )
              Text(
                text = aiStrings.emptyChatHint,
                style = MaterialTheme.typography.bodyMedium,
                color = BasoSecondaryText,
              )
            }
          }
        }
      }

      items(chatTurns, key = { it.id }) { turn ->
        val isUser = turn.role == "user"
        Box(
          contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Surface(
            color = if (isUser) BasoChatBubbleOut else BasoSurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = if (isUser) null else BorderStroke(1.dp, BasoBorder),
            modifier = Modifier.fillMaxWidth(0.90f),
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              SelectionContainer {
                Text(
                  text = turn.text,
                  style = MaterialTheme.typography.bodyLarge,
                  color = Color.White,
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = turn.timestampLabel,
                  style = MaterialTheme.typography.labelMedium,
                  color = Color.White.copy(alpha = 0.7f),
                )
                if (!isUser) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier =
                      Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCopy(turn.text) }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Default.ContentCopy,
                      contentDescription = aiStrings.copyAction,
                      tint = BasoNeonTurquoise,
                      modifier = Modifier.size(14.dp),
                    )
                    Text(
                      text = aiStrings.copyAction,
                      style = MaterialTheme.typography.labelMedium,
                      color = BasoNeonTurquoise,
                    )
                  }
                }
              }
            }
          }
        }
      }

      if (isLoading) {
        item {
          AiLoadingCard(message = aiStrings.loadingMessage)
        }
      }

      if (errorMessage != null) {
        item {
          AiErrorCard(
            message = errorMessage ?: "",
            retryLabel = aiStrings.retryAction,
            onRetry =
              if (lastFailedPrompt != null) {
                { sendPrompt(lastFailedPrompt!!) }
              } else {
                null
              },
          )
        }
      }
    }

    // Input Field + Send CTA
    Row(
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Box(modifier = Modifier.weight(1f)) {
        BasoInputField(
          value = promptText,
          onValueChange = { promptText = it },
          label = aiStrings.chatInputLabel,
          placeholder = aiStrings.chatInputPlaceholder,
          testTag = "ai_chat_input_field",
        )
      }
      BasoPrimaryButton(
        text = aiStrings.sendAction,
        onClick = { sendPrompt(promptText) },
        enabled = !isLoading && promptText.isNotBlank(),
        leadingIcon = Icons.AutoMirrored.Filled.Send,
        testTag = "ai_chat_send_button",
      )
    }
  }
}

/**
 * TOOL B: AI Code Assistant Panel
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiCodeToolPanel(
  aiService: BasoAiService,
  currentLanguage: AppLanguage,
  aiStrings: BasoAiUiStrings,
  onCopy: (String) -> Unit,
) {
  var taskInput by rememberSaveable { mutableStateOf("") }
  var selectedMode by rememberSaveable { mutableStateOf(BasoCodeTaskMode.GENERATE) }
  var selectedProgLang by rememberSaveable { mutableStateOf("Kotlin") }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
  var codeOutput by remember { mutableStateOf<BasoCodeAssistantOutput?>(null) }
  val coroutineScope = rememberCoroutineScope()

  val progLanguages = listOf("Kotlin", "Python", "TypeScript", "JavaScript", "SQL", "Swift")
  val modes =
    listOf(
      BasoCodeTaskMode.GENERATE to aiStrings.codeModeGenerate,
      BasoCodeTaskMode.EXPLAIN to aiStrings.codeModeExplain,
      BasoCodeTaskMode.BUG_ANALYSIS to aiStrings.codeModeBug,
      BasoCodeTaskMode.REFACTOR to aiStrings.codeModeRefactor,
      BasoCodeTaskMode.EXAMPLE to aiStrings.codeModeExample,
    )

  Column(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize().testTag("ai_code_tool_panel"),
  ) {
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize().weight(1f),
    ) {
    item {
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        modes.forEach { (mode, label) ->
          val selected = selectedMode == mode
          SelectableChipPill(
            text = label,
            selected = selected,
            onClick = { selectedMode = mode },
            testTag = "ai_code_mode_${mode.name.lowercase()}",
          )
        }
      }
    }

    item {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      ) {
        progLanguages.forEach { lang ->
          SelectableChipPill(
            text = lang,
            selected = selectedProgLang == lang,
            onClick = { selectedProgLang = lang },
            accentYellow = true,
            testTag = "ai_code_lang_${lang.lowercase()}",
          )
        }
      }
    }

    item {
      BasoInputField(
        value = taskInput,
        onValueChange = { taskInput = it },
        label = aiStrings.codeInputLabel,
        placeholder = aiStrings.codeInputPlaceholder,
        singleLine = false,
        minLines = 3,
        testTag = "ai_code_input_field",
      )
    }

    item {
      BasoPrimaryButton(
        text = aiStrings.codeSubmitAction,
        onClick = {
          if (taskInput.isNotBlank() && !isLoading) {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
              when (
                val res =
                  aiService.runCodeAssistant(
                    taskDescription = taskInput,
                    mode = selectedMode,
                    programmingLanguage = selectedProgLang,
                    appLanguage = currentLanguage,
                  )
              ) {
                is BasoAiResult.Success -> {
                  isLoading = false
                  codeOutput = res.data
                }
                is BasoAiResult.Failure -> {
                  isLoading = false
                  errorMessage = res.safeMessage
                }
              }
            }
          }
        },
        enabled = !isLoading && taskInput.isNotBlank(),
        leadingIcon = Icons.Default.Code,
        modifier = Modifier.fillMaxWidth(),
        testTag = "ai_code_submit_button",
      )
    }

    item {
      Text(
        text = aiStrings.codeSafetyNotice,
        style = MaterialTheme.typography.labelMedium,
        color = BasoSecondaryText,
      )
    }

    if (isLoading) {
      item { AiLoadingCard(message = aiStrings.loadingMessage) }
    }

    if (errorMessage != null) {
      item {
        AiErrorCard(
          message = errorMessage ?: "",
          retryLabel = aiStrings.retryAction,
          onRetry = null,
        )
      }
    }

    codeOutput?.let { output ->
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("ai_code_result_card")) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = "${output.language} • ${output.summary}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BasoNeonTurquoise,
                modifier = Modifier.weight(1f),
              )
              BasoSecondaryButton(
                text = aiStrings.copyAction,
                onClick = { onCopy(output.code) },
                leadingIcon = Icons.Default.ContentCopy,
                highlightTurquoise = true,
                testTag = "ai_code_copy_button",
              )
            }

            // Selectable Code Viewer Box
            Surface(
              color = BasoPrimaryBackground,
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, BasoBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              SelectionContainer {
                Text(
                  text = output.code,
                  style =
                    MaterialTheme.typography.bodyMedium.copy(
                      fontFamily = FontFamily.Monospace,
                      lineHeight = 20.sp,
                    ),
                  color = BasoPrimaryText,
                  modifier =
                    Modifier
                      .fillMaxWidth()
                      .horizontalScroll(rememberScrollState())
                      .padding(14.dp)
                      .testTag("ai_code_selectable_view"),
                )
              }
            }

            if (output.notes.isNotBlank()) {
              Text(
                text = output.notes,
                style = MaterialTheme.typography.bodyMedium,
                color = BasoSecondaryText,
              )
            }
          }
        }
      }
    }
  }
}
}

/**
 * TOOL C: AI Website Generator Panel
 */
@Composable
private fun AiWebsiteToolPanel(
  aiService: BasoAiService,
  currentLanguage: AppLanguage,
  aiStrings: BasoAiUiStrings,
  onCopy: (String) -> Unit,
) {
  var sitePrompt by rememberSaveable { mutableStateOf("") }
  var activeFileTab by rememberSaveable { mutableIntStateOf(0) } // 0=index.html, 1=styles.css, 2=app.js
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
  var bundleOutput by remember { mutableStateOf<BasoWebsiteBundleOutput?>(null) }
  val coroutineScope = rememberCoroutineScope()

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize().testTag("ai_website_tool_panel"),
  ) {
    item {
      BasoInputField(
        value = sitePrompt,
        onValueChange = { sitePrompt = it },
        label = aiStrings.webInputLabel,
        placeholder = aiStrings.webInputPlaceholder,
        singleLine = false,
        minLines = 3,
        testTag = "ai_website_input_field",
      )
    }

    item {
      BasoPrimaryButton(
        text = aiStrings.webSubmitAction,
        onClick = {
          if (sitePrompt.isNotBlank() && !isLoading) {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
              when (val res = aiService.generateWebsiteBundle(sitePrompt, currentLanguage)) {
                is BasoAiResult.Success -> {
                  isLoading = false
                  bundleOutput = res.data
                }
                is BasoAiResult.Failure -> {
                  isLoading = false
                  errorMessage = res.safeMessage
                }
              }
            }
          }
        },
        enabled = !isLoading && sitePrompt.isNotBlank(),
        leadingIcon = Icons.Default.Web,
        modifier = Modifier.fillMaxWidth(),
        testTag = "ai_website_submit_button",
      )
    }

    item {
      Text(
        text = aiStrings.webSafetyNotice,
        style = MaterialTheme.typography.labelMedium,
        color = BasoSecondaryText,
      )
    }

    if (isLoading) {
      item { AiLoadingCard(message = aiStrings.loadingMessage) }
    }

    if (errorMessage != null) {
      item {
        AiErrorCard(
          message = errorMessage ?: "",
          retryLabel = aiStrings.retryAction,
          onRetry = null,
        )
      }
    }

    bundleOutput?.let { bundle ->
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("ai_website_result_card")) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = bundle.summary,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
            )

            // Separated File Tabs: index.html | styles.css | app.js
            val fileTabs =
              listOf(
                "index.html" to bundle.indexHtml,
                "styles.css" to bundle.stylesCss,
                "app.js" to bundle.appJs,
              )

            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              fileTabs.forEachIndexed { idx, (fileName, _) ->
                SelectableChipPill(
                  text = fileName,
                  selected = activeFileTab == idx,
                  onClick = { activeFileTab = idx },
                  accentYellow = idx == 0,
                  testTag = "ai_website_file_tab_$idx",
                )
              }
            }

            val (currentFileName, currentFileContent) = fileTabs[activeFileTab]

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              BasoSecondaryButton(
                text = "${aiStrings.copyAction} ($currentFileName)",
                onClick = { onCopy(currentFileContent) },
                leadingIcon = Icons.Default.ContentCopy,
                highlightTurquoise = true,
                testTag = "ai_website_copy_active_file_button",
              )
              BasoSecondaryButton(
                text = aiStrings.copyAllFilesAction,
                onClick = {
                  val combined =
                    "<!-- index.html -->\n${bundle.indexHtml}\n\n/* styles.css */\n${bundle.stylesCss}\n\n// app.js\n${bundle.appJs}"
                  onCopy(combined)
                },
                testTag = "ai_website_copy_all_button",
              )
            }

            Surface(
              color = BasoPrimaryBackground,
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, BasoBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              SelectionContainer {
                Text(
                  text = currentFileContent,
                  style =
                    MaterialTheme.typography.bodyMedium.copy(
                      fontFamily = FontFamily.Monospace,
                      lineHeight = 20.sp,
                    ),
                  color = BasoPrimaryText,
                  modifier =
                    Modifier
                      .fillMaxWidth()
                      .horizontalScroll(rememberScrollState())
                      .padding(14.dp)
                      .testTag("ai_website_code_view"),
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * TOOL D: AI Design Assistant Panel
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiDesignToolPanel(
  aiService: BasoAiService,
  currentLanguage: AppLanguage,
  aiStrings: BasoAiUiStrings,
  onCopy: (String) -> Unit,
) {
  var designBrief by rememberSaveable { mutableStateOf("") }
  var selectedMode by rememberSaveable { mutableStateOf(BasoDesignTaskMode.CONCEPT) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
  var designOutput by remember { mutableStateOf<BasoDesignAssistantOutput?>(null) }
  val coroutineScope = rememberCoroutineScope()

  val modes =
    listOf(
      BasoDesignTaskMode.CONCEPT to aiStrings.designModeConcept,
      BasoDesignTaskMode.COLOR_PALETTE to aiStrings.designModePalette,
      BasoDesignTaskMode.TYPOGRAPHY_LAYOUT to aiStrings.designModeTypeLayout,
      BasoDesignTaskMode.IMAGE_PROMPT to aiStrings.designModePrompt,
      BasoDesignTaskMode.GRAPHIC_BRIEF to aiStrings.designModeBrief,
      BasoDesignTaskMode.SOCIAL_IDEAS to aiStrings.designModeSocial,
    )

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize().testTag("ai_design_tool_panel"),
  ) {
    item {
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        modes.forEach { (mode, label) ->
          SelectableChipPill(
            text = label,
            selected = selectedMode == mode,
            onClick = { selectedMode = mode },
            testTag = "ai_design_mode_${mode.name.lowercase()}",
          )
        }
      }
    }

    item {
      BasoInputField(
        value = designBrief,
        onValueChange = { designBrief = it },
        label = aiStrings.designInputLabel,
        placeholder = aiStrings.designInputPlaceholder,
        singleLine = false,
        minLines = 3,
        testTag = "ai_design_input_field",
      )
    }

    item {
      BasoPrimaryButton(
        text = aiStrings.designSubmitAction,
        onClick = {
          if (designBrief.isNotBlank() && !isLoading) {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
              when (val res = aiService.runDesignAssistant(designBrief, selectedMode, currentLanguage)) {
                is BasoAiResult.Success -> {
                  isLoading = false
                  designOutput = res.data
                }
                is BasoAiResult.Failure -> {
                  isLoading = false
                  errorMessage = res.safeMessage
                }
              }
            }
          }
        },
        enabled = !isLoading && designBrief.isNotBlank(),
        leadingIcon = Icons.Default.Palette,
        modifier = Modifier.fillMaxWidth(),
        testTag = "ai_design_submit_button",
      )
    }

    item {
      Text(
        text = aiStrings.designPromptDisclaimer,
        style = MaterialTheme.typography.labelMedium,
        color = BasoSecondaryText,
      )
    }

    if (isLoading) {
      item { AiLoadingCard(message = aiStrings.loadingMessage) }
    }

    if (errorMessage != null) {
      item {
        AiErrorCard(
          message = errorMessage ?: "",
          retryLabel = aiStrings.retryAction,
          onRetry = null,
        )
      }
    }

    designOutput?.let { out ->
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("ai_design_result_card")) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = out.creativeDirection,
              style = MaterialTheme.typography.bodyLarge,
              color = BasoPrimaryText,
            )

            // Color Palette Swatches
            if (out.colorPaletteHex.isNotEmpty()) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
              ) {
                out.colorPaletteHex.forEach { hex ->
                  val parsedColor = parseHexColorSafely(hex)
                  Surface(
                    color = BasoSecondaryBackground,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BasoBorder),
                    modifier = Modifier.clickable { onCopy(hex) },
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                      Box(
                        modifier =
                          Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                            .border(1.dp, BasoBorder, CircleShape),
                      )
                      Text(
                        text = hex,
                        style = MaterialTheme.typography.labelMedium,
                        color = BasoPrimaryText,
                      )
                    }
                  }
                }
              }
            }

            if (out.typographyAndLayout.isNotBlank()) {
              Text(
                text = out.typographyAndLayout,
                style = MaterialTheme.typography.bodyMedium,
                color = BasoSecondaryText,
              )
            }

            // Distinctly labeled Image-Generation Text Prompt box
            if (out.imageGenerationPrompt.isNotBlank()) {
              Surface(
                color = BasoSecondaryBackground,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BasoNeonTurquoise.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.padding(12.dp),
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                  ) {
                    Text(
                      text = aiStrings.designPromptLabel,
                      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                      color = BasoNeonTurquoise,
                    )
                    IconButton(
                      onClick = { onCopy(out.imageGenerationPrompt) },
                      modifier = Modifier.size(28.dp).testTag("ai_design_copy_prompt_button"),
                    ) {
                      Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = aiStrings.copyAction,
                        tint = BasoNeonTurquoise,
                        modifier = Modifier.size(16.dp),
                      )
                    }
                  }
                  SelectionContainer {
                    Text(
                      text = out.imageGenerationPrompt,
                      style = MaterialTheme.typography.bodyMedium,
                      color = BasoPrimaryText,
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

/**
 * TOOL E: AI Project Planning Assistant Panel
 * Generates a structured draft project plan and requires explicit user confirmation before saving to persistent projects.
 */
@Composable
private fun AiProjectPlanningToolPanel(
  aiService: BasoAiService,
  currentLanguage: AppLanguage,
  aiStrings: BasoAiUiStrings,
  onSavePlannedProject: (title: String, categoryIndex: Int, subtitle: String) -> Unit,
  onCopy: (String) -> Unit,
) {
  var projectInput by rememberSaveable { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
  var planDraft by remember { mutableStateOf<BasoProjectPlanOutput?>(null) }
  var showSaveConfirmationDialog by rememberSaveable { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize().testTag("ai_planning_tool_panel"),
  ) {
    item {
      BasoInputField(
        value = projectInput,
        onValueChange = { projectInput = it },
        label = aiStrings.planInputLabel,
        placeholder = aiStrings.planInputPlaceholder,
        singleLine = false,
        minLines = 3,
        testTag = "ai_planning_input_field",
      )
    }

    item {
      BasoPrimaryButton(
        text = aiStrings.planSubmitAction,
        onClick = {
          if (projectInput.isNotBlank() && !isLoading) {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
              when (val res = aiService.generateProjectPlan(projectInput, currentLanguage)) {
                is BasoAiResult.Success -> {
                  isLoading = false
                  planDraft = res.data
                }
                is BasoAiResult.Failure -> {
                  isLoading = false
                  errorMessage = res.safeMessage
                }
              }
            }
          }
        },
        enabled = !isLoading && projectInput.isNotBlank(),
        leadingIcon = Icons.AutoMirrored.Filled.FactCheck,
        modifier = Modifier.fillMaxWidth(),
        testTag = "ai_planning_submit_button",
      )
    }

    if (isLoading) {
      item { AiLoadingCard(message = aiStrings.loadingMessage) }
    }

    if (errorMessage != null) {
      item {
        AiErrorCard(
          message = errorMessage ?: "",
          retryLabel = aiStrings.retryAction,
          onRetry = null,
        )
      }
    }

    planDraft?.let { draft ->
      item {
        BasoCard(modifier = Modifier.fillMaxWidth().testTag("ai_planning_draft_card")) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = draft.projectTitle,
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoNeonYellow,
            )

            PlanBulletSection(title = aiStrings.planObjectivesTitle, items = draft.objectives)
            PlanBulletSection(title = aiStrings.planMilestonesTitle, items = draft.milestones)
            PlanBulletSection(title = aiStrings.planTasksTitle, items = draft.tasks)

            // Estimated Effort clearly marked as an estimate
            Surface(
              color = BasoSecondaryBackground,
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, BasoNeonTurquoise.copy(alpha = 0.45f)),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = aiStrings.planEffortTitle,
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                  color = BasoNeonTurquoise,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = draft.estimatedEffort,
                  style = MaterialTheme.typography.bodyMedium,
                  color = BasoPrimaryText,
                )
              }
            }

            PlanBulletSection(title = aiStrings.planPrioritiesTitle, items = draft.priorities)
          }
        }
      }

      item {
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          BasoPrimaryButton(
            text = aiStrings.planSaveDraftButton,
            onClick = { showSaveConfirmationDialog = true },
            modifier = Modifier.weight(1f),
            testTag = "ai_planning_save_to_projects_button",
          )
          BasoSecondaryButton(
            text = aiStrings.copyAction,
            onClick = {
              onCopy(
                "${draft.projectTitle}\n${draft.objectives.joinToString("\n")}\n${draft.tasks.joinToString("\n")}",
              )
            },
            leadingIcon = Icons.Default.ContentCopy,
            testTag = "ai_planning_copy_button",
          )
        }
      }
    }
  }

  if (showSaveConfirmationDialog && planDraft != null) {
    val draft = planDraft!!
    BasoConfirmationDialog(
      title = aiStrings.planConfirmDialogTitle,
      message = aiStrings.planConfirmDialogBody,
      confirmLabel = aiStrings.planConfirmAccept,
      cancelLabel = aiStrings.planConfirmCancel,
      onConfirm = {
        showSaveConfirmationDialog = false
        val catIdx = draft.recommendedCategoryIndex.coerceIn(1, 4)
        val subtitleSummary = "${draft.tasks.size} ئەرک • ${draft.estimatedEffort}"
        onSavePlannedProject(draft.projectTitle, catIdx, subtitleSummary)
      },
      onDismiss = { showSaveConfirmationDialog = false },
    )
  }
}

@Composable
private fun PlanBulletSection(
  title: String,
  items: List<String>,
) {
  if (items.isEmpty()) return
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = BasoNeonTurquoise,
    )
    items.forEach { line ->
      Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(
          text = "•",
          color = BasoNeonYellow,
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(
          text = line,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoPrimaryText,
        )
      }
    }
  }
}

@Composable
private fun SelectableChipPill(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
  accentYellow: Boolean = false,
  testTag: String,
) {
  val activeColor = if (accentYellow) BasoNeonYellow else BasoNeonTurquoise
  Surface(
    color = if (selected) BasoSecondaryBackground else BasoSurfaceCard,
    shape = RoundedCornerShape(12.dp),
    border =
      BorderStroke(
        width = if (selected) 1.5.dp else 1.dp,
        color = if (selected) activeColor else BasoBorder,
      ),
    modifier =
      Modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable(onClick = onClick)
        .testTag(testTag),
  ) {
    Text(
      text = text,
      style =
        MaterialTheme.typography.labelLarge.copy(
          fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        ),
      color = if (selected) activeColor else BasoPrimaryText,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
    )
  }
}

@Composable
private fun AiLoadingCard(message: String) {
  Surface(
    color = BasoSurfaceCard,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, BasoNeonTurquoise.copy(alpha = 0.5f)),
    modifier = Modifier.fillMaxWidth().testTag("ai_loading_card"),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(14.dp),
    ) {
      CircularProgressIndicator(
        color = BasoNeonTurquoise,
        strokeWidth = 2.5.dp,
        modifier = Modifier.size(22.dp),
      )
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = BasoPrimaryText,
      )
    }
  }
}

@Composable
private fun AiErrorCard(
  message: String,
  retryLabel: String,
  onRetry: (() -> Unit)?,
) {
  Surface(
    color = BasoErrorSubtleBg,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, BasoError.copy(alpha = 0.65f)),
    modifier = Modifier.fillMaxWidth().testTag("ai_error_card"),
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.padding(14.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Icon(
          imageVector = Icons.Default.ErrorOutline,
          contentDescription = null,
          tint = BasoError,
          modifier = Modifier.size(20.dp),
        )
        Text(
          text = message,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoPrimaryText,
          modifier = Modifier.weight(1f),
        )
      }
      if (onRetry != null) {
        BasoSecondaryButton(
          text = retryLabel,
          onClick = onRetry,
          leadingIcon = Icons.Default.Refresh,
          highlightTurquoise = true,
          testTag = "ai_error_retry_button",
        )
      }
    }
  }
}

private fun parseHexColorSafely(hex: String): Color {
  val cleaned = hex.trim().removePrefix("#")
  return try {
    when (cleaned.length) {
      6 -> Color(("FF$cleaned").toLong(16))
      8 -> Color(cleaned.toLong(16))
      else -> BasoNeonTurquoise
    }
  } catch (_: Exception) {
    BasoNeonTurquoise
  }
}
