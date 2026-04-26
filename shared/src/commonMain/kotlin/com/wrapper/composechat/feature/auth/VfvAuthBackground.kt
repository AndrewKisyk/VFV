package com.wrapper.composechat.feature.auth

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.lileyka
import com.wrapper.composechat.resources.title
import kotlin.math.PI
import org.jetbrains.compose.resources.painterResource

private val VfvGradientIndigo = Color(0xFF24076A)
private val VfvGradientViolet = Color(0xFF8A4AD3)

// Bottom → top: #24076A at bottom, #8A4AD3 at top; the crossfade layer swaps stops.
private val vfvGradientMainStops: Array<Pair<Float, Color>> = arrayOf(
    0f to VfvGradientIndigo,
    1f to VfvGradientViolet,
)

private val vfvGradientTitleStops: Array<Pair<Float, Color>> = arrayOf(
    0f to VfvGradientViolet,
    1f to VfvGradientIndigo,
)

// Smooth ease: gentle acceleration at the color-turnarounds (no hard velocity kinks).
private val VfvAuthGradientEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

/**
 * Full-screen VFV auth backdrop: two gradients cross-fading, [AlarmStyleVectorBackdrop], then
 * teardrop + ring shapes as animated [Path]/Canvas (replaces the old shape XMLs), and images.
 * Place behind form content; keep [content] in a new layer above.
 */
@Composable
fun VfvAuthBackground(
    modifier: Modifier = Modifier,
) {
    val inf = rememberInfiniteTransition(label = "vfvAuth")
    val blend by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14_000, easing = VfvAuthGradientEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gradientBlend",
    )
    val morphA by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "morphA",
    )
    val morphB by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9_200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "morphB",
    )
    val pulseT by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "twinkle",
    )
    val phA = 2f * PI.toFloat() * morphA
    val phB = 2f * PI.toFloat() * morphB

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val wPx = with(density) { maxWidth.toPx() }
        val hPx = with(density) { maxHeight.toPx() }
        // Vertical gradient: y = 0 (top) … y = h (bottom); 0% stop at bottom, 100% at top.
        val bottomCenter = Offset(wPx * 0.5f, hPx)
        val topCenter = Offset(wPx * 0.5f, 0f)
        val brush0 = remember(wPx, hPx) {
            Brush.linearGradient(
                *vfvGradientMainStops,
                start = bottomCenter,
                end = topCenter,
            )
        }
        val brush1 = remember(wPx, hPx) {
            Brush.linearGradient(
                *vfvGradientTitleStops,
                start = bottomCenter,
                end = topCenter,
            )
        }
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(brush0)
                    .alpha(1f - blend),
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(brush1)
                    .alpha(blend),
            )
        }
        // Alarm art–style vector glow + morphing paths (simplified; source SVG is not bundled at full size)
        AlarmStyleVectorBackdrop(
            phA = phA,
            phB = phB,
            pulseT = pulseT,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.92f),
        )

        BigFillTeardropDecoration(
            phA = phA,
            phB = phB,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
        )
        RingOvalStrokeDecoration(
            phA = phA,
            phB = phB,
            large = true,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    VfvAuthShapeOffsets.bigRingNegativeStart,
                    VfvAuthShapeOffsets.bigRingNegativeStart,
                )
                .size(VfvAuthShapeOffsets.bigRingSize),
        )
        RingOvalStrokeDecoration(
            phA = phA,
            phB = phB,
            large = false,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 132.dp, end = 8.dp)
                .size(90.dp),
        )
        RingOvalStrokeDecoration(
            phA = phA,
            phB = phB,
            large = false,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 44.dp)
                .size(90.dp),
        )
        Image(
            painter = painterResource(Res.drawable.lileyka),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = (-72).dp)
                .size(200.dp),
        )
    }
}

@Composable
fun VfvAuthTitleImage(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(Res.drawable.title),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .width(220.dp)
            .height(160.dp),
    )
}
