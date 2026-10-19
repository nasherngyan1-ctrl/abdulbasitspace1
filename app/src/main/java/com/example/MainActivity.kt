package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.ai.BasoAiChatTurn
import com.example.ai.BasoAiService
import com.example.builder.BasoBuilderAiEngine
import com.example.builder.BasoBuilderProject
import com.example.builder.BasoBuilderProjectType
import com.example.data.BasoLocalStore
import com.example.ui.components.BasoBottomNavigationBar
import com.example.ui.components.BasoBrandEmblem
import com.example.ui.components.BasoCard
import com.example.ui.components.BasoConfirmationDialog
import com.example.ui.components.BasoDestination
import com.example.ui.components.BasoFilterChipsRow
import com.example.ui.components.BasoInputField
import com.example.ui.components.BasoPrimaryButton
import com.example.ui.components.BasoProjectProgressCard
import com.example.ui.components.BasoSearchBox
import com.example.ui.components.BasoSecondaryButton
import com.example.ui.components.BasoSideNavigationRail
import com.example.ui.components.BasoStateCard
import com.example.ui.components.BasoSystemState
import com.example.ui.components.BasoTopBar
import com.example.ui.screens.BasoAiHubScreen
import com.example.ui.screens.BasoAppBuilderScreen
import com.example.ui.screens.BasoChatMessage
import com.example.ui.screens.BasoClientsScreen
import com.example.ui.screens.BasoNewProjectScreen
import com.example.ui.screens.BasoPortfolioScreen
import com.example.ui.screens.BasoProfileScreen
import com.example.ui.screens.BasoReviewChatScreen
import com.example.ui.screens.BasoServicesScreen
import com.example.ui.screens.BasoSplashScreen
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.BasoBorder
import com.example.ui.theme.BasoCyanPill
import com.example.ui.theme.BasoError
import com.example.ui.theme.BasoErrorSubtleBg
import com.example.ui.theme.BasoIconBlue
import com.example.ui.theme.BasoIconIndigo
import com.example.ui.theme.BasoIconPurple
import com.example.ui.theme.BasoNeonTurquoise
import com.example.ui.theme.BasoNeonYellow
import com.example.ui.theme.BasoPrimaryBackground
import com.example.ui.theme.BasoPrimaryText
import com.example.ui.theme.BasoSecondaryBackground
import com.example.ui.theme.BasoSecondaryText
import com.example.ui.theme.BasoStrings
import com.example.ui.theme.BasoSuccess
import com.example.ui.theme.BasoSuccessSubtleBg
import com.example.ui.theme.BasoSurfaceCard
import com.example.ui.theme.BasoTurquoiseSubtleGlow
import com.example.ui.theme.BasoYellowSubtleGlow
import com.example.ai.BasoAiRouter
import com.example.ai.GeminiRestAiProvider
import com.example.ui.screens.BasoAiSettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.stringsForLanguage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { BasoApp() }
  }
}

data class BasoProjectRecord(
  val id: String,
  val title: String,
  val subtitle: String,
  val statusType: String, // "IN_PROGRESS", "COMPLETED", "PUBLISHED"
  val progressPercent: Int,
  val categoryIndex: Int, // 1=Design, 2=App, 3=Website, 4=Video
  val icon: ImageVector,
  val iconColor: Color,
)

@Composable
fun BasoApp(
  aiService: BasoAiService? = null,
  builderEngine: BasoBuilderAiEngine? = null,
) {
  val context = LocalContext.current
  val localStore = remember(context) { BasoLocalStore(context) }
  val aiRouter =
    remember(localStore) {
      BasoAiRouter(
        settingsProvider = { localStore.loadAiRoutingSettings() },
      )
    }
  val resolvedAiService = remember(aiService, aiRouter) { aiService ?: BasoAiService(aiRouter) }
  val resolvedBuilderEngine =
    remember(builderEngine, aiRouter) { builderEngine ?: BasoBuilderAiEngine(aiRouter) }

  var currentLanguage by rememberSaveable { mutableStateOf(localStore.loadLanguage()) }
  var currentDestination by rememberSaveable { mutableStateOf(BasoDestination.HOME) }
  var searchQuery by rememberSaveable { mutableStateOf("") }
  var selectedFilterIndex by rememberSaveable { mutableIntStateOf(0) }
  var selectedItemId by rememberSaveable { mutableStateOf("proj_1") }
  var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
  var highContrastBorders by rememberSaveable { mutableStateOf(localStore.loadHighContrast()) }
  var spaciousDensity by rememberSaveable { mutableStateOf(localStore.loadSpaciousDensity()) }
  var activeClientChatName by rememberSaveable { mutableStateOf("سەردار محەمەد") }

  val strings = remember(currentLanguage) { stringsForLanguage(currentLanguage) }
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  // Persisted user-added projects, client chat messages, AI chat turns, and App Builder projects
  val customProjects =
    remember(localStore) {
      mutableStateListOf<BasoProjectRecord>().apply { addAll(localStore.loadCustomProjects()) }
    }
  val chatMessages =
    remember(localStore) {
      mutableStateListOf<BasoChatMessage>().apply { addAll(localStore.loadChatMessages()) }
    }
  val aiChatTurns =
    remember(localStore) {
      mutableStateListOf<BasoAiChatTurn>().apply { addAll(localStore.loadAiChatHistory()) }
    }
  val builderProjects =
    remember(localStore) {
      val stored = localStore.loadBuilderProjects()
      val initialList =
        if (stored.isNotEmpty()) {
          stored
        } else {
          listOf(
            BasoBuilderProject(
              id = "builder_proj_1",
              name = "فرۆشگای سەرهێڵی کوردی",
              projectType = BasoBuilderProjectType.WEB_APP,
            ),
          )
        }
      mutableStateListOf<BasoBuilderProject>().apply { addAll(initialList) }
    }
  var activeBuilderProjectId by rememberSaveable {
    mutableStateOf(localStore.loadActiveBuilderProjectId() ?: builderProjects.first().id)
  }

  val updateLanguage: (AppLanguage) -> Unit = { newLang ->
    currentLanguage = newLang
    localStore.saveLanguage(newLang)
  }

  val updateHighContrast: (Boolean) -> Unit = { enabled ->
    highContrastBorders = enabled
    localStore.saveHighContrast(enabled)
  }

  val updateSpaciousDensity: (Boolean) -> Unit = { enabled ->
    spaciousDensity = enabled
    localStore.saveSpaciousDensity(enabled)
  }

  if (currentDestination != BasoDestination.HOME) {
    BackHandler { currentDestination = BasoDestination.HOME }
  }

  val cycleLanguage: () -> Unit = {
    val nextLang =
      when (currentLanguage) {
        AppLanguage.KURDISH_SORANI -> AppLanguage.ARABIC
        AppLanguage.ARABIC -> AppLanguage.ENGLISH
        AppLanguage.ENGLISH -> AppLanguage.KURDISH_SORANI
      }
    updateLanguage(nextLang)
  }

  MyApplicationTheme(layoutDirection = currentLanguage.layoutDirection) {
    BoxWithConstraints(
      modifier =
        Modifier
          .fillMaxSize()
          .background(BasoPrimaryBackground),
    ) {
      val useSideRail = maxWidth >= 640.dp

      if (currentDestination == BasoDestination.SPLASH) {
        BasoSplashScreen(
          strings = strings,
          onStartClick = { currentDestination = BasoDestination.HOME },
        )
      } else {
        Scaffold(
          containerColor = BasoPrimaryBackground,
          contentColor = BasoPrimaryText,
          contentWindowInsets = WindowInsets(0, 0, 0, 0),
          topBar = {
            BasoTopBar(
              strings = strings,
              currentLanguage = currentLanguage,
              onLanguageCycle = cycleLanguage,
              onAvatarClick = { currentDestination = BasoDestination.PROFILE },
              onLogoClick = { currentDestination = BasoDestination.SPLASH },
              onMenuAction = { action ->
                when (action) {
                  "RESET" -> {
                    localStore.resetSettingsToDefault()
                    currentLanguage = AppLanguage.KURDISH_SORANI
                    currentDestination = BasoDestination.HOME
                    searchQuery = ""
                    selectedFilterIndex = 0
                    highContrastBorders = false
                    spaciousDensity = true
                    coroutineScope.launch {
                      snackbarHostState.showSnackbar(
                        stringsForLanguage(AppLanguage.KURDISH_SORANI).resetCompletedFeedback,
                      )
                    }
                  }
                  "SERVICES" -> currentDestination = BasoDestination.SERVICES
                  "COMPONENTS" -> currentDestination = BasoDestination.COMPONENTS
                  "AI_HUB" -> currentDestination = BasoDestination.AI_HUB
                  "APP_BUILDER" -> currentDestination = BasoDestination.APP_BUILDER
                  "SPLASH" -> currentDestination = BasoDestination.SPLASH
                  "REVIEW_CHAT" -> currentDestination = BasoDestination.REVIEW_CHAT
                  "NOTIFICATIONS" -> currentDestination = BasoDestination.REVIEW_CHAT
                  else -> coroutineScope.launch { snackbarHostState.showSnackbar(action) }
                }
              },
            )
          },
          bottomBar = {
            if (!useSideRail) {
              BasoBottomNavigationBar(
                currentDestination = currentDestination,
                onNavigate = { currentDestination = it },
                strings = strings,
              )
            }
          },
          snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
              Snackbar(
                containerColor = BasoSurfaceCard,
                contentColor = BasoPrimaryText,
                actionContentColor = BasoNeonTurquoise,
                shape = RoundedCornerShape(14.dp),
                modifier =
                  Modifier.border(
                    1.dp,
                    BasoNeonTurquoise.copy(alpha = 0.5f),
                    RoundedCornerShape(14.dp),
                  ),
              ) {
                Text(text = data.visuals.message, style = MaterialTheme.typography.bodyMedium)
              }
            }
          },
        ) { innerPadding ->
          Row(
            modifier =
              Modifier
                .fillMaxSize()
                .padding(innerPadding),
          ) {
            if (useSideRail) {
              BasoSideNavigationRail(
                currentDestination = currentDestination,
                onNavigate = { currentDestination = it },
                strings = strings,
              )
            }

            Box(
              contentAlignment = Alignment.TopCenter,
              modifier = Modifier.fillMaxSize(),
            ) {
              Crossfade(
                targetState = currentDestination,
                label = "baso_screen_transition",
                modifier = Modifier.widthIn(max = 720.dp).fillMaxSize(),
              ) { destination ->
                when (destination) {
                  BasoDestination.HOME ->
                    BasoHomeScreen(
                      strings = strings,
                      projectsCount = 5 + customProjects.size,
                      highContrastBorders = highContrastBorders,
                      spaciousDensity = spaciousDensity,
                      onViewProjects = { currentDestination = BasoDestination.PROJECTS },
                      onNewProject = { currentDestination = BasoDestination.NEW_PROJECT },
                      onOpenClients = { currentDestination = BasoDestination.CLIENTS },
                      onOpenDesignPortfolio = { currentDestination = BasoDestination.PORTFOLIO },
                      onOpenServices = { currentDestination = BasoDestination.SERVICES },
                      onOpenActiveProjectReview = { currentDestination = BasoDestination.REVIEW_CHAT },
                      onOpenAiHub = { currentDestination = BasoDestination.AI_HUB },
                      onOpenAppBuilder = { currentDestination = BasoDestination.APP_BUILDER },
                    )
                  BasoDestination.PROJECTS ->
                    BasoProjectsScreen(
                      strings = strings,
                      customProjects = customProjects,
                      searchQuery = searchQuery,
                      onSearchQueryChange = { searchQuery = it },
                      selectedFilterIndex = selectedFilterIndex,
                      onFilterSelect = { selectedFilterIndex = it },
                      selectedItemId = selectedItemId,
                      onSelectItem = { id ->
                        selectedItemId = id
                        if (id == "proj_5") {
                          currentDestination = BasoDestination.AI_HUB
                        } else {
                          currentDestination = BasoDestination.REVIEW_CHAT
                        }
                      },
                      highContrastBorders = highContrastBorders,
                      spaciousDensity = spaciousDensity,
                    )
                  BasoDestination.CLIENTS ->
                    BasoClientsScreen(
                      strings = strings,
                      highContrastBorders = highContrastBorders,
                      onClientClick = { client ->
                        activeClientChatName = client.name
                        currentDestination = BasoDestination.REVIEW_CHAT
                      },
                    )
                  BasoDestination.PORTFOLIO ->
                    BasoPortfolioScreen(
                      strings = strings,
                      highContrastBorders = highContrastBorders,
                      onSeeAllProjects = { currentDestination = BasoDestination.PROJECTS },
                    )
                  BasoDestination.SERVICES ->
                    BasoServicesScreen(
                      strings = strings,
                      highContrastBorders = highContrastBorders,
                      onSelectService = { currentDestination = BasoDestination.NEW_PROJECT },
                      onContactClick = { currentDestination = BasoDestination.REVIEW_CHAT },
                    )
                  BasoDestination.NEW_PROJECT ->
                    BasoNewProjectScreen(
                      strings = strings,
                      onBack = { currentDestination = BasoDestination.HOME },
                      onCreateProject = { title, mappedCategoryIdx, subtitleText ->
                        val (catIcon, catColor) = BasoLocalStore.categoryVisualsForIndex(mappedCategoryIdx)
                        val newProj =
                          BasoProjectRecord(
                            id = "custom_${System.currentTimeMillis()}_${customProjects.size + 1}",
                            title = title,
                            subtitle = subtitleText,
                            statusType = "IN_PROGRESS",
                            progressPercent = 15,
                            categoryIndex = mappedCategoryIdx,
                            icon = catIcon,
                            iconColor = catColor,
                          )
                        customProjects.add(0, newProj)
                        localStore.saveCustomProjects(customProjects)
                        selectedFilterIndex = 0
                        currentDestination = BasoDestination.PROJECTS
                        coroutineScope.launch {
                          snackbarHostState.showSnackbar(strings.actionConfirmedFeedback)
                        }
                      },
                    )
                  BasoDestination.REVIEW_CHAT ->
                    BasoReviewChatScreen(
                      strings = strings,
                      clientName = activeClientChatName,
                      externalMessages = chatMessages,
                      onSendMessage = { newText ->
                        chatMessages.add(
                          BasoChatMessage(
                            id = "m_${System.currentTimeMillis()}_${chatMessages.size + 1}",
                            text = newText,
                            time = "10:28",
                            isOutgoing = true,
                          ),
                        )
                        localStore.saveChatMessages(chatMessages)
                      },
                      onBack = { currentDestination = BasoDestination.HOME },
                    )
                  BasoDestination.PROFILE ->
                    BasoProfileScreen(
                      strings = strings,
                      currentLanguage = currentLanguage,
                      projectsCount = 5 + customProjects.size,
                      clientsCount = 3,
                      onLanguageCycle = cycleLanguage,
                      onOpenSettings = { currentDestination = BasoDestination.SETTINGS },
                      onOpenHelp = { currentDestination = BasoDestination.SERVICES },
                      onOpenAbout = { currentDestination = BasoDestination.SPLASH },
                    )
                  BasoDestination.COMPONENTS ->
                    BasoComponentsScreen(
                      strings = strings,
                      highContrastBorders = highContrastBorders,
                      spaciousDensity = spaciousDensity,
                      onOpenDialog = { showConfirmDialog = true },
                      onFeedbackMessage = { msg ->
                        coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                      },
                    )
                  BasoDestination.AI_HUB ->
                    BasoAiHubScreen(
                      aiService = resolvedAiService,
                      currentLanguage = currentLanguage,
                      persistedChatTurns = aiChatTurns,
                      onUpdateChatTurns = { updated ->
                        aiChatTurns.clear()
                        aiChatTurns.addAll(updated)
                        localStore.saveAiChatHistory(aiChatTurns)
                      },
                      onClearChatTurns = {
                        aiChatTurns.clear()
                        localStore.clearAiChatHistory()
                      },
                      onSavePlannedProject = { title, mappedCategoryIdx, subtitleText ->
                        val (catIcon, catColor) = BasoLocalStore.categoryVisualsForIndex(mappedCategoryIdx)
                        val newProj =
                          BasoProjectRecord(
                            id = "custom_ai_${System.currentTimeMillis()}_${customProjects.size + 1}",
                            title = title,
                            subtitle = subtitleText,
                            statusType = "IN_PROGRESS",
                            progressPercent = 15,
                            categoryIndex = mappedCategoryIdx,
                            icon = catIcon,
                            iconColor = catColor,
                          )
                        customProjects.add(0, newProj)
                        localStore.saveCustomProjects(customProjects)
                        selectedFilterIndex = 0
                        currentDestination = BasoDestination.PROJECTS
                        coroutineScope.launch {
                          snackbarHostState.showSnackbar(strings.actionConfirmedFeedback)
                        }
                      },
                      onShowFeedback = { msg ->
                        coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                      },
                      onBack = { currentDestination = BasoDestination.HOME },
                      onOpenAiSettings = { currentDestination = BasoDestination.AI_SETTINGS },
                    )
                  BasoDestination.APP_BUILDER ->
                    BasoAppBuilderScreen(
                      builderEngine = resolvedBuilderEngine,
                      currentLanguage = currentLanguage,
                      projects = builderProjects,
                      activeProjectId = activeBuilderProjectId,
                      onSelectProject = { id ->
                        activeBuilderProjectId = id
                        localStore.saveActiveBuilderProjectId(id)
                      },
                      onUpdateProject = { updated ->
                        val idx = builderProjects.indexOfFirst { it.id == updated.id }
                        if (idx >= 0) {
                          builderProjects[idx] = updated
                        } else {
                          builderProjects.add(0, updated)
                        }
                        localStore.saveBuilderProjects(builderProjects)
                      },
                      onCreateNewProject = { name, type ->
                        val newProj =
                          BasoBuilderProject(
                            id = "builder_${System.currentTimeMillis()}_${builderProjects.size + 1}",
                            name = name,
                            projectType = type,
                          )
                        builderProjects.add(0, newProj)
                        activeBuilderProjectId = newProj.id
                        localStore.saveBuilderProjects(builderProjects)
                        localStore.saveActiveBuilderProjectId(newProj.id)
                      },
                      onDeleteProject = { deleteId ->
                        builderProjects.removeAll { it.id == deleteId }
                        if (builderProjects.isEmpty()) {
                          val fallback =
                            BasoBuilderProject(
                              id = "builder_${System.currentTimeMillis()}",
                              name = "پڕۆژەی نوێی BASO",
                              projectType = BasoBuilderProjectType.WEB_APP,
                            )
                          builderProjects.add(fallback)
                        }
                        activeBuilderProjectId = builderProjects.first().id
                        localStore.saveBuilderProjects(builderProjects)
                        localStore.saveActiveBuilderProjectId(activeBuilderProjectId)
                      },
                      onOpenSettings = { currentDestination = BasoDestination.AI_SETTINGS },
                      onShowFeedback = { msg ->
                        coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                      },
                      onBack = { currentDestination = BasoDestination.HOME },
                    )
                  BasoDestination.AI_SETTINGS ->
                    BasoAiSettingsScreen(
                      localStore = localStore,
                      aiRouter = aiRouter,
                      currentLanguage = currentLanguage,
                      highContrastBorders = highContrastBorders,
                      spaciousDensity = spaciousDensity,
                      onBack = { currentDestination = BasoDestination.SETTINGS },
                      onShowFeedback = { msg ->
                        coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                      },
                    )
                  BasoDestination.SETTINGS, BasoDestination.SPLASH ->
                    BasoSettingsScreen(
                      strings = strings,
                      currentLanguage = currentLanguage,
                      onSelectLanguage = updateLanguage,
                      highContrastBorders = highContrastBorders,
                      onToggleHighContrast = updateHighContrast,
                      spaciousDensity = spaciousDensity,
                      onToggleSpaciousDensity = updateSpaciousDensity,
                      onOpenProfile = { currentDestination = BasoDestination.PROFILE },
                      onOpenComponents = { currentDestination = BasoDestination.COMPONENTS },
                      onOpenServices = { currentDestination = BasoDestination.SERVICES },
                      onOpenAiHub = { currentDestination = BasoDestination.AI_HUB },
                      onOpenAppBuilder = { currentDestination = BasoDestination.APP_BUILDER },
                      onOpenAiSettings = { currentDestination = BasoDestination.AI_SETTINGS },
                      onOpenSplash = { currentDestination = BasoDestination.SPLASH },
                      onSignOutConfirm = { showConfirmDialog = true },
                    )
                }
              }
            }
          }
        }
      }

      if (showConfirmDialog) {
        BasoConfirmationDialog(
          title = strings.dialogTitle,
          message = strings.dialogMessage,
          confirmLabel = strings.dialogConfirm,
          cancelLabel = strings.dialogCancel,
          onConfirm = {
            showConfirmDialog = false
            coroutineScope.launch {
              snackbarHostState.showSnackbar(strings.actionConfirmedFeedback)
            }
          },
          onDismiss = { showConfirmDialog = false },
        )
      }
    }
  }
}

/**
 * SCREEN 2: Home Screen (ماڵەوە) matching the 2nd screen of the reference image.
 */
@Composable
private fun BasoHomeScreen(
  strings: BasoStrings,
  projectsCount: Int,
  highContrastBorders: Boolean,
  spaciousDensity: Boolean,
  onViewProjects: () -> Unit,
  onNewProject: () -> Unit,
  onOpenClients: () -> Unit,
  onOpenDesignPortfolio: () -> Unit,
  onOpenServices: () -> Unit,
  onOpenActiveProjectReview: () -> Unit,
  onOpenAiHub: () -> Unit = {},
  onOpenAppBuilder: () -> Unit = {},
) {
  val verticalGap = if (spaciousDensity) 16.dp else 12.dp

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(verticalGap),
    modifier = Modifier.fillMaxSize().testTag("baso_home_screen"),
  ) {
    // Greeting Block ("سڵاو" / "باسۆ 👋" / "بەخێربێیت بۆ ستۆدیۆی فریلانسەری")
    item {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = strings.greetingHello,
          style = MaterialTheme.typography.titleMedium,
          color = BasoSecondaryText,
        )
        Text(
          text = strings.greetingName,
          style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = strings.greetingWelcome,
            style = MaterialTheme.typography.bodyMedium,
            color = BasoSecondaryText,
          )
          // Subtle brand subtitle tag ("بەخێربێیت بۆ BASO")
          Text(
            text = strings.heroTitle,
            style = MaterialTheme.typography.labelMedium,
            color = BasoNeonTurquoise,
          )
        }
      }
    }

    // Hero Banner Card with Laptop Workspace Illustration & Neon Yellow CTA Pill
    item {
      Surface(
        color = BasoSurfaceCard,
        shape = RoundedCornerShape(20.dp),
        border =
          BorderStroke(
            width = 1.dp,
            color = if (highContrastBorders) BasoNeonTurquoise else BasoBorder,
          ),
        modifier = Modifier.fillMaxWidth().testTag("baso_hero_card"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier =
            Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  listOf(
                    BasoSurfaceCard,
                    Color(0xFF0E2238),
                  ),
                ),
              )
              .padding(16.dp),
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1.15f),
          ) {
            Text(
              text = strings.heroBannerText,
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
            )

            // Yellow pill CTA ("بینینی پڕۆژەکان <-")
            Surface(
              color = BasoNeonYellow,
              shape = RoundedCornerShape(22.dp),
              modifier =
                Modifier
                  .clip(RoundedCornerShape(22.dp))
                  .clickable(onClick = onViewProjects)
                  .testTag("home_primary_cta_button"),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
              ) {
                Text(
                  text = strings.heroBannerCta,
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                  color = BasoPrimaryBackground,
                )
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  tint = BasoPrimaryBackground,
                  modifier = Modifier.size(16.dp),
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          // Laptop Digital Studio Illustration matching Screen 2 Hero Card
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier
                .weight(0.85f)
                .height(114.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                  Brush.radialGradient(
                    listOf(Color(0xFF1294F2).copy(alpha = 0.32f), BasoSecondaryBackground),
                  ),
                )
                .border(1.dp, BasoNeonTurquoise.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
          ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val w = size.width
              val h = size.height
              // Desk surface glow
              drawRoundRect(
                color = Color(0xFF0A1422),
                topLeft = Offset(w * 0.08f, h * 0.72f),
                size = Size(w * 0.84f, h * 0.16f),
                cornerRadius = CornerRadius(8f, 8f),
              )
              // Laptop Screen Bezel
              drawRoundRect(
                color = Color(0xFF192B40),
                topLeft = Offset(w * 0.16f, h * 0.18f),
                size = Size(w * 0.68f, h * 0.48f),
                cornerRadius = CornerRadius(8f, 8f),
              )
              // Glowing Screen Display
              drawRoundRect(
                brush =
                  Brush.linearGradient(
                    listOf(Color(0xFF1294F2), BasoNeonTurquoise, BasoNeonYellow.copy(alpha = 0.85f)),
                  ),
                topLeft = Offset(w * 0.20f, h * 0.22f),
                size = Size(w * 0.60f, h * 0.40f),
                cornerRadius = CornerRadius(5f, 5f),
              )
              // Laptop Base
              drawRoundRect(
                color = Color(0xFF6A829A),
                topLeft = Offset(w * 0.10f, h * 0.66f),
                size = Size(w * 0.80f, h * 0.07f),
                cornerRadius = CornerRadius(6f, 6f),
              )
            }
          }
        }
      }
    }

    // 3 Stat Cards Row: پڕۆژەکان (5) | کڕیاران (3) | ئەرکەکان (8)
    item {
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        HomeStatCard(
          label = strings.statProjectsLabel,
          value = "$projectsCount",
          onClick = onViewProjects,
          modifier = Modifier.weight(1f),
        )
        HomeStatCard(
          label = strings.statClientsLabel,
          value = "3",
          onClick = onOpenClients,
          modifier = Modifier.weight(1f),
        )
        HomeStatCard(
          label = strings.statTasksLabel,
          value = "8",
          onClick = onOpenActiveProjectReview,
          modifier = Modifier.weight(1f),
        )
      }
    }

    // Quick Actions Section ("یارمەتی خێرا") with 4 square icon cards
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = strings.quickActionsTitle,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          QuickActionTile(
            label = strings.quickActionNewProject,
            icon = Icons.Default.Add,
            isPrimaryYellow = true,
            onClick = onNewProject,
            modifier = Modifier.weight(1f),
            testTag = "quick_action_new_project",
          )
          QuickActionTile(
            label = strings.quickActionAddClient,
            icon = Icons.Default.GroupAdd,
            isPrimaryYellow = false,
            onClick = onOpenClients,
            modifier = Modifier.weight(1f),
            testTag = "quick_action_add_client",
          )
          QuickActionTile(
            label = strings.quickActionDesign,
            icon = Icons.Default.Brush,
            isPrimaryYellow = false,
            onClick = onOpenDesignPortfolio,
            modifier = Modifier.weight(1f),
            testTag = "quick_action_design",
          )
          QuickActionTile(
            label = strings.quickActionVideo,
            icon = Icons.Default.Videocam,
            isPrimaryYellow = false,
            onClick = onOpenServices,
            modifier = Modifier.weight(1f),
            testTag = "quick_action_video",
          )
        }
      }
    }

    // Active Project Section ("پڕۆژەی چالاک") matching bottom of Screen 2
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = strings.activeProjectSectionTitle,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )

        BasoCard(
          highContrastBorder = highContrastBorders,
          onClick = onOpenActiveProjectReview,
          modifier = Modifier.fillMaxWidth().testTag("home_active_project_card"),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier =
                Modifier
                  .size(52.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(
                    Brush.linearGradient(
                      listOf(BasoIconBlue, BasoNeonTurquoise.copy(alpha = 0.85f)),
                    ),
                  ),
            ) {
              Icon(
                imageVector = Icons.Default.Web,
                contentDescription = strings.activeProjectTitle,
                tint = Color.White,
                modifier = Modifier.size(26.dp),
              )
            }

            Column(
              verticalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.weight(1f),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = strings.activeProjectTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BasoPrimaryText,
                  )
                  Text(
                    text = strings.activeProjectSubtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = BasoSecondaryText,
                  )
                }
                Icon(
                  imageVector = Icons.Default.MoreVert,
                  contentDescription = null,
                  tint = BasoSecondaryText,
                  modifier = Modifier.size(18.dp),
                )
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Box(
                  modifier =
                    Modifier
                      .weight(1f)
                      .height(6.dp)
                      .clip(CircleShape)
                      .background(BasoSecondaryBackground),
                ) {
                  Box(
                    modifier =
                      Modifier
                        .fillMaxWidth(0.75f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                          Brush.horizontalGradient(
                            listOf(Color(0xFF1294F2), BasoNeonTurquoise),
                          ),
                        ),
                  )
                }
                Text(
                  text = "75%",
                  style = MaterialTheme.typography.labelMedium,
                  color = BasoSecondaryText,
                )
              }
            }
          }
        }
      }
    }

    // BASO AI Multi-Tool Assistant Launch Card (Phase 3)
    item {
      BasoCard(
        highContrastBorder = highContrastBorders,
        onClick = onOpenAiHub,
        modifier = Modifier.fillMaxWidth().testTag("home_open_ai_hub_card"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BasoTurquoiseSubtleGlow)
                .border(1.dp, BasoNeonTurquoise.copy(alpha = 0.55f), RoundedCornerShape(14.dp)),
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "BASO AI",
              tint = BasoNeonTurquoise,
              modifier = Modifier.size(24.dp),
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "BASO AI • یاریدەدەری فرە-ئامراز",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
            )
            Text(
              text = "چاتی زیرەک • کۆد • وێبسایت • دیزاین • پلاندانانی پڕۆژە",
              style = MaterialTheme.typography.labelMedium,
              color = BasoSecondaryText,
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = BasoNeonYellow,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }

    // BASO AI Studio App Builder Launch Card (Phase 4)
    item {
      BasoCard(
        highContrastBorder = highContrastBorders,
        onClick = onOpenAppBuilder,
        modifier = Modifier.fillMaxWidth().testTag("home_open_app_builder_card"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BasoYellowSubtleGlow)
                .border(1.dp, BasoNeonYellow.copy(alpha = 0.65f), RoundedCornerShape(14.dp)),
          ) {
            Icon(
              imageVector = Icons.Default.Smartphone,
              contentDescription = "BASO AI Studio App Builder",
              tint = BasoNeonYellow,
              modifier = Modifier.size(24.dp),
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "BASO AI Studio • دروستکەری ئەپ و وێبسایت",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
            )
            Text(
              text = "ژینگەی دروستکردنی ئەندرۆید، وێبسایت و وێب-ئەپ بە پێشبینینی زیندوو و هەناردەی ZIP",
              style = MaterialTheme.typography.labelMedium,
              color = BasoSecondaryText,
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = BasoNeonTurquoise,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun HomeStatCard(
  label: String,
  value: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = BasoSurfaceCard,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, BasoBorder),
    modifier =
      modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick),
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = BasoSecondaryText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = BasoPrimaryText,
      )
    }
  }
}

@Composable
private fun QuickActionTile(
  label: String,
  icon: ImageVector,
  isPrimaryYellow: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String,
) {
  Surface(
    color = BasoSurfaceCard,
    shape = RoundedCornerShape(16.dp),
    border =
      BorderStroke(
        width = 1.dp,
        color = if (isPrimaryYellow) BasoNeonYellow.copy(alpha = 0.45f) else BasoBorder,
      ),
    modifier =
      modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .testTag(testTag),
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              if (isPrimaryYellow) BasoNeonYellow else BasoTurquoiseSubtleGlow,
            ),
      ) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = if (isPrimaryYellow) BasoPrimaryBackground else BasoNeonTurquoise,
          modifier = Modifier.size(20.dp),
        )
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = BasoPrimaryText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

/**
 * SCREEN 3: Projects Screen (پڕۆژەکان) matching the 3rd screen of the reference image.
 */
@Composable
private fun BasoProjectsScreen(
  strings: BasoStrings,
  customProjects: List<BasoProjectRecord>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  selectedFilterIndex: Int,
  onFilterSelect: (Int) -> Unit,
  selectedItemId: String,
  onSelectItem: (String) -> Unit,
  highContrastBorders: Boolean,
  spaciousDensity: Boolean,
) {
  val defaultProjects =
    remember(strings) {
      listOf(
        BasoProjectRecord(
          id = "proj_1",
          title = "وێبسایت بۆ کۆمپانیا",
          subtitle = "پڕۆژەی نهێنی",
          statusType = "IN_PROGRESS",
          progressPercent = 75,
          categoryIndex = 3,
          icon = Icons.Default.Language,
          iconColor = BasoIconBlue,
        ),
        BasoProjectRecord(
          id = "proj_2",
          title = "ئەپی مۆبایل",
          subtitle = "پڕۆژەی بازاڕ",
          statusType = "IN_PROGRESS",
          progressPercent = 40,
          categoryIndex = 2,
          icon = Icons.Default.Smartphone,
          iconColor = BasoIconIndigo,
        ),
        BasoProjectRecord(
          id = "proj_3",
          title = "دیزاین بۆ پۆستەکان",
          subtitle = "پڕۆژەی پلاتفۆرم",
          statusType = "COMPLETED",
          progressPercent = 100,
          categoryIndex = 1,
          icon = Icons.Default.Image,
          iconColor = Color(0xFFE5A900),
        ),
        BasoProjectRecord(
          id = "proj_4",
          title = "ڤیدیۆی رێکلامەوە",
          subtitle = "پڕۆژەی بازرگانی",
          statusType = "IN_PROGRESS",
          progressPercent = 75,
          categoryIndex = 4,
          icon = Icons.Default.Videocam,
          iconColor = BasoIconPurple,
        ),
        BasoProjectRecord(
          id = "proj_5",
          title = "AI ئامێر",
          subtitle = "پڕۆژەی تایبەت",
          statusType = "PUBLISHED",
          progressPercent = 20,
          categoryIndex = 2,
          icon = Icons.Default.AutoAwesome,
          iconColor = BasoIconBlue,
        ),
      )
    }

  val allProjects = remember(customProjects, defaultProjects) { customProjects + defaultProjects }

  val filteredProjects =
    remember(allProjects, searchQuery, selectedFilterIndex) {
      allProjects.filter { item ->
        val matchesQuery =
          searchQuery.isBlank() ||
            item.title.contains(searchQuery, ignoreCase = true) ||
            item.subtitle.contains(searchQuery, ignoreCase = true)
        val matchesFilter =
          selectedFilterIndex == 0 || item.categoryIndex == selectedFilterIndex
        matchesQuery && matchesFilter
      }
    }

  val verticalGap = if (spaciousDensity) 14.dp else 10.dp

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(verticalGap),
    modifier = Modifier.fillMaxSize().testTag("baso_projects_screen"),
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = strings.projectsTitle,
          style = MaterialTheme.typography.headlineLarge,
          color = BasoPrimaryText,
        )
        Text(
          text = strings.projectsSubtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
        )
      }
    }

    item {
      BasoSearchBox(
        query = searchQuery,
        onQueryChange = onSearchQueryChange,
        placeholder = strings.projectsSearchHint,
        testTag = "projects_search_box",
      )
    }

    item {
      BasoFilterChipsRow(
        filters =
          listOf(
            strings.filterAll,
            strings.filterDesign,
            strings.filterApp,
            strings.filterWebsite,
            strings.filterVideo,
          ),
        selectedIndex = selectedFilterIndex,
        onSelect = onFilterSelect,
        tagPrefix = "projects_filter_chip",
      )
    }

    if (filteredProjects.isEmpty()) {
      item {
        BasoStateCard(
          state = BasoSystemState.EMPTY,
          strings = strings.copy(stateEmptyBody = strings.searchEmptyMessage),
          onRetryOrAction = { onSearchQueryChange("") },
        )
      }
    } else {
      items(filteredProjects, key = { it.id }) { project ->
        val (statusText, statusColor, statusBg) =
          when (project.statusType) {
            "COMPLETED" -> Triple(strings.statusCompleted, BasoNeonTurquoise, BasoTurquoiseSubtleGlow)
            "PUBLISHED" -> Triple(strings.statusPublished, BasoCyanPill, BasoTurquoiseSubtleGlow)
            else -> Triple(strings.statusInProgress, BasoNeonYellow, BasoYellowSubtleGlow)
          }

        BasoProjectProgressCard(
          title = project.title,
          subtitle = project.subtitle,
          statusText = statusText,
          statusColor = statusColor,
          statusBgColor = statusBg,
          progressPercent = project.progressPercent,
          icon = project.icon,
          iconBoxColor = project.iconColor,
          isSelected = project.id == selectedItemId,
          highContrastBorder = highContrastBorders,
          onClick = { onSelectItem(project.id) },
          testTag = "project_item_${project.id}",
        )
      }
    }
  }
}

/**
 * SCREEN 10: Settings / More Screen (ڕێکخستنەکان) matching the bottom-right screen of the reference image,
 * while preserving direct access to Language RTL/LTR switching, Design System Components, Services, Profile, and Palette.
 */
@Composable
private fun BasoSettingsScreen(
  strings: BasoStrings,
  currentLanguage: AppLanguage,
  onSelectLanguage: (AppLanguage) -> Unit,
  highContrastBorders: Boolean,
  onToggleHighContrast: (Boolean) -> Unit,
  spaciousDensity: Boolean,
  onToggleSpaciousDensity: (Boolean) -> Unit,
  onOpenProfile: () -> Unit,
  onOpenComponents: () -> Unit,
  onOpenServices: () -> Unit,
  onOpenAiHub: () -> Unit = {},
  onOpenAppBuilder: () -> Unit = {},
  onOpenAiSettings: () -> Unit = {},
  onOpenSplash: () -> Unit,
  onSignOutConfirm: () -> Unit,
) {
  val verticalGap = if (spaciousDensity) 14.dp else 10.dp

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(verticalGap),
    modifier = Modifier.fillMaxSize().testTag("baso_settings_screen"),
  ) {
    item {
      Text(
        text = strings.settingsTitle,
        style = MaterialTheme.typography.headlineLarge,
        color = BasoPrimaryText,
      )
    }

    // Top Studio Brand Card matching Screen 10 ("BASO / Freelance Studio")
    item {
      BasoCard(
        highContrastBorder = highContrastBorders,
        onClick = onOpenProfile,
        modifier = Modifier.fillMaxWidth().testTag("settings_studio_card"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier =
                Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(BasoSecondaryBackground)
                  .border(1.dp, BasoBorder, RoundedCornerShape(14.dp)),
            ) {
              BasoBrandEmblem(size = 32.dp)
            }
            Column {
              Text(
                text = strings.appName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = BasoPrimaryText,
              )
              Text(
                text = strings.studioSubtitle,
                style = MaterialTheme.typography.labelMedium,
                color = BasoSecondaryText,
              )
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            contentDescription = null,
            tint = BasoSecondaryText,
            modifier = Modifier.size(16.dp),
          )
        }
      }
    }

    // BASO AI Studio App Builder entry in Settings
    item {
      SettingsActionRowCard(
        icon = Icons.Default.Smartphone,
        title = "BASO AI Studio • App Builder",
        subtitle = "دروستکەری ئەندرۆید، وێبسایت و وێب-ئەپ",
        trailingText = null,
        onClick = onOpenAppBuilder,
        testTag = "settings_open_app_builder_row",
      )
    }

    // BASO AI Multi-Tool Hub entry in Settings
    item {
      SettingsActionRowCard(
        icon = Icons.Default.AutoAwesome,
        title = "BASO AI",
        subtitle = "چات • کۆد • وێبسایت • دیزاین • پلان",
        trailingText = null,
        onClick = onOpenAiHub,
        testTag = "settings_open_ai_hub_row",
      )
    }

    // AI Provider Settings entry in Settings
    item {
      SettingsActionRowCard(
        icon = Icons.Outlined.Settings,
        title = "ڕێکخستنەکانی دابینکەری AI",
        subtitle = "Google Gemini & OpenAI • کلیلی API • گۆڕینی خودکار",
        trailingText = null,
        onClick = onOpenAiSettings,
        testTag = "settings_open_ai_settings_row",
      )
    }

    // 5 Action Cards matching Screen 10 of the reference image
    item {
      SettingsActionRowCard(
        icon = Icons.Outlined.Palette,
        title = strings.settingsAppearanceRow,
        subtitle = null,
        trailingText = null,
        onClick = onOpenComponents,
        testTag = "settings_open_components_row",
      )
    }

    item {
      SettingsActionRowCard(
        icon = Icons.Outlined.Language,
        title = strings.settingsLanguageRow,
        subtitle = currentLanguage.nativeLabel,
        trailingText = null,
        onClick = {
          val next =
            when (currentLanguage) {
              AppLanguage.KURDISH_SORANI -> AppLanguage.ARABIC
              AppLanguage.ARABIC -> AppLanguage.ENGLISH
              AppLanguage.ENGLISH -> AppLanguage.KURDISH_SORANI
            }
          onSelectLanguage(next)
        },
        testTag = "settings_cycle_language_row",
      )
    }

    item {
      SettingsActionRowCard(
        icon = Icons.Outlined.Settings,
        title = strings.settingsGeneralRow,
        subtitle = null,
        trailingText = null,
        onClick = onOpenProfile,
        testTag = "settings_open_profile_row",
      )
    }

    item {
      SettingsActionRowCard(
        icon = Icons.Outlined.WorkspacePremium,
        title = strings.settingsGoalsHelpRow,
        subtitle = null,
        trailingText = null,
        onClick = onOpenServices,
        testTag = "settings_open_services_row",
      )
    }

    item {
      SettingsActionRowCard(
        icon = Icons.Outlined.Info,
        title = strings.settingsAboutAppRow,
        subtitle = null,
        trailingText = "v1.0.0",
        onClick = onOpenSplash,
        testTag = "settings_open_about_row",
      )
    }

    // Red-bordered Sign Out / Reset Button matching bottom of Screen 10 ("چوونە دەرەوە")
    item {
      Surface(
        color = BasoErrorSubtleBg,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BasoError.copy(alpha = 0.65f)),
        modifier =
          Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onSignOutConfirm)
            .testTag("settings_logout_button"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.padding(vertical = 15.dp),
        ) {
          Text(
            text = strings.settingsLogoutCta,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = BasoError,
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = BasoError,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }

    // Direct Language Switcher pills (Kurdish Sorani RTL, Arabic RTL, English LTR)
    item {
      BasoCard(highContrastBorder = highContrastBorders, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = strings.languageSectionTitle,
            style = MaterialTheme.typography.titleMedium,
            color = BasoNeonTurquoise,
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            AppLanguage.entries.forEach { lang ->
              val selected = lang == currentLanguage
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
                    .clickable { onSelectLanguage(lang) }
                    .testTag("settings_lang_${lang.code}"),
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                ) {
                  Text(
                    text = lang.shortBadge,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (selected) BasoNeonYellow else BasoNeonTurquoise,
                  )
                  Text(
                    text = lang.nativeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = BasoPrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
              }
            }
          }
        }
      }
    }

    // Visual Accessibility Controls
    item {
      BasoCard(highContrastBorder = highContrastBorders, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
              Text(
                text = strings.highContrastModeLabel,
                style = MaterialTheme.typography.titleMedium,
                color = BasoPrimaryText,
              )
              Text(
                text = strings.highContrastModeSubtitle,
                style = MaterialTheme.typography.labelMedium,
                color = BasoSecondaryText,
              )
            }
            Switch(
              checked = highContrastBorders,
              onCheckedChange = onToggleHighContrast,
              colors =
                SwitchDefaults.colors(
                  checkedThumbColor = BasoPrimaryBackground,
                  checkedTrackColor = BasoNeonTurquoise,
                  uncheckedThumbColor = BasoSecondaryText,
                  uncheckedTrackColor = BasoSecondaryBackground,
                ),
              modifier = Modifier.testTag("settings_high_contrast_switch"),
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
              Text(
                text = strings.compactDensityLabel,
                style = MaterialTheme.typography.titleMedium,
                color = BasoPrimaryText,
              )
              Text(
                text = strings.compactDensitySubtitle,
                style = MaterialTheme.typography.labelMedium,
                color = BasoSecondaryText,
              )
            }
            Switch(
              checked = spaciousDensity,
              onCheckedChange = onToggleSpaciousDensity,
              colors =
                SwitchDefaults.colors(
                  checkedThumbColor = BasoPrimaryBackground,
                  checkedTrackColor = BasoNeonYellow,
                  uncheckedThumbColor = BasoSecondaryText,
                  uncheckedTrackColor = BasoSecondaryBackground,
                ),
              modifier = Modifier.testTag("settings_spacious_density_switch"),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsActionRowCard(
  icon: ImageVector,
  title: String,
  subtitle: String?,
  trailingText: String?,
  onClick: () -> Unit,
  testTag: String,
) {
  Surface(
    color = BasoSurfaceCard,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, BasoBorder),
    modifier =
      Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .testTag(testTag),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(BasoSecondaryBackground),
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = BasoPrimaryText,
            modifier = Modifier.size(20.dp),
          )
        }
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = BasoPrimaryText,
          )
          if (subtitle != null) {
            Text(
              text = subtitle,
              style = MaterialTheme.typography.labelMedium,
              color = BasoSecondaryText,
            )
          }
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        if (trailingText != null) {
          Text(
            text = trailingText,
            style = MaterialTheme.typography.labelMedium,
            color = BasoSecondaryText,
          )
        }
        Icon(
          imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
          contentDescription = null,
          tint = BasoSecondaryText,
          modifier = Modifier.size(14.dp),
        )
      }
    }
  }
}

/**
 * Reusable Components Showcase Screen preserved from Phase 1.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BasoComponentsScreen(
  strings: BasoStrings,
  highContrastBorders: Boolean,
  spaciousDensity: Boolean,
  onOpenDialog: () -> Unit,
  onFeedbackMessage: (String) -> Unit,
) {
  var sampleInput by rememberSaveable { mutableStateOf("BASO • دیزاینی کوردی") }
  var inputError by rememberSaveable { mutableStateOf(false) }
  var activeSystemState by rememberSaveable { mutableStateOf(BasoSystemState.SUCCESS) }

  val verticalGap = if (spaciousDensity) 18.dp else 12.dp

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
    verticalArrangement = Arrangement.spacedBy(verticalGap),
    modifier = Modifier.fillMaxSize().testTag("baso_components_screen"),
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = strings.componentsHeaderTitle,
          style = MaterialTheme.typography.headlineMedium,
          color = BasoPrimaryText,
        )
        Text(
          text = strings.componentsHeaderSubtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
        )
      }
    }

    item {
      BasoCard(highContrastBorder = highContrastBorders, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Text(
            text = strings.sectionButtonsTitle,
            style = MaterialTheme.typography.titleMedium,
            color = BasoNeonTurquoise,
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            BasoPrimaryButton(
              text = strings.primaryActionLabel,
              onClick = { onFeedbackMessage(strings.actionConfirmedFeedback) },
              leadingIcon = Icons.Default.CheckCircleOutline,
              testTag = "showcase_primary_button",
            )
            BasoSecondaryButton(
              text = strings.openDialogButton,
              onClick = onOpenDialog,
              highlightTurquoise = true,
              testTag = "showcase_dialog_button",
            )
          }
        }
      }
    }

    item {
      BasoCard(highContrastBorder = highContrastBorders, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Text(
            text = strings.sectionInputsTitle,
            style = MaterialTheme.typography.titleMedium,
            color = BasoNeonTurquoise,
          )
          BasoInputField(
            value = sampleInput,
            onValueChange = {
              sampleInput = it
              if (it.isNotBlank()) inputError = false
            },
            label = strings.inputLabelTitle,
            placeholder = strings.inputPlaceholderTitle,
            isError = inputError,
            supportingText = if (inputError) strings.inputErrorEmpty else strings.inputHelperValid,
            testTag = "showcase_input_field",
          )
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BasoPrimaryButton(
              text = strings.validateInputButton,
              onClick = {
                inputError = sampleInput.isBlank()
                if (!inputError) {
                  activeSystemState = BasoSystemState.SUCCESS
                  onFeedbackMessage(strings.inputHelperValid)
                } else {
                  activeSystemState = BasoSystemState.ERROR
                }
              },
              testTag = "showcase_validate_button",
            )
            BasoSecondaryButton(
              text = "بەتاڵکردنەوە / Clear",
              onClick = {
                sampleInput = ""
                inputError = true
                activeSystemState = BasoSystemState.ERROR
              },
              testTag = "showcase_clear_input_button",
            )
          }
        }
      }
    }

    item {
      BasoCard(highContrastBorder = highContrastBorders, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Text(
            text = strings.sectionStatesTitle,
            style = MaterialTheme.typography.titleMedium,
            color = BasoNeonTurquoise,
          )

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            val stateLabels =
              listOf(
                BasoSystemState.LOADING to strings.stateLoadingLabel,
                BasoSystemState.EMPTY to strings.stateEmptyLabel,
                BasoSystemState.SUCCESS to strings.stateSuccessLabel,
                BasoSystemState.ERROR to strings.stateErrorLabel,
              )
            stateLabels.forEach { (state, label) ->
              val selected = activeSystemState == state
              FilterChip(
                selected = selected,
                onClick = { activeSystemState = state },
                label = { Text(label) },
                colors =
                  FilterChipDefaults.filterChipColors(
                    containerColor = BasoSecondaryBackground,
                    labelColor = BasoSecondaryText,
                    selectedContainerColor = BasoSecondaryBackground,
                    selectedLabelColor = BasoNeonYellow,
                  ),
                border =
                  FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = BasoBorder,
                    selectedBorderColor = BasoNeonYellow,
                  ),
                modifier = Modifier.testTag("showcase_state_chip_${state.name.lowercase()}"),
              )
            }
          }

          BasoStateCard(
            state = activeSystemState,
            strings = strings,
            onRetryOrAction = { activeSystemState = BasoSystemState.SUCCESS },
          )
        }
      }
    }
  }
}

// Preserved existing composable function for backward compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "بەخێربێیت بۆ BASO، $name!",
    color = BasoPrimaryText,
    style = MaterialTheme.typography.bodyLarge,
    modifier = modifier,
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { BasoApp() }
}
