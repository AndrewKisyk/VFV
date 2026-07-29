package com.wrapper.composechat.feature.maindashboard

import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

internal data class VfvRecommendationTopic(
    val id: String,
    val titleRes: StringResource,
    val subtitleRes: StringResource,
    val articleRes: StringResource,
    val iconDrawable: DrawableResource,
)

internal val vfvRecommendationTopics = listOf(
    VfvRecommendationTopic(
        id = "cardio",
        titleRes = Res.string.recommendations_topic1_title,
        subtitleRes = Res.string.recommendations_topic1_subtitle,
        articleRes = Res.string.recommendation_article_cardio,
        iconDrawable = Res.drawable.heart,
    ),
    VfvRecommendationTopic(
        id = "jump",
        titleRes = Res.string.recommendations_topic2_title,
        subtitleRes = Res.string.recommendations_topic2_subtitle,
        articleRes = Res.string.recommendation_article_jump,
        iconDrawable = Res.drawable.long_jump,
    ),
    VfvRecommendationTopic(
        id = "running",
        titleRes = Res.string.recommendations_topic3_title,
        subtitleRes = Res.string.recommendations_topic3_subtitle,
        articleRes = Res.string.recommendation_article_running,
        iconDrawable = Res.drawable.`run`,
    ),
    VfvRecommendationTopic(
        id = "strength",
        titleRes = Res.string.recommendations_topic4_title,
        subtitleRes = Res.string.recommendations_topic4_subtitle,
        articleRes = Res.string.recommendation_article_strength,
        iconDrawable = Res.drawable.weight,
    ),
)

internal fun vfvRecommendationTopic(topicId: String): VfvRecommendationTopic? =
    vfvRecommendationTopics.firstOrNull { it.id == topicId }
