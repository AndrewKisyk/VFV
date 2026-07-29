package com.wrapper.composechat.ui.liquidglass

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kashif_e.backdrop.Backdrop
import com.kashif_e.backdrop.backdrops.LayerBackdrop
import com.kashif_e.backdrop.backdrops.layerBackdrop
import com.kashif_e.backdrop.backdrops.rememberLayerBackdrop
import com.kashif_e.backdrop.drawBackdrop
import com.kashif_e.backdrop.effects.blur
import com.wrapper.composechat.platform.isBackdropBlurAvailable
import com.wrapper.composechat.platform.isLiquidGlassAvailable
import com.wrapper.composechat.platform.optionalBackdropBlur

/** Screen-level backdrop for liquid-glass list row cards on groups / recommendations screens. */
val LocalVfvScreenBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }

@Composable
fun rememberVfvScreenBackdrop(): LayerBackdrop = rememberLayerBackdrop()

fun Modifier.vfvScreenLayerBackdrop(backdrop: LayerBackdrop): Modifier = layerBackdrop(backdrop)

/** Liquid-glass fill for list row cards. Must be used outside the [vfvScreenLayerBackdrop] subtree. */
@Composable
fun Modifier.vfvLiquidGlass(
    blurRadiusDp: Float,
    shape: Shape = RoundedCornerShape(20.dp),
    surfaceColor: Color = Color.White.copy(alpha = 0.19f),
    backdrop: Backdrop? = null,
): Modifier {
    val screenBackdrop = backdrop ?: LocalVfvScreenBackdrop.current
    if (screenBackdrop == null || !isLiquidGlassAvailable()) {
        return if (isBackdropBlurAvailable()) {
            background(surfaceColor, shape).optionalBackdropBlur(blurRadiusDp)
        } else {
            background(surfaceColor, shape)
        }
    }
    return drawBackdrop(
        backdrop = screenBackdrop,
        shape = { shape },
        effects = {
            blur(blurRadiusDp.dp.toPx())
        },
        onDrawSurface = { drawRect(surfaceColor) },
    )
}
