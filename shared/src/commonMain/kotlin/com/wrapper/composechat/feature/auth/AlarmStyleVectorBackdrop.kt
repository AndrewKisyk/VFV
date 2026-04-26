package com.wrapper.composechat.feature.auth

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Distilled from the Alarm.svg VFV art: the full file is not bundled (multi‑MB with embedded
 * pattern data). Recreates the three large soft radial orbs, stacked oval “clouds”, and a light
 * starfield.
 */
@Composable
fun AlarmStyleVectorBackdrop(
    phA: Float,
    phB: Float,
    pulseT: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val minD = minOf(w, h)
        val safePad = minD * 0.04f
        // SVG viewBox 0 0 585 1022 — normalized centers & radii
        fun pos(svgX: Float, svgY: Float) = Offset(w * (svgX / 585f), h * (svgY / 1022f))
        fun centerInView(o: Offset) = Offset(
            o.x.coerceIn(safePad, w - safePad),
            o.y.coerceIn(safePad, h - safePad),
        )
        val rad = { svgR: Float -> h * (svgR / 1022f) }

        // DrawScope: content is not clipped to bounds by default; keep all layers on-screen.
        clipRect(0f, 0f, w, h) {
        val c1 = pos(475f, 614f)
        val c2 = pos(112.5f, 479f)
        val c3 = pos(120f, 927f)
        // Same multi-sine “breath” (t=0) for radius scale
        val r1 = rad(235f) * orbRadiusScaleFromBlob(phA + 0.4f, 1f)
        val r2 = rad(206.5f) * orbRadiusScaleFromBlob(phB * 1.1f, 1.15f)
        val r3 = rad(199f) * orbRadiusScaleFromBlob((phA - phB * 0.7f) * 0.9f, 0.9f)
        // Drift: three-frequency mix
        val drift1 = orbDriftFromBlobWobble(phA, phB, index = 0, magnitude = 18f)
        val drift2 = orbDriftFromBlobWobble(phA, phB, index = 1, magnitude = 16f)
        val drift3 = orbDriftFromBlobWobble(phA, phB, index = 2, magnitude = 15f)

        // Primary design radials (from SVG paint1–3)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFD83AFF).copy(alpha = 0.28f),
                    Color(0xFFAC00FF).copy(alpha = 0f),
                ),
                center = c1 + drift1,
                radius = r1,
            ),
            radius = r1,
            center = c1 + drift1,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF3AE7FF).copy(alpha = 0.24f),
                    Color(0xFF00F0FF).copy(alpha = 0f),
                ),
                center = c2 + drift2,
                radius = r2,
            ),
            radius = r2,
            center = c2 + drift2,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF3AFA).copy(alpha = 0.2f),
                    Color(0xFFFA00FF).copy(alpha = 0f),
                ),
                center = c3 + drift3,
                radius = r3,
            ),
            radius = r3,
            center = c3 + drift3,
        )

        // Large soft “clouds”: stacked ovals — larger, drift scales with [minD]; [centerInView] + clip keeps them in-screen
        val b1c = centerInView(
            pos(420f, 360f) + Offset(
                minD * 0.02f * sin(phA * 0.3f),
                minD * 0.016f * cos(phA * 0.4f),
            ),
        )
        drawSoftCloudWithOvals(
            center = b1c, baseSize = minD * 0.4f, phA = phA, phB = phB, phase = phA + 0.4f, freq = 1f,
            color = Color.White, targetLayerAlpha = 0.045f,
        )
        val b2c = centerInView(
            pos(200f, 450f) + Offset(
                minD * 0.018f * sin(phB * 0.4f),
                minD * 0.014f * cos(phB * 0.5f),
            ),
        )
        drawSoftCloudWithOvals(
            center = b2c, baseSize = minD * 0.32f, phA = phA, phB = phB, phase = phB * 1.1f, freq = 1.15f,
            color = Color(0xFFE8D4FF), targetLayerAlpha = 0.04f,
        )
        val b3c = centerInView(
            pos(300f, 200f) + Offset(0f, minD * 0.02f * sin(phA * 0.8f)),
        )
        drawSoftCloudWithOvals(
            center = b3c, baseSize = minD * 0.26f, phA = phA, phB = phB, phase = phA - phB * 0.7f, freq = 0.9f,
            color = Color(0xFF8A4AD3), targetLayerAlpha = 0.05f,
        )

        // Procedural star field (mimic tiny white path specks) — phasing alpha only
        val seeds = intArrayOf(2, 5, 7, 9, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47)
        for (i in 0..14) {
            val sx = (0.1f + 0.78f * ((seeds[i] * 13) % 100) / 100f) * w
            val sy = (0.08f + 0.4f * ((seeds[i] * 7) % 100) / 100f) * h
            val tw = 0.3f + 0.4f * abs(sin(4f * PI.toFloat() * pulseT + i * 0.4f))
            val sPh = phA + i * 0.31f
            val sT = 0.45f + i * 0.12f
            // Radius & position use same blobMagnitude() as the large orbs, scaled for specks
            val baseR = 1.2f + 0.4f * (i % 3).toFloat()
            val rStar = baseR * (1f + blobMagnitude(sPh, 1.2f, sT) * 0.55f)
            val wobbleM = minD * 0.012f
            val dx = wobbleM * (blobMagnitude(sPh, 1.1f, sT) + 0.04f * sin(4f * phB + i * 0.2f))
            val dy = wobbleM * (blobMagnitude(sPh + 0.3f, 0.95f, sT * 0.8f) + 0.04f * sin(3f * phA + i * 0.15f)) +
                2.4f * sin(phA + i * 0.2f)
            drawCircle(
                color = Color.White.copy(0.12f * tw),
                radius = rStar,
                center = Offset(sx + dx, sy + dy),
            )
        }
        }
    }
}

/**
 * Puffy, slowly morphing haze: several overlapping ovals (ellipses) with different aspect ratios,
 * rotations, and per-layer motion — no polygon corners or spline knots.
 */
private fun DrawScope.drawSoftCloudWithOvals(
    center: Offset,
    baseSize: Float,
    phA: Float,
    phB: Float,
    phase: Float,
    freq: Float,
    color: Color,
    targetLayerAlpha: Float,
    layers: Int = 5,
) {
    val m = minOf(size.width, size.height)
    // Drift uses screen size so ovals don’t throw fixed px past edges on different densities.
    val driftA = m * 0.042f
    val a = (targetLayerAlpha / layers * 1.15f).coerceIn(0.004f, 0.08f)
    val rCap = m * 0.44f
    for (j in 0 until layers) {
        val t = 2f * PI.toFloat() * (j / layers.toFloat())
        val w1 = 1f + 0.12f * blobMagnitude(phase, freq, t)
        val w2 = 1f + 0.12f * blobMagnitude(phase, freq, t + 0.5f)
        // Slightly different radii = ellipses, not circles; cap so a single layer can’t cover > ~screen width
        val rx = (baseSize * (0.38f + 0.1f * j) * w1).coerceAtMost(rCap)
        val ry = (baseSize * (0.32f + 0.12f * j) * w2).coerceAtMost(rCap)
        val ox = baseSize * 0.12f * sin(phase * 0.45f + t) + driftA * 1.05f * sin(phA * 0.18f + j * 0.4f)
        val oy = baseSize * 0.1f * cos(phase * 0.38f - t * 0.7f) + driftA * 0.9f * cos(phB * 0.22f + j * 0.35f)
        val deg = 40f * sin(phase * 0.25f + j * 0.6f) + 24f * sin(phB * 0.15f + t) + 18f * j
        val c = color.copy(alpha = a)
        translate(center.x + ox, center.y + oy) {
            rotate(deg) {
                drawOval(
                    color = c,
                    topLeft = Offset(-rx, -ry),
                    size = Size(rx * 2f, ry * 2f),
                    style = Fill,
                )
            }
        }
    }
}

/** Radius multiplier for soft radial orbs: “same” wobble as blob, evaluated isotropically (t = 0). */
private fun orbRadiusScaleFromBlob(phase: Float, freq: Float): Float =
    1f + blobMagnitude(phase, freq, 0f) * 1.3f

/**
 * Drift from the same frequency mix as the blob, split across X/Y with index offsets.
 */
private fun orbDriftFromBlobWobble(phA: Float, phB: Float, index: Int, magnitude: Float): Offset {
    val k = 0.28f * magnitude
    val p0 = if (index == 0) phA else if (index == 1) phB else (phA + phB) * 0.5f
    val p1 = phA * 0.6f + phB * 0.4f + index * 0.25f
    val t0 = 0.15f * index
    val t1 = 0.2f * index
    return Offset(
        k * (blobMagnitude(p0, 0.85f, t0) + 0.5f * blobMagnitude(p1, 1.05f, t1 * 0.5f)),
        k * (blobMagnitude(p0 + 0.25f, 0.8f, t1) + 0.5f * blobMagnitude(p1, 0.95f, t0 * 0.6f + 0.1f)),
    )
}
