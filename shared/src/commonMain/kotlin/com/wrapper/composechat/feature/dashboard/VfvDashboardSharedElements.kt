package com.wrapper.composechat.feature.dashboard

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import com.wrapper.composechat.feature.home.LocalAnimatedVisibilityScope
import com.wrapper.composechat.feature.home.LocalSharedTransitionScope
import com.wrapper.composechat.feature.home.LocalVfvTransitionInteractor

/**
 * Hero bounds spec. Must settle within [com.wrapper.composechat.navigation.VfvNavTransitionDurationMs]:
 * a softer spring keeps running after the outgoing destination leaves composition, and the
 * unmatched element then jumps to its final bounds (the stutter seen on back).
 */
private val VfvHeroBoundsSpring = spring<Rect>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

/** Shared element key: requirements thumbnail on dashboard ↔ hero on [VfvGroupsScreen]. */
const val VfvRequirementsHeroSharedElementKey = "vfv_requirements_hero_image"

/** Shared element key: recommendations thumbnail on dashboard ↔ hero on [VfvRecommendationsScreen]. */
const val VfvRecommendationsHeroSharedElementKey = "vfv_recommendations_hero_image"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.requirementsHeroSharedElement(): Modifier =
    vfvDashboardHeroSharedElement(VfvRequirementsHeroSharedElementKey)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.recommendationsHeroSharedElement(): Modifier =
    vfvDashboardHeroSharedElement(VfvRecommendationsHeroSharedElementKey)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Modifier.vfvDashboardHeroSharedElement(key: String): Modifier {
    val st = LocalSharedTransitionScope.current ?: return this
    val av = LocalAnimatedVisibilityScope.current ?: return this
    val interactor = LocalVfvTransitionInteractor.current
    if (interactor != null && !interactor.sharedElementsEnabled) {
        // Drop the match so an in-flight morph cancels; re-enabled when transition goes inactive.
        return this
    }
    return with(st) {
        then(
            Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = av,
                boundsTransform = { _, _ -> VfvHeroBoundsSpring },
            ),
        )
    }
}
