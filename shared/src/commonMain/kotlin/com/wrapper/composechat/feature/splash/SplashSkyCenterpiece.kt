package com.wrapper.composechat.feature.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.wrapper.composechat.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

/**
 * Centred splash block: sun on canvas, soft purple glow, secondary scene bitmap, and entering clouds.
 * Place with [Modifier.align] [Alignment.Center] in a full-screen [Box] and size (e.g. [fillMaxWidth] + [height]).
 */
object SplashSkyLayout {
    /** Share of the shorter screen side used as min dimension inside the centrepiece. */
    const val BoxHeightFraction: Float = 1f
    const val BoxWidthFraction: Float = 1f
    const val SceneHeightInBox: Float = 0.55f
    /** Shift the secondary scene upward from bottom (fraction of centrepiece height); brings it above mid-box. */
    const val SceneLiftFromBottomFraction: Float = 0.30f
    const val CloudLeftSizeMinDFraction: Float = 0.5f
    const val CloudRightSizeMinDFraction: Float = 0.6f
    const val CloudMidSizeMinDFraction: Float = 0.38f
    /** 0 = top of scene image, 1 = bottom; anchors cloud top to this band. */
    const val CloudLeftTopInSceneFraction: Float = 0.12f
    const val CloudMidTopInSceneFraction: Float = 0.21f
    const val CloudRightTopInSceneFraction: Float = 0.20f
    /** Sun radius = min(box width, height) * this (matches original full-screen splash). */
    const val SunRMinDFraction: Float = 0.12f
    /** [drawSkyOrb]: horizontal center as fraction of box width. */
    const val SkyOrbCenterXFraction: Float = 0.5f
    /** Center Y = scene top + this * scene image height (positions orb in the scene band). */
    const val SkyOrbCenterInSceneFraction: Float = 0.5f
    /** Orb outer radius = min(box width, height) * this. */
    const val SkyOrbRadiusMinDFraction: Float = 0.52f
    const val SkyOrbIntroDurationMs: Int = 2_200
    const val SkyOrbBreathDurationMs: Int = 5_200
    /** Idle radius oscillates in [1 - this, 1 + this] (relative to base draw radius). */
    const val SkyOrbBreathAmplitude: Float = 0.06f

    /**
     * Y (px) from the top of the full screen to the **top** edge of `splash_secondary_scene` —
     * same as `sceneTopY` in [SplashSkyCenterpiece]: `boxH - liftPx - sceneHpx` in the centrepiece, plus
     * vertical centring offset of the centrepiece on the screen.
     */
    fun sceneImageTopFromScreenTopPx(screenHeightPx: Float): Float {
        val childH = screenHeightPx * BoxHeightFraction
        val yTop = (screenHeightPx - childH) / 2f
        val liftPx = childH * SceneLiftFromBottomFraction
        val sceneHpx = childH * SceneHeightInBox
        return yTop + (childH - liftPx - sceneHpx)
    }

    /**
     * Y (px) from the top of the full screen to the **bottom** edge of `splash_secondary_scene`
     * in [SplashSkyCenterpiece] (centrally placed with [BoxWidthFraction] / [BoxHeightFraction]).
     */
    fun sceneImageBottomFromScreenTopPx(screenHeightPx: Float): Float {
        val childH = screenHeightPx * BoxHeightFraction
        val yTop = (screenHeightPx - childH) / 2f
        val lift = childH * SceneLiftFromBottomFraction
        return yTop + childH - lift
    }
}

private val SunCore = Color(0xFFFFD4E3)
private val SunEdge = Color(0xFFFF9A5C)
/** Splash sky orb — user color #8800DC */
private val SkyOrbColor = Color(0xFF8800DC)

/**
 * Soft purple radial disc behind the scene. [alphaMul] scales gradient opacity (intro + idle pulse from caller).
 */
private fun DrawScope.drawSkyOrb(center: Offset, radius: Float, alphaMul: Float) {
    val a = alphaMul.coerceIn(0f, 1.25f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                SkyOrbColor.copy(alpha = 0.5f * a),
                SkyOrbColor.copy(alpha = 0.2f * a),
                SkyOrbColor.copy(alpha = 0f),
            ),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}

@Composable
fun SplashSkyCenterpiece(
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val leftEnter = remember { Animatable(1f) }
    val rightEnter = remember { Animatable(1f) }
    val midEnter = remember { Animatable(0f) }
    val midFromLeft = remember { Animatable(1f) }
    val sunRise = remember { Animatable(0f) }
    val skyOrbReveal = remember { Animatable(0f) }
    val skyOrbLoop = rememberInfiniteTransition(label = "skyOrb")
    val skyOrbBreathT by skyOrbLoop.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SplashSkyLayout.SkyOrbBreathDurationMs,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath",
    )

    LaunchedEffect(Unit) {
        coroutineScope {
            listOf(
                async {
                    skyOrbReveal.animateTo(
                        1f,
                        tween(
                            SplashSkyLayout.SkyOrbIntroDurationMs,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                },
                async { sunRise.animateTo(1f, tween(2_500, easing = FastOutSlowInEasing)) },
                async { leftEnter.animateTo(0f, tween(1_000, easing = FastOutSlowInEasing)) },
                async {
                    delay(60)
                    rightEnter.animateTo(0f, tween(1_000, easing = FastOutSlowInEasing))
                },
                async {
                    delay(100)
                    midEnter.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
                },
                async {
                    delay(100)
                    midFromLeft.animateTo(0f, tween(1_000, easing = FastOutSlowInEasing))
                },
            ).awaitAll()
        }
    }

    val cloudLeft = painterResource(Res.drawable.splash_claude_left)
    val cloudMid = painterResource(Res.drawable.splash_claude_middle)
    val cloudRight = painterResource(Res.drawable.splash_claude_right)
    val scene = painterResource(Res.drawable.splash_secondary_scene)

    BoxWithConstraints(modifier = modifier) {
        val boxW = with(density) { maxWidth.toPx().coerceAtLeast(1f) }
        val boxH = with(density) { maxHeight.toPx().coerceAtLeast(1f) }
        val minD = minOf(boxW, boxH)
        val sceneH = with(density) { (boxH * SplashSkyLayout.SceneHeightInBox).toDp() }
        val sceneHpx = boxH * SplashSkyLayout.SceneHeightInBox
        val liftPx = boxH * SplashSkyLayout.SceneLiftFromBottomFraction
        val sceneTopY = boxH - liftPx - sceneHpx
        val slidePx = boxW * 0.28f

        Canvas(Modifier.fillMaxSize()) {
            val liftScenePx = boxH * SplashSkyLayout.SceneLiftFromBottomFraction
            val sceneHCanvas = boxH * SplashSkyLayout.SceneHeightInBox
            val sceneTopYCanvas = boxH - liftScenePx - sceneHCanvas
            val orbCenter = Offset(
                boxW * SplashSkyLayout.SkyOrbCenterXFraction,
                sceneTopYCanvas + sceneHCanvas * SplashSkyLayout.SkyOrbCenterInSceneFraction,
            )
            val baseOrbR = minD * SplashSkyLayout.SkyOrbRadiusMinDFraction
            val breathScale = 1f + (skyOrbBreathT * 2f - 1f) * SplashSkyLayout.SkyOrbBreathAmplitude
            val introScale = 0.82f + 0.18f * skyOrbReveal.value
            val orbR = baseOrbR * breathScale * introScale
            val orbAlpha = skyOrbReveal.value * (0.88f + 0.12f * skyOrbBreathT)
            drawSkyOrb(orbCenter, orbR, alphaMul = orbAlpha)

            // Sun: 0f = low (behind scene/mountain), 1f = risen — [sunRise] 0→1 = upward
            val sunCx = boxW * 0.78f
            val startBelow = boxH * 0.58f
            val endY = boxH * 0.23f
            val sy = startBelow + (endY - startBelow) * sunRise.value
            val rSun = minD * SplashSkyLayout.SunRMinDFraction
            val sunOuterR = rSun * 0.7f
            val sunCenter = Offset(sunCx, sy)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SunCore,
                        SunEdge.copy(alpha = 0.75f),
                        Color(0xFFF6583E).copy(alpha = 0f),
                    ),
                    center = sunCenter,
                    radius = sunOuterR,
                ),
                radius = sunOuterR,
                center = sunCenter,
            )
            drawCircle(
                color = Color(0xFFFFF0E0).copy(alpha = 0.9f),
                radius = rSun * 0.55f,
                center = sunCenter,
            )
        }

        // Side clouds: same vertical band as [scene], behind it
        val yLeft = (sceneTopY + sceneHpx * SplashSkyLayout.CloudLeftTopInSceneFraction).roundToInt()
        val yMid = (sceneTopY + sceneHpx * SplashSkyLayout.CloudMidTopInSceneFraction).roundToInt()
        val yRight = (sceneTopY + sceneHpx * SplashSkyLayout.CloudRightTopInSceneFraction).roundToInt()
        Image(
            painter = cloudLeft,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(with(density) { (minD * SplashSkyLayout.CloudLeftSizeMinDFraction).toDp() })
                .offset {
                    IntOffset(
                        (-slidePx * leftEnter.value).roundToInt(),
                        yLeft,
                    )
                }
                .alpha(0.92f * midEnter.value + 0.08f)
                .clipToBounds(),
        )
        Image(
            painter = cloudRight,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(with(density) { (minD * SplashSkyLayout.CloudRightSizeMinDFraction).toDp() })
                .offset {
                    IntOffset(
                        (slidePx * rightEnter.value).roundToInt(),
                        yRight,
                    )
                }
                .alpha(0.92f * midEnter.value + 0.08f)
                .clipToBounds(),
        )

        Image(
            painter = scene,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(sceneH)
                .offset {
                    IntOffset(
                        0,
                        (-boxH * SplashSkyLayout.SceneLiftFromBottomFraction).roundToInt(),
                    )
                },
        )
        // Center cloud: in front of [scene], slides in from the left
        Image(
            painter = cloudMid,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(with(density) { (minD * SplashSkyLayout.CloudMidSizeMinDFraction).toDp() })
                .offset {
                    IntOffset(
                        (-slidePx * midFromLeft.value).roundToInt(),
                        yMid,
                    )
                }
                .alpha(0.92f * midEnter.value + 0.08f)
                .clipToBounds(),
        )
    }
}
