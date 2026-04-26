package com.wrapper.composechat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.wrapper.composechat.resources.*
import org.jetbrains.compose.resources.Font as ResourceFont

/**
 * Build Material3 typography using SF UI Display. [vfvDisplayFontFamily] must be created in Composable
 * (Compose Resources font loading is Composable in CMP 1.7+).
 */
fun buildVfvDisplayTypography(family: FontFamily): Typography {
    fun TextStyle.withFamily(): TextStyle = copy(fontFamily = family)
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.withFamily(),
        displayMedium = base.displayMedium.withFamily(),
        displaySmall = base.displaySmall.withFamily(),
        headlineLarge = base.headlineLarge.withFamily(),
        headlineMedium = base.headlineMedium.withFamily(),
        headlineSmall = base.headlineSmall.withFamily(),
        titleLarge = base.titleLarge.withFamily(),
        titleMedium = base.titleMedium.withFamily(),
        titleSmall = base.titleSmall.withFamily(),
        bodyLarge = base.bodyLarge.withFamily(),
        bodyMedium = base.bodyMedium.withFamily(),
        bodySmall = base.bodySmall.withFamily(),
        labelLarge = base.labelLarge.withFamily(),
        labelMedium = base.labelMedium.withFamily(),
        labelSmall = base.labelSmall.withFamily(),
    )
}

@Composable
fun vfvDisplayFontFamily(): FontFamily = FontFamily(
    ResourceFont(Res.font.sfui_display_light, FontWeight.Thin, FontStyle.Normal),
    ResourceFont(Res.font.sfui_display_light, FontWeight.ExtraLight, FontStyle.Normal),
    ResourceFont(Res.font.sfui_display_light, FontWeight.Light, FontStyle.Normal),
    ResourceFont(Res.font.sfui_display_regular, FontWeight.Normal, FontStyle.Normal),
    ResourceFont(Res.font.sfui_display_regular, FontWeight.Medium, FontStyle.Normal),
    ResourceFont(Res.font.sfui_display_bold, FontWeight.SemiBold, FontStyle.Normal),
    ResourceFont(Res.font.sfui_display_bold, FontWeight.Bold, FontStyle.Normal),
    ResourceFont(Res.font.sfui_display_bold, FontWeight.ExtraBold, FontStyle.Normal),
    ResourceFont(Res.font.sfui_display_bold, FontWeight.Black, FontStyle.Normal),
)
