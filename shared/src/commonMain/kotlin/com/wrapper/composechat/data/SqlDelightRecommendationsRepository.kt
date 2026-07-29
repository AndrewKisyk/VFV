package com.wrapper.composechat.data

import com.wrapper.composechat.data.recommendations.RecommendationsRepository
import com.wrapper.composechat.data.recommendations.VfvRecommendationTopicIds
import com.wrapper.composechat.db.VfvSqlDatabase
import com.wrapper.composechat.progress.VfvProgressCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightRecommendationsRepository(
    private val database: VfvSqlDatabase,
) : RecommendationsRepository {

    override suspend fun getReadTopicIds(): Set<String> = withContext(Dispatchers.Default) {
        database.recommendationReadQueries
            .selectAllReadTopicIds()
            .executeAsList()
            .toSet()
    }

    override suspend fun markTopicRead(topicId: String) = withContext(Dispatchers.Default) {
        if (topicId !in VfvRecommendationTopicIds) return@withContext
        database.recommendationReadQueries.markTopicRead(topic_id = topicId)
    }

    override suspend fun resetAllReads() = withContext(Dispatchers.Default) {
        database.recommendationReadQueries.resetAllReads()
    }

    override suspend fun getReadPercent(): Int = withContext(Dispatchers.Default) {
        val readCount = database.recommendationReadQueries
            .countReadTopics()
            .executeAsOne()
            .toInt()
            .coerceIn(0, VfvRecommendationTopicIds.size)
        VfvProgressCalculator.getRecomStatus(VfvProgressCalculator.legacyRecomCount(readCount))
    }
}
