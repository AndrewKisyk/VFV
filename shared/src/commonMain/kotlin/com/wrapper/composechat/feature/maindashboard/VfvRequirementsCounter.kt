package com.wrapper.composechat.feature.maindashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.ui.liquidglass.vfvLiquidGlass
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily

private val CounterWidth = 67.dp
private val CounterHeight = 28.dp
private val CounterPadding = 4.dp
private val CounterCornerRadius = 14.dp
private val CounterSurfaceColor = Color.White.copy(alpha = 0.19f)
private val CounterBorderColor = Color.White.copy(alpha = 0.22f)
private val CounterRingColor = Color.White.copy(alpha = 0.92f)
private val CounterIconSize = 16.dp
private val CounterIconTextGap = 4.dp
private const val CounterBlurRadiusDp = VfvListCardCompleteBlurRadiusDp

/** Pill badge with liquid glass: ring icon + fraction text (e.g. `1/2`). Figma: 67×28, padding 4. */
@Composable
fun VfvRequirementsCounter(
    label: String,
    modifier: Modifier = Modifier,
) {
    val family = LocalVfvDisplayFontFamily.current
    val shape = RoundedCornerShape(CounterCornerRadius)

    Box(
        modifier = modifier
            .width(CounterWidth)
            .height(CounterHeight),
    ) {
        Box(
            Modifier
                .matchParentSize()
                .clip(shape)
                .vfvLiquidGlass(
                    blurRadiusDp = CounterBlurRadiusDp,
                    shape = shape,
                    surfaceColor = CounterSurfaceColor,
                ),
        )
        Box(
            Modifier
                .matchParentSize()
                .border(1.dp, CounterBorderColor, shape),
        )
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(CounterPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CounterIconTextGap),
        ) {
            RequirementsCounterRingIcon(Modifier.size(CounterIconSize))
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = family,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun RequirementsCounterRingIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val strokeWidth = 1.25.dp.toPx()
        drawCircle(
            color = CounterRingColor,
            radius = size.minDimension / 2f - strokeWidth / 2f,
            style = Stroke(width = strokeWidth),
        )
    }
}
