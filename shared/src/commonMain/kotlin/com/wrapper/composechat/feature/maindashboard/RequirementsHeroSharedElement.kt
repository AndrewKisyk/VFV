package com.wrapper.composechat.feature.maindashboard

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wrapper.composechat.feature.home.LocalAnimatedVisibilityScope
import com.wrapper.composechat.feature.home.LocalSharedTransitionScope
import com.wrapper.composechat.feature.home.playfulSpring

/** Shared element key: requirements thumbnail on dashboard ↔ hero on [VfvGroupsScreen]. */
const val VfvRequirementsHeroSharedElementKey = "vfv_requirements_hero_image"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.requirementsHeroSharedElement(): Modifier {
    val st = LocalSharedTransitionScope.current ?: return this
    val av = LocalAnimatedVisibilityScope.current ?: return this
    return with(st) {
        then(
            Modifier.sharedElement(
                state = rememberSharedContentState(key = VfvRequirementsHeroSharedElementKey),
                animatedVisibilityScope = av,
                boundsTransform = { _, _ -> playfulSpring },
            ),
        )
    }
}
