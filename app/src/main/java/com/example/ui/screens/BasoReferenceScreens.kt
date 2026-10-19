package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BasoBrandEmblem
import com.example.ui.components.BasoCard
import com.example.ui.components.BasoFilterChipsRow
import com.example.ui.components.BasoFreelancerAvatar
import com.example.ui.components.BasoGradientPillButton
import com.example.ui.components.BasoInputField
import com.example.ui.components.BasoPrimaryButton
import com.example.ui.components.BasoSearchBox
import com.example.ui.components.BasoStateCard
import com.example.ui.components.BasoSystemState
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.BasoBorder
import com.example.ui.theme.BasoChatBubbleOut
import com.example.ui.theme.BasoError
import com.example.ui.theme.BasoErrorSubtleBg
import com.example.ui.theme.BasoIconBlue
import com.example.ui.theme.BasoIconIndigo
import com.example.ui.theme.BasoIconOrange
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
import com.example.ui.theme.BasoYellowSubtleGlow

/**
 * SCREEN 1: Splash / Onboarding Hero Screen from top-left of the reference image.
 */
@Composable
fun BasoSplashScreen(
  strings: BasoStrings,
  onStartClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(BasoPrimaryBackground)
        .testTag("baso_splash_screen"),
  ) {
    // Subtle glowing wave lines and ambient aura matching Screen 1
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Top-left gold ambient glow
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(BasoNeonYellow.copy(alpha = 0.16f), Color.Transparent),
            center = Offset(w * 0.25f, h * 0.18f),
            radius = w * 0.65f,
          ),
        center = Offset(w * 0.25f, h * 0.18f),
        radius = w * 0.65f,
      )

      // Bottom-right turquoise ambient glow
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(BasoNeonTurquoise.copy(alpha = 0.14f), Color.Transparent),
            center = Offset(w * 0.75f, h * 0.72f),
            radius = w * 0.65f,
          ),
        center = Offset(w * 0.75f, h * 0.72f),
        radius = w * 0.65f,
      )

      // Flowing golden wave ribbon across lower-middle
      val wave1 =
        Path().apply {
          moveTo(0f, h * 0.72f)
          cubicTo(w * 0.35f, h * 0.64f, w * 0.65f, h * 0.68f, w, h * 0.56f)
        }
      drawPath(
        path = wave1,
        brush =
          Brush.horizontalGradient(
            colors = listOf(BasoNeonYellow.copy(alpha = 0.75f), BasoNeonTurquoise.copy(alpha = 0.3f)),
          ),
        style = Stroke(width = 3.dp.toPx()),
      )

      val wave2 =
        Path().apply {
          moveTo(0f, h * 0.66f)
          cubicTo(w * 0.30f, h * 0.74f, w * 0.70f, h * 0.58f, w, h * 0.63f)
        }
      drawPath(
        path = wave2,
        brush =
          Brush.horizontalGradient(
            colors = listOf(BasoNeonYellow.copy(alpha = 0.35f), BasoNeonTurquoise.copy(alpha = 0.55f)),
          ),
        style = Stroke(width = 1.8.dp.toPx()),
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
      modifier =
        Modifier
          .fillMaxSize()
          .padding(horizontal = 28.dp, vertical = 36.dp),
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        BasoBrandEmblem(size = 112.dp)

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "BASO",
          style =
            MaterialTheme.typography.displayLarge.copy(
              fontSize = 44.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 2.sp,
            ),
          color = BasoPrimaryText,
        )

        Text(
          text = strings.splashTitle,
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
          color = BasoPrimaryText,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = strings.splashCategories,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
          textAlign = TextAlign.Center,
        )

        Box(
          modifier =
            Modifier
              .width(180.dp)
              .height(1.dp)
              .background(
                Brush.horizontalGradient(
                  listOf(Color.Transparent, BasoNeonYellow.copy(alpha = 0.5f), Color.Transparent),
                ),
              ),
        )

        Text(
          text = strings.splashTagline,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
          textAlign = TextAlign.Center,
        )
      }

      BasoGradientPillButton(
        text = strings.splashCta,
        onClick = onStartClick,
        showArrow = true,
        testTag = "splash_start_button",
      )
    }
  }
}

/**
 * SCREEN 4: Clients Screen (کڕیاران) from top row, 4th column of the reference image.
 */
data class BasoClientItem(
  val id: String,
  val initials: String,
  val name: String,
  val email: String,
  val statusType: String, // "ACTIVE", "REVIEW", "INACTIVE"
  val badgeColor: Color,
)

@Composable
fun BasoClientsScreen(
  strings: BasoStrings,
  highContrastBorders: Boolean,
  onClientClick: (BasoClientItem) -> Unit,
  modifier: Modifier = Modifier,
) {
  var searchQuery by rememberSaveable { mutableStateOf("") }
  var selectedFilter by rememberSaveable { mutableIntStateOf(0) }

  val clients =
    remember {
      listOf(
        BasoClientItem("c1", "T", "Tech Solutions", "sardar@example.com", "ACTIVE", Color(0xFFD69E00)),
        BasoClientItem("c2", "CA", "Creative Agency", "hello@creative.com", "ACTIVE", Color(0xFF00B8A9)),
        BasoClientItem("c3", "GB", "Global Business", "info@global.com", "REVIEW", Color(0xFFE85D2A)),
        BasoClientItem("c4", "DS", "Digital Studio", "contact@studio.com", "ACTIVE", Color(0xFF2962FF)),
        BasoClientItem("c5", "SC", "Smart Corp", "hello@smartcorp.com", "INACTIVE", Color(0xFF39A2DB)),
      )
    }

  val filteredClients =
    remember(clients, searchQuery, selectedFilter) {
      clients.filter { client ->
        val matchesSearch =
          searchQuery.isBlank() ||
            client.name.contains(searchQuery, ignoreCase = true) ||
            client.email.contains(searchQuery, ignoreCase = true)
        val matchesFilter =
          when (selectedFilter) {
            1 -> client.statusType == "ACTIVE"
            2 -> client.statusType != "ACTIVE"
            else -> true
          }
        matchesSearch && matchesFilter
      }
    }

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = modifier.fillMaxSize().testTag("baso_clients_screen"),
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = strings.clientsTitle,
          style = MaterialTheme.typography.headlineLarge,
          color = BasoPrimaryText,
        )
        Text(
          text = strings.clientsSubtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
        )
      }
    }

    item {
      BasoSearchBox(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        placeholder = strings.clientsSearchHint,
        testTag = "clients_search_box",
      )
    }

    item {
      BasoFilterChipsRow(
        filters = listOf(strings.filterAll, strings.filterActive, strings.filterInactive),
        selectedIndex = selectedFilter,
        onSelect = { selectedFilter = it },
        tagPrefix = "clients_filter_chip",
      )
    }

    if (filteredClients.isEmpty()) {
      item {
        BasoStateCard(
          state = BasoSystemState.EMPTY,
          strings = strings,
          onRetryOrAction = { searchQuery = "" },
        )
      }
    } else {
      items(filteredClients, key = { it.id }) { client ->
        val (statusText, statusColor, statusBg) =
          when (client.statusType) {
            "ACTIVE" -> Triple(strings.statusActive, BasoSuccess, BasoSuccessSubtleBg)
            "REVIEW" -> Triple(strings.statusReview, BasoNeonYellow, BasoYellowSubtleGlow)
            else -> Triple(strings.statusInactive, BasoError, BasoErrorSubtleBg)
          }

        BasoCard(
          highContrastBorder = highContrastBorders,
          onClick = { onClientClick(client) },
          modifier = Modifier.fillMaxWidth().testTag("client_card_${client.id}"),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            // Glowing circular initial avatar matching Screen 4
            Box(
              contentAlignment = Alignment.Center,
              modifier =
                Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      listOf(client.badgeColor, client.badgeColor.copy(alpha = 0.55f)),
                    ),
                  )
                  .border(1.5.dp, client.badgeColor, CircleShape),
            ) {
              Text(
                text = client.initials,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = client.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BasoPrimaryText,
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = client.email,
                style = MaterialTheme.typography.bodyMedium,
                color = BasoSecondaryText,
              )
            }

            Box(
              modifier =
                Modifier
                  .clip(CircleShape)
                  .background(statusBg)
                  .border(1.dp, statusColor.copy(alpha = 0.5f), CircleShape)
                  .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
              Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = statusColor,
              )
            }
          }
        }
      }
    }
  }
}

/**
 * SCREEN 5: Portfolio Screen (پۆرتفۆلیۆ) from top-right of the reference image.
 */
data class BasoPortfolioItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val categoryIndex: Int,
  val primaryAccent: Color,
  val secondaryAccent: Color,
)

@Composable
fun BasoPortfolioScreen(
  strings: BasoStrings,
  highContrastBorders: Boolean,
  onSeeAllProjects: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedFilter by rememberSaveable { mutableIntStateOf(0) }

  val portfolioItems =
    remember {
      listOf(
        BasoPortfolioItem("p1", "Web Design", "Modern Website", 2, Color(0xFF1294F2), BasoNeonTurquoise),
        BasoPortfolioItem("p2", "Mobile App UI", "Android & iOS", 1, Color(0xFF3E54FF), BasoNeonTurquoise),
        BasoPortfolioItem("p3", "Graphic Design", "Brand Identity", 1, BasoNeonYellow, Color(0xFFFF8C00)),
        BasoPortfolioItem("p4", "Video Editing", "Creative Video", 3, Color(0xFF1294F2), Color(0xFF9B3DFF)),
      )
    }

  val filtered =
    remember(portfolioItems, selectedFilter) {
      if (selectedFilter == 0) portfolioItems
      else portfolioItems.filter { it.categoryIndex == selectedFilter }
    }

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxSize().testTag("baso_portfolio_screen"),
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = strings.portfolioTitle,
          style = MaterialTheme.typography.headlineLarge,
          color = BasoPrimaryText,
        )
        Text(
          text = strings.portfolioSubtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
        )
      }
    }

    item {
      BasoFilterChipsRow(
        filters = listOf(strings.filterAll, strings.filterDesign, strings.filterWebsite, strings.filterVideo),
        selectedIndex = selectedFilter,
        onSelect = { selectedFilter = it },
        tagPrefix = "portfolio_filter_chip",
      )
    }

    // 2x2 Grid of Portfolio Cards matching Screen 5
    items(filtered.chunked(2)) { rowItems ->
      Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        rowItems.forEach { item ->
          PortfolioGridCard(
            item = item,
            highContrastBorders = highContrastBorders,
            onClick = onSeeAllProjects,
            modifier = Modifier.weight(1f),
          )
        }
        if (rowItems.size == 1) {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      BasoGradientPillButton(
        text = strings.portfolioSeeAllCta,
        onClick = onSeeAllProjects,
        testTag = "portfolio_see_all_button",
      )
    }
  }
}

@Composable
private fun PortfolioGridCard(
  item: BasoPortfolioItem,
  highContrastBorders: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = BasoSurfaceCard,
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, if (highContrastBorders) BasoSecondaryText else BasoBorder),
    modifier =
      modifier
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onClick)
        .testTag("portfolio_card_${item.id}"),
  ) {
    Column {
      // Illustrated preview canvas matching the 4 thumbnails in Screen 5
      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier
            .fillMaxWidth()
            .height(132.dp)
            .background(
              Brush.linearGradient(
                listOf(
                  BasoSecondaryBackground,
                  item.primaryAccent.copy(alpha = 0.28f),
                  item.secondaryAccent.copy(alpha = 0.22f),
                ),
              ),
            ),
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          when (item.id) {
            "p1" -> {
              // Laptop mockup illustration
              drawRoundRect(
                color = Color(0xFF0A1626),
                topLeft = Offset(w * 0.18f, h * 0.18f),
                size = Size(w * 0.64f, h * 0.48f),
                cornerRadius = CornerRadius(8f, 8f),
              )
              drawRoundRect(
                brush = Brush.linearGradient(listOf(item.primaryAccent, item.secondaryAccent)),
                topLeft = Offset(w * 0.22f, h * 0.23f),
                size = Size(w * 0.56f, h * 0.38f),
                cornerRadius = CornerRadius(4f, 4f),
              )
              drawRoundRect(
                color = Color(0xFF4A607A),
                topLeft = Offset(w * 0.10f, h * 0.67f),
                size = Size(w * 0.80f, h * 0.06f),
                cornerRadius = CornerRadius(6f, 6f),
              )
            }
            "p2" -> {
              // Smartphone mockup illustration
              drawRoundRect(
                color = Color(0xFF0A1626),
                topLeft = Offset(w * 0.30f, h * 0.10f),
                size = Size(w * 0.40f, h * 0.78f),
                cornerRadius = CornerRadius(14f, 14f),
              )
              drawRoundRect(
                brush = Brush.verticalGradient(listOf(item.primaryAccent, item.secondaryAccent)),
                topLeft = Offset(w * 0.33f, h * 0.15f),
                size = Size(w * 0.34f, h * 0.68f),
                cornerRadius = CornerRadius(10f, 10f),
              )
            }
            "p3" -> {
              // Graphic Design golden geometric tablet
              drawRoundRect(
                color = Color(0xFF142234),
                topLeft = Offset(w * 0.22f, h * 0.14f),
                size = Size(w * 0.56f, h * 0.72f),
                cornerRadius = CornerRadius(12f, 12f),
              )
              val triangle =
                Path().apply {
                  moveTo(w * 0.50f, h * 0.26f)
                  lineTo(w * 0.68f, h * 0.70f)
                  lineTo(w * 0.32f, h * 0.70f)
                  close()
                }
              drawPath(
                path = triangle,
                brush = Brush.verticalGradient(listOf(BasoNeonYellow, Color(0xFFFF8800))),
              )
            }
            else -> {
              // Video Editing timeline & play button
              drawRoundRect(
                color = Color(0xFF0C1A2B),
                topLeft = Offset(w * 0.14f, h * 0.16f),
                size = Size(w * 0.72f, h * 0.68f),
                cornerRadius = CornerRadius(12f, 12f),
              )
              drawCircle(
                brush = Brush.radialGradient(listOf(BasoNeonTurquoise.copy(alpha = 0.5f), Color.Transparent)),
                radius = w * 0.24f,
                center = Offset(w * 0.5f, h * 0.5f),
              )
              drawCircle(
                color = Color(0xFF1294F2),
                radius = w * 0.14f,
                center = Offset(w * 0.5f, h * 0.5f),
              )
            }
          }
        }
        if (item.id == "p4") {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp),
          )
        }
      }

      Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(14.dp),
      ) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = item.subtitle,
          style = MaterialTheme.typography.labelMedium,
          color = BasoSecondaryText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

/**
 * SCREEN 6: Services Screen (خزمەتگوزارییەکان) from bottom-left of the reference image.
 */
@Composable
fun BasoServicesScreen(
  strings: BasoStrings,
  highContrastBorders: Boolean,
  onSelectService: () -> Unit,
  onContactClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val services =
    remember(strings) {
      listOf(
        Triple(strings.serviceWebTitle, strings.serviceWebSub, Pair(Icons.Default.Web, BasoIconBlue)),
        Triple(strings.serviceAppTitle, strings.serviceAppSub, Pair(Icons.Default.Smartphone, BasoIconIndigo)),
        Triple(strings.serviceGraphicTitle, strings.serviceGraphicSub, Pair(Icons.Default.Brush, BasoIconOrange)),
        Triple(strings.serviceVideoTitle, strings.serviceVideoSub, Pair(Icons.Default.Videocam, BasoIconPurple)),
        Triple(strings.serviceAiTitle, strings.serviceAiSub, Pair(Icons.Default.AutoAwesome, BasoIconBlue)),
      )
    }

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier.fillMaxSize().testTag("baso_services_screen"),
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = strings.servicesTitle,
          style = MaterialTheme.typography.headlineLarge,
          color = BasoPrimaryText,
        )
        Text(
          text = strings.servicesSubtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
        )
      }
    }

    items(services.size) { index ->
      val (title, subtitle, iconPair) = services[index]
      val (icon, boxColor) = iconPair
      BasoCard(
        highContrastBorder = highContrastBorders,
        onClick = onSelectService,
        modifier = Modifier.fillMaxWidth().testTag("service_card_$index"),
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
                .background(Brush.linearGradient(listOf(boxColor, boxColor.copy(alpha = 0.72f)))),
          ) {
            Icon(
              imageVector = icon,
              contentDescription = title,
              tint = Color.White,
              modifier = Modifier.size(24.dp),
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodyMedium,
              color = BasoSecondaryText,
            )
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

    item {
      Spacer(modifier = Modifier.height(6.dp))
      BasoGradientPillButton(
        text = strings.servicesContactCta,
        onClick = onContactClick,
        testTag = "services_contact_button",
      )
    }
  }
}

/**
 * SCREEN 7: New Project Screen (پڕۆژەی نوێ) from bottom row, 2nd column of the reference image.
 */
@Composable
fun BasoNewProjectScreen(
  strings: BasoStrings,
  onBack: () -> Unit,
  onCreateProject: (title: String, categoryIndex: Int, budget: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedCategory by rememberSaveable { mutableIntStateOf(0) }
  var projectName by rememberSaveable { mutableStateOf("") }
  var projectDescription by rememberSaveable { mutableStateOf("") }
  var selectedBudget by rememberSaveable { mutableStateOf("") }
  var budgetExpanded by remember { mutableStateOf(false) }
  var showNameError by rememberSaveable { mutableStateOf(false) }

  val categories =
    remember(strings) {
      listOf(
        Triple(strings.filterWebsite, Icons.Default.Language, BasoNeonTurquoise),
        Triple(strings.filterApp, Icons.Default.Smartphone, BasoIconPurple),
        Triple(strings.filterDesign, Icons.Default.Brush, BasoNeonTurquoise),
        Triple(strings.filterVideo, Icons.Default.Videocam, BasoIconPurple),
      )
    }

  val budgetOptions = listOf("$300 - $600", "$600 - $1,500", "$1,500 - $3,000", "$3,000+")

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxSize().testTag("baso_new_project_screen"),
  ) {
    // Header with Back button + Title
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        IconButton(
          onClick = onBack,
          modifier =
            Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(BasoSurfaceCard)
              .testTag("new_project_back_button"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = BasoPrimaryText,
          )
        }
        Column {
          Text(
            text = strings.newProjectTitle,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = BasoPrimaryText,
          )
          Text(
            text = strings.newProjectSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = BasoSecondaryText,
          )
        }
      }
    }

    // 2x2 Category Selection Grid matching Screen 7 (Neon Yellow border on selected card)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categories.chunked(2).forEachIndexed { rowIdx, rowItems ->
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            rowItems.forEachIndexed { colIdx, (label, icon, tint) ->
              val index = rowIdx * 2 + colIdx
              val selected = selectedCategory == index
              Surface(
                color = BasoSurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border =
                  BorderStroke(
                    width = if (selected) 1.8.dp else 1.dp,
                    color = if (selected) BasoNeonYellow else BasoBorder,
                  ),
                modifier =
                  Modifier
                    .weight(1f)
                    .height(104.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { selectedCategory = index }
                    .testTag("new_project_type_$index"),
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center,
                  modifier = Modifier.fillMaxSize().padding(12.dp),
                ) {
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                      Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = 0.16f)),
                  ) {
                    Icon(
                      imageVector = icon,
                      contentDescription = label,
                      tint = tint,
                      modifier = Modifier.size(22.dp),
                    )
                  }
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = BasoPrimaryText,
                  )
                }
              }
            }
          }
        }
      }
    }

    // Project Name Field
    item {
      BasoInputField(
        value = projectName,
        onValueChange = {
          projectName = it
          if (it.isNotBlank()) showNameError = false
        },
        label = strings.projectNameLabel,
        placeholder = strings.projectNamePlaceholder,
        isError = showNameError,
        supportingText = if (showNameError) strings.inputErrorEmpty else null,
        testTag = "new_project_name_input",
      )
    }

    // Project Description Field
    item {
      BasoInputField(
        value = projectDescription,
        onValueChange = { projectDescription = it },
        label = strings.projectDescLabel,
        placeholder = strings.projectDescPlaceholder,
        singleLine = false,
        minLines = 3,
        testTag = "new_project_desc_input",
      )
    }

    // Budget Dropdown Field
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = strings.projectBudgetLabel,
          style = MaterialTheme.typography.titleMedium,
          color = BasoPrimaryText,
          modifier = Modifier.padding(bottom = 8.dp),
        )
        Box {
          Surface(
            color = BasoSurfaceCard,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BasoBorder),
            modifier =
              Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { budgetExpanded = true }
                .testTag("new_project_budget_selector"),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            ) {
              Text(
                text = selectedBudget.ifEmpty { strings.projectBudgetPlaceholder },
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedBudget.isEmpty()) BasoSecondaryText else BasoPrimaryText,
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = BasoSecondaryText,
              )
            }
          }

          DropdownMenu(
            expanded = budgetExpanded,
            onDismissRequest = { budgetExpanded = false },
            modifier = Modifier.background(BasoSurfaceCard).border(1.dp, BasoBorder, RoundedCornerShape(12.dp)),
          ) {
            budgetOptions.forEach { option ->
              DropdownMenuItem(
                text = { Text(option, color = BasoPrimaryText) },
                onClick = {
                  selectedBudget = option
                  budgetExpanded = false
                },
              )
            }
          }
        }
      }
    }

    // Bottom Neon Yellow Button ("دواتر <-")
    item {
      Spacer(modifier = Modifier.height(4.dp))
      BasoPrimaryButton(
        text = "${strings.nextButtonLabel}  →",
        onClick = {
          if (projectName.isBlank()) {
            showNameError = true
          } else {
            // Map 2x2 grid index (0=Website, 1=App, 2=Design, 3=Video) to Projects filter index (1=Design, 2=App, 3=Website, 4=Video)
            val mappedCategoryIndex =
              when (selectedCategory) {
                0 -> 3
                1 -> 2
                2 -> 1
                3 -> 4
                else -> 3
              }
            val resolvedSubtitle =
              when {
                projectDescription.isNotBlank() && selectedBudget.isNotBlank() ->
                  "${projectDescription.trim()} • $selectedBudget"
                projectDescription.isNotBlank() -> projectDescription.trim()
                selectedBudget.isNotBlank() -> selectedBudget
                else -> "$600 - $1,500"
              }
            onCreateProject(
              projectName.trim(),
              mappedCategoryIndex,
              resolvedSubtitle,
            )
          }
        },
        modifier = Modifier.fillMaxWidth(),
        testTag = "new_project_submit_button",
      )
    }
  }
}

/**
 * SCREEN 8: Review & Client Chat Screen (پێداچوونەوە) from bottom row, 3rd column of the reference image.
 */
data class BasoChatMessage(
  val id: String,
  val text: String,
  val time: String,
  val isOutgoing: Boolean,
)

@Composable
fun BasoReviewChatScreen(
  strings: BasoStrings,
  clientName: String = "سەردار محەمەد",
  externalMessages: List<BasoChatMessage>? = null,
  onSendMessage: ((String) -> Unit)? = null,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var messageDraft by rememberSaveable { mutableStateOf("") }
  val fallbackMessages =
    remember {
      mutableStateListOf(
        BasoChatMessage(
          id = "m1",
          text = "سڵاو.. دەتوانیت بۆم وێبسایتێکی سادە و پێشکەوتوو دروست بکەیت؟",
          time = "10:24",
          isOutgoing = false,
        ),
        BasoChatMessage(
          id = "m2",
          text = "بەدڵنیایی.. دەمەوێت وێبسایتێکی پێشکەوتوو و سادە بۆت دروست بکەم.",
          time = "10:26",
          isOutgoing = true,
        ),
      )
    }
  val messages = externalMessages ?: fallbackMessages

  val checklist =
    remember(strings) {
      listOf(
        strings.chatFeature1,
        strings.chatFeature2,
        strings.chatFeature3,
        strings.chatFeature4,
      )
    }

  Column(
    modifier =
      modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 14.dp)
        .testTag("baso_review_chat_screen"),
  ) {
    // Top Header
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
            .testTag("chat_back_button"),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = BasoPrimaryText,
        )
      }
      Text(
        text = strings.reviewTitle,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = BasoPrimaryText,
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Client Profile Row
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      BasoFreelancerAvatar(size = 44.dp)
      Column {
        Text(
          text = clientName,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )
        Text(
          text = strings.clientRoleLabel,
          style = MaterialTheme.typography.labelMedium,
          color = BasoSecondaryText,
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Messages + Deliverables Checklist
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.weight(1f).fillMaxWidth(),
    ) {
      items(messages, key = { it.id }) { msg ->
        Box(
          contentAlignment = if (msg.isOutgoing) Alignment.CenterEnd else Alignment.CenterStart,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Surface(
            color = if (msg.isOutgoing) BasoChatBubbleOut else BasoSurfaceCard,
            shape =
              RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (msg.isOutgoing) 18.dp else 4.dp,
                bottomEnd = if (msg.isOutgoing) 4.dp else 18.dp,
              ),
            border = if (msg.isOutgoing) null else BorderStroke(1.dp, BasoBorder),
            modifier = Modifier.fillMaxWidth(0.86f),
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = msg.text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = msg.time,
                  style = MaterialTheme.typography.labelMedium,
                  color = Color.White.copy(alpha = 0.75f),
                )
                if (msg.isOutgoing) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                  )
                }
              }
            }
          }
        }
      }

      // Checklist Section (تایبەتمەندییەکان) matching Screen 8
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = strings.chatFeaturesTitle,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          checklist.forEach { itemText ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = BasoNeonYellow,
                modifier = Modifier.size(22.dp),
              )
              Text(
                text = itemText,
                style = MaterialTheme.typography.bodyLarge,
                color = BasoPrimaryText,
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Bottom Chat Input Bar with Emoji + Neon Yellow Send Button matching Screen 8
    Surface(
      color = BasoSurfaceCard,
      shape = RoundedCornerShape(26.dp),
      border = BorderStroke(1.dp, BasoBorder),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      ) {
        Icon(
          imageVector = Icons.Outlined.SentimentSatisfied,
          contentDescription = null,
          tint = BasoSecondaryText,
          modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
          value = messageDraft,
          onValueChange = { messageDraft = it },
          singleLine = true,
          placeholder = {
            Text(
              text = strings.chatInputPlaceholder,
              style = MaterialTheme.typography.bodyMedium,
              color = BasoSecondaryText,
            )
          },
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedTextColor = BasoPrimaryText,
              unfocusedTextColor = BasoPrimaryText,
              cursorColor = BasoNeonTurquoise,
            ),
          modifier = Modifier.weight(1f).testTag("chat_message_input"),
        )
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(BasoNeonYellow)
              .clickable {
                if (messageDraft.isNotBlank()) {
                  val trimmed = messageDraft.trim()
                  if (onSendMessage != null) {
                    onSendMessage(trimmed)
                  } else {
                    fallbackMessages.add(
                      BasoChatMessage(
                        id = "m_${fallbackMessages.size + 1}",
                        text = trimmed,
                        time = "10:28",
                        isOutgoing = true,
                      ),
                    )
                  }
                  messageDraft = ""
                }
              }
              .testTag("chat_send_button"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = BasoPrimaryBackground,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

/**
 * SCREEN 9: My Profile Screen (پڕۆفایلی من) from bottom row, 4th column of the reference image.
 */
@Composable
fun BasoProfileScreen(
  strings: BasoStrings,
  currentLanguage: AppLanguage,
  projectsCount: Int,
  clientsCount: Int,
  onLanguageCycle: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenHelp: () -> Unit,
  onOpenAbout: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxSize().testTag("baso_profile_screen"),
  ) {
    item {
      Box(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = strings.profileTitle,
          style = MaterialTheme.typography.headlineLarge,
          color = BasoPrimaryText,
        )
      }
    }

    // Centered Avatar + Name + Role
    item {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        BasoFreelancerAvatar(
          size = 112.dp,
          showVerifiedBadge = true,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "باسۆ",
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
          color = BasoPrimaryText,
        )
        Text(
          text = strings.profileRole,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
        )
      }
    }

    // 3 Stat Cards Row: 5 پڕۆژە | 3 کڕیار | 4.8 ڕەیتینگ
    item {
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        ProfileStatBox(
          value = "$projectsCount",
          label = strings.profileStatProjects,
          modifier = Modifier.weight(1f),
        )
        ProfileStatBox(
          value = "$clientsCount",
          label = strings.profileStatClients,
          modifier = Modifier.weight(1f),
        )
        ProfileStatBox(
          value = "4.8",
          label = strings.profileStatRating,
          modifier = Modifier.weight(1f),
        )
      }
    }

    // Profile Action List Card matching Screen 9
    item {
      Surface(
        color = BasoSurfaceCard,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, BasoBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column {
          ProfileMenuRow(
            icon = Icons.Outlined.PersonOutline,
            title = strings.profileEditRow,
            trailingText = null,
            onClick = onOpenSettings,
          )
          ProfileRowDivider()
          ProfileMenuRow(
            icon = Icons.Outlined.Settings,
            title = strings.profileSettingsRow,
            trailingText = null,
            onClick = onOpenSettings,
          )
          ProfileRowDivider()
          ProfileMenuRow(
            icon = Icons.Outlined.Language,
            title = strings.profileLanguageRow,
            trailingText = currentLanguage.nativeLabel,
            onClick = onLanguageCycle,
          )
          ProfileRowDivider()
          ProfileMenuRow(
            icon = Icons.AutoMirrored.Outlined.HelpOutline,
            title = strings.profileHelpRow,
            trailingText = null,
            onClick = onOpenHelp,
          )
          ProfileRowDivider()
          ProfileMenuRow(
            icon = Icons.Outlined.Info,
            title = strings.profileAboutRow,
            trailingText = null,
            onClick = onOpenAbout,
          )
        }
      }
    }
  }
}

@Composable
private fun ProfileStatBox(
  value: String,
  label: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = BasoSurfaceCard,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, BasoBorder),
    modifier = modifier,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = BasoPrimaryText,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = BasoSecondaryText,
      )
    }
  }
}

@Composable
private fun ProfileMenuRow(
  icon: ImageVector,
  title: String,
  trailingText: String?,
  onClick: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier =
      Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 15.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = BasoPrimaryText,
        modifier = Modifier.size(20.dp),
      )
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = BasoPrimaryText,
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
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

@Composable
private fun ProfileRowDivider() {
  Box(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .height(1.dp)
        .background(BasoBorder),
  )
}
