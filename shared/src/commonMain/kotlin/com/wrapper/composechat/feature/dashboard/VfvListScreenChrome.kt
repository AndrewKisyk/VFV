package com.wrapper.composechat.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.platform.optionalBackdropBlur
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.groups_requirements_img_bg
import com.wrapper.composechat.ui.components.VfvChromeBackIconButton
import com.wrapper.composechat.ui.components.VfvChromeTrashIconButton
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** Horizontal inset shared by list screens so chrome icons align with Groups. */
internal val VfvListChromeHorizontalPadding = 16.dp

internal const val VfvListBackdropBlurRadiusDp = 38f
internal const val VfvListCardCompleteBlurRadiusDp = 20f

/** Figma Groups `requirements_screen` → `IMG BG` (520×900, top −40, blur 45). */
internal const val VfvGroupsBackdropHeroBlurRadiusDp = 45f
private const val VfvGroupsBackdropHeroOverscale = 520f / 375f
private val VfvGroupsBackdropHeroOffsetY = (-40).dp
private val VfvGroupsScreenBaseColor = Color(0xFF010C21)

private val VfvListScrimGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0f to Color(0xFF3A0084).copy(alpha = 0.42f),
        0.40037f to Color(0xFF09001F).copy(alpha = 0.60f),
        1f to Color(0xFF09001F).copy(alpha = 0.60f),
    ),
)

private val VfvGroupsContentGradientTop = Color(0xFF3A0084).copy(alpha = 0.42f)
private val VfvGroupsContentGradientBottom = Color(0xFF09001F).copy(alpha = 0.60f)

/**
 * Figma `Content` fill:
 * `linear-gradient(to bottom, rgba(58,0,132,0.42), rgba(9,0,31,0.6) 40.037%)`
 * — dark 60% stop lands at ~40% of the screen, then holds to the bottom.
 */
private val VfvGroupsContentScrim = Brush.verticalGradient(
    colorStops = arrayOf(
        0f to VfvGroupsContentGradientTop,
        0.40037f to VfvGroupsContentGradientBottom,
        1f to VfvGroupsContentGradientBottom,
    ),
)

internal fun Modifier.vfvGroupsContentGradient(): Modifier =
    background(brush = VfvGroupsContentScrim)

@Composable
internal fun VfvListScreenBackdrop(
    heroDrawable: DrawableResource,
    modifier: Modifier = Modifier,
    blurHeroBackground: Boolean = true,
    heroBlurRadiusDp: Float = VfvListBackdropBlurRadiusDp,
    baseColor: Color = Color(0xFF09001F),
    heroOverscale: Float = 1f,
    heroOffsetY: Dp = 0.dp,
    contentScrimGradient: Brush? = VfvListScrimGradient,
) {
    Box(modifier = modifier.fillMaxSize().background(baseColor)) {
        Image(
            painter = painterResource(heroDrawable),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (heroOverscale != 1f || heroOffsetY != 0.dp) {
                        scaleX = heroOverscale
                        scaleY = heroOverscale
                        translationY = heroOffsetY.toPx()
                    }
                }
                .then(
                    if (blurHeroBackground) {
                        Modifier.optionalBackdropBlur(heroBlurRadiusDp)
                    } else {
                        Modifier
                    },
                ),
            contentScale = ContentScale.Crop,
        )
        if (contentScrimGradient != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = contentScrimGradient),
            )
        }
    }
}

/** Groups — Figma `IMG BG` + `Content` gradient scrim (inside layer backdrop for liquid glass). */
@Composable
internal fun VfvGroupsScreenBackdrop(
    modifier: Modifier = Modifier,
    heroDrawable: DrawableResource = Res.drawable.groups_requirements_img_bg,
) {
    Box(modifier = modifier.fillMaxSize().background(VfvGroupsScreenBaseColor)) {
        // Figma `requirements_screen/IMG BG`: 520×900, top −40, blur 45.
        Image(
            painter = painterResource(heroDrawable),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = VfvGroupsBackdropHeroOverscale
                    scaleY = VfvGroupsBackdropHeroOverscale
                    translationY = VfvGroupsBackdropHeroOffsetY.toPx()
                }
                .optionalBackdropBlur(VfvGroupsBackdropHeroBlurRadiusDp),
            contentScale = ContentScale.Crop,
        )
        // Figma `requirements_screen/Content` fill gradient (42% → 60% dark).
        Box(
            Modifier
                .fillMaxSize()
                .vfvGroupsContentGradient(),
        )
    }
}

@Composable
internal fun VfvListChromeTopBar(
    title: String,
    onBack: () -> Unit,
    onTrash: () -> Unit,
    family: FontFamily?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VfvChromeBackIconButton(onClick = onBack)
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            // Groups Figma chrome: 11sp Light, 60% white, no letter-spacing.
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Light,
            fontFamily = family,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        VfvChromeTrashIconButton(onClick = onTrash)
    }
}
