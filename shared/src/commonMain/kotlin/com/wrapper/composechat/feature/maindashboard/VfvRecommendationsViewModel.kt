package com.wrapper.composechat.feature.maindashboard

import com.wrapper.composechat.data.recommendations.RecommendationsRepository
import com.wrapper.composechat.data.recommendations.VfvRecommendationTopicIds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VfvRecommendationsScreenState(
    val readTopicIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
) {
    val totalCount: Int get() = VfvRecommendationTopicIds.size
    val totalRead: Int get() = readTopicIds.count { it in VfvRecommendationTopicIds }
}

class VfvRecommendationsViewModel(
    private val recommendationsRepository: RecommendationsRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(VfvRecommendationsScreenState())
    val state: StateFlow<VfvRecommendationsScreenState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            val readIds = recommendationsRepository.getReadTopicIds()
            _state.update {
                it.copy(
                    readTopicIds = readIds,
                    isLoading = false,
                )
            }
        }
    }

    fun resetAllReads() {
        scope.launch {
            recommendationsRepository.resetAllReads()
            refresh()
        }
    }

    fun markTopicRead(topicId: String) {
        scope.launch {
            recommendationsRepository.markTopicRead(topicId)
            refresh()
        }
    }

    fun onCleared() {
        scope.cancel()
    }
}
