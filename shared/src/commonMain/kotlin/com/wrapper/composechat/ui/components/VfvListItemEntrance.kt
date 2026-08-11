package com.wrapper.composechat.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.feature.home.LocalVfvTransitionInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Same entrance as chats [RecentsListShared]: staggered fade + horizontal spring slide-in.
 *
 * Snaps to the final pose only when [LocalVfvTransitionInteractor] bumps the snap generation
 * *while this item is already on screen* — not when remounting after a prior navigation
 * (that previously skipped entrance on every revisit).
 */
@Composable
fun VfvListItemEntrance(
    index: Int,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean = true,
    staggerDelayMs: Long = 60L,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val startOffset = -maxWidth
        val alphaAnim = remember { Animatable(if (shouldAnimate) 0f else 1f) }
        val slideAnim = remember { Animatable(if (shouldAnimate) startOffset.value else 0f) }
        val interactor = LocalVfvTransitionInteractor.current
        val snapGeneration = interactor?.entranceSnapGeneration ?: 0
        // Baseline at first composition: ignore the current generation so revisit still animates in.
        val snapBaseline = remember { snapGeneration }

        if (shouldAnimate) {
            LaunchedEffect(Unit) {
                delay(index * staggerDelayMs)
                launch { alphaAnim.animateTo(1f, tween(400)) }
                launch { slideAnim.animateTo(0f, spring(0.8f, Spring.StiffnessLow)) }
            }
        }

        LaunchedEffect(snapGeneration) {
            if (snapGeneration <= snapBaseline) return@LaunchedEffect
            alphaAnim.snapTo(1f)
            slideAnim.snapTo(0f)
        }

        Box(
            modifier = Modifier
                .offset(x = slideAnim.value.dp)
                .alpha(alphaAnim.value),
        ) {
            content()
        }
    }
}
