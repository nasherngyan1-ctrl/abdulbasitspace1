package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val BasoColorScheme =
  darkColorScheme(
    primary = BasoNeonYellow,
    onPrimary = BasoPrimaryBackground,
    primaryContainer = BasoSurfaceCard,
    onPrimaryContainer = BasoNeonYellow,
    secondary = BasoNeonTurquoise,
    onSecondary = BasoPrimaryBackground,
    secondaryContainer = BasoSecondaryBackground,
    onSecondaryContainer = BasoNeonTurquoise,
    tertiary = BasoSuccess,
    onTertiary = BasoPrimaryBackground,
    background = BasoPrimaryBackground,
    onBackground = BasoPrimaryText,
    surface = BasoSurfaceCard,
    onSurface = BasoPrimaryText,
    surfaceVariant = BasoSecondaryBackground,
    onSurfaceVariant = BasoSecondaryText,
    outline = BasoBorder,
    outlineVariant = BasoBorder,
    error = BasoError,
    onError = BasoPrimaryBackground,
    errorContainer = BasoErrorSubtleBg,
    onErrorContainer = BasoError,
  )

val BasoShapes =
  Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  layoutDirection: LayoutDirection = LayoutDirection.Rtl,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    MaterialTheme(
      colorScheme = BasoColorScheme,
      typography = Typography,
      shapes = BasoShapes,
      content = content,
    )
  }
}
