package com.wrapper.composechat.feature.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp

// From drawable XML (solid, stroke colors)
private val BigFillColor = Color(0x40CABCF8)
private val BigRingStrokeColor = Color(0x26CABCF8)
private val SmallRingStrokeColor = Color(0x40CABCF8)

// Design size ratio from bigfillcircle.xml: 215w x 190h, one corner 220dp
private const val BigFillW = 215f
private const val BigFillH = 190f
private const val BigFillTopRightDp = 220f
private const val BigNoneDesign = 550f
private const val BigNoneStrokeDp = 120f
private const val SmallDesign = 90f
private const val SmallStrokeDp = 16f

@Composable
fun BigFillTeardropDecoration(
    phA: Float,
    phB: Float,
    modifier: Modifier = Modifier,
) {
    val path = remember { Path() }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(BigFillW / BigFillH),
    ) {
        val w = size.width
        val h = size.height
        val tr0 = (BigFillTopRightDp / BigFillW) * w
        val trR = (tr0 * (1f + 0.11f * blobMagnitude(phA + 0.2f, 0.9f, 0f))).coerceIn(tr0 * 0.86f, tr0 * 1.16f)
        val dx = 10f * blobMagnitude(phA, 0.75f, 0.12f) + 5f * blobMagnitude(phB * 0.6f, 0.5f, 0f)
        val dy = 8.5f * blobMagnitude(phB + 0.15f, 0.8f, 0.1f) + 4f * blobMagnitude(phA * 0.5f, 0.55f, 0.2f)
        path.reset()
        path.addRoundRect(
            RoundRect(
                0f,
                0f,
                w,
                h,
                CornerRadius.Zero,
                CornerRadius(trR, trR),
                CornerRadius.Zero,
                CornerRadius.Zero,
            ),
        )
        translate(dx, dy) {
            drawPath(path, color = BigFillColor, style = Fill)
        }
    }
}

@Composable
fun RingOvalStrokeDecoration(
    phA: Float,
    phB: Float,
    large: Boolean,
    modifier: Modifier = Modifier,
) {
    val d = if (large) BigNoneDesign else SmallDesign
    val strokeD = if (large) BigNoneStrokeDp else SmallStrokeDp
    Canvas(modifier) {
        val s = kotlin.math.min(size.width, size.height)
        val tMult = if (large) 0.08f else 0.1f
        val stroke = (strokeD / d) * s * (1f + 0.1f * blobMagnitude(
            phA * (if (large) 0.7f else 1.1f),
            1.05f,
            tMult,
        ))
        val sc = 1f + 0.09f * blobMagnitude(phB + 0.3f, 0.85f, 0.05f)
        val left = (size.width - s * sc) * 0.5f
        val top = (size.height - s * sc) * 0.5f
        val dx = 8f * blobMagnitude(phA, 0.9f, 0.1f * (if (large) 0.5f else 1f)) + 4f * blobMagnitude((phA + phB) * 0.4f, 0.6f, 0f)
        val dy = 7.5f * blobMagnitude(phB, 0.88f, 0.12f) + 3.2f * blobMagnitude(phA * 0.4f, 0.7f, 0.15f)
        translate(dx, dy) {
            val color = if (large) BigRingStrokeColor else SmallRingStrokeColor
            val sw = stroke.coerceIn(0.5f, s * 0.48f)
            drawOval(
                color = color,
                topLeft = Offset(left, top),
                size = Size(s * sc, s * sc),
                style = Stroke(width = sw),
            )
        }
    }
}

/**
 * Pixels for the legacy bignonefill offset: top start with negative margins (see VfvAuthBackground).
 * Converted from dp in layout for a single [RingOvalStrokeDecoration] that mirrors old placement.
 */
object VfvAuthShapeOffsets {
    val bigRingNegativeStart = (-100).dp
    val bigRingSize = 280.dp
}
