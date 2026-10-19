package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ai.BasoAiResult
import com.example.ai.BasoAiRouter
import com.example.ai.BasoAiRoutingSettings
import com.example.ai.BasoConnectionStatus
import com.example.ai.BasoKeyCipher
import com.example.ai.BasoProviderConfig
import com.example.ai.BasoProviderType
import com.example.ai.GeminiAiProvider
import com.example.ai.OpenAiProvider
import com.example.data.BasoLocalStore
import com.example.ui.components.BasoCard
import com.example.ui.components.BasoPrimaryButton
import com.example.ui.components.BasoSecondaryButton
import com.example.ui.theme.AppLanguage
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
import kotlinx.coroutines.launch

// Localized strings data for AI Provider Settings
private data class AiSettingsStrings(
  val screenTitle: String,
  val screenSubtitle: String,
  val tabGemini: String,
  val tabOpenAi: String,
  val primaryBadge: String,
  val setAsPrimary: String,
  val apiKeyLabel: String,
  val apiKeyPlaceholder: String,
  val apiKeyStoredSafe: String,
  val modelLabel: String,
  val modelPlaceholder: String,
  val recommendedModels: String,
  val endpointLabel: String,
  val endpointPlaceholder: String,
  val testConnectionAction: String,
  val testingAction: String,
  val saveAction: String,
  val deleteKeyAction: String,
  val statusConnected: String,
  val statusError: String,
  val statusTesting: String,
  val statusUntested: String,
  val statusUnconfigured: String,
  val routingCardTitle: String,
  val routingCardSubtitle: String,
  val selectPrimaryProvider: String,
  val failoverToggleTitle: String,
  val failoverToggleSubtitle: String,
  val timeoutLabel: String,
  val retriesLabel: String,
  val costPrivacyTitle: String,
  val costNoticeText: String,
  val keySafetyNoticeText: String,
  val privacyNoticeText: String,
  val savedFeedback: String,
  val deletedFeedback: String,
  val testSuccessFeedback: String,
  val testFailedFeedback: String,
  val confirmDeleteTitle: String,
  val confirmDeleteBody: String,
  val confirmYes: String,
  val confirmCancel: String,
)

private fun stringsForAiSettings(language: AppLanguage): AiSettingsStrings =
  when (language) {
    AppLanguage.KURDISH_SORANI ->
      AiSettingsStrings(
        screenTitle = "ڕێکخستنەکانی دابینکەری AI",
        screenSubtitle = "بەڕێوەبردنی کلیلی Google Gemini و OpenAI، گۆڕینی خودکار و تاقیکردنەوە",
        tabGemini = "Google Gemini",
        tabOpenAi = "OpenAI (ChatGPT)",
        primaryBadge = "سەرەکی",
        setAsPrimary = "دیاریکردن وەک دابینکەری سەرەکی",
        apiKeyLabel = "کلیلی نهێنیی API (Secret Key)",
        apiKeyPlaceholder = "کلیلی تایبەتی خۆت لێرە دابنێ...",
        apiKeyStoredSafe = "کلیل بە پارێزراوی لە ڕێگەی Android Keystore لەسەر ئامێرەکەت هەڵدەگیرێت.",
        modelLabel = "ناوی مۆدێل (Model ID)",
        modelPlaceholder = "بۆ نموونە gemini-1.5-flash یان gpt-4o-mini",
        recommendedModels = "مۆدێلە پێشنیارکراوەکان:",
        endpointLabel = "ناونیشانی سێرڤەری HTTPS (Endpoint URL)",
        endpointPlaceholder = "ناونیشانی فەرمی یان پرۆکسی پارێزراوی خۆت",
        testConnectionAction = "تاقیکردنەوەی پەیوەندی",
        testingAction = "پشکنین...",
        saveAction = "پاشەکەوتکردنی ڕێکخستنەکان",
        deleteKeyAction = "سڕینەوەی کلیل",
        statusConnected = "پەیوەندیی سەرکەوتوو (چالاک)",
        statusError = "هەڵەی پەیوەندی یان کلیل",
        statusTesting = "تاقیکردنەوە لە کاردایە...",
        statusUntested = "تاقینەکراوەتەوە",
        statusUnconfigured = "ڕێکنەخراوە",
        routingCardTitle = "ڕێڕەو و گۆڕینی خودکار (Failover)",
        routingCardSubtitle = "دیاریکردنی دابینکەری سەرەکی و یەدەگ بۆ ڕێگریکردن لە پچڕانی خزمەتگوزاری",
        selectPrimaryProvider = "دابینکەری سەرەکی هەڵبژێرە:",
        failoverToggleTitle = "گۆڕینی خودکاری دابینکەر (Automatic Failover)",
        failoverToggleSubtitle = "ئەگەر دابینکەری سەرەکی تووشی هەڵە، کاتی بەسەرچوو، یان کۆتا بوو، ڕاستەوخۆ دابینکەری دووەم بەکاردێت.",
        timeoutLabel = "کاتی بەسەرچوونی داواکاری (Timeout):",
        retriesLabel = "دووبارەکردنەوە لە کاتی هەڵەی کاتیدا:",
        costPrivacyTitle = "ئاگاداریی خەرجی، ئاسایش و تایبەتمەندی",
        costNoticeText = "ئاگاداریی خەرجی: هەردوو خزمەتگوزاریی Gemini و OpenAI پێویستیان بە هەژمار و پشکی بەکارهێنان هەیە و ڕەنگە تێچوویان هەبێت بەپێی پلانی بەکارهێنەر. هەرگیز بەکارهێنانی بێسنوور یان بێبەرامبەر بەڵێن نادرێت.",
        keySafetyNoticeText = "پاراستنی کلیل: هەڵگرتنی کلیلی تایبەت لەسەر مۆبایل ناتوانێت پاراستنی تەواوەتی دابین بکات. بۆ بەرهەمهێنانی بازرگانی و پاراستنی باڵا، بەکارهێنانی سێرڤەری تایبەتی خۆت (Backend Proxy) پێشنیار دەکرێت.",
        privacyNoticeText = "تایبەتمەندی: زانیاری و پرۆمپتی تۆ تەنها کاتێک بۆ دابینکەری دووەم دەنێردرێت کە گۆڕینی خودکار چالاک بێت و دابینکەری سەرەکی شکستی هێنابێت.",
        savedFeedback = "ڕێکخستنەکانی دابینکەر بە سەرکەوتوویی پاشەکەوت کران.",
        deletedFeedback = "کلیلی ئەم دابینکەرە سڕایەوە.",
        testSuccessFeedback = "پەیوەندی بە سەرکەوتوویی تاقیکرایەوە!",
        testFailedFeedback = "شکست لە تاقیکردنەوەی پەیوەندی:",
        confirmDeleteTitle = "سڕینەوەی کلیل",
        confirmDeleteBody = "دڵنیایت لە سڕینەوەی کلیلی ئەم دابینکەرە لەسەر ئامێرەکەت؟",
        confirmYes = "بەڵێ، بسڕەوە",
        confirmCancel = "پاشگەزبوونەوە",
      )
    AppLanguage.ARABIC ->
      AiSettingsStrings(
        screenTitle = "إعدادات موفري الذكاء الاصطناعي",
        screenSubtitle = "إدارة مفاتيح Google Gemini و OpenAI، التبديل التلقائي والاختبار",
        tabGemini = "Google Gemini",
        tabOpenAi = "OpenAI (ChatGPT)",
        primaryBadge = "الأساسي",
        setAsPrimary = "تعيين كموفر أساسي",
        apiKeyLabel = "مفتاح API السري (Secret Key)",
        apiKeyPlaceholder = "أدخل مفتاحك الخاص هنا...",
        apiKeyStoredSafe = "يتم حفظ المفتاح مشفراً بأمان عبر Android Keystore على جهازك.",
        modelLabel = "معرّف النموذج (Model ID)",
        modelPlaceholder = "مثال: gemini-1.5-flash أو gpt-4o-mini",
        recommendedModels = "النماذج المقترحة:",
        endpointLabel = "عنوان نقطة النهاية HTTPS (Endpoint URL)",
        endpointPlaceholder = "العنوان الرسمي أو البروكسي الخاص بك",
        testConnectionAction = "اختبار الاتصال",
        testingAction = "جارٍ الفحص...",
        saveAction = "حفظ الإعدادات",
        deleteKeyAction = "حذف المفتاح",
        statusConnected = "الاتصال ناجح (نشط)",
        statusError = "خطأ في الاتصال أو المفتاح",
        statusTesting = "جارٍ الاختبار...",
        statusUntested = "لم يتم الاختبار",
        statusUnconfigured = "غير مهيأ",
        routingCardTitle = "التوجيه والتبديل التلقائي (Failover)",
        routingCardSubtitle = "تحديد الموفر الأساسي والاحتياطي لتجنب انقطاع الخدمة",
        selectPrimaryProvider = "اختر الموفر الأساسي:",
        failoverToggleTitle = "التبديل التلقائي (Automatic Failover)",
        failoverToggleSubtitle = "في حال فشل الموفر الأساسي أو انتهاء الحصة، يتم التبديل للموفر الاحتياطي تلقائياً.",
        timeoutLabel = "مهلة انتظار الطلب (Timeout):",
        retriesLabel = "إعادة المحاولة عند الأخطاء المؤقتة:",
        costPrivacyTitle = "تنبيه التكلفة والأمان والخصوصية",
        costNoticeText = "تنبيه التكلفة: كل من Gemini و OpenAI يتطلبان حساباً وحصة استخدام مدفوعة وفق خطتك. لا يوجد استخدام مجاني غير محدود.",
        keySafetyNoticeText = "أمان المفاتيح: تخزين المفتاح في التطبيق لا يوفر حماية مطلقة. للإنتاج، يوصى باستخدام خادم وسيط آمن (Backend Proxy).",
        privacyNoticeText = "الخصوصية: لا يتم إرسال بياناتك للموفر الاحتياطي إلا إذا كان التبديل التلقائي مفعلاً وفشل الموفر الأول.",
        savedFeedback = "تم حفظ إعدادات موفر الذكاء الاصطناعي بنجاح.",
        deletedFeedback = "تم حذف مفتاح هذا الموفر.",
        testSuccessFeedback = "تم اختبار الاتصال بنجاح!",
        testFailedFeedback = "فشل اختبار الاتصال:",
        confirmDeleteTitle = "حذف المفتاح",
        confirmDeleteBody = "هل أنت متأكد من حذف مفتاح هذا الموفر من جهازك؟",
        confirmYes = "نعم، حذف",
        confirmCancel = "إلغاء",
      )
    AppLanguage.ENGLISH ->
      AiSettingsStrings(
        screenTitle = "AI Provider Settings",
        screenSubtitle = "Manage Google Gemini & OpenAI keys, automatic failover, and connection tests",
        tabGemini = "Google Gemini",
        tabOpenAi = "OpenAI (ChatGPT)",
        primaryBadge = "Primary",
        setAsPrimary = "Set as Primary Provider",
        apiKeyLabel = "Secret API Key",
        apiKeyPlaceholder = "Paste your private API key here...",
        apiKeyStoredSafe = "Keys are securely encrypted on-device via Android Keystore.",
        modelLabel = "Model Identifier (Model ID)",
        modelPlaceholder = "e.g., gemini-1.5-flash or gpt-4o-mini",
        recommendedModels = "Recommended Models:",
        endpointLabel = "HTTPS Endpoint URL",
        endpointPlaceholder = "Official endpoint or custom backend proxy URL",
        testConnectionAction = "Test Connection",
        testingAction = "Testing...",
        saveAction = "Save Settings",
        deleteKeyAction = "Delete Key",
        statusConnected = "Connected & Verified",
        statusError = "Connection / Auth Error",
        statusTesting = "Testing in progress...",
        statusUntested = "Untested",
        statusUnconfigured = "Not Configured",
        routingCardTitle = "Routing & Automatic Failover",
        routingCardSubtitle = "Designate primary and fallback providers to ensure uninterrupted service",
        selectPrimaryProvider = "Select Primary Provider:",
        failoverToggleTitle = "Automatic Failover",
        failoverToggleSubtitle = "If the primary provider encounters a rate limit, timeout, or outage, seamlessly fall back to the secondary provider.",
        timeoutLabel = "Request Timeout:",
        retriesLabel = "Retries on Transient Failure:",
        costPrivacyTitle = "Cost, Security & Privacy Notice",
        costNoticeText = "Billing Notice: Both Gemini and OpenAI are paid/quota-based services that may incur costs under your account plan. No free or unlimited usage is promised.",
        keySafetyNoticeText = "Credential Storage: Storing API keys on mobile devices cannot offer complete protection against reverse engineering. For enterprise production, use an authenticated HTTPS backend proxy.",
        privacyNoticeText = "Privacy: Your requests are only sent to the secondary provider if automatic failover is enabled and the primary provider request fails.",
        savedFeedback = "AI provider settings saved successfully.",
        deletedFeedback = "Provider API key deleted.",
        testSuccessFeedback = "Connection test passed successfully!",
        testFailedFeedback = "Connection test failed:",
        confirmDeleteTitle = "Delete API Key",
        confirmDeleteBody = "Are you sure you want to remove this provider's API key from this device?",
        confirmYes = "Delete",
        confirmCancel = "Cancel",
      )
  }

@Composable
fun BasoAiSettingsScreen(
  localStore: BasoLocalStore,
  aiRouter: BasoAiRouter,
  currentLanguage: AppLanguage,
  highContrastBorders: Boolean = false,
  spaciousDensity: Boolean = true,
  onBack: () -> Unit,
  onShowFeedback: (String) -> Unit,
) {
  val strings = remember(currentLanguage) { stringsForAiSettings(currentLanguage) }
  val coroutineScope = rememberCoroutineScope()

  // Load persistent settings
  var routingSettings by remember { mutableStateOf(localStore.loadAiRoutingSettings()) }
  var selectedTab by remember { mutableStateOf(routingSettings.primaryProvider) }

  // Editable form state for Gemini
  var geminiKey by remember { mutableStateOf(routingSettings.geminiConfig.apiKey) }
  var geminiShowKey by remember { mutableStateOf(false) }
  var geminiModel by remember { mutableStateOf(routingSettings.geminiConfig.model) }
  var geminiEndpoint by remember { mutableStateOf(routingSettings.geminiConfig.endpointUrl) }
  var geminiStatus by remember { mutableStateOf(routingSettings.geminiConfig.status) }
  var geminiStatusMessage by remember { mutableStateOf(routingSettings.geminiConfig.statusMessage) }
  var geminiIsTesting by remember { mutableStateOf(false) }

  // Editable form state for OpenAI
  var openAiKey by remember { mutableStateOf(routingSettings.openAiConfig.apiKey) }
  var openAiShowKey by remember { mutableStateOf(false) }
  var openAiModel by remember { mutableStateOf(routingSettings.openAiConfig.model) }
  var openAiEndpoint by remember { mutableStateOf(routingSettings.openAiConfig.endpointUrl) }
  var openAiStatus by remember { mutableStateOf(routingSettings.openAiConfig.status) }
  var openAiStatusMessage by remember { mutableStateOf(routingSettings.openAiConfig.statusMessage) }
  var openAiIsTesting by remember { mutableStateOf(false) }

  // Routing controls
  var primaryProvider by remember { mutableStateOf(routingSettings.primaryProvider) }
  var failoverEnabled by remember { mutableStateOf(routingSettings.automaticFailoverEnabled) }
  var timeoutSeconds by remember { mutableStateOf(routingSettings.timeoutSeconds) }
  var maxRetries by remember { mutableStateOf(routingSettings.maxRetries) }

  var showDeleteDialogFor by remember { mutableStateOf<BasoProviderType?>(null) }

  val verticalSpacing = if (spaciousDensity) 16.dp else 12.dp

  CompositionLocalProvider(LocalLayoutDirection provides currentLanguage.layoutDirection) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(BasoPrimaryBackground)
        .testTag("baso_ai_settings_screen")
    ) {
      LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = Modifier.fillMaxSize(),
      ) {
        // --- 1. Top Bar & Title ---
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            IconButton(
              onClick = onBack,
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(BasoSecondaryBackground)
                .border(1.dp, BasoBorder, CircleShape)
                .testTag("ai_settings_back_button"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = BasoPrimaryText,
              )
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = strings.screenTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = BasoPrimaryText,
              )
              Text(
                text = strings.screenSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = BasoSecondaryText,
              )
            }
          }
        }

        // --- 2. Provider Tabs (Google Gemini vs OpenAI) ---
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(BasoSecondaryBackground)
              .border(1.dp, BasoBorder, RoundedCornerShape(14.dp))
              .padding(4.dp),
          ) {
            ProviderTabItem(
              title = strings.tabGemini,
              isSelected = selectedTab == BasoProviderType.GEMINI,
              isPrimary = primaryProvider == BasoProviderType.GEMINI,
              primaryLabel = strings.primaryBadge,
              onClick = { selectedTab = BasoProviderType.GEMINI },
              modifier = Modifier.weight(1f).testTag("tab_gemini"),
            )
            ProviderTabItem(
              title = strings.tabOpenAi,
              isSelected = selectedTab == BasoProviderType.OPENAI,
              isPrimary = primaryProvider == BasoProviderType.OPENAI,
              primaryLabel = strings.primaryBadge,
              onClick = { selectedTab = BasoProviderType.OPENAI },
              modifier = Modifier.weight(1f).testTag("tab_openai"),
            )
          }
        }

        // --- 3. Active Provider Details Card ---
        item {
          BasoCard(
            highContrastBorder = highContrastBorders,
            modifier = Modifier.fillMaxWidth().testTag("provider_details_card"),
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
              val isGemini = selectedTab == BasoProviderType.GEMINI
              val currentKey = if (isGemini) geminiKey else openAiKey
              val showKey = if (isGemini) geminiShowKey else openAiShowKey
              val currentModel = if (isGemini) geminiModel else openAiModel
              val currentEndpoint = if (isGemini) geminiEndpoint else openAiEndpoint
              val currentStatus = if (isGemini) geminiStatus else openAiStatus
              val currentStatusMsg = if (isGemini) geminiStatusMessage else openAiStatusMessage
              val isTesting = if (isGemini) geminiIsTesting else openAiIsTesting

              // Header: Provider Name + Set Primary Button
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = if (isGemini) BasoNeonTurquoise else BasoNeonYellow,
                    modifier = Modifier.size(22.dp),
                  )
                  Text(
                    text = if (isGemini) strings.tabGemini else strings.tabOpenAi,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BasoPrimaryText,
                  )
                }

                if (primaryProvider != selectedTab) {
                  BasoSecondaryButton(
                    text = strings.setAsPrimary,
                    onClick = {
                      primaryProvider = selectedTab
                      val updated = routingSettings.copy(primaryProvider = selectedTab)
                      routingSettings = updated
                      localStore.saveAiRoutingSettings(updated)
                      onShowFeedback(strings.savedFeedback)
                    },
                    modifier = Modifier.testTag("set_as_primary_button"),
                  )
                } else {
                  Surface(
                    color = BasoSuccessSubtleBg,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BasoSuccess.copy(alpha = 0.5f)),
                  ) {
                    Text(
                      text = "✓ ${strings.primaryBadge}",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = BasoSuccess,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                  }
                }
              }

              // Status Indicator Banner
              StatusBanner(
                status = currentStatus,
                statusMessage = currentStatusMsg,
                strings = strings,
              )

              // API Key Field with Show/Hide Control
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = strings.apiKeyLabel,
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = BasoPrimaryText,
                )
                OutlinedTextField(
                  value = currentKey,
                  onValueChange = {
                    if (isGemini) geminiKey = it else openAiKey = it
                  },
                  placeholder = {
                    Text(
                      text = strings.apiKeyPlaceholder,
                      color = BasoSecondaryText.copy(alpha = 0.5f),
                    )
                  },
                  visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                  singleLine = true,
                  trailingIcon = {
                    IconButton(
                      onClick = {
                        if (isGemini) geminiShowKey = !geminiShowKey else openAiShowKey = !openAiShowKey
                      },
                      modifier = Modifier.testTag("toggle_key_visibility_button"),
                    ) {
                      Icon(
                        imageVector = if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Visibility",
                        tint = BasoSecondaryText,
                      )
                    }
                  },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BasoNeonTurquoise,
                    unfocusedBorderColor = BasoBorder,
                    focusedTextColor = BasoPrimaryText,
                    unfocusedTextColor = BasoPrimaryText,
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_settings_api_key_input"),
                )
                Text(
                  text = strings.apiKeyStoredSafe,
                  style = MaterialTheme.typography.bodySmall,
                  color = BasoSecondaryText,
                )
              }

              // Model Selection & Suggested Chips
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = strings.modelLabel,
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = BasoPrimaryText,
                )
                OutlinedTextField(
                  value = currentModel,
                  onValueChange = {
                    if (isGemini) geminiModel = it else openAiModel = it
                  },
                  placeholder = {
                    Text(text = strings.modelPlaceholder, color = BasoSecondaryText.copy(alpha = 0.5f))
                  },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BasoNeonTurquoise,
                    unfocusedBorderColor = BasoBorder,
                    focusedTextColor = BasoPrimaryText,
                    unfocusedTextColor = BasoPrimaryText,
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_settings_model_input"),
                )

                Text(
                  text = strings.recommendedModels,
                  style = MaterialTheme.typography.bodySmall,
                  color = BasoSecondaryText,
                )
                val modelChips = if (isGemini) {
                  listOf("gemini-1.5-flash", "gemini-2.0-flash", "gemini-1.5-pro")
                } else {
                  listOf("gpt-4o-mini", "gpt-4o", "gpt-4-turbo")
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  items(modelChips) { chip ->
                    Surface(
                      color = if (currentModel == chip) BasoTurquoiseSubtleGlow else BasoSecondaryBackground,
                      shape = RoundedCornerShape(8.dp),
                      border = BorderStroke(
                        1.dp,
                        if (currentModel == chip) BasoNeonTurquoise else BasoBorder,
                      ),
                      modifier = Modifier
                        .clickable {
                          if (isGemini) geminiModel = chip else openAiModel = chip
                        }
                        .testTag("model_chip_$chip"),
                    ) {
                      Text(
                        text = chip,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (currentModel == chip) BasoNeonTurquoise else BasoPrimaryText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      )
                    }
                  }
                }
              }

              // HTTPS Endpoint URL
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = strings.endpointLabel,
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = BasoPrimaryText,
                )
                OutlinedTextField(
                  value = currentEndpoint,
                  onValueChange = {
                    if (isGemini) geminiEndpoint = it else openAiEndpoint = it
                  },
                  placeholder = {
                    Text(text = strings.endpointPlaceholder, color = BasoSecondaryText.copy(alpha = 0.5f))
                  },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BasoNeonTurquoise,
                    unfocusedBorderColor = BasoBorder,
                    focusedTextColor = BasoPrimaryText,
                    unfocusedTextColor = BasoPrimaryText,
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_settings_endpoint_input"),
                )
              }

              // Action Buttons: Save, Test Connection, Delete
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
              ) {
                // Test Connection
                BasoSecondaryButton(
                  text = if (isTesting) strings.testingAction else strings.testConnectionAction,
                  leadingIcon = Icons.Default.Refresh,
                  onClick = {
                    if (isGemini) {
                      geminiIsTesting = true
                      geminiStatus = BasoConnectionStatus.TESTING
                    } else {
                      openAiIsTesting = true
                      openAiStatus = BasoConnectionStatus.TESTING
                    }
                    coroutineScope.launch {
                      val tempProvider = if (isGemini) {
                        GeminiAiProvider(configProvider = {
                          BasoProviderConfig(
                            providerType = BasoProviderType.GEMINI,
                            apiKey = geminiKey,
                            model = geminiModel.ifBlank { GeminiAiProvider.DEFAULT_MODEL },
                            endpointUrl = geminiEndpoint.ifBlank { GeminiAiProvider.DEFAULT_ENDPOINT },
                          )
                        })
                      } else {
                        OpenAiProvider(configProvider = {
                          BasoProviderConfig(
                            providerType = BasoProviderType.OPENAI,
                            apiKey = openAiKey,
                            model = openAiModel.ifBlank { OpenAiProvider.DEFAULT_MODEL },
                            endpointUrl = openAiEndpoint.ifBlank { OpenAiProvider.DEFAULT_ENDPOINT },
                          )
                        })
                      }
                      val testResult = tempProvider.testConnection()
                      if (isGemini) {
                        geminiIsTesting = false
                        when (testResult) {
                          is BasoAiResult.Success -> {
                            geminiStatus = BasoConnectionStatus.CONNECTED
                            geminiStatusMessage = strings.testSuccessFeedback
                            onShowFeedback(strings.testSuccessFeedback)
                          }
                          is BasoAiResult.Failure -> {
                            geminiStatus = BasoConnectionStatus.ERROR
                            geminiStatusMessage = testResult.safeMessage
                            onShowFeedback("${strings.testFailedFeedback} ${testResult.safeMessage}")
                          }
                        }
                      } else {
                        openAiIsTesting = false
                        when (testResult) {
                          is BasoAiResult.Success -> {
                            openAiStatus = BasoConnectionStatus.CONNECTED
                            openAiStatusMessage = strings.testSuccessFeedback
                            onShowFeedback(strings.testSuccessFeedback)
                          }
                          is BasoAiResult.Failure -> {
                            openAiStatus = BasoConnectionStatus.ERROR
                            openAiStatusMessage = testResult.safeMessage
                            onShowFeedback("${strings.testFailedFeedback} ${testResult.safeMessage}")
                          }
                        }
                      }
                    }
                  },
                  modifier = Modifier.weight(1f).testTag("test_connection_button"),
                )

                // Save
                BasoPrimaryButton(
                  text = strings.saveAction,
                  leadingIcon = Icons.Default.Save,
                  onClick = {
                    val updatedGemini = routingSettings.geminiConfig.copy(
                      apiKey = geminiKey.trim(),
                      model = geminiModel.trim().ifBlank { GeminiAiProvider.DEFAULT_MODEL },
                      endpointUrl = geminiEndpoint.trim().ifBlank { GeminiAiProvider.DEFAULT_ENDPOINT },
                      status = geminiStatus,
                      statusMessage = geminiStatusMessage,
                      lastTestedTimestamp = System.currentTimeMillis(),
                    )
                    val updatedOpenAi = routingSettings.openAiConfig.copy(
                      apiKey = openAiKey.trim(),
                      model = openAiModel.trim().ifBlank { OpenAiProvider.DEFAULT_MODEL },
                      endpointUrl = openAiEndpoint.trim().ifBlank { OpenAiProvider.DEFAULT_ENDPOINT },
                      status = openAiStatus,
                      statusMessage = openAiStatusMessage,
                      lastTestedTimestamp = System.currentTimeMillis(),
                    )
                    val newSettings = routingSettings.copy(
                      primaryProvider = primaryProvider,
                      automaticFailoverEnabled = failoverEnabled,
                      timeoutSeconds = timeoutSeconds,
                      maxRetries = maxRetries,
                      geminiConfig = updatedGemini,
                      openAiConfig = updatedOpenAi,
                    )
                    routingSettings = newSettings
                    localStore.saveAiRoutingSettings(newSettings)
                    onShowFeedback(strings.savedFeedback)
                  },
                  modifier = Modifier.weight(1f).testTag("save_settings_button"),
                )
              }

              // Delete Key Button
              if (currentKey.isNotBlank()) {
                BasoSecondaryButton(
                  text = strings.deleteKeyAction,
                  leadingIcon = Icons.Default.Delete,
                  onClick = { showDeleteDialogFor = selectedTab },
                  modifier = Modifier.fillMaxWidth().testTag("delete_key_button"),
                )
              }
            }
          }
        }

        // --- 4. Central Routing & Automatic Failover Settings Card ---
        item {
          BasoCard(
            highContrastBorder = highContrastBorders,
            modifier = Modifier.fillMaxWidth().testTag("routing_failover_card"),
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Default.SwapHoriz,
                  contentDescription = null,
                  tint = BasoNeonYellow,
                  modifier = Modifier.size(22.dp),
                )
                Text(
                  text = strings.routingCardTitle,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = BasoPrimaryText,
                )
              }
              Text(
                text = strings.routingCardSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = BasoSecondaryText,
              )

              // Automatic Failover Toggle Switch
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(BasoSecondaryBackground)
                  .padding(12.dp),
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = strings.failoverToggleTitle,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = BasoPrimaryText,
                  )
                  Text(
                    text = strings.failoverToggleSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = BasoSecondaryText,
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                  checked = failoverEnabled,
                  onCheckedChange = {
                    failoverEnabled = it
                    val updated = routingSettings.copy(automaticFailoverEnabled = it)
                    routingSettings = updated
                    localStore.saveAiRoutingSettings(updated)
                  },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = BasoNeonYellow,
                    checkedTrackColor = BasoSurfaceCard,
                    uncheckedThumbColor = BasoSecondaryText,
                    uncheckedTrackColor = BasoSecondaryBackground,
                  ),
                  modifier = Modifier.testTag("failover_toggle_switch"),
                )
              }

              // Timeout and Retries Configuration
              Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
              ) {
                // Timeout Selector
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    text = strings.timeoutLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = BasoSecondaryText,
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(15, 30, 60).forEach { sec ->
                      Surface(
                        color = if (timeoutSeconds == sec) BasoYellowSubtleGlow else BasoSecondaryBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (timeoutSeconds == sec) BasoNeonYellow else BasoBorder),
                        modifier = Modifier
                          .clickable {
                            timeoutSeconds = sec
                            val updated = routingSettings.copy(timeoutSeconds = sec)
                            routingSettings = updated
                            localStore.saveAiRoutingSettings(updated)
                          }
                          .testTag("timeout_choice_$sec"),
                      ) {
                        Text(
                          text = "${sec}s",
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                          color = if (timeoutSeconds == sec) BasoNeonYellow else BasoPrimaryText,
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                      }
                    }
                  }
                }

                // Retries Selector
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    text = strings.retriesLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = BasoSecondaryText,
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0, 1, 2).forEach { retries ->
                      Surface(
                        color = if (maxRetries == retries) BasoTurquoiseSubtleGlow else BasoSecondaryBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (maxRetries == retries) BasoNeonTurquoise else BasoBorder),
                        modifier = Modifier
                          .clickable {
                            maxRetries = retries
                            val updated = routingSettings.copy(maxRetries = retries)
                            routingSettings = updated
                            localStore.saveAiRoutingSettings(updated)
                          }
                          .testTag("retries_choice_$retries"),
                      ) {
                        Text(
                          text = "$retries",
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                          color = if (maxRetries == retries) BasoNeonTurquoise else BasoPrimaryText,
                          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // --- 5. Cost, Security & Privacy Notice Card ---
        item {
          BasoCard(
            highContrastBorder = highContrastBorders,
            modifier = Modifier.fillMaxWidth().testTag("cost_privacy_card"),
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Default.Security,
                  contentDescription = null,
                  tint = BasoNeonTurquoise,
                  modifier = Modifier.size(20.dp),
                )
                Text(
                  text = strings.costPrivacyTitle,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = BasoPrimaryText,
                )
              }
              Text(
                text = strings.costNoticeText,
                style = MaterialTheme.typography.bodySmall,
                color = BasoSecondaryText,
              )
              Text(
                text = strings.keySafetyNoticeText,
                style = MaterialTheme.typography.bodySmall,
                color = BasoSecondaryText,
              )
              Text(
                text = strings.privacyNoticeText,
                style = MaterialTheme.typography.bodySmall,
                color = BasoSecondaryText,
              )
            }
          }
        }
      }

      // Confirm Delete Key Dialog
      showDeleteDialogFor?.let { providerType ->
        androidx.compose.material3.AlertDialog(
          onDismissRequest = { showDeleteDialogFor = null },
          title = { Text(text = strings.confirmDeleteTitle, color = BasoPrimaryText) },
          text = { Text(text = strings.confirmDeleteBody, color = BasoSecondaryText) },
          confirmButton = {
            BasoPrimaryButton(
              text = strings.confirmYes,
              onClick = {
                if (providerType == BasoProviderType.GEMINI) {
                  geminiKey = ""
                  geminiStatus = BasoConnectionStatus.UNCONFIGURED
                  geminiStatusMessage = ""
                  val updated = routingSettings.copy(
                    geminiConfig = routingSettings.geminiConfig.copy(
                      apiKey = "",
                      status = BasoConnectionStatus.UNCONFIGURED,
                      statusMessage = "",
                    )
                  )
                  routingSettings = updated
                  localStore.saveAiRoutingSettings(updated)
                } else {
                  openAiKey = ""
                  openAiStatus = BasoConnectionStatus.UNCONFIGURED
                  openAiStatusMessage = ""
                  val updated = routingSettings.copy(
                    openAiConfig = routingSettings.openAiConfig.copy(
                      apiKey = "",
                      status = BasoConnectionStatus.UNCONFIGURED,
                      statusMessage = "",
                    )
                  )
                  routingSettings = updated
                  localStore.saveAiRoutingSettings(updated)
                }
                showDeleteDialogFor = null
                onShowFeedback(strings.deletedFeedback)
              },
              modifier = Modifier.testTag("confirm_delete_key_button"),
            )
          },
          dismissButton = {
            BasoSecondaryButton(
              text = strings.confirmCancel,
              onClick = { showDeleteDialogFor = null },
            )
          },
          containerColor = BasoSurfaceCard,
          shape = RoundedCornerShape(16.dp),
        )
      }
    }
  }
}

@Composable
private fun ProviderTabItem(
  title: String,
  isSelected: Boolean,
  isPrimary: Boolean,
  primaryLabel: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) BasoSurfaceCard else Color.Transparent)
      .clickable(onClick = onClick)
      .padding(vertical = 10.dp, horizontal = 12.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        ),
        color = if (isSelected) BasoPrimaryText else BasoSecondaryText,
      )
      if (isPrimary) {
        Surface(
          color = BasoSuccessSubtleBg,
          shape = RoundedCornerShape(4.dp),
        ) {
          Text(
            text = primaryLabel,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = BasoSuccess,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun StatusBanner(
  status: BasoConnectionStatus,
  statusMessage: String,
  strings: AiSettingsStrings,
) {
  val (bgColor, borderColor, icon, iconColor, statusLabel) = when (status) {
    BasoConnectionStatus.CONNECTED -> Tuple5(
      BasoSuccessSubtleBg,
      BasoSuccess.copy(alpha = 0.5f),
      Icons.Default.CheckCircle,
      BasoSuccess,
      strings.statusConnected,
    )
    BasoConnectionStatus.ERROR -> Tuple5(
      BasoErrorSubtleBg,
      BasoError.copy(alpha = 0.5f),
      Icons.Default.ErrorOutline,
      BasoError,
      strings.statusError,
    )
    BasoConnectionStatus.TESTING -> Tuple5(
      BasoYellowSubtleGlow,
      BasoNeonYellow.copy(alpha = 0.5f),
      Icons.Default.Refresh,
      BasoNeonYellow,
      strings.statusTesting,
    )
    BasoConnectionStatus.UNTESTED -> Tuple5(
      BasoSecondaryBackground,
      BasoBorder,
      Icons.Default.Info,
      BasoSecondaryText,
      strings.statusUntested,
    )
    BasoConnectionStatus.UNCONFIGURED -> Tuple5(
      BasoSecondaryBackground,
      BasoBorder,
      Icons.Default.Warning,
      BasoSecondaryText,
      strings.statusUnconfigured,
    )
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, borderColor),
    modifier = Modifier.fillMaxWidth().testTag("connection_status_banner"),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.padding(12.dp),
    ) {
      if (status == BasoConnectionStatus.TESTING) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          strokeWidth = 2.dp,
          color = BasoNeonYellow,
        )
      } else {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(20.dp),
        )
      }
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = statusLabel,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )
        if (statusMessage.isNotBlank()) {
          Text(
            text = statusMessage,
            style = MaterialTheme.typography.bodySmall,
            color = BasoSecondaryText,
          )
        }
      }
    }
  }
}

private data class Tuple5<A, B, C, D, E>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
)
