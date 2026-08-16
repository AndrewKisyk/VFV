package com.wrapper.composechat.feature.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.progress.VfvProgressCalculator
import com.wrapper.composechat.resources.*
import org.jetbrains.compose.resources.painterResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val BgColor = Color(0xFF09001F)
/** Full track ring behind the gradient progress (drawn in code, replaces PNG). */
private val RingTrackColor = Color(0xFF161B36)
private val EdgePurple = Color(0xFF42098F)
private val Magenta = Color(0xFFB53FFE)
// BgRingSize = outer view. The #161B36 ring is inset from the edge by [RingTrackInset].
// Gradient progress uses the *same* track centreline as that ring, drawn on top to cover it.
private val BgRingSize = 290.dp
private val StrokeWidth = 32.dp
private val RingTrackInset = 4.dp
private val ScaleSize = 208.dp
/** Width/height: 4dp less than the progress stroke; fits inside the arc band. */
private val ProgressThumbSize = (StrokeWidth - 4.dp).coerceAtLeast(1.dp)
private val TrophySize = 124.dp

/**
 * Dual-track circular progress gauge.
 *
 * * [requirementsProgress] — fraction of the **full** ring (0…0.75) swept **clockwise** from 12 oʼclock
 * * [recommendationsProgress] — fraction of the **full** ring (0…0.25) swept **counter-clockwise** from 12 oʼclock
 * * Sweep gradient on active arcs: #42098F → #B53FFE
 *
 * Layers (back → front):
 *   solid #09001F + #161B36 RingTrack → gradient arcs (same path, *covers* the track) →
 *   [ProgressScaleDial] (dashed double ring + 0/25/50/75) → thumb icons → center trophy.
 */
@Composable
fun MainDashboardRingGauge(
    recommendationsProgress: Float,
    requirementsProgress: Float,
    vfvAllDone: Boolean,
    modifier: Modifier = Modifier,
) {
    val reqSweepMax = VfvProgressCalculator.REQUIREMENTS_RING_MAX / 100f
    val recSweepMax = VfvProgressCalculator.RECOMMENDATIONS_RING_MAX / 100f
    val recP by animateFloatAsState(
        targetValue = recommendationsProgress.coerceIn(0f, recSweepMax),
        animationSpec = tween(800),
        label = "recRing",
    )
    val reqP by animateFloatAsState(
        targetValue = requirementsProgress.coerceIn(0f, reqSweepMax),
        animationSpec = tween(800),
        label = "reqRing",
    )
    val ringComplete = reqP >= reqSweepMax - 0.002f && recP >= recSweepMax - 0.002f
    val reqIconPainter = painterResource(Res.drawable.requirements_progress_icon)
    val recIconPainter = painterResource(Res.drawable.recommendations_progress_icon)
    val cupIdle = painterResource(Res.drawable.cup_medal)
    val cupActive = painterResource(Res.drawable.cup_medal_active)
    val cupPainter = if (vfvAllDone) cupActive else cupIdle
    val density = LocalDensity.current
    // Stroke centreline of the progress arc (matches [rRing] in Canvas; px).
    val rTrackPx = with(density) {
        (BgRingSize - StrokeWidth).toPx() * 0.5f - RingTrackInset.toPx()
    }
    // Icons on the stroke centreline — the coloured arc tip and the other marker sit on the same
    // path as the gradient (вістря прогресу), not on the band’s inner/outer skin.
    val rThumbPx = rTrackPx
    val halfThumbPx = with(density) { ProgressThumbSize.toPx() * 0.5f }
    // Recommendations: ccw from 12: shift the icon centre *forward* along the path by half a thumb
    // (arc length = r·Δθ  →  Δθ = halfW / r) so the icon slightly «leads» the drawn arc tip.
    val recIconLead = halfThumbPx / rTrackPx
    // 12 oʼclock = −90°: requirements sweeps CW (+), recommendations sweeps CCW (−).
    val recAngle = -PI * 0.5 - 2.0 * PI * recP
    val reqAngle = -PI * 0.5 + 2.0 * PI * reqP
    val recIconAngle = recAngle - recIconLead
    val recDX = (rThumbPx * cos(recIconAngle)).toFloat()
    val recDY = (rThumbPx * sin(recIconAngle)).toFloat()
    val reqDX = (rThumbPx * cos(reqAngle)).toFloat()
    val reqDY = (rThumbPx * sin(reqAngle)).toFloat()

    Box(
        modifier = Modifier.size(BgRingSize).then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        // 1) Backdrop + RingTrack (same geometry as the gradient pass below).
        Canvas(modifier = Modifier.size(BgRingSize)) {
            val d = this.size.minDimension
            val cc = this.center
            val sw = with(density) { StrokeWidth.toPx() }
            val inset = with(density) { RingTrackInset.toPx() }
            drawCircle(color = BgColor, radius = d * 0.5f, center = cc)
            val rRing = d * 0.5f - sw * 0.5f - inset
            drawCircle(
                color = RingTrackColor,
                radius = rRing,
                center = cc,
                style = Stroke(width = sw, cap = StrokeCap.Round),
            )
        }
        // 2) Active gradient: drawArc() fits an oval in [size]; the path’s radius is
        //   min(w,h)/2, so for a match with drawCircle(radius = rRing) we need
        //   size = 2 * rRing (not d − 2*inset — that made the arc radius too large by sw/2).
        Canvas(modifier = Modifier.size(BgRingSize)) {
            val d = this.size.minDimension
            val sw = with(density) { StrokeWidth.toPx() }
            val inset = with(density) { RingTrackInset.toPx() }
            val rRing = d * 0.5f - sw * 0.5f - inset
            val arcSize = 2f * rRing
            val topLeft = Offset((d - arcSize) * 0.5f, (d - arcSize) * 0.5f)
            val sweep = Brush.sweepGradient(
                listOf(EdgePurple, Magenta, EdgePurple),
                center = this.center,
            )
            if (recP > 0.0001f) {
                drawArc(
                    brush = sweep,
                    startAngle = -90f,
                    sweepAngle = -360f * recP,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = sw, cap = StrokeCap.Round),
                )
            }
            if (reqP > 0.0001f) {
                drawArc(
                    brush = sweep,
                    startAngle = -90f,
                    sweepAngle = 360f * reqP,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = sw, cap = StrokeCap.Round),
                )
            }
        }
        // 3) Dashed double ring + labels (replaces small_marks / large_marks SVGs + numerals).
        ProgressScaleDial(modifier = Modifier.size(ScaleSize))
        // 4) Thumbs on the stroke centreline — hidden when both arcs close the ring (75 + 25).
        if (!ringComplete) {
            Image(
                painter = recIconPainter,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(ProgressThumbSize)
                    .offset { IntOffset(recDX.roundToInt(), recDY.roundToInt()) },
            )
            Image(
                painter = reqIconPainter,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(ProgressThumbSize)
                    .offset { IntOffset(reqDX.roundToInt(), reqDY.roundToInt()) },
            )
        }
        // 5) Center trophy.
        Image(
            painter = cupPainter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(TrophySize),
        )
    }
}

/**
 * Alias matching the “CircularProgressView” name from the spec; same behaviour as
 * [MainDashboardRingGauge].
 */
@Composable
fun CircularProgressView(
    recommendationsProgress: Float,
    requirementsProgress: Float,
    vfvAllDone: Boolean,
    modifier: Modifier = Modifier,
) {
    MainDashboardRingGauge(
        recommendationsProgress = recommendationsProgress,
        requirementsProgress = requirementsProgress,
        vfvAllDone = vfvAllDone,
        modifier = modifier,
    )
}
