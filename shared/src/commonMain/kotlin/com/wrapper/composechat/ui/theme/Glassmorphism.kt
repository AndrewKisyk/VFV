package com.wrapper.composechat.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Glass-style panels: translucent frost + specular band + light border.
 */
object Glassmorphism {
    val outline: Color = Color.White.copy(alpha = 0.22f)
    val outlineStrong: Color = Color.White.copy(alpha = 0.32f)
    val outlineSubtle: Color = Color.White.copy(alpha = 0.12f)

    val primaryActionBrush: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0xF06C63FF),
                0.5f to Color(0xD26C63FF),
                1f to Color(0xB85A4FE0),
            ),
            start = Offset(0f, 0f),
            end = Offset(80f, 80f),
        )
}
