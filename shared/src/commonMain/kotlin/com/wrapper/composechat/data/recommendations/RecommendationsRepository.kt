package com.wrapper.composechat.data.recommendations

/** Topic ids from legacy [com.plstudio.a123.vfv.fragments.RecomendationListFragment]. */
val VfvRecommendationTopicIds = listOf("cardio", "jump", "running", "strength")

interface RecommendationsRepository {
    suspend fun getReadTopicIds(): Set<String>
    suspend fun markTopicRead(topicId: String)
    suspend fun resetAllReads()
    /** Dashboard ring gauge 0–100 (legacy [com.plstudio.a123.vfv.helpers.ProgressCulculator.getRecomStatus]). */
    suspend fun getReadPercent(): Int
}
