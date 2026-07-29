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
}

data class MainDashboardProgress(
    /** Overall steps bar 0–100 (was [com.plstudio.a123.vfv.interfaces.MainContract.View.setMainProgress]). */
    val mainPercent: Int,
    /** Requirements (todo) circular gauge 0–100. */
    val requirementsPercent: Int,
    /** Recommendations circular gauge 0–100. */
    val recommendationsPercent: Int,
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
                requirementsPercent = 0,
                recommendationsPercent = 0,
            )
        requirementsRepository.ensureSeeded()
        val groupProgress = requirementsRepository.getAllGroupsProgress(profile.sex, profile.age)
        val totalDone = groupProgress.sumOf { it.done }
        val legacyRecomCount = VfvProgressCalculator.legacyRecomCount(
            recommendationsRepository.getReadTopicIds().size,
        )
        val requirementsPercent = VfvProgressCalculator.getToDoRes(
            doneCount = totalDone,
            sex = profile.sex,
            age = profile.age,
        )
        val recommendationsPercent = VfvProgressCalculator.getRecomStatus(legacyRecomCount)
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
            requirementsPercent = requirementsPercent,
            recommendationsPercent = recommendationsPercent,
            vfvAllDone = vfvAllDone,
        )
    }
}
