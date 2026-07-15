package com.wrapper.composechat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wrapper.composechat.platform.isBackdropBlurAvailable
import com.wrapper.composechat.platform.optionalBackdropBlur
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Matches dashboard card / scrim: #020725. */
private val ScrimBottomColor = Color(0xFF020725)

/**
 * @param backgroundSnapshot A bitmap of the screen **before** this overlay (from [com.wrapper.composechat.platform.rememberComposeViewBitmapCapture]).
 *   If null, falls back to blur-only tint (e.g. iOS until a capture is wired).
 * @param fullScreenBlurredSnapshot If true, draws [backgroundSnapshot] **full screen** (with blur) behind the sheet so the
 *   entire area looks like a blurred underlay (e.g. post–splash auth). If false (default, e.g. main dashboard), only the
 *   bottom “rising” strip uses the snapshot.
 * @param revealLiveBackdrop If true, the rising strip is a scrim only — blur the screen **behind** this overlay (e.g. live
 *   [MainDashboardScreen] blur driven by [onOpenProgressChange]) instead of drawing a blurred snapshot inside the strip.
 * @param onOpenProgressChange 0f = closed, 1f = fully open; updates while animating and while dragging.
 * @param swipeToDismissEnabled If false, vertical drag does not move or dismiss the sheet (e.g. required auth).
 */
@Composable
fun VfvGlassFullScreenBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundSnapshot: ImageBitmap? = null,
    fullScreenBlurredSnapshot: Boolean = false,
    revealLiveBackdrop: Boolean = false,
    blurBackgroundSnapshot: Boolean = true,
    backdropBlurRadiusDp: Float = 20f,
    onOpenProgressChange: ((Float) -> Unit)? = null,
    swipeToDismissEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val dismissTap = remember { MutableInteractionSource() }
    val density = LocalDensity.current
    val dismissDragThresholdPx = with(density) { 88.dp.toPx() }
    val scope = rememberCoroutineScope()
    var dragY by remember { mutableFloatStateOf(0f) }
    val onProgressUpdated = rememberUpdatedState(onOpenProgressChange)
    val onDismissUpdated = rememberUpdatedState(onDismissRequest)

    // Fade only: no slide on the whole overlay — the snapshot stays screen-fixed; only the dialog
    // moves via offset(dragY). SlideInVertically was moving the backdrop together with the sheet.
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(180)),
    ) {
        BoxWithConstraints(
            modifier
                .fillMaxSize()
                .zIndex(10_000f),
        ) {
            val maxHPx = with(density) { maxHeight.toPx() }
            if (maxHPx > 0f) {
            val onDismiss: () -> Unit = {
                scope.launch {
                    animate(
                        initialValue = dragY,
                        targetValue = maxHPx,
                        initialVelocity = 0f,
                        animationSpec = tween(280),
                    ) { v, _ ->
                        dragY = v
                    }
                    onDismissUpdated.value()
                }
            }

            // Dialog offset maxHPx → 0: same as strip height; snapshot does not use offset — only height grows.
            LaunchedEffect(visible, maxHPx) {
                if (!visible) return@LaunchedEffect
                dragY = maxHPx
                animate(
                    initialValue = maxHPx,
                    targetValue = 0f,
                    initialVelocity = 0f,
                    animationSpec = tween(320),
                ) { v, _ ->
                    dragY = v
                }
            }

            val openProgress = (1f - (dragY / maxHPx).coerceIn(0f, 1f))
            SideEffect {
                onProgressUpdated.value?.invoke(openProgress)
            }

            // Strip height = open amount. Snapshot is NOT in the offset(dragY) box — only content() rides the sheet.
            // ContentScale.Crop defaults to center — use BottomCenter so the capture stays fixed to the screen bottom
            // (like blurView + y in the XML sample, but without a third-party blur view).
            val visibleHeightPx = (maxHPx - dragY).coerceIn(0f, maxHPx)
            val visibleHeightDp = with(density) { visibleHeightPx.toDp() }
            val fullScreenH = maxHeight
            val snapshotBlurMod = if (blurBackgroundSnapshot) {
                Modifier.blur(backdropBlurRadiusDp.dp)
            } else {
                Modifier
            }
            val frostedFallbackBlurMod = if (isBackdropBlurAvailable()) {
                Modifier.optionalBackdropBlur(backdropBlurRadiusDp)
            } else {
                Modifier
            }
            val fullScreenSnapshotGradient = Brush.verticalGradient(
                0f to Color.Black.copy(alpha = 0.12f),
                0.45f to Color.Transparent,
                1f to ScrimBottomColor.copy(alpha = 0.75f),
            )

            Box(Modifier.fillMaxSize()) {
                if (fullScreenBlurredSnapshot) {
                    if (backgroundSnapshot != null) {
                        Image(
                            bitmap = backgroundSnapshot,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(0f)
                                .then(snapshotBlurMod),
                        )
                    } else {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .zIndex(0f)
                                .then(frostedFallbackBlurMod)
                                .background(ScrimBottomColor.copy(alpha = 0.85f)),
                        )
                    }
                    Box(
                        Modifier
                            .fillMaxSize()
                            .zIndex(0.5f)
                            .background(brush = fullScreenSnapshotGradient),
                    )
                }
                if (visibleHeightPx > 0.5f) {
                    val stripGradient = Brush.verticalGradient(
                        colorStops = arrayOf(0f to Color.Transparent, 1f to ScrimBottomColor),
                        startY = 0f,
                        endY = visibleHeightPx,
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(visibleHeightDp)
                            .clip(RectangleShape)
                            .zIndex(0.6f),
                    ) {
                        if (!fullScreenBlurredSnapshot && revealLiveBackdrop) {
                            Box(Modifier.matchParentSize().background(brush = stripGradient))
                        } else if (!fullScreenBlurredSnapshot && backgroundSnapshot != null) {
                            Image(
                                bitmap = backgroundSnapshot,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.BottomCenter,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(fullScreenH)
                                    .then(snapshotBlurMod),
                            )
                            Box(Modifier.matchParentSize().background(brush = stripGradient))
                        } else if (!fullScreenBlurredSnapshot) {
                            Box(
                                Modifier
                                    .matchParentSize()
                                    .then(frostedFallbackBlurMod)
                                    .background(brush = stripGradient),
                            )
                        } else {
                            Box(Modifier.matchParentSize().background(brush = stripGradient))
                        }
                    }
                }

                // Front: dialog follows finger; snapshot does not move with this box
                Box(
                    Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .offset { IntOffset(0, dragY.roundToInt()) }
                        .then(
                            if (swipeToDismissEnabled) {
                                Modifier.pointerInput(maxHPx, dismissDragThresholdPx) {
                                    detectVerticalDragGestures(
                                        onDragEnd = {
                                            scope.launch {
                                                if (dragY > dismissDragThresholdPx) {
                                                    onDismiss()
                                                } else {
                                                    animate(
                                                        initialValue = dragY,
                                                        targetValue = 0f,
                                                        initialVelocity = 0f,
                                                        animationSpec = tween(220),
                                                    ) { v, _ ->
                                                        dragY = v
                                                    }
                                                }
                                            }
                                        },
                                        onVerticalDrag = { _, delta ->
                                            dragY = (dragY + delta).coerceIn(0f, maxHPx)
                                        },
                                    )
                                }
                            } else {
                                Modifier
                            },
                        ),
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                    ) {
                        Spacer(
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = dismissTap,
                                    indication = null,
                                    onClick = onDismiss,
                                ),
                        )
                        content()
                    }
                }
            }
        }
    }
    }
}
