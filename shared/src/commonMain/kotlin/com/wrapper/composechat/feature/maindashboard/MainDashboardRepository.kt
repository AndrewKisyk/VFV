package com.wrapper.composechat.feature.maindashboard

import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.data.recommendations.RecommendationsRepository
import com.wrapper.composechat.data.requirements.RequirementsRepository
import com.wrapper.composechat.progress.VfvProgressCalculator

/**
 * Loads progress for the VFV-style main screen (replaces [com.plstudio.a123.vfv.presenters.MainActivityPresenter] data).
 */
interface MainDashboardRepository {
    suspend fun loadProgress(): MainDashboardProgress
    suspend fun resetAllProgress()
}

data class MainDashboardProgress(
    /** Overall steps bar 0–100 (was [com.plstudio.a123.vfv.interfaces.MainContract.View.setMainProgress]). */
    val mainPercent: Int,
    /** Home ring CW arc: 0–75 points (75% of the track when complete). */
    val requirementsRingPoints: Int,
    /** Home ring CCW arc: 0–25 points (25% of the track when complete). */
    val recommendationsRingPoints: Int,
    /** "здано" label when [com.plstudio.a123.vfv.helpers.ProgressCulculator] deems VFV complete. */
    val vfvAllDone: Boolean = false,
)

class DefaultMainDashboardRepository(
    private val requirementsRepository: RequirementsRepository,
    private val recommendationsRepository: RecommendationsRepository,
    private val authRepository: AuthRepository,
) : MainDashboardRepository {
    override suspend fun loadProgress(): MainDashboardProgress {
        val profile = authRepository.getProfile()
            ?: return MainDashboardProgress(
                mainPercent = 0,
                requirementsRingPoints = 0,
                recommendationsRingPoints = 0,
            )
        requirementsRepository.ensureSeeded()
        val groupProgress = requirementsRepository.getAllGroupsProgress(profile.sex, profile.age)
        val totalDone = groupProgress.sumOf { it.done }
        val readTopicCount = recommendationsRepository.getReadTopicIds().size
        val legacyRecomCount = VfvProgressCalculator.legacyRecomCount(readTopicCount)
        val requirementsRingPoints = VfvProgressCalculator.requirementsRingPoints(
            doneCount = totalDone,
            sex = profile.sex,
            age = profile.age,
        )
        val recommendationsRingPoints = VfvProgressCalculator.recommendationsRingPoints(
            readTopicCount = readTopicCount,
            totalTopics = vfvRecommendationTopics.size,
        )
        val mainPercent = VfvProgressCalculator.computeAllRes(
            doneCount = totalDone,
            legacyRecomCount = legacyRecomCount,
            sex = profile.sex,
            age = profile.age,
        )
        val vfvAllDone = VfvProgressCalculator.compareAllVfvRes(
            groupProgress.map { it.done },
        )
        return MainDashboardProgress(
            mainPercent = mainPercent,
            requirementsRingPoints = requirementsRingPoints,
            recommendationsRingPoints = recommendationsRingPoints,
            vfvAllDone = vfvAllDone,
        )
    }

    override suspend fun resetAllProgress() {
        requirementsRepository.resetAllProgress()
        recommendationsRepository.resetAllReads()
    }
}
