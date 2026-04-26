package com.wrapper.composechat.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Glass-style panels: translucent frost + specular band + light border.
 * Works on dark chat backgrounds and auth (no true backdrop blur on low API).
 */
object Glassmorphism {
    val outline: Color = Color.White.copy(alpha = 0.22f)
    val outlineStrong: Color = Color.White.copy(alpha = 0.32f)
    val outlineSubtle: Color = Color.White.copy(alpha = 0.12f)

    /** List rows, form fields area */
    val listItemBrush: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0x45FFFFFF),
                0.35f to Color(0x22FFFFFF),
                1f to Color(0x12FFFFFF),
            ),
            start = Offset(0f, 0f),
            end = Offset(280f, 320f),
        )

    val composerBrush: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0x3DFFFFFF),
                0.5f to Color(0x1EFFFFFF),
                1f to Color(0x10FFFFFF),
            ),
            start = Offset(0f, 0f),
            end = Offset(0f, 200f),
        )

    val dockBrush: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0x52FFFFFF),
                0.4f to Color(0x28FFFFFF),
                1f to Color(0x18FFFFFF),
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 120f),
        )

    val formBrush: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0x4AFFFFFF),
                0.45f to Color(0x26FFFFFF),
                1f to Color(0x14FFFFFF),
            ),
            start = Offset(0f, 0f),
            end = Offset(220f, 400f),
        )

    val headerBarBrush: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0x3CFFFFFF),
                0.55f to Color(0x1EFFFFFF),
                1f to Color(0x12FFFFFF),
            ),
            start = Offset(0f, 0f),
            end = Offset(0f, 160f),
        )

    val searchPillBrush: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0x48FFFFFF),
                0.4f to Color(0x24FFFFFF),
                1f to Color(0x16FFFFFF),
            ),
            start = Offset(0f, 0f),
            end = Offset(200f, 50f),
        )

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

    fun bubbleFromMeBrush(): Brush = Brush.linearGradient(
        colorStops = arrayOf(
            0f to Color(0xF06C63FF),
            0.45f to Color(0xD86C63FF),
            1f to Color(0xB0554DDB),
        ),
        start = Offset(0f, 0f),
        end = Offset(180f, 220f),
    )

    fun bubbleOtherBrush(): Brush = Brush.linearGradient(
        colorStops = arrayOf(
            0f to Color(0x6A3E3C4E),
            0.5f to Color(0x453E3C4E),
            1f to Color(0x383E3C4E),
        ),
        start = Offset(0f, 0f),
        end = Offset(200f, 240f),
    )

    val bubbleFromMeTopSheen: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0x35FFFFFF),
                0.5f to Color(0x00FFFFFF),
            ),
            start = Offset(0f, 0f),
            end = Offset(0f, 80f),
        )

    val bubbleOtherTopSheen: Brush
        get() = Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color(0x22FFFFFF),
                1f to Color(0x00FFFFFF),
            ),
            start = Offset(0f, 0f),
            end = Offset(0f, 64f),
        )

    fun favoritesSwipeBrush(tint: Color = ChatColors.favorites): Brush = Brush.linearGradient(
        colorStops = arrayOf(
            0f to tint.copy(alpha = 0.88f),
            1f to tint.copy(alpha = 0.72f),
        ),
        start = Offset(0f, 0f),
        end = Offset(72f, 72f),
    )
}
