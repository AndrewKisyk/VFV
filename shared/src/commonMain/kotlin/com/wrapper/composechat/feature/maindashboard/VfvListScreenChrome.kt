package com.wrapper.composechat.feature.maindashboard

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.platform.optionalBackdropBlur
import com.wrapper.composechat.ui.components.VfvChromeBackIconButton
import com.wrapper.composechat.ui.components.VfvChromeTrashIconButton
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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
 * Figma `Groups/requirements_screen/Content` measures its gradient against the 1479dp content box,
 * so the dark stop (40.037%) lands 592dp below the top edge however tall the screen is.
 */
private val VfvGroupsContentGradientEndY = 592.dp

internal fun Modifier.vfvGroupsContentGradient(): Modifier = drawBehind {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(VfvGroupsContentGradientTop, VfvGroupsContentGradientBottom),
            startY = 0f,
            endY = VfvGroupsContentGradientEndY.toPx(),
        ),
    )
}

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

/** Groups — blurred hero only (`IMG BG`); scrim lives on [VfvGroupsContentGradient]. */
@Composable
internal fun VfvGroupsScreenBackdrop(
    heroDrawable: DrawableResource,
    modifier: Modifier = Modifier,
) {
    VfvListScreenBackdrop(
        heroDrawable = heroDrawable,
        modifier = modifier,
        heroBlurRadiusDp = VfvGroupsBackdropHeroBlurRadiusDp,
        baseColor = VfvGroupsScreenBaseColor,
        heroOverscale = VfvGroupsBackdropHeroOverscale,
        heroOffsetY = VfvGroupsBackdropHeroOffsetY,
        contentScrimGradient = null,
    )
}

@Composable
internal fun VfvListChromeTopBar(
    title: String,
    onBack: () -> Unit,
    onTrash: () -> Unit,
    family: FontFamily?,
    modifier: Modifier = Modifier,
    titleFontSize: TextUnit = 15.sp,
    titleFontWeight: FontWeight = FontWeight.Medium,
    titleColor: Color = Color.White.copy(alpha = 0.92f),
    titleLetterSpacing: TextUnit = 1.2.sp,
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
            color = titleColor,
            fontSize = titleFontSize,
            fontWeight = titleFontWeight,
            fontFamily = family,
            letterSpacing = titleLetterSpacing,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        VfvChromeTrashIconButton(onClick = onTrash)
    }
}
