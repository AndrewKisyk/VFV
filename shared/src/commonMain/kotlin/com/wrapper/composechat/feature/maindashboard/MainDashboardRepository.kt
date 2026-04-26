package com.wrapper.composechat.feature.maindashboard

/**
 * Loads progress for the VFV-style main screen (replaces [com.plstudio.a123.vfv.presenters.MainActivityPresenter] data).
 * Android can later swap in [RequirementsLab], [FileIO], etc.
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

class DefaultMainDashboardRepository : MainDashboardRepository {
    override suspend fun loadProgress(): MainDashboardProgress = MainDashboardProgress(
        mainPercent = 35,
        requirementsPercent = 43,
        recommendationsPercent = 18,
        vfvAllDone = false,
    )
}
