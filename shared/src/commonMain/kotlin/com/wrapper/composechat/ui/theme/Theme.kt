package com.wrapper.composechat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.font.FontFamily

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
)

/**
 * SF UI Display family (same instance as in [VfvMultiplatformTheme]’s [MaterialTheme.typography]).
 */
val LocalVfvDisplayFontFamily = compositionLocalOf<FontFamily> { FontFamily.Default }

@Composable
fun VfvMultiplatformTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val displayFontFamily = vfvDisplayFontFamily()
    val typography = buildVfvDisplayTypography(displayFontFamily)
    CompositionLocalProvider(
        LocalVfvDisplayFontFamily provides displayFontFamily,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
        )
    }
}
