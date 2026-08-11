package com.wrapper.composechat.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.wrapper.composechat.resources.*
import kotlin.math.min
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource

/**
 * Split-asset VFV runner: body + head. Nod: [Animatable] + [tween] with [LinearEasing] in degrees;
 * pivot at collar via [graphicsLayer] ([rotationZ] + [NodTransformOrigin]).
 * Head [offset] is derived in px from the scout box and asset intrinsics; see [computeScoutHeadOffsetPx]. Tune [ScoutLayout] if needed.
 *
 * **Чому “на одному девайсі ок, на іншому — ні”:** два `Image` + [ContentScale.Fit] у прямокутнику з *різним* aspect ratio на різних екранах
 * дають різні масштаби/letterbox для тіла та голови. Внутрішній бокс з [CombinedArtViewBoxWidthPx]/[CombinedArtViewBoxHeightPx] тримає однакові
 * пропорції, як **єдине** “полотно” (як viewBox у повному SVG). Інші варіанти: один мердж-артефакт без шва; `onGloballyPositioned` після
 * лайауту; anchor-точки в SVG (id у viewBox-координатах) і ті ж дроби в коді; один vector drawable на обидві частини з однаковим viewBox.
 */
object ScoutLayout {
    /**
     * Має збігатися з **спільним** design-space експорту [splash_scout_body] + [splash_scout_head], інакше піджени константи під фактичний кроп.
     */
    const val CombinedArtViewBoxWidthPx: Float = 160f
    const val CombinedArtViewBoxHeightPx: Float = 192f

    /** Head Y offset: fraction of box height, measured up from the bottom (used only as fallback if intrinsics are missing). */
    const val HeadLiftFromBottomFraction: Float = 0.78f

    /** Nudge the head back down in px as a fraction of min(width,height) to close the neck gap (fallback path). */
    const val NeckOverlapFineTuneMinDFraction: Float = 0.07f

    /** Optional extra nudge (× minD) on the geometry-based Y when intrinsics are used (tune the seam on real devices). */
    const val NeckYFineTuneByMinDFraction: Float = 0.02f

    /** Head width relative to the full composable width. */
    const val HeadWidthFraction: Float = 0.72f

    /**
     * Horizontal: shift the head **left** by this × **laid-out head width in px** (so it scales on any display).
     * A quarter of the head’s width (≈0.25) matches “зміщення на четвертину свого розміру”.
     */
    const val HeadShiftLeftByLaidOutHeadWidth: Float = 0.25f

    /**
     * In the **body** bitmap, Y of the neck seam as a fraction of body height, measured from the **top** of the art.
     * Tuned to match [splash_scout_body]; if your asset changes, adjust this and/or use fallback fractions.
     */
    const val BodyNeckYFromTopOfBodyArt: Float = 0.01f

    /** Rotation pivot: center-x, and y in 0..1 (collar / neck). */
    val NodTransformOrigin: TransformOrigin = TransformOrigin(0.5f, 0.90f)

    /** Nod: linear swing from +this° to -this° (see [LaunchedEffect] in [SplashScoutCharacter]). */
    const val NodMaxDegrees: Float = 4.5f

    /** Duration of each linear leg (0 → +Nod, +Nod → -Nod, or -Nod → 0) in ms. */
    const val NodLinearLegDurationMs: Int = 2_200
}

/**
 * Offsets the head in **pixels** (density already applied: pass sizes from the same [LocalDensity] as the box):
 *
 * * **X:** −[ScoutLayout.HeadShiftLeftByLaidOutHeadWidth] × (laid-out head width) so the art stays a fixed fraction
 *   of its own size on all displays (not the same as centering the head box on the body).
 * * **Y:** With valid body intrinsics, uses the same scale as [ContentScale.Fit] for the body in the full box, then
 *   places the **bottom of the head** (neck seam) at [ScoutLayout.BodyNeckYFromTopOfBodyArt] in body draw space.
 *   If intrinsics are missing or degenerate, falls back to [ScoutLayout.HeadLiftFromBottomFraction] + fine tune.
 *   (A future version may also use the head art’s [Size] to correct letterboxing; vertical alignment is **body**-driven.)
 */
private fun computeScoutHeadOffsetPx(
    boxW: Float,
    boxH: Float,
    minD: Float,
    bodyIntrinsic: Size,
): IntOffset {
    val headSlotW = boxW * ScoutLayout.HeadWidthFraction
    val shiftX = -(headSlotW * ScoutLayout.HeadShiftLeftByLaidOutHeadWidth).roundToInt()

    fun legacyVerticalDy(): Int {
        val headUp = boxH * ScoutLayout.HeadLiftFromBottomFraction
        val neckDown = minD * ScoutLayout.NeckOverlapFineTuneMinDFraction
        return (-headUp + neckDown).roundToInt()
    }

    val ibW = bodyIntrinsic.width
    val ibH = bodyIntrinsic.height
    if (ibW <= 0f || ibH <= 0f || !ibW.isFinite() || !ibH.isFinite()) {
        return IntOffset(shiftX, legacyVerticalDy())
    }

    val sBody = min(boxW / ibW, boxH / ibH)
    if (!sBody.isFinite() || sBody <= 0f) {
        return IntOffset(shiftX, legacyVerticalDy())
    }

    val drawnBodyH = ibH * sBody
    var yNeck = boxH - drawnBodyH
    yNeck += minD * ScoutLayout.NeckYFineTuneByMinDFraction
    yNeck = yNeck.coerceIn(0f, boxH)
    // Bottom-aligned child: y_bottom = boxH + headDy; want y_bottom = yNeck (neck line at bottom of head art)
    var headDy = (yNeck - boxH).roundToInt()
    if (headDy >= 0) {
        headDy = legacyVerticalDy()
    }
    return IntOffset(shiftX, headDy)
}

/**
 * Largest axis-aligned size with aspect [artW]:[artH] that fits inside [maxW]×[maxH] (same as scale-uniform "contain").
 */
private fun sizeWithFixedAspectInside(
    maxW: Float,
    maxH: Float,
    artW: Float,
    artH: Float,
): Pair<Float, Float> {
    if (maxW <= 0f || maxH <= 0f || artW <= 0f || artH <= 0f) return maxOf(1f, maxW) to maxOf(1f, maxH)
    val r = artW / artH
    val wIfTall = maxH * r
    return if (wIfTall <= maxW) {
        wIfTall to maxH
    } else {
        maxW to (maxW / r)
    }
}

@Composable
fun SplashScoutCharacter(
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val headRotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        val max = ScoutLayout.NodMaxDegrees
        val leg = tween<Float>(
            durationMillis = ScoutLayout.NodLinearLegDurationMs,
            easing = LinearEasing,
        )
        while (true) {
            headRotation.animateTo(max, leg)
            headRotation.animateTo(-max, leg)
            headRotation.animateTo(0f, leg)
        }
    }
    val headNodDeg = headRotation.value

    val body = painterResource(Res.drawable.splash_scout_body)
    val head = painterResource(Res.drawable.splash_scout_head)

    BoxWithConstraints(modifier = modifier) {
        val maxWpx = with(density) { maxWidth.toPx() }
        val maxHpx = with(density) { maxHeight.toPx() }
        val (boxWpx, boxHpx) = sizeWithFixedAspectInside(
            maxW = maxWpx,
            maxH = maxHpx,
            artW = ScoutLayout.CombinedArtViewBoxWidthPx,
            artH = ScoutLayout.CombinedArtViewBoxHeightPx,
        )
        val minD = minOf(boxWpx, boxHpx)
        val innerW = with(density) { boxWpx.toDp() }
        val innerH = with(density) { boxHpx.toDp() }
        val bi = body.intrinsicSize
        val headOffsetPx = remember(
            bi.width, bi.height,
            boxWpx, boxHpx, minD,
        ) {
            computeScoutHeadOffsetPx(
                boxW = boxWpx,
                boxH = boxHpx,
                minD = minD,
                bodyIntrinsic = bi,
            )
        }

        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .width(innerW)
                    .height(innerH),
            ) {
                Image(
                    painter = body,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                )
                Image(
                    painter = head,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(ScoutLayout.HeadWidthFraction)
                        .offset { headOffsetPx }
                        .graphicsLayer {
                            rotationZ = headNodDeg
                            transformOrigin = ScoutLayout.NodTransformOrigin
                        },
                )
            }
        }
    }
}
