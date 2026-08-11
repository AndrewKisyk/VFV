package com.wrapper.composechat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val ProgressTrackFill = Color(0xFF28046B).copy(alpha = 0.6f)
private val ProgressTrackStroke = Color(0xFF28046B).copy(alpha = 0.4f)
private val ProgressActiveGradient = Brush.horizontalGradient(
    0f to Color(0xFFDF18FF),
    0.38f to Color(0xFF8800DC),
    1f to Color(0xFF6400EC),
)

/**
 * Shared splash / Groups progress pill — same look as the loading screen bar.
 *
 * Track `#28046B` at 60% + 1.5px stroke; active fill is 80% of track height with
 * magenta→purple horizontal gradient.
 */
@Composable
fun VfvGradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 8.dp,
) {
    val p = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight),
    ) {
        val trackBackgroundH = size.height
        val trackH = trackBackgroundH * 0.8f
        val trackW = size.width
        val r = trackBackgroundH * 0.5f
        drawRoundRect(
            color = ProgressTrackFill,
            size = Size(trackW, trackBackgroundH),
            cornerRadius = CornerRadius(r, r),
        )
        drawRoundRect(
            color = ProgressTrackStroke,
            size = Size(trackW, trackBackgroundH),
            cornerRadius = CornerRadius(r, r),
            style = Stroke(width = 1.5f),
        )
        val fillW = trackW * p
        if (fillW > 2.5f) {
            drawRoundRect(
                brush = ProgressActiveGradient,
                size = Size(fillW, trackH),
                cornerRadius = CornerRadius(r, r),
            )
        }
    }
}
