package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.core.app.ApplicationProvider
import com.example.ai.BasoAiConfigMode
import com.example.ai.BasoAiEndpointConfig
import com.example.ai.BasoAiErrorType
import com.example.ai.BasoAiProvider
import com.example.ai.BasoAiResult
import com.example.ai.BasoAiRouter
import com.example.ai.BasoAiRoutingSettings
import com.example.ai.BasoAiService
import com.example.ai.BasoConnectionStatus
import com.example.ai.BasoKeyCipher
import com.example.ai.BasoProviderConfig
import com.example.ai.BasoProviderType
import com.example.ai.BasoCodeTaskMode
import com.example.ai.BasoDesignTaskMode
import com.example.ai.BasoAiSanitizer
import com.example.ai.GeminiAiProvider
import com.example.ai.GeminiContent
import com.example.ai.GeminiGenerateContentResponse
import com.example.ai.GeminiPart
import com.example.ai.GeminiRestAiProvider
import com.example.ai.OpenAiChatResponse
import com.example.ai.OpenAiProvider
import com.example.builder.BasoBuilderAiEngine
import com.example.builder.BasoBuilderLifecycleStatus
import com.example.builder.BasoBuilderProject
import com.example.builder.BasoBuilderProjectType
import com.example.builder.BasoBuilderValidatorAndExporter
import com.example.data.BasoLocalStore
import com.example.ui.screens.BasoChatMessage
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.BasoBorder
import com.example.ui.theme.BasoError
import com.example.ui.theme.BasoNeonTurquoise
import com.example.ui.theme.BasoNeonYellow
import com.example.ui.theme.BasoPrimaryBackground
import com.example.ui.theme.BasoPrimaryText
import com.example.ui.theme.BasoSecondaryBackground
import com.example.ui.theme.BasoSecondaryText
import com.example.ui.theme.BasoSuccess
import com.example.ui.theme.BasoSurfaceCard
import java.io.ByteArrayInputStream
import java.net.SocketTimeoutException
import java.util.zip.ZipInputStream
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    BasoLocalStore(context).clearAllForTesting()
  }

  @After
  fun tearDown() {
    BasoLocalStore(context).clearAllForTesting()
  }

  @Test
  fun `read string from context`() {
    val appName = context.getString(R.string.app_name)
    assertEquals("BASO", appName)
  }

  @Test
  fun `verify exact BASO color palette tokens`() {
    assertEquals(0xFF05090F, BasoPrimaryBackground.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFF0B1420, BasoSecondaryBackground.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFF111E2B, BasoSurfaceCard.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFFFFD400, BasoNeonYellow.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFF00F5D4, BasoNeonTurquoise.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFFF5F7FA, BasoPrimaryText.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFF9BAAB8, BasoSecondaryText.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFF243747, BasoBorder.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFF37D99A, BasoSuccess.value.toLong() shr 32 and 0xFFFFFFFFL)
    assertEquals(0xFFFF5B68, BasoError.value.toLong() shr 32 and 0xFFFFFFFFL)
  }

  @Test
  fun `verify default Kurdish Sorani RTL layout and reference screens navigation`() {
    assertEquals(LayoutDirection.Rtl, AppLanguage.KURDISH_SORANI.layoutDirection)
    assertEquals(LayoutDirection.Rtl, AppLanguage.ARABIC.layoutDirection)
    assertEquals(LayoutDirection.Ltr, AppLanguage.ENGLISH.layoutDirection)

    composeTestRule.setContent { BasoApp() }
    composeTestRule.waitForIdle()

    // Default Home screen in Kurdish Sorani
    composeTestRule.onNodeWithTag("baso_header_title").assertIsDisplayed()
    composeTestRule.onNodeWithText("بەخێربێیت بۆ BASO").assertIsDisplayed()
    composeTestRule.onNodeWithTag("baso_home_screen").assertIsDisplayed()

    // Navigate to Projects screen (پڕۆژەکان)
    composeTestRule.onNodeWithTag("baso_nav_projects").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_projects_screen").assertIsDisplayed()

    // Navigate to Clients screen (کڕیاران)
    composeTestRule.onNodeWithTag("baso_nav_clients").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_clients_screen").assertIsDisplayed()

    // Navigate to Portfolio screen (پۆرتفۆلیۆ)
    composeTestRule.onNodeWithTag("baso_nav_portfolio").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_portfolio_screen").assertIsDisplayed()

    // Navigate to Settings / More screen (ڕێکخستنەکان)
    composeTestRule.onNodeWithTag("baso_nav_settings").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_settings_screen").assertIsDisplayed()

    // Open Profile screen from top avatar
    composeTestRule.onNodeWithTag("baso_top_avatar").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_profile_screen").assertIsDisplayed()
  }

  @Test
  fun `verify project creation validation and persistence across navigation and restart`() {
    composeTestRule.setContent { BasoApp() }
    composeTestRule.waitForIdle()

    // 1. Open New Project screen from Home Quick Action
    composeTestRule
      .onNodeWithTag("baso_home_screen")
      .performScrollToNode(hasTestTag("quick_action_new_project"))
    composeTestRule.onNodeWithTag("quick_action_new_project").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_new_project_screen").assertIsDisplayed()

    // 2. Verify empty input validation shows Kurdish Sorani error message
    composeTestRule
      .onNodeWithTag("baso_new_project_screen")
      .performScrollToNode(hasTestTag("new_project_submit_button"))
    composeTestRule.onNodeWithTag("new_project_submit_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
      .onNodeWithTag("baso_new_project_screen")
      .performScrollToNode(hasTestTag("new_project_name_input"))
    composeTestRule.onNodeWithTag("baso_input_supporting_text", useUnmergedTree = true).assertIsDisplayed()

    // 3. Enter test project title and description and save
    val testProjectTitle = "پڕۆژەی تاقیکردنەوەی باسۆ"
    val testProjectDesc = "سیستەمی پاشەکەوتکردنی ناوخۆیی"
    composeTestRule.onNodeWithTag("new_project_name_input").performTextInput(testProjectTitle)
    composeTestRule
      .onNodeWithTag("baso_new_project_screen")
      .performScrollToNode(hasTestTag("new_project_desc_input"))
    composeTestRule.onNodeWithTag("new_project_desc_input").performTextInput(testProjectDesc)
    composeTestRule
      .onNodeWithTag("baso_new_project_screen")
      .performScrollToNode(hasTestTag("new_project_submit_button"))
    composeTestRule.onNodeWithTag("new_project_submit_button").performClick()
    composeTestRule.waitForIdle()

    // 4. Verify navigation to Projects screen and newly created project is displayed
    composeTestRule.onNodeWithTag("baso_projects_screen").assertIsDisplayed()
    composeTestRule.onNodeWithText(testProjectTitle).assertIsDisplayed()

    // 5. Navigate away to Home and Settings, then reopen Projects screen
    composeTestRule.onNodeWithTag("baso_nav_home").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_home_screen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("baso_nav_settings").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_settings_screen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("baso_nav_projects").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText(testProjectTitle).assertIsDisplayed()

    // 6. Verify disk persistence across process restart via a new BasoLocalStore instance
    val restartedStore = BasoLocalStore(context)
    val persistedProjects = restartedStore.loadCustomProjects()
    assertEquals(1, persistedProjects.size)
    assertEquals(testProjectTitle, persistedProjects.first().title)
    assertTrue(persistedProjects.first().subtitle.contains(testProjectDesc))
  }

  @Test
  fun `verify settings language and chat messages persist across app restarts`() {
    val store1 = BasoLocalStore(context)
    assertEquals(AppLanguage.KURDISH_SORANI, store1.loadLanguage())
    assertEquals(false, store1.loadHighContrast())
    assertEquals(true, store1.loadSpaciousDensity())

    store1.saveLanguage(AppLanguage.ARABIC)
    store1.saveHighContrast(true)
    store1.saveSpaciousDensity(false)
    val updatedMessages =
      BasoLocalStore.defaultChatMessages() +
        BasoChatMessage(
          id = "m3",
          text = "پەیامی تاقیکردنەوەی پاشەکەوتکراو",
          time = "10:30",
          isOutgoing = true,
        )
    store1.saveChatMessages(updatedMessages)

    val store2 = BasoLocalStore(context)
    assertEquals(AppLanguage.ARABIC, store2.loadLanguage())
    assertEquals(true, store2.loadHighContrast())
    assertEquals(false, store2.loadSpaciousDensity())
    val reloadedMessages = store2.loadChatMessages()
    assertEquals(3, reloadedMessages.size)
    assertEquals("پەیامی تاقیکردنەوەی پاشەکەوتکراو", reloadedMessages.last().text)

    store2.resetSettingsToDefault()
    assertEquals(AppLanguage.KURDISH_SORANI, store2.loadLanguage())
    assertEquals(false, store2.loadHighContrast())
    assertEquals(true, store2.loadSpaciousDensity())
  }

  @Test
  fun `verify AI provider security redaction unconfigured state and all 5 AI tools`() = runTest {
    val unconfiguredProvider =
      GeminiRestAiProvider(
        apiKeyProvider = { "MY_GEMINI_API_KEY" },
        backendUrlProvider = { "" },
      )
    assertFalse(unconfiguredProvider.isConfigured())
    assertEquals(BasoAiConfigMode.UNCONFIGURED, unconfiguredProvider.configurationMode())
    val unconfiguredService = BasoAiService(unconfiguredProvider)
    val missingKeyResult =
      unconfiguredService.sendChatMessage(
        history = emptyList(),
        userMessage = "سڵاو",
        language = AppLanguage.KURDISH_SORANI,
      )
    assertTrue(missingKeyResult is BasoAiResult.Failure)
    assertEquals(
      BasoAiErrorType.MISSING_CREDENTIALS,
      (missingKeyResult as BasoAiResult.Failure).errorType,
    )

    val fakeSecret = "AIzaSySecretTestKey998877"
    val rawError = "HTTP failure calling https://generativelanguage.googleapis.com/v1beta?key=$fakeSecret"
    val sanitized = GeminiRestAiProvider.sanitizeSecretFromMessage(rawError, fakeSecret)
    assertFalse(sanitized.contains(fakeSecret))
    assertTrue(sanitized.contains("REDACTED"))

    val timeoutFailure = GeminiRestAiProvider.mapExceptionSafely(SocketTimeoutException("timeout"), fakeSecret)
    assertEquals(BasoAiErrorType.TIMEOUT, timeoutFailure.errorType)

    var cannedResponse = "سڵاو! چۆن دەتوانم یارمەتیت بدەم؟"
    val controlledProvider =
      object : BasoAiProvider {
        override fun configurationMode(): BasoAiConfigMode = BasoAiConfigMode.DIRECT_GEMINI_REST

        override fun isConfigured(): Boolean = true

        override suspend fun generateText(
          model: String,
          systemPrompt: String,
          contents: List<GeminiContent>,
          responseMimeType: String?,
          responseSchema: JsonObject?,
          temperature: Float,
        ): BasoAiResult<String> = BasoAiResult.Success(cannedResponse, model)
      }

    val service = BasoAiService(controlledProvider)

    val chatRes = service.sendChatMessage(emptyList(), "سڵاو", AppLanguage.KURDISH_SORANI)
    assertTrue(chatRes is BasoAiResult.Success)
    assertEquals("سڵاو! چۆن دەتوانم یارمەتیت بدەم؟", (chatRes as BasoAiResult.Success).data)

    cannedResponse =
      """{"summary":"Email Validator","language":"Kotlin","code":"fun isValidEmail(s: String) = s.contains(\"@\")","notes":"Pure function"}"""
    val codeRes =
      service.runCodeAssistant(
        taskDescription = "Validate email",
        mode = BasoCodeTaskMode.GENERATE,
        programmingLanguage = "Kotlin",
        appLanguage = AppLanguage.KURDISH_SORANI,
      )
    assertTrue(codeRes is BasoAiResult.Success)
    assertTrue((codeRes as BasoAiResult.Success).data.code.contains("isValidEmail"))

    cannedResponse =
      """{"summary":"Studio Landing","indexHtml":"<!DOCTYPE html><html><body><h1>BASO</h1></body></html>","stylesCss":"body { background: #05090F; }","appJs":"console.log('ready');","securityNote":"Safe static bundle"}"""
    val webRes = service.generateWebsiteBundle("Dark studio website", AppLanguage.KURDISH_SORANI)
    assertTrue(webRes is BasoAiResult.Success)
    val bundle = (webRes as BasoAiResult.Success).data
    assertTrue(bundle.indexHtml.contains("<h1>BASO</h1>"))
    assertTrue(bundle.stylesCss.contains("#05090F"))
    assertTrue(bundle.appJs.contains("ready"))

    cannedResponse =
      """{"creativeDirection":"Minimalist Kurdish dark luxury","colorPaletteHex":["#05090F","#FFD400","#00F5D4"],"typographyAndLayout":"Vazirmatn bold headers","imageGenerationPrompt":"Minimalist dark neon emblem","deliverablesList":["Logo","Brand Guide"]}"""
    val designRes =
      service.runDesignAssistant(
        designBrief = "Coffee brand",
        mode = BasoDesignTaskMode.CONCEPT,
        appLanguage = AppLanguage.KURDISH_SORANI,
      )
    assertTrue(designRes is BasoAiResult.Success)
    assertEquals(3, (designRes as BasoAiResult.Success).data.colorPaletteHex.size)

    cannedResponse =
      """{"projectTitle":"ئەپی گەیاندنی خێرا","objectives":["ئامانجی یەکەم"],"milestones":["قۆناغی یەکەم"],"tasks":["دیزاینی ڕووکار","دروستکردنی API"],"estimatedEffort":"خەمڵاندن: ٣ هەفتە","priorities":["پێشینەی یەکەم"],"recommendedCategoryIndex":2}"""
    val planRes = service.generateProjectPlan("Delivery app", AppLanguage.KURDISH_SORANI)
    assertTrue(planRes is BasoAiResult.Success)
    assertEquals("ئەپی گەیاندنی خێرا", (planRes as BasoAiResult.Success).data.projectTitle)
  }

  @Test
  fun `verify AI Hub screen navigation 5 tool tabs and confirmed project plan saving`() {
    val controlledProvider =
      object : BasoAiProvider {
        override fun configurationMode(): BasoAiConfigMode = BasoAiConfigMode.DIRECT_GEMINI_REST

        override fun isConfigured(): Boolean = true

        override suspend fun generateText(
          model: String,
          systemPrompt: String,
          contents: List<GeminiContent>,
          responseMimeType: String?,
          responseSchema: JsonObject?,
          temperature: Float,
        ): BasoAiResult<String> {
          return BasoAiResult.Success(
            data =
              """{"projectTitle":"پڕۆژەی زیرەکی باسۆ","objectives":["ئامانجی ١"],"milestones":["قۆناغی ١"],"tasks":["ئەرکی ١","ئەرکی ٢"],"estimatedEffort":"خەمڵاندن: ٢ هەفتە","priorities":["گرنگ"],"recommendedCategoryIndex":3}""",
            modelUsed = model,
          )
        }
      }

    composeTestRule.setContent { BasoApp(aiService = BasoAiService(controlledProvider)) }
    composeTestRule.waitForIdle()

    composeTestRule
      .onNodeWithTag("baso_home_screen")
      .performScrollToNode(hasTestTag("home_open_ai_hub_card"))
    composeTestRule.onNodeWithTag("home_open_ai_hub_card").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_ai_hub_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ai_chat_tool_panel").assertExists()

    composeTestRule.onNodeWithTag("ai_tool_tab_code").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("ai_code_tool_panel").assertExists()

    composeTestRule.onNodeWithTag("ai_tool_tab_website").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("ai_website_tool_panel").assertExists()

    composeTestRule.onNodeWithTag("ai_tool_tab_design").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("ai_design_tool_panel").assertExists()

    composeTestRule.onNodeWithTag("ai_tool_tab_planning").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("ai_planning_tool_panel").assertExists()

    composeTestRule.onNodeWithTag("ai_planning_input_field").performTextInput("دروستکردنی ماڵپەڕ")
    composeTestRule.onNodeWithTag("ai_planning_submit_button").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("ai_planning_draft_card").assertExists()
    composeTestRule.onNodeWithTag("ai_planning_save_to_projects_button").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("baso_confirmation_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("baso_dialog_confirm_button").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("baso_projects_screen").assertIsDisplayed()
    composeTestRule.onNodeWithText("پڕۆژەی زیرەکی باسۆ").assertIsDisplayed()
    assertEquals(1, BasoLocalStore(context).loadCustomProjects().size)
  }

  @Test
  fun `verify Phase 4 App Builder path safety multi-file generation follow-up iteration and ZIP export`() = runTest {
    // 1. Verify safe path sanitization prevents path traversal and host package overwriting
    assertNull(BasoBuilderValidatorAndExporter.sanitizeAndValidateRelativePath("../secret.txt"))
    assertNull(BasoBuilderValidatorAndExporter.sanitizeAndValidateRelativePath("/etc/passwd"))
    assertNull(BasoBuilderValidatorAndExporter.sanitizeAndValidateRelativePath("app/../../build.gradle"))
    assertEquals(
      "src/components/Cart.js",
      BasoBuilderValidatorAndExporter.sanitizeAndValidateRelativePath("src\\components\\Cart.js"),
    )
    // Colliding com/example paths get rewritten safely to com/generated/basoapp
    assertEquals(
      "app/src/main/java/com/generated/basoapp/StoreActivity.kt",
      BasoBuilderValidatorAndExporter.sanitizeAndValidateRelativePath(
        "app/src/main/java/com/example/StoreActivity.kt",
      ),
    )

    // 2. Verify initial build + contextual follow-up modification + ZIP archive contents
    var modelPayload =
      """
      {
        "projectName": "Kurdish Online Store",
        "summary": "Generated multi-file Kurdish online store web app.",
        "architectureNotes": "Modular state and router",
        "externalSetupNotes": ["Configure payment gateway webhook URL for live checkout."],
        "files": [
          {"path": "index.html", "content": "<!DOCTYPE html><html><head><title>Store</title></head><body><div id='app'>Kurdish Store</div></body></html>", "language": "html"},
          {"path": "styles.css", "content": "body { background: #05090F; color: #F5F7FA; }", "language": "css"},
          {"path": "src/state.js", "content": "const storeState = { cart: [] };", "language": "javascript"},
          {"path": "src/app.js", "content": "function renderStore() { return true; }", "language": "javascript"}
        ]
      }
      """.trimIndent()

    val testProvider =
      object : BasoAiProvider {
        override fun configurationMode(): BasoAiConfigMode = BasoAiConfigMode.DIRECT_GEMINI_REST

        override fun isConfigured(): Boolean = true

        override suspend fun generateText(
          model: String,
          systemPrompt: String,
          contents: List<GeminiContent>,
          responseMimeType: String?,
          responseSchema: JsonObject?,
          temperature: Float,
        ): BasoAiResult<String> = BasoAiResult.Success(modelPayload, model)
      }

    val engine = BasoBuilderAiEngine(testProvider)
    val initialProject =
      BasoBuilderProject(
        id = "test_store_1",
        name = "New Store",
        projectType = BasoBuilderProjectType.WEB_APP,
      )

    val buildRes =
      engine.buildOrModifyProject(
        project = initialProject,
        userPrompt = "Create a professional Kurdish online store with product categories and cart.",
        appLanguage = AppLanguage.KURDISH_SORANI,
      )
    assertTrue(buildRes is BasoAiResult.Success)
    val outcome1 = (buildRes as BasoAiResult.Success).data
    assertEquals(4, outcome1.mergedFiles.size)
    assertEquals("Kurdish Online Store", outcome1.suggestedProjectName)

    val projectAfterInitial =
      initialProject.copy(
        name = outcome1.suggestedProjectName,
        status = BasoBuilderLifecycleStatus.FILES_GENERATED,
        files = outcome1.mergedFiles,
        externalSetupNotes = outcome1.externalSetupNotes,
      )

    // 3. Follow-up prompt modifies only src/app.js and adds src/search.js while preserving index.html, styles.css, and src/state.js
    modelPayload =
      """
      {
        "projectName": "Kurdish Online Store",
        "summary": "Added product search module and updated app.js.",
        "architectureNotes": "Added search filter",
        "externalSetupNotes": [],
        "files": [
          {"path": "src/app.js", "content": "function renderStore() { return searchProducts(); }", "language": "javascript"},
          {"path": "src/search.js", "content": "function searchProducts() { return []; }", "language": "javascript"}
        ]
      }
      """.trimIndent()

    val followUpRes =
      engine.buildOrModifyProject(
        project = projectAfterInitial,
        userPrompt = "Add a product search feature.",
        appLanguage = AppLanguage.KURDISH_SORANI,
      )
    assertTrue(followUpRes is BasoAiResult.Success)
    val outcome2 = (followUpRes as BasoAiResult.Success).data
    // 4 original files + 1 new file = 5 files total (index.html, styles.css, src/state.js preserved!)
    assertEquals(5, outcome2.mergedFiles.size)
    assertTrue(outcome2.mergedFiles.any { it.path == "index.html" })
    assertTrue(outcome2.mergedFiles.any { it.path == "src/state.js" })
    assertTrue(outcome2.mergedFiles.any { it.path == "src/search.js" })
    assertTrue(
      outcome2.mergedFiles.first { it.path == "src/app.js" }.content.contains("searchProducts()"),
    )

    // 4. Verify validation and real ZIP archive generation
    val finalProject = projectAfterInitial.copy(files = outcome2.mergedFiles)
    val validation =
      BasoBuilderValidatorAndExporter.validateProjectFiles(
        projectType = finalProject.projectType,
        files = finalProject.files,
      )
    assertTrue(validation.isValid)

    val zipBytes = BasoBuilderValidatorAndExporter.exportProjectToZipBytes(finalProject)
    assertTrue(zipBytes.isNotEmpty())
    val zipEntries = mutableListOf<String>()
    ZipInputStream(ByteArrayInputStream(zipBytes)).use { zis ->
      var entry = zis.nextEntry
      while (entry != null) {
        zipEntries.add(entry.name)
        entry = zis.nextEntry
      }
    }
    assertTrue(zipEntries.contains("index.html"))
    assertTrue(zipEntries.contains("src/search.js"))
    assertTrue(zipEntries.contains("README.md"))

    // 5. Verify persistence of builder projects in BasoLocalStore
    val store = BasoLocalStore(context)
    store.saveBuilderProjects(listOf(finalProject))
    store.saveActiveBuilderProjectId(finalProject.id)
    val reloaded = store.loadBuilderProjects()
    assertEquals(1, reloaded.size)
    assertEquals(5, reloaded.first().files.size)
    assertEquals(finalProject.id, store.loadActiveBuilderProjectId())
  }

  @Test
  fun `verify Phase 4 App Builder workspace UI navigation prompt build file tree and export`() {
    val controlledProvider =
      object : BasoAiProvider {
        override fun configurationMode(): BasoAiConfigMode = BasoAiConfigMode.DIRECT_GEMINI_REST

        override fun isConfigured(): Boolean = true

        override suspend fun generateText(
          model: String,
          systemPrompt: String,
          contents: List<GeminiContent>,
          responseMimeType: String?,
          responseSchema: JsonObject?,
          temperature: Float,
        ): BasoAiResult<String> {
          return BasoAiResult.Success(
            data =
              """
              {
                "projectName": "فرۆشگای کوردستان",
                "summary": "پڕۆژەی فرۆشگای سەرهێڵ دروستکرا",
                "architectureNotes": "HTML5 + CSS3 + JS",
                "externalSetupNotes": [],
                "files": [
                  {"path": "index.html", "content": "<!DOCTYPE html><html><body><h1>Kurdistan Shop</h1></body></html>", "language": "html"},
                  {"path": "styles.css", "content": "body { color: #FFD400; }", "language": "css"},
                  {"path": "src/app.js", "content": "function initShop() { return 1; }", "language": "javascript"}
                ]
              }
              """.trimIndent(),
            modelUsed = model,
          )
        }
      }

    composeTestRule.setContent {
      BasoApp(
        aiService = BasoAiService(controlledProvider),
        builderEngine = BasoBuilderAiEngine(controlledProvider),
      )
    }
    composeTestRule.waitForIdle()

    // Navigate from Home screen card to BASO AI Studio App Builder
    composeTestRule
      .onNodeWithTag("baso_home_screen")
      .performScrollToNode(hasTestTag("home_open_app_builder_card"))
    composeTestRule.onNodeWithTag("home_open_app_builder_card").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_app_builder_screen").assertIsDisplayed()

    // Enter build prompt and submit
    composeTestRule
      .onNodeWithTag("builder_prompt_input")
      .performTextInput("دروستکردنی فرۆشگای سەرهێڵی کوردی")
    composeTestRule.onNodeWithTag("builder_send_button").performClick()
    Thread.sleep(600)
    composeTestRule.waitForIdle()

    // Switch to Code & Files tab and verify real generated file tree and code viewer
    composeTestRule.onNodeWithTag("builder_tab_code").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("builder_code_panel").assertExists()
    composeTestRule.onNodeWithTag("builder_file_tree").assertExists()
    composeTestRule.onNodeWithTag("builder_file_item_index.html").assertExists()

    // Trigger project validation and ZIP export
    composeTestRule.onNodeWithTag("builder_validate_project_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("builder_export_zip_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("builder_last_export_label").assertIsDisplayed()
  }

  @Test
  fun `verify Phase 4_1 Firebase-free HTTPS REST AI architecture no client-embedded secrets and generated file redaction`() {
    // 1. Verify BuildConfig does NOT package GEMINI_API_KEY into the APK
    val buildConfigFieldNames = BuildConfig::class.java.declaredFields.map { it.name }
    assertFalse(
      "GEMINI_API_KEY must never be embedded in BuildConfig or the APK",
      buildConfigFieldNames.any { it.contains("GEMINI_API_KEY", ignoreCase = true) },
    )

    // 2. Verify default provider is unconfigured without Firebase and returns clear setup status
    val defaultProvider = GeminiRestAiProvider()
    assertFalse(defaultProvider.isConfigured())
    assertEquals(BasoAiConfigMode.UNCONFIGURED, defaultProvider.configurationMode())

    // 3. Verify configurable HTTPS Backend Proxy & custom model resolution via BasoLocalStore
    val store = BasoLocalStore(context)
    val customConfig =
      BasoAiEndpointConfig(
        endpointUrl = "https://api.baso-secure-proxy.example.com",
        defaultTextModel = "custom-kurdish-text-v1",
        defaultCodeModel = "custom-kurdish-coder-v1",
      )
    store.saveAiEndpointConfig(customConfig)
    val reloadedConfig = store.loadAiEndpointConfig()
    assertEquals("https://api.baso-secure-proxy.example.com", reloadedConfig.endpointUrl)

    val proxyProvider =
      GeminiRestAiProvider(
        endpointConfigProvider = { store.loadAiEndpointConfig() },
      )
    assertTrue(proxyProvider.isConfigured())
    assertEquals(BasoAiConfigMode.BACKEND_PROXY, proxyProvider.configurationMode())
    assertEquals("https://api.baso-secure-proxy.example.com/", proxyProvider.resolveBaseUrl())
    assertEquals(
      "custom-kurdish-text-v1",
      proxyProvider.resolveModelName(GeminiRestAiProvider.MODEL_BASIC_TEXT),
    )
    assertEquals(
      "custom-kurdish-coder-v1",
      proxyProvider.resolveModelName(GeminiRestAiProvider.MODEL_COMPLEX_CODE),
    )

    // 4. Verify generated web/app files are scanned and redacted if they ever contain embedded private API keys
    val syntheticGoogleKey = "AIza" + "SyTestPrivateKey1234567890abcdefXYZ"
    val unsafeJsContent = "const GEMINI_API_KEY = \"$syntheticGoogleKey\";\nfetch('https://example.com');"
    assertTrue(BasoBuilderValidatorAndExporter.containsEmbeddedPrivateSecret(unsafeJsContent))

    val redactedJs = BasoBuilderValidatorAndExporter.redactEmbeddedSecrets(unsafeJsContent)
    assertFalse(redactedJs.contains(syntheticGoogleKey))
    assertTrue(redactedJs.contains("REDACTED"))
  }

  @Test
  fun `verify Keystore encryption and persistent AI routing settings`() {
    // 1. Verify BasoKeyCipher encryption and decryption roundtrip
    val testKey = "AIzaSyTestApiKeyForBasoFreelanceStudio123"
    val encrypted = BasoKeyCipher.encrypt(testKey)
    assertFalse("Encrypted token must not be plaintext", encrypted == testKey)
    assertTrue("Encrypted token must be non-empty", encrypted.isNotEmpty())
    val decrypted = BasoKeyCipher.decrypt(encrypted)
    assertEquals(testKey, decrypted)

    // 2. Verify key masking helper
    assertEquals("AIza••••o123", BasoKeyCipher.maskApiKey(testKey))
    assertEquals("", BasoKeyCipher.maskApiKey(""))

    // 3. Verify BasoLocalStore stores encrypted settings and reloads decrypted values
    val store = BasoLocalStore(context)
    val originalSettings = BasoAiRoutingSettings(
      primaryProvider = BasoProviderType.OPENAI,
      automaticFailoverEnabled = true,
      timeoutSeconds = 60,
      maxRetries = 2,
      geminiConfig = BasoProviderConfig(
        providerType = BasoProviderType.GEMINI,
        apiKey = "AIzaSyGeminiSecretKey987654321",
        model = "gemini-2.0-flash",
      ),
      openAiConfig = BasoProviderConfig(
        providerType = BasoProviderType.OPENAI,
        apiKey = "sk-proj-OpenAiSecretKey123456789",
        model = "gpt-4o",
      ),
    )
    store.saveAiRoutingSettings(originalSettings)

    // Verify raw SharedPreferences does NOT contain plaintext secrets
    val sharedPrefs = context.getSharedPreferences(BasoLocalStore.PREFS_NAME, Context.MODE_PRIVATE)
    val rawStored = sharedPrefs.getString("ai_routing_settings_json", "").orEmpty()
    assertFalse("Raw SharedPreferences must never contain plaintext Gemini key", rawStored.contains("AIzaSyGeminiSecretKey987654321"))
    assertFalse("Raw SharedPreferences must never contain plaintext OpenAI key", rawStored.contains("sk-proj-OpenAiSecretKey123456789"))

    // Verify reloading from store decrypts properly
    val reloaded = store.loadAiRoutingSettings()
    assertEquals(BasoProviderType.OPENAI, reloaded.primaryProvider)
    assertTrue(reloaded.automaticFailoverEnabled)
    assertEquals(60, reloaded.timeoutSeconds)
    assertEquals(2, reloaded.maxRetries)
    assertEquals("AIzaSyGeminiSecretKey987654321", reloaded.geminiConfig.apiKey)
    assertEquals("sk-proj-OpenAiSecretKey123456789", reloaded.openAiConfig.apiKey)
  }

  @Test
  fun `verify BasoAiRouter routing primary success failover on transient error and fatal rejection`() = runTest {
    // Mock providers for precise deterministic unit testing of the router
    val mockGemini = MockTestProvider(
      name = "Google Gemini",
      providerType = BasoProviderType.GEMINI,
      configured = true,
    )
    val mockOpenAi = MockTestProvider(
      name = "OpenAI",
      providerType = BasoProviderType.OPENAI,
      configured = true,
    )

    var settings = BasoAiRoutingSettings(
      primaryProvider = BasoProviderType.GEMINI,
      automaticFailoverEnabled = true,
      maxRetries = 1,
    )

    val router = BasoAiRouter(
      settingsProvider = { settings },
      geminiProvider = mockGemini,
      openAiProvider = mockOpenAi,
    )

    val testContents = listOf(
      GeminiContent(role = "user", parts = listOf(GeminiPart(text = "Hello router")))
    )

    // 1. Scenario: Primary (Gemini) succeeds
    mockGemini.resultToReturn = BasoAiResult.Success("Gemini response", "gemini-1.5-flash", "Google Gemini")
    mockOpenAi.resultToReturn = BasoAiResult.Success("OpenAI response", "gpt-4o-mini", "OpenAI")

    val res1 = router.generateText("test-model", "sys", testContents)
    assertTrue(res1 is BasoAiResult.Success)
    val success1 = res1 as BasoAiResult.Success
    assertEquals("Google Gemini", success1.providerUsed)
    assertFalse(success1.isFailover)
    assertEquals(1, mockGemini.callCount)
    assertEquals(0, mockOpenAi.callCount)

    // 2. Scenario: Primary fails with RATE_LIMITED (HTTP 429) -> Automatic Failover to OpenAI succeeds!
    mockGemini.callCount = 0
    mockOpenAi.callCount = 0
    mockGemini.resultToReturn = BasoAiResult.Failure(
      errorType = BasoAiErrorType.RATE_LIMITED,
      safeMessage = "Rate limit reached on Gemini",
      providerAttempted = "Google Gemini",
    )
    mockOpenAi.resultToReturn = BasoAiResult.Success("OpenAI fallback response", "gpt-4o-mini", "OpenAI")

    val res2 = router.generateText("test-model", "sys", testContents)
    assertTrue(res2 is BasoAiResult.Success)
    val success2 = res2 as BasoAiResult.Success
    assertEquals("OpenAI", success2.providerUsed)
    assertTrue("Should be marked as failover", success2.isFailover)
    assertEquals("OpenAI fallback response", success2.data)
    assertTrue("Primary should have been attempted", mockGemini.callCount >= 1)
    assertEquals(1, mockOpenAi.callCount)

    // 3. Scenario: Both providers fail -> Returns comprehensive failure notice
    mockGemini.callCount = 0
    mockOpenAi.callCount = 0
    mockGemini.resultToReturn = BasoAiResult.Failure(
      errorType = BasoAiErrorType.TIMEOUT,
      safeMessage = "Gemini timed out",
    )
    mockOpenAi.resultToReturn = BasoAiResult.Failure(
      errorType = BasoAiErrorType.SERVICE_UNAVAILABLE,
      safeMessage = "OpenAI 503 outage",
    )

    val res3 = router.generateText("test-model", "sys", testContents)
    assertTrue(res3 is BasoAiResult.Failure)
    val failure3 = res3 as BasoAiResult.Failure
    assertTrue(failure3.failoverAttempted)
    assertTrue(failure3.providerAttempted.contains("Google Gemini"))
    assertTrue(failure3.providerAttempted.contains("OpenAI"))

    // 4. Scenario: Primary fails with INVALID_REQUEST (HTTP 400) -> Must NOT blindly failover
    mockGemini.callCount = 0
    mockOpenAi.callCount = 0
    mockGemini.resultToReturn = BasoAiResult.Failure(
      errorType = BasoAiErrorType.INVALID_REQUEST,
      safeMessage = "Invalid prompt formatting",
    )

    val res4 = router.generateText("test-model", "sys", testContents)
    assertTrue(res4 is BasoAiResult.Failure)
    val failure4 = res4 as BasoAiResult.Failure
    assertFalse("Must NOT attempt failover on fatal INVALID_REQUEST", failure4.failoverAttempted)
    assertEquals(0, mockOpenAi.callCount)

    // 5. Scenario: Failover disabled by user setting -> fails directly on primary failure
    mockGemini.callCount = 0
    mockOpenAi.callCount = 0
    settings = settings.copy(automaticFailoverEnabled = false)
    mockGemini.resultToReturn = BasoAiResult.Failure(
      errorType = BasoAiErrorType.RATE_LIMITED,
      safeMessage = "Quota reached",
    )

    val res5 = router.generateText("test-model", "sys", testContents)
    assertTrue(res5 is BasoAiResult.Failure)
    assertFalse((res5 as BasoAiResult.Failure).failoverAttempted)
    assertEquals(0, mockOpenAi.callCount)
  }

  @Test
  fun `verify navigation to AI Settings screen and provider tabs display`() {
    composeTestRule.setContent { BasoApp() }
    composeTestRule.waitForIdle()

    // 1. Navigate to Settings
    composeTestRule.onNodeWithTag("baso_nav_settings").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_settings_screen").assertIsDisplayed()

    // 2. Open AI Provider Settings from Settings row
    composeTestRule
      .onNodeWithTag("baso_settings_screen")
      .performScrollToNode(hasTestTag("settings_open_ai_settings_row"))
    composeTestRule.onNodeWithTag("settings_open_ai_settings_row").performClick()
    composeTestRule.waitForIdle()

    // 3. Verify AI Provider Settings screen is displayed
    composeTestRule.onNodeWithTag("baso_ai_settings_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tab_gemini").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tab_openai").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ai_settings_api_key_input").assertExists()

    // Switch to OpenAI tab
    composeTestRule.onNodeWithTag("tab_openai").performClick()
    // Back to Settings
    composeTestRule.onNodeWithTag("ai_settings_back_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("baso_settings_screen").assertIsDisplayed()
  }

  @Test
  fun `verify Gemini and OpenAI provider-specific request and response parsing missing keys rate limits and zero Firebase`() = runTest {
    val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true; isLenient = true }

    // 1. Verify Gemini provider-specific response parsing
    val rawGeminiJson = """
      {
        "candidates": [
          {
            "content": {
              "parts": [
                {"text": "سڵاو لە BASO AI! ئەم وەڵامە لە Gemini یەوە هاتووە."}
              ],
              "role": "model"
            },
            "finishReason": "STOP"
          }
        ]
      }
    """.trimIndent()
    val geminiParsed = json.decodeFromString(GeminiGenerateContentResponse.serializer(), rawGeminiJson)
    val geminiText = geminiParsed.candidates?.firstOrNull()?.content?.parts?.mapNotNull { it.text }?.joinToString("\n")
    assertEquals("سڵاو لە BASO AI! ئەم وەڵامە لە Gemini یەوە هاتووە.", geminiText)

    // 2. Verify OpenAI provider-specific response parsing with usage metadata
    val rawOpenAiJson = """
      {
        "id": "chatcmpl-test12345",
        "object": "chat.completion",
        "created": 1700000000,
        "model": "gpt-4o-mini",
        "choices": [
          {
            "index": 0,
            "message": {
              "role": "assistant",
              "content": "Hello from OpenAI provider adapter!"
            },
            "finish_reason": "stop"
          }
        ],
        "usage": {
          "prompt_tokens": 12,
          "completion_tokens": 8,
          "total_tokens": 20
        }
      }
    """.trimIndent()
    val openAiParsed = json.decodeFromString(OpenAiChatResponse.serializer(), rawOpenAiJson)
    val openAiText = openAiParsed.choices?.firstOrNull()?.message?.content
    assertEquals("Hello from OpenAI provider adapter!", openAiText)
    assertEquals(20, openAiParsed.usage?.totalTokens)

    // 3. Verify missing API key handling returns MISSING_CREDENTIALS error
    val unconfiguredGemini = GeminiAiProvider(configProvider = { BasoProviderConfig(BasoProviderType.GEMINI, apiKey = "") })
    assertFalse(unconfiguredGemini.isConfigured())
    val geminiFail = unconfiguredGemini.generateText("gemini-1.5-flash", "sys", listOf(GeminiContent(parts = listOf(GeminiPart(text = "hi")))))
    assertTrue(geminiFail is BasoAiResult.Failure)
    assertEquals(BasoAiErrorType.MISSING_CREDENTIALS, (geminiFail as BasoAiResult.Failure).errorType)

    val unconfiguredOpenAi = OpenAiProvider(configProvider = { BasoProviderConfig(BasoProviderType.OPENAI, apiKey = "") })
    assertFalse(unconfiguredOpenAi.isConfigured())
    val openAiFail = unconfiguredOpenAi.generateText("gpt-4o-mini", "sys", listOf(GeminiContent(parts = listOf(GeminiPart(text = "hi")))))
    assertTrue(openAiFail is BasoAiResult.Failure)
    assertEquals(BasoAiErrorType.MISSING_CREDENTIALS, (openAiFail as BasoAiResult.Failure).errorType)

    // 4. Verify BasoAiSanitizer scrubs credentials, bearer tokens, and keys from error messages
    val rawSecretMsg = "Error at https://api.openai.com/v1/chat/completions?key=AIzaSySecret1234567890123456789012345 using Bearer sk-proj-1234567890abcdefghijklmnopqrstuvwxyz"
    val sanitized = BasoAiSanitizer.sanitizeSecretFromMessage(rawSecretMsg, "sk-proj-1234567890abcdefghijklmnopqrstuvwxyz", "AIzaSySecret1234567890123456789012345")
    assertFalse("Must not leak Gemini key", sanitized.contains("AIzaSySecret"))
    assertFalse("Must not leak OpenAI sk- key", sanitized.contains("sk-proj-"))
    assertTrue("Must redact authorization headers", sanitized.contains("Bearer REDACTED") || sanitized.contains("REDACTED"))

    // 5. Verify zero Firebase dependencies in runtime classpath
    val hasFirebaseApp = runCatching { Class.forName("com.google.firebase.FirebaseApp") }.isSuccess
    val hasFirebaseAi = runCatching { Class.forName("com.google.firebase.ai.GenerativeBackend") }.isSuccess
    assertFalse("FirebaseApp must not exist on the classpath", hasFirebaseApp)
    assertFalse("GenerativeBackend must not exist on the classpath", hasFirebaseAi)
  }

  private class MockTestProvider(
    override val name: String,
    override val providerType: BasoProviderType,
    var configured: Boolean = true,
    var resultToReturn: BasoAiResult<String> = BasoAiResult.Success("Mock answer", "test-model", name),
    var callCount: Int = 0,
  ) : BasoAiProvider {
    override fun configurationMode(): BasoAiConfigMode =
      if (configured) BasoAiConfigMode.DIRECT_GEMINI_REST else BasoAiConfigMode.UNCONFIGURED
    override fun isConfigured(): Boolean = configured
    override suspend fun testConnection(): BasoAiResult<String> = resultToReturn
    override suspend fun generateText(
      model: String,
      systemPrompt: String,
      contents: List<GeminiContent>,
      responseMimeType: String?,
      responseSchema: JsonObject?,
      temperature: Float,
    ): BasoAiResult<String> {
      callCount++
      return resultToReturn
    }
  }
}
