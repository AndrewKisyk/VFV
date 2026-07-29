package com.wrapper.composechat.feature.maindashboard

import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.data.requirements.RequirementsRepository

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

private val GroupMaxPerGroup = listOf(2, 2, 2, 3, 3)

class DefaultMainDashboardRepository(
    private val requirementsRepository: RequirementsRepository,
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
        val progress = requirementsRepository.getAllGroupsProgress(profile.sex, profile.age)
        val totalDone = progress.zip(GroupMaxPerGroup).sumOf { (p, max) -> p.done.coerceIn(0, max) }
        val totalMax = GroupMaxPerGroup.sum()
        val requirementsPercent =
            if (totalMax > 0) (totalDone * 100 / totalMax).coerceIn(0, 100) else 0
        val vfvAllDone = progress.zip(GroupMaxPerGroup).all { (p, max) -> p.done >= max }
        // Recommendations progress is not persisted yet — keep placeholder until wired.
        val recommendationsPercent = 0
        val mainPercent = ((requirementsPercent + recommendationsPercent) / 2).coerceIn(0, 100)
        return MainDashboardProgress(
            mainPercent = mainPercent,
            requirementsPercent = requirementsPercent,
            recommendationsPercent = recommendationsPercent,
            vfvAllDone = vfvAllDone,
        )
    }
}
