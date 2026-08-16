package com.wrapper.composechat.feature.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.feature.dashboard.drawCrossSparkle
import com.wrapper.composechat.resources.*
import com.wrapper.composechat.ui.components.VfvGradientProgressBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

private val SparkleLayout = listOf(
    Offset(0.12f, 0.08f),
    Offset(0.28f, 0.14f),
    Offset(0.45f, 0.07f),
    Offset(0.62f, 0.12f),
    Offset(0.88f, 0.09f),
    Offset(0.75f, 0.20f),
    Offset(0.20f, 0.24f),
    Offset(0.50f, 0.19f),
)

/**
 * VFV launch splash: gradient sky, starfield, centred [SplashSkyCenterpiece] (sun, glow, scene, clouds),
 * [SplashForegroundRoadAndScout], copy, and progress bar.
 */
@Composable
fun SplashScreen(
    loadProgress: Float,
    modifier: Modifier = Modifier,
    /** Applied after size (e.g. [androidx.compose.animation.SharedTransitionScope.sharedElement] for the title asset). */
    titleImageModifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val inf = rememberInfiniteTransition(label = "splash")
    val sp0 by inf.animateFloat(
        0.1f,
        0.95f,
        infiniteRepeatable(tween(1_800), RepeatMode.Reverse),
        "s0"
    )
    val sp1 by inf.animateFloat(
        0.12f,
        0.9f,
        infiniteRepeatable(tween(2_400), RepeatMode.Reverse),
        "s1"
    )
    val sp2 by inf.animateFloat(
        0.15f,
        0.88f,
        infiniteRepeatable(tween(1_500), RepeatMode.Reverse),
        "s2"
    )
    val sp3 by inf.animateFloat(
        0.1f,
        0.92f,
        infiniteRepeatable(tween(2_100), RepeatMode.Reverse),
        "s3"
    )
    val sp4 by inf.animateFloat(
        0.2f,
        0.9f,
        infiniteRepeatable(tween(1_700), RepeatMode.Reverse),
        "s4"
    )
    val sp5 by inf.animateFloat(
        0.1f,
        0.85f,
        infiniteRepeatable(tween(2_600), RepeatMode.Reverse),
        "s5"
    )
    val sp6 by inf.animateFloat(
        0.14f,
        0.94f,
        infiniteRepeatable(tween(1_950), RepeatMode.Reverse),
        "s6"
    )
    val sp7 by inf.animateFloat(
        0.1f,
        0.9f,
        infiniteRepeatable(tween(2_250), RepeatMode.Reverse),
        "s7"
    )
    val sparkles = floatArrayOf(sp0, sp1, sp2, sp3, sp4, sp5, sp6, sp7)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Color(0xFF0D0424)
            ),
    ) {
        val w = max(constraints.maxWidth, 1).toFloat()
        val h = max(constraints.maxHeight, 1).toFloat()
        val minD = minOf(w, h)

        Canvas(Modifier.fillMaxSize()) {
            clipRect(0f, 0f, w, h * 0.5f) {
                SparkleLayout.forEachIndexed { i, p ->
                    val sz = 8f + 3f * (i % 3)
                    drawCrossSparkle(
                        center = Offset(p.x * w, p.y * h),
                        size = sz,
                        alpha = sparkles.getOrElse(i) { 0.5f } * 0.9f,
                    )
                }
            }
        }

        SplashSkyCenterpiece(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(SplashSkyLayout.BoxWidthFraction)
                .fillMaxHeight()
            //.height(with(density) { (h * SplashSkyLayout.BoxHeightFraction).toDp() }),
        )

        SplashForegroundRoadAndScout(
            screenWidthPx = w,
            screenHeightPx = h,
        )

        val subSize = (13f * (minD / 400f).coerceIn(0.9f, 1.05f)).sp
        val titleImageMaxH = with(density) { (minD * 0.16f).coerceAtLeast(64f).toDp() }
        val roadLayout = splashForegroundRoadLayout(h)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = with(density) { roadLayout.roadBottomY.toDp() })
                .fillMaxHeight()
                .background(
                    Color(0xFF0C012B)
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.title),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = titleImageModifier
                    .fillMaxWidth(0.88f)
                    .height(titleImageMaxH),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(Res.string.splash_vfv_subtitle),
                color = Color.White.copy(alpha = 0.88f),
                fontSize = subSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        }

        val p = loadProgress.coerceIn(0f, 1f)
        VfvGradientProgressBar(
            progress = p,
            modifier = Modifier
                .fillMaxWidth(0.533f)
                .padding(bottom = with(density) { (h * 0.10f + 2f).toDp() })
                .align(Alignment.BottomCenter),
        )
    }
}
