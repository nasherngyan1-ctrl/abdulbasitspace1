package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.BasoStrings
import com.example.ui.theme.BasoSuccess
import com.example.ui.theme.BasoSuccessSubtleBg
import com.example.ui.theme.BasoSurfaceCard
import com.example.ui.theme.BasoTurquoiseSubtleGlow
import com.example.ui.theme.BasoYellowSubtleGlow

enum class BasoDestination(
  val route: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val showInMainBar: Boolean = true,
) {
  HOME("home", Icons.Filled.Home, Icons.Outlined.Home, true),
  PROJECTS("projects", Icons.Filled.Folder, Icons.Outlined.FolderOpen, true),
  CLIENTS("clients", Icons.Filled.Groups, Icons.Outlined.Groups, true),
  PORTFOLIO("portfolio", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary, true),
  SETTINGS("settings", Icons.Filled.Menu, Icons.Outlined.Menu, true),
  // Secondary screens accessible via More/Settings, Quick Actions, or direct navigation
  SERVICES("services", Icons.Filled.Widgets, Icons.Outlined.Widgets, false),
  NEW_PROJECT("new_project", Icons.Filled.Folder, Icons.Outlined.FolderOpen, false),
  REVIEW_CHAT("review_chat", Icons.Filled.Groups, Icons.Outlined.Groups, false),
  PROFILE("profile", Icons.Filled.Settings, Icons.Outlined.Settings, false),
  COMPONENTS("components", Icons.Filled.Widgets, Icons.Outlined.Widgets, false),
  AI_HUB("ai_hub", Icons.Filled.Widgets, Icons.Outlined.Widgets, false),
  APP_BUILDER("app_builder", Icons.Filled.Widgets, Icons.Outlined.Widgets, false),
  AI_SETTINGS("ai_settings", Icons.Filled.Settings, Icons.Outlined.Settings, false),
  SPLASH("splash", Icons.Filled.Home, Icons.Outlined.Home, false),
}

enum class BasoSystemState {
  LOADING,
  EMPTY,
  SUCCESS,
  ERROR,
}

/**
 * Custom Canvas recreation of the 3D geometric 'B' ribbon logo from the BASO reference image.
 */
@Composable
fun BasoBrandEmblem(
  size: Dp = 42.dp,
  modifier: Modifier = Modifier,
) {
  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Lower cyan-blue loop of the 'B'
    val lowerPath =
      Path().apply {
        moveTo(w * 0.28f, h * 0.50f)
        lineTo(w * 0.84f, h * 0.66f)
        lineTo(w * 0.34f, h * 0.92f)
        close()
      }
    drawPath(
      path = lowerPath,
      brush =
        Brush.linearGradient(
          colors = listOf(Color(0xFF00F5D4), Color(0xFF1294F2)),
          start = Offset(w * 0.25f, h * 0.5f),
          end = Offset(w * 0.85f, h * 0.9f),
        ),
    )

    // Upper yellow-orange loop of the 'B'
    val upperPath =
      Path().apply {
        moveTo(w * 0.28f, h * 0.10f)
        lineTo(w * 0.86f, h * 0.36f)
        lineTo(w * 0.36f, h * 0.62f)
        close()
      }
    drawPath(
      path = upperPath,
      brush =
        Brush.linearGradient(
          colors = listOf(BasoNeonYellow, Color(0xFFFF9800)),
          start = Offset(w * 0.25f, h * 0.1f),
          end = Offset(w * 0.85f, h * 0.55f),
        ),
    )

    // Left vertical gold pillar of the 'B'
    drawRoundRect(
      brush =
        Brush.verticalGradient(
          colors = listOf(BasoNeonYellow, Color(0xFFE6A100)),
        ),
      topLeft = Offset(w * 0.14f, h * 0.08f),
      size = Size(w * 0.22f, h * 0.84f),
      cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
    )
  }
}

/**
 * Circular Freelancer Avatar matching the portrait icon in the reference top-bar and profile screen.
 */
@Composable
fun BasoFreelancerAvatar(
  size: Dp = 38.dp,
  showVerifiedBadge: Boolean = false,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
) {
  Box(
    contentAlignment = Alignment.BottomEnd,
    modifier =
      modifier
        .size(size)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val r = this.size.minDimension / 2f
      // Outer glowing ring (yellow-to-turquoise)
      drawCircle(
        brush = Brush.sweepGradient(listOf(BasoNeonYellow, BasoNeonTurquoise, BasoNeonYellow)),
        radius = r,
      )
      // Dark background inside ring
      drawCircle(
        color = Color(0xFF152638),
        radius = r * 0.89f,
      )
      // Stylized shoulders
      drawArc(
        color = Color(0xFF1D354D),
        startAngle = 25f,
        sweepAngle = 130f,
        useCenter = true,
        topLeft = Offset(r * 0.22f, r * 0.72f),
        size = Size(r * 1.56f, r * 1.3f),
      )
      // Stylized head
      drawCircle(
        color = Color(0xFFE8B998),
        radius = r * 0.34f,
        center = Offset(r, r * 0.78f),
      )
      // Stylized hair & beard
      drawArc(
        color = Color(0xFF1B1F26),
        startAngle = 190f,
        sweepAngle = 160f,
        useCenter = true,
        topLeft = Offset(r * 0.64f, r * 0.38f),
        size = Size(r * 0.72f, r * 0.55f),
      )
      drawArc(
        color = Color(0xFF232830),
        startAngle = 15f,
        sweepAngle = 150f,
        useCenter = false,
        topLeft = Offset(r * 0.70f, r * 0.68f),
        size = Size(r * 0.60f, r * 0.42f),
        style = Stroke(width = r * 0.10f),
      )
    }

    if (showVerifiedBadge) {
      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier
            .size(size * 0.28f)
            .clip(CircleShape)
            .background(Color(0xFF1294F2))
            .border(1.5.dp, BasoPrimaryBackground, CircleShape),
      ) {
        Text(
          text = "✓",
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
        )
      }
    }
  }
}

/**
 * Yellow-to-Turquoise Gradient Pill CTA Button matching the reference Splash, Portfolio, and Services screens.
 */
@Composable
fun BasoGradientPillButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  showArrow: Boolean = false,
  testTag: String = "baso_gradient_pill_button",
) {
  val shape = RoundedCornerShape(26.dp)
  Box(
    contentAlignment = Alignment.Center,
    modifier =
      modifier
        .fillMaxWidth()
        .heightIn(min = 52.dp)
        .clip(shape)
        .background(
          brush =
            Brush.horizontalGradient(
              colors = listOf(BasoNeonTurquoise, BasoNeonYellow, BasoNeonYellow),
            ),
        )
        .clickable(onClick = onClick)
        .padding(horizontal = 24.dp, vertical = 13.dp)
        .testTag(testTag),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = BasoPrimaryBackground,
      )
      if (showArrow) {
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = BasoPrimaryBackground,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

/**
 * Primary CTA Button using Neon Yellow (#FFD400) with high-contrast deep black text (#05090F).
 */
@Composable
fun BasoPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  leadingIcon: ImageVector? = null,
  enabled: Boolean = true,
  testTag: String = "baso_primary_button",
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(16.dp),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = BasoNeonYellow,
        contentColor = BasoPrimaryBackground,
        disabledContainerColor = BasoBorder,
        disabledContentColor = BasoSecondaryText,
      ),
    modifier = modifier.heightIn(min = 50.dp).testTag(testTag),
  ) {
    if (leadingIcon != null) {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
    )
  }
}

/**
 * Secondary Button using subtle dark surface (#111E2B), clean border (#243747 / #00F5D4), and soft white or turquoise accent.
 */
@Composable
fun BasoSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  leadingIcon: ImageVector? = null,
  highlightTurquoise: Boolean = false,
  testTag: String = "baso_secondary_button",
) {
  OutlinedButton(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    border =
      BorderStroke(
        width = 1.dp,
        color = if (highlightTurquoise) BasoNeonTurquoise.copy(alpha = 0.65f) else BasoBorder,
      ),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
    colors =
      ButtonDefaults.outlinedButtonColors(
        containerColor = BasoSurfaceCard,
        contentColor = if (highlightTurquoise) BasoNeonTurquoise else BasoPrimaryText,
      ),
    modifier = modifier.heightIn(min = 50.dp).testTag(testTag),
  ) {
    if (leadingIcon != null) {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        tint = if (highlightTurquoise) BasoNeonTurquoise else BasoPrimaryText,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge,
    )
  }
}

/**
 * Filter Pill Row matching the Projects, Clients, and Portfolio filter chips in the reference image.
 */
@Composable
fun BasoFilterChipsRow(
  filters: List<String>,
  selectedIndex: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier,
  tagPrefix: String = "home_filter_chip",
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
  ) {
    filters.forEachIndexed { index, label ->
      val selected = index == selectedIndex
      val shape = RoundedCornerShape(20.dp)
      Surface(
        color = if (selected) BasoCyanPill else BasoSurfaceCard,
        contentColor = if (selected) BasoPrimaryBackground else BasoPrimaryText,
        shape = shape,
        border =
          BorderStroke(
            width = 1.dp,
            color = if (selected) BasoNeonTurquoise else BasoBorder,
          ),
        modifier =
          Modifier
            .clip(shape)
            .clickable { onSelect(index) }
            .testTag("${tagPrefix}_$index"),
      ) {
        Text(
          text = label,
          style =
            MaterialTheme.typography.labelLarge.copy(
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            ),
          color = if (selected) BasoPrimaryBackground else BasoPrimaryText,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
      }
    }
  }
}

/**
 * Reusable BASO Search Box matching the reference image search bars.
 */
@Composable
fun BasoSearchBox(
  query: String,
  onQueryChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  testTag: String = "baso_search_box",
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    singleLine = true,
    shape = RoundedCornerShape(14.dp),
    placeholder = {
      Text(
        text = placeholder,
        style = MaterialTheme.typography.bodyMedium,
        color = BasoSecondaryText,
      )
    },
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = placeholder,
        tint = if (query.isNotEmpty()) BasoNeonTurquoise else BasoSecondaryText,
      )
    },
    trailingIcon =
      if (query.isNotEmpty()) {
        {
          IconButton(
            onClick = { onQueryChange("") },
            modifier = Modifier.testTag("baso_search_clear_button"),
          ) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear",
              tint = BasoSecondaryText,
            )
          }
        }
      } else {
        null
      },
    colors =
      OutlinedTextFieldDefaults.colors(
        focusedContainerColor = BasoSurfaceCard,
        unfocusedContainerColor = BasoSurfaceCard,
        focusedBorderColor = BasoNeonTurquoise,
        unfocusedBorderColor = BasoBorder,
        focusedTextColor = BasoPrimaryText,
        unfocusedTextColor = BasoPrimaryText,
        cursorColor = BasoNeonTurquoise,
      ),
    textStyle = MaterialTheme.typography.bodyLarge,
    modifier = modifier.fillMaxWidth().testTag(testTag),
  )
}

/**
 * Reusable BASO Input Field with validation message support.
 */
@Composable
fun BasoInputField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  placeholder: String,
  modifier: Modifier = Modifier,
  isError: Boolean = false,
  supportingText: String? = null,
  singleLine: Boolean = true,
  minLines: Int = 1,
  testTag: String = "baso_input_field",
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = label,
      style = MaterialTheme.typography.titleMedium,
      color = BasoPrimaryText,
      modifier = Modifier.padding(bottom = 8.dp),
    )
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      singleLine = singleLine,
      minLines = minLines,
      isError = isError,
      shape = RoundedCornerShape(14.dp),
      placeholder = {
        Text(
          text = placeholder,
          style = MaterialTheme.typography.bodyMedium,
          color = BasoSecondaryText,
        )
      },
      colors =
        OutlinedTextFieldDefaults.colors(
          focusedContainerColor = BasoSurfaceCard,
          unfocusedContainerColor = BasoSurfaceCard,
          errorContainerColor = BasoSurfaceCard,
          focusedBorderColor = BasoNeonTurquoise,
          unfocusedBorderColor = BasoBorder,
          errorBorderColor = BasoError,
          focusedTextColor = BasoPrimaryText,
          unfocusedTextColor = BasoPrimaryText,
          errorTextColor = BasoPrimaryText,
          cursorColor = BasoNeonTurquoise,
        ),
      textStyle = MaterialTheme.typography.bodyLarge,
      modifier = modifier.fillMaxWidth().testTag(testTag),
    )
    if (!supportingText.isNullOrBlank()) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = supportingText,
        style = MaterialTheme.typography.labelMedium,
        color = if (isError) BasoError else BasoSuccess,
        modifier = Modifier.testTag("baso_input_supporting_text"),
      )
    }
  }
}

/**
 * Reusable BASO Surface Card with consistent 16.dp corner radius and subtle border.
 */
@Composable
fun BasoCard(
  modifier: Modifier = Modifier,
  isSelected: Boolean = false,
  highContrastBorder: Boolean = false,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit,
) {
  val borderColor =
    when {
      isSelected -> BasoNeonYellow
      highContrastBorder -> BasoSecondaryText.copy(alpha = 0.55f)
      else -> BasoBorder
    }
  val shape = RoundedCornerShape(16.dp)
  Surface(
    color = BasoSurfaceCard,
    contentColor = BasoPrimaryText,
    shape = shape,
    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
    modifier =
      modifier
        .clip(shape)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
  ) {
    Box(modifier = Modifier.padding(16.dp)) { content() }
  }
}

/**
 * Project Card with colored icon box, status badge, and glowing cyan progress bar matching Screen 2 & Screen 3.
 */
@Composable
fun BasoProjectProgressCard(
  title: String,
  subtitle: String,
  statusText: String,
  statusColor: Color,
  statusBgColor: Color,
  progressPercent: Int,
  icon: ImageVector,
  iconBoxColor: Color,
  isSelected: Boolean = false,
  highContrastBorder: Boolean = false,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "baso_project_card",
) {
  BasoCard(
    isSelected = isSelected,
    highContrastBorder = highContrastBorder,
    onClick = onClick,
    modifier = modifier.fillMaxWidth().testTag(testTag),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      // Colored rounded icon container (matches reference left/right icon box)
      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
              Brush.linearGradient(
                listOf(iconBoxColor, iconBoxColor.copy(alpha = 0.75f)),
              ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp)),
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
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
              text = title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BasoPrimaryText,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = subtitle,
              style = MaterialTheme.typography.labelMedium,
              color = BasoSecondaryText,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }

          // Status badge pill
          Box(
            modifier =
              Modifier
                .clip(CircleShape)
                .background(statusBgColor)
                .border(1.dp, statusColor.copy(alpha = 0.55f), CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
          ) {
            Text(
              text = statusText,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = statusColor,
            )
          }
        }

        // Progress Bar + Percentage row
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
                  .fillMaxWidth((progressPercent.coerceIn(0, 100)) / 100f)
                  .fillMaxHeight()
                  .clip(CircleShape)
                  .background(
                    Brush.horizontalGradient(
                      colors = listOf(Color(0xFF1294F2), BasoNeonTurquoise),
                    ),
                  ),
            )
          }

          Text(
            text = "$progressPercent%",
            style = MaterialTheme.typography.labelMedium,
            color = BasoSecondaryText,
          )
        }
      }
    }
  }
}

/**
 * Reusable BASO List Item preserved for full compatibility.
 */
@Composable
fun BasoListItem(
  title: String,
  subtitle: String,
  icon: ImageVector,
  badgeText: String,
  isCompleted: Boolean = false,
  isSelected: Boolean = false,
  highContrastBorder: Boolean = false,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "baso_list_item",
) {
  BasoProjectProgressCard(
    title = title,
    subtitle = subtitle,
    statusText = badgeText,
    statusColor = if (isCompleted) BasoSuccess else BasoNeonYellow,
    statusBgColor = if (isCompleted) BasoSuccessSubtleBg else BasoYellowSubtleGlow,
    progressPercent = if (isCompleted) 100 else 75,
    icon = icon,
    iconBoxColor = if (isCompleted) Color(0xFFE5A900) else Color(0xFF1E6BFF),
    isSelected = isSelected,
    highContrastBorder = highContrastBorder,
    onClick = onClick,
    modifier = modifier,
    testTag = testTag,
  )
}

/**
 * Reusable System State Card for Loading, Empty, Success (#37D99A), and Error (#FF5B68) states.
 */
@Composable
fun BasoStateCard(
  state: BasoSystemState,
  strings: BasoStrings,
  onRetryOrAction: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val (accentColor, bgTint, title, body) =
    when (state) {
      BasoSystemState.LOADING ->
        StateVisuals(
          BasoNeonTurquoise,
          BasoTurquoiseSubtleGlow,
          strings.stateLoadingTitle,
          strings.stateLoadingBody,
        )
      BasoSystemState.EMPTY ->
        StateVisuals(
          BasoSecondaryText,
          BasoSecondaryBackground,
          strings.stateEmptyTitle,
          strings.stateEmptyBody,
        )
      BasoSystemState.SUCCESS ->
        StateVisuals(
          BasoSuccess,
          BasoSuccessSubtleBg,
          strings.stateSuccessTitle,
          strings.stateSuccessBody,
        )
      BasoSystemState.ERROR ->
        StateVisuals(
          BasoError,
          BasoErrorSubtleBg,
          strings.stateErrorTitle,
          strings.stateErrorBody,
        )
    }

  Surface(
    color = BasoSurfaceCard,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
    modifier = modifier.fillMaxWidth().testTag("baso_state_card_${state.name.lowercase()}"),
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(18.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(bgTint),
        ) {
          when (state) {
            BasoSystemState.LOADING ->
              CircularProgressIndicator(
                color = BasoNeonTurquoise,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(22.dp),
              )
            BasoSystemState.EMPTY ->
              Icon(
                imageVector = Icons.Outlined.Inbox,
                contentDescription = title,
                tint = BasoNeonTurquoise,
              )
            BasoSystemState.SUCCESS ->
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = title,
                tint = BasoSuccess,
              )
            BasoSystemState.ERROR ->
              Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = title,
                tint = BasoError,
              )
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = BasoPrimaryText,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = BasoSecondaryText,
          )
        }
      }

      if (state == BasoSystemState.ERROR) {
        BasoSecondaryButton(
          text = strings.stateRetryAction,
          onClick = onRetryOrAction,
          highlightTurquoise = true,
          testTag = "baso_state_retry_button",
        )
      }
    }
  }
}

private data class StateVisuals(
  val accentColor: Color,
  val bgTint: Color,
  val title: String,
  val body: String,
)

/**
 * Reusable Confirmation Dialog with BASO dark surface, subtle border, and Neon Yellow primary action.
 */
@Composable
fun BasoConfirmationDialog(
  title: String,
  message: String,
  confirmLabel: String,
  cancelLabel: String,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BasoSurfaceCard,
    titleContentColor = BasoPrimaryText,
    textContentColor = BasoSecondaryText,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = BasoPrimaryText,
      )
    },
    text = {
      Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = BasoSecondaryText,
      )
    },
    confirmButton = {
      BasoPrimaryButton(
        text = confirmLabel,
        onClick = onConfirm,
        testTag = "baso_dialog_confirm_button",
      )
    },
    dismissButton = {
      BasoSecondaryButton(
        text = cancelLabel,
        onClick = onDismiss,
        testTag = "baso_dialog_cancel_button",
      )
    },
    modifier = Modifier.border(1.dp, BasoBorder, RoundedCornerShape(20.dp)).testTag("baso_confirmation_dialog"),
  )
}

/**
 * Top Bar matching Screen 2 of the reference image:
 * BASO + Freelance Studio + 3D 'B' emblem on one side, Notification bell with red badge + Avatar + Language pill + Overflow menu on the other.
 */
@Composable
fun BasoTopBar(
  strings: BasoStrings,
  currentLanguage: AppLanguage,
  onLanguageCycle: () -> Unit,
  onMenuAction: (String) -> Unit,
  onAvatarClick: () -> Unit = {},
  onLogoClick: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  var menuExpanded by remember { mutableStateOf(false) }

  Surface(
    color = BasoPrimaryBackground,
    modifier = modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier =
        Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 10.dp),
    ) {
      // Brand Emblem + BASO / Freelance Studio
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        BasoBrandEmblem(
          size = 38.dp,
          modifier = Modifier.clickable(onClick = onLogoClick),
        )

        Column {
          Text(
            text = strings.appName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = BasoPrimaryText,
            modifier = Modifier.testTag("baso_header_title"),
          )
          Text(
            text = strings.studioSubtitle,
            style = MaterialTheme.typography.labelMedium,
            color = BasoSecondaryText,
          )
        }
      }

      // Notification Bell + Profile Avatar + Language Switcher + Overflow Menu
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        // Language pill button
        Surface(
          color = BasoSecondaryBackground,
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, BasoNeonTurquoise.copy(alpha = 0.45f)),
          modifier =
            Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable(onClick = onLanguageCycle)
              .testTag("baso_language_switcher_pill"),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.Language,
              contentDescription = currentLanguage.nativeLabel,
              tint = BasoNeonTurquoise,
              modifier = Modifier.size(15.dp),
            )
            Text(
              text = currentLanguage.shortBadge,
              style = MaterialTheme.typography.labelMedium,
              color = BasoPrimaryText,
            )
          }
        }

        // Notification bell with red dot indicator matching reference image
        Box(
          contentAlignment = Alignment.TopEnd,
          modifier =
            Modifier
              .size(38.dp)
              .clip(CircleShape)
              .clickable { onMenuAction("NOTIFICATIONS") }
              .padding(6.dp)
              .testTag("baso_notification_bell"),
        ) {
          Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = "Notifications",
            tint = BasoPrimaryText,
            modifier = Modifier.fillMaxSize(),
          )
          Box(
            modifier =
              Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(BasoError),
          )
        }

        // Circular Freelancer Avatar (tapping opens Profile Screen)
        BasoFreelancerAvatar(
          size = 36.dp,
          onClick = onAvatarClick,
          modifier = Modifier.testTag("baso_top_avatar"),
        )

        // Overflow menu for quick access to Splash, Services, Components, and Reset
        Box {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.size(34.dp).testTag("baso_overflow_menu_button"),
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = strings.menuMoreTitle,
              tint = BasoSecondaryText,
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
              text = {
                Text(
                  text = "BASO AI Studio • دروستکەری ئەپ",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = BasoNeonTurquoise,
                )
              },
              onClick = {
                menuExpanded = false
                onMenuAction("APP_BUILDER")
              },
              modifier = Modifier.testTag("baso_menu_app_builder"),
            )
            DropdownMenuItem(
              text = {
                Text(
                  text = "BASO AI",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = BasoNeonYellow,
                )
              },
              onClick = {
                menuExpanded = false
                onMenuAction("AI_HUB")
              },
              modifier = Modifier.testTag("baso_menu_ai_hub"),
            )
            DropdownMenuItem(
              text = {
                Text(
                  text = strings.servicesTitle,
                  style = MaterialTheme.typography.bodyMedium,
                  color = BasoPrimaryText,
                )
              },
              onClick = {
                menuExpanded = false
                onMenuAction("SERVICES")
              },
              modifier = Modifier.testTag("baso_menu_services"),
            )
            DropdownMenuItem(
              text = {
                Text(
                  text = strings.navComponents,
                  style = MaterialTheme.typography.bodyMedium,
                  color = BasoPrimaryText,
                )
              },
              onClick = {
                menuExpanded = false
                onMenuAction("COMPONENTS")
              },
              modifier = Modifier.testTag("baso_menu_components"),
            )
            DropdownMenuItem(
              text = {
                Text(
                  text = strings.menuAbout,
                  style = MaterialTheme.typography.bodyMedium,
                  color = BasoPrimaryText,
                )
              },
              onClick = {
                menuExpanded = false
                onMenuAction("SPLASH")
              },
              modifier = Modifier.testTag("baso_menu_about"),
            )
            DropdownMenuItem(
              text = {
                Text(
                  text = strings.menuHelp,
                  style = MaterialTheme.typography.bodyMedium,
                  color = BasoPrimaryText,
                )
              },
              onClick = {
                menuExpanded = false
                onMenuAction("REVIEW_CHAT")
              },
              modifier = Modifier.testTag("baso_menu_help"),
            )
            DropdownMenuItem(
              text = {
                Text(
                  text = strings.menuReset,
                  style = MaterialTheme.typography.bodyMedium,
                  color = BasoNeonTurquoise,
                )
              },
              onClick = {
                menuExpanded = false
                onMenuAction("RESET")
              },
              modifier = Modifier.testTag("baso_menu_reset"),
            )
          }
        }
      }
    }
  }
}

/**
 * 5-Tab Bottom Navigation Bar matching the reference image:
 * ماڵەوە (Home), پڕۆژەکان (Projects), کڕیاران (Clients), پۆرتفۆلیۆ (Portfolio), زیاتر (More/Settings)
 * Active item uses Neon Yellow (#FFD400) icon & label with subtle bottom glow bar.
 */
@Composable
fun BasoBottomNavigationBar(
  currentDestination: BasoDestination,
  onNavigate: (BasoDestination) -> Unit,
  strings: BasoStrings,
  modifier: Modifier = Modifier,
) {
  val mainTabs = remember { BasoDestination.entries.filter { it.showInMainBar } }

  Column(modifier = modifier.fillMaxWidth()) {
    Box(
      modifier =
        Modifier
          .fillMaxWidth()
          .height(1.dp)
          .background(BasoBorder),
    )
    NavigationBar(
      containerColor = BasoSecondaryBackground,
      contentColor = BasoPrimaryText,
      tonalElevation = 0.dp,
      windowInsets = WindowInsets.navigationBars,
      modifier = Modifier.testTag("baso_bottom_nav"),
    ) {
      mainTabs.forEach { destination ->
        val selected =
          destination == currentDestination ||
            (destination == BasoDestination.SETTINGS &&
              currentDestination in
                listOf(
                  BasoDestination.COMPONENTS,
                  BasoDestination.PROFILE,
                  BasoDestination.SERVICES,
                  BasoDestination.AI_HUB,
                  BasoDestination.APP_BUILDER,
                ))
        val label = destinationLabel(destination, strings)
        NavigationBarItem(
          selected = selected,
          onClick = { onNavigate(destination) },
          icon = {
            Icon(
              imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
              contentDescription = label,
            )
          },
          label = {
            Text(
              text = label,
              style =
                MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                ),
              maxLines = 1,
            )
          },
          colors =
            NavigationBarItemDefaults.colors(
              selectedIconColor = BasoNeonYellow,
              selectedTextColor = BasoNeonYellow,
              indicatorColor = BasoYellowSubtleGlow,
              unselectedIconColor = BasoSecondaryText,
              unselectedTextColor = BasoSecondaryText,
            ),
          modifier = Modifier.testTag("baso_nav_${destination.route}"),
        )
      }
    }
  }
}

/**
 * Responsive Navigation Rail for medium/expanded screens (tablets & foldables).
 */
@Composable
fun BasoSideNavigationRail(
  currentDestination: BasoDestination,
  onNavigate: (BasoDestination) -> Unit,
  strings: BasoStrings,
  modifier: Modifier = Modifier,
) {
  val mainTabs = remember { BasoDestination.entries.filter { it.showInMainBar } }

  Row(modifier = modifier.fillMaxHeight()) {
    NavigationRail(
      containerColor = BasoSecondaryBackground,
      contentColor = BasoPrimaryText,
      modifier = Modifier.fillMaxHeight().testTag("baso_side_rail"),
    ) {
      Spacer(modifier = Modifier.height(16.dp))
      mainTabs.forEach { destination ->
        val selected = destination == currentDestination
        val label = destinationLabel(destination, strings)
        NavigationRailItem(
          selected = selected,
          onClick = { onNavigate(destination) },
          icon = {
            Icon(
              imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
              contentDescription = label,
            )
          },
          label = {
            Text(
              text = label,
              style = MaterialTheme.typography.labelMedium,
            )
          },
          colors =
            NavigationRailItemDefaults.colors(
              selectedIconColor = BasoNeonYellow,
              selectedTextColor = BasoNeonYellow,
              indicatorColor = BasoYellowSubtleGlow,
              unselectedIconColor = BasoSecondaryText,
              unselectedTextColor = BasoSecondaryText,
            ),
          modifier = Modifier.padding(vertical = 6.dp).testTag("baso_rail_${destination.route}"),
        )
      }
    }
    Box(
      modifier =
        Modifier
          .fillMaxHeight()
          .width(1.dp)
          .background(BasoBorder),
    )
  }
}

fun destinationLabel(destination: BasoDestination, strings: BasoStrings): String =
  when (destination) {
    BasoDestination.HOME -> strings.navHome
    BasoDestination.PROJECTS -> strings.navProjects
    BasoDestination.CLIENTS -> strings.navClients
    BasoDestination.PORTFOLIO -> strings.navPortfolio
    BasoDestination.SETTINGS -> strings.navMore
    BasoDestination.SERVICES -> strings.servicesTitle
    BasoDestination.NEW_PROJECT -> strings.newProjectTitle
    BasoDestination.REVIEW_CHAT -> strings.reviewTitle
    BasoDestination.PROFILE -> strings.profileTitle
    BasoDestination.COMPONENTS -> strings.navComponents
    BasoDestination.AI_HUB -> "BASO AI"
    BasoDestination.APP_BUILDER -> "BASO AI Studio"
    BasoDestination.AI_SETTINGS -> "ڕێکخستنی AI"
    BasoDestination.SPLASH -> strings.appName
  }
