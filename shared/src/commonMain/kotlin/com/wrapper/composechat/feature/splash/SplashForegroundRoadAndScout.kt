package com.wrapper.composechat.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.wrapper.composechat.resources.*
import kotlin.math.max
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource

/**
 * Road placement shared with [SplashScreen] (e.g. align copy under the road image).
 * [roadBottomY] is the Y coordinate of the **bottom edge of the road** from the top of the screen in px.
 */
internal data class SplashForegroundRoadLayout(
    val roadTopY: Float,
    val roadHpx: Float,
    val bandH: Float,
) {
    val roadBottomY: Float get() = roadTopY + roadHpx
}

internal fun splashForegroundRoadLayout(
    screenHeightPx: Float,
): SplashForegroundRoadLayout {
    val h = max(screenHeightPx, 1f)
    val sceneBottomY = SplashSkyLayout.sceneImageBottomFromScreenTopPx(h)
    val roadHpx = (h * 0.2f).coerceIn(1f, h * 0.26f)
    val roadTopY = (sceneBottomY - roadHpx).coerceIn(0f, h - 1f)
    val bandH = (h - roadTopY).coerceAtLeast(1f)
    return SplashForegroundRoadLayout(roadTopY = roadTopY, roadHpx = roadHpx, bandH = bandH)
}

/**
 * [splash_foreground_road] and [SplashScoutCharacter]. The **bottom** of the road lines up with the **bottom** of
 * [splash_secondary_scene] ([SplashSkyLayout.sceneImageBottomFromScreenTopPx]).
 * The band from [roadTopY] to the screen bottom is for the scout. Tune the scene with [SplashSkyLayout] constants.
 */
@Composable
fun SplashForegroundRoadAndScout(
    screenWidthPx: Float,
    screenHeightPx: Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val w = max(screenWidthPx, 1f)
    val h = max(screenHeightPx, 1f)
    val layout = splashForegroundRoadLayout(h)
    val roadTopY = layout.roadTopY
    val roadHpx = layout.roadHpx
    val bandH = layout.bandH

    val roadH = with(density) { roadHpx.toDp() }
    val scoutH = with(density) { (h * 0.15f).coerceIn(1f, h * 0.21f).toDp() }
    val scoutW = with(density) { (w * 0.21f).toDp() }
    val road = painterResource(Res.drawable.splash_foreground_road)

    Box(
        modifier = modifier
            .offset { IntOffset(0, roadTopY.roundToInt()) }
            .fillMaxWidth()
            .height(with(density) { bandH.toDp() }),
    ) {
        Image(
            painter = road,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(roadH),
        )
        SplashScoutCharacter(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = with(density) { (w * 0.3f).toDp() },
                    bottom = with(density) { (h * 0.20f).toDp() },
                )
                .widthIn(max = scoutW)
                .height(scoutH),
        )
    }
}
