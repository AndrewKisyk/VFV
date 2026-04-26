package com.wrapper.composechat.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import com.wrapper.composechat.platform.isBackdropBlurAvailable
import com.wrapper.composechat.platform.optionalBackdropBlur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Track: medium purple (left) → deep indigo (right), non-frosted swipe only.
private val SwipeTrackGradientStart = Color(0xFF8B5FD6)
private val SwipeTrackGradientEnd = Color(0xFF3D2A6B)
private val SwipeTextColor = Color(0xFFE8D4FF)
private val SwipeBorderColor = Color.White.copy(alpha = 0.45f)
private val SwipeFrostedBorder = Color.White.copy(alpha = 0.2f)
private val SwipeArrowTint = Color(0xFF7B4BC4)
private const val SwipeCompleteThreshold = 0.85f
/** Crossing this progress during drag triggers a “success” milestone haptic. */
private const val SwipeHalfwayProgress = 0.5f
private const val SwipeTextFadeFactor = 0.88f

/** Fully rounded pill (non-frosted). */
private val SwipePillTrackShape = RoundedCornerShape(50)

@Composable
fun SwipeToActionButton(
    text: String,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    height: Dp = 56.dp,
    trackHorizontalPadding: Dp = 4.dp,
    thumbSize: Dp = 48.dp,
    /**
     * If true, no solid gradient: near-transparent fill + blur (frost) and a light stroke,
     * aligned with VFV auth pill fields. Use with [trackCorner] = [com.wrapper.composechat.ui.theme.ChatDimens.vfvAuthPillCorner] and matching [height].
     */
    frostedGlass: Boolean = false,
    /** Corner radius for the track when [frostedGlass] is true; use shared auth pill dimen for age-field parity. */
    trackCorner: Dp = 28.dp,
    frostedBlur: Dp = 18.dp,
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current
    val trackShape = if (frostedGlass) RoundedCornerShape(trackCorner) else SwipePillTrackShape

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(height)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics(mergeDescendants = true) {
                        this.contentDescription = contentDescription
                    }
                } else {
                    Modifier
                }
            )
            .alpha(if (enabled) 1f else 0.45f),
    ) {
        val density = LocalDensity.current
        val wPx = with(density) { maxWidth.toPx() }
        val thumbPx = with(density) { thumbSize.toPx() }
        val trackPadPx = with(density) { trackHorizontalPadding.toPx() }
        val maxOffsetPx = (wPx - thumbPx - 2f * trackPadPx).coerceAtLeast(0f)
        val brush = remember(wPx) {
            Brush.horizontalGradient(
                colors = listOf(SwipeTrackGradientStart, SwipeTrackGradientEnd),
                endX = wPx,
            )
        }

        LaunchedEffect(maxOffsetPx) {
            offsetX.updateBounds(0f, maxOffsetPx)
            if (offsetX.value > maxOffsetPx) {
                offsetX.snapTo(maxOffsetPx.coerceAtLeast(0f))
            }
        }

        val progress = if (maxOffsetPx > 0f) offsetX.value / maxOffsetPx else 0f
        val labelAlpha = (1f - SwipeTextFadeFactor * progress).fastCoerceIn(0.15f, 1f)

        if (frostedGlass) {
            if (isBackdropBlurAvailable()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(trackShape)
                        .background(Color.White.copy(alpha = 0.07f), trackShape)
                        .then(Modifier.optionalBackdropBlur(frostedBlur.value)),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(trackShape)
                        .background(Color.White.copy(alpha = 0.12f), trackShape),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, SwipeFrostedBorder, trackShape),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = brush, shape = trackShape)
                    .background(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = trackShape,
                    )
                    .border(1.dp, SwipeBorderColor, trackShape),
            )
        }

        Text(
            text = text,
            color = SwipeTextColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = thumbSize + 12.dp)
                .alpha(labelAlpha),
        )

        if (maxOffsetPx > 0f) {
            val thumbX = with(density) { trackPadPx } + offsetX.value
            val thumbColor = if (frostedGlass) {
                Color.White.copy(alpha = 0.88f)
            } else {
                if (enabled) Color.White else Color.White.copy(alpha = 0.5f)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(thumbX.roundToInt(), 0) }
                    .size(thumbSize)
                    .then(
                        if (enabled) {
                            Modifier.pointerInput(maxOffsetPx, enabled) {
                                detectHorizontalDragGestures(
                                    onDragStart = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    onHorizontalDrag = { _, dragAmount ->
                                        if (!enabled) return@detectHorizontalDragGestures
                                        scope.launch {
                                            val prev = offsetX.value
                                            val next = (prev + dragAmount).fastCoerceIn(0f, maxOffsetPx)
                                            if (maxOffsetPx > 0f) {
                                                val prevP = prev / maxOffsetPx
                                                val newP = next / maxOffsetPx
                                                if (prevP < SwipeHalfwayProgress && newP >= SwipeHalfwayProgress) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                }
                                            }
                                            offsetX.snapTo(next)
                                        }
                                    },
                                    onDragEnd = {
                                        if (!enabled) return@detectHorizontalDragGestures
                                        scope.launch {
                                            if (offsetX.value >= maxOffsetPx * SwipeCompleteThreshold) {
                                                offsetX.animateTo(
                                                    maxOffsetPx,
                                                    spring(
                                                        dampingRatio = 0.9f,
                                                        stiffness = 400f,
                                                    ),
                                                )
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                onComplete()
                                                offsetX.animateTo(0f, spring(dampingRatio = 0.78f, stiffness = 380f))
                                            } else {
                                                if (offsetX.value > 0f) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                }
                                                offsetX.animateTo(
                                                    0f,
                                                    spring(dampingRatio = 0.78f, stiffness = 400f),
                                                )
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        if (!enabled) return@detectHorizontalDragGestures
                                        scope.launch {
                                            if (offsetX.value > 0f) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            offsetX.animateTo(0f, spring(dampingRatio = 0.78f, stiffness = 400f))
                                        }
                                    },
                                )
                            }
                        } else {
                            Modifier
                        }
                    )
                    .background(thumbColor, CircleShape)
                    .then(
                        if (frostedGlass) {
                            Modifier.border(1.dp, SwipeFrostedBorder, CircleShape)
                        } else {
                            Modifier
                        }
                    ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = SwipeArrowTint,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
