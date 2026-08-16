package com.wrapper.composechat.feature.maindashboard

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import com.wrapper.composechat.feature.home.LocalAnimatedVisibilityScope
import com.wrapper.composechat.feature.home.LocalSharedTransitionScope
import com.wrapper.composechat.feature.home.LocalVfvTransitionInteractor

private val VfvTitleBoundsSpring = spring<Rect>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMedium,
)

/** Topic title on [VfvRecommendationsScreen] ↔ chrome title on [VfvRecommendationDetailScreen]. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.recommendationTitleSharedBounds(topicId: String): Modifier =
    recommendationTextSharedBounds(
        key = "vfv_recommendation_title_$topicId",
        alignment = Alignment.Center,
    )

/** Topic subtitle on [VfvRecommendationsScreen] ↔ chrome subtitle on [VfvRecommendationDetailScreen]. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.recommendationSubtitleSharedBounds(
    topicId: String,
    alignment: Alignment = Alignment.Center,
): Modifier = recommendationTextSharedBounds(
    key = "vfv_recommendation_subtitle_$topicId",
    alignment = alignment,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Modifier.recommendationTextSharedBounds(
    key: String,
    alignment: Alignment,
): Modifier {
    val st = LocalSharedTransitionScope.current ?: return this
    val av = LocalAnimatedVisibilityScope.current ?: return this
    val interactor = LocalVfvTransitionInteractor.current
    if (interactor != null && !interactor.sharedElementsEnabled) {
        return this
    }
    return with(st) {
        then(
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = av,
                boundsTransform = { _, _ -> VfvTitleBoundsSpring },
                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(
                    contentScale = ContentScale.Fit,
                    alignment = alignment,
                ),
            ),
        )
    }
}
