package com.wrapper.composechat.feature.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.requirements_tick_circle
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.painterResource

private val CounterHeight = 28.dp
private val CounterShape = RoundedCornerShape(30.dp)
private val CounterSurfaceColor = Color.White.copy(alpha = 0.20f)
private val CounterRingBorder = Color.White.copy(alpha = 0.20f)
private val CompletedIconBorder = Color(0xFF96FD9F)
private val CompletedIconGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6BEE76), Color(0xFF2DA838)),
)

/**
 * Figma «Elements / Features»: pill 28dp, white 20% fill.
 * Incomplete → empty ring (`not_completed_icon`); complete → green (`completed_icon`).
 */
@Composable
fun VfvRequirementsCounter(
    label: String,
    modifier: Modifier = Modifier,
    completed: Boolean = false,
) {
    val family = LocalVfvDisplayFontFamily.current

    Row(
        modifier = modifier
            .height(CounterHeight)
            .clip(CounterShape)
            .background(CounterSurfaceColor)
            .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (completed) {
            RequirementsCompletedIcon()
        } else {
            RequirementsNotCompletedIcon()
        }
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = family,
            maxLines = 1,
        )
    }
}

/** Figma `not_completed_icon`: 20×20 empty circle, stroke white 20% 0.5. */
@Composable
private fun RequirementsNotCompletedIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(0.5.dp, CounterRingBorder, CircleShape),
    )
}

/** Figma `completed_icon`: 20×20 green gradient + tick 12 with padding 4. */
@Composable
private fun RequirementsCompletedIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(CompletedIconGradient, CircleShape)
            .border(0.5.dp, CompletedIconBorder, CircleShape)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.requirements_tick_circle),
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            contentScale = ContentScale.Fit,
        )
    }
}
