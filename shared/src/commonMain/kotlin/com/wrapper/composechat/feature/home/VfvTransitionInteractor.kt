package com.wrapper.composechat.feature.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * - [finishForNavigation]: snaps list-entrance only (safe for push/pop).
 * - [settleActiveSharedTransition]: drops shared-element matches so an in-flight hero morph
 *   jumps to its final layout (e.g. tapping a group while enter is still running). Restored
 *   as soon as [isTransitionActive] becomes false — does not stick across the next navigation.
 */
@Stable
class VfvTransitionInteractor {
    /**
     * When false, dashboard hero [sharedElement] modifiers are omitted so an active morph
     * cancels immediately. Always restored when the scope reports inactive.
     */
    var sharedElementsEnabled by mutableStateOf(true)
        private set

    /** Bumped to snap all [com.wrapper.composechat.ui.components.VfvListItemEntrance] to rest. */
    var entranceSnapGeneration by mutableIntStateOf(0)
        private set

    var isTransitionActive by mutableStateOf(false)
        private set

    fun finishForNavigation(action: () -> Unit) {
        entranceSnapGeneration++
        action()
    }

    /** Cancel an in-flight shared morph (no-op if none). */
    fun settleActiveSharedTransition() {
        if (!isTransitionActive) return
        sharedElementsEnabled = false
        entranceSnapGeneration++
    }

    fun onSharedTransitionActiveness(active: Boolean) {
        isTransitionActive = active
        if (!active) {
            sharedElementsEnabled = true
        }
    }
}

val LocalVfvTransitionInteractor = compositionLocalOf<VfvTransitionInteractor?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun rememberVfvTransitionInteractor(
    sharedTransitionScope: SharedTransitionScope,
): VfvTransitionInteractor {
    val interactor = remember { VfvTransitionInteractor() }
    val active = sharedTransitionScope.isTransitionActive
    LaunchedEffect(active) {
        interactor.onSharedTransitionActiveness(active)
    }
    return interactor
}

fun VfvTransitionInteractor?.navigateSettled(action: () -> Unit) {
    if (this == null) {
        action()
    } else {
        finishForNavigation(action)
    }
}
