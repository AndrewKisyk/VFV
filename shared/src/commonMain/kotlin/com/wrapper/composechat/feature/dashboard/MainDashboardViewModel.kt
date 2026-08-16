package com.wrapper.composechat.feature.dashboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainDashboardUiState(
    val mainPercent: Int = 0,
    val requirementsRingPoints: Int = 0,
    val recommendationsRingPoints: Int = 0,
    val vfvAllDone: Boolean = false,
    val isLoading: Boolean = true,
)

class MainDashboardViewModel(
    private val repository: MainDashboardRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(MainDashboardUiState())
    val state: StateFlow<MainDashboardUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            val p = repository.loadProgress()
            _state.update {
                MainDashboardUiState(
                    mainPercent = p.mainPercent.coerceIn(0, 100),
                    requirementsRingPoints = p.requirementsRingPoints.coerceIn(0, 75),
                    recommendationsRingPoints = p.recommendationsRingPoints.coerceIn(0, 25),
                    vfvAllDone = p.vfvAllDone,
                    isLoading = false,
                )
            }
        }
    }

    fun resetAllProgress() {
        scope.launch {
            repository.resetAllProgress()
            val p = repository.loadProgress()
            _state.update {
                MainDashboardUiState(
                    mainPercent = p.mainPercent.coerceIn(0, 100),
                    requirementsRingPoints = p.requirementsRingPoints.coerceIn(0, 75),
                    recommendationsRingPoints = p.recommendationsRingPoints.coerceIn(0, 25),
                    vfvAllDone = p.vfvAllDone,
                    isLoading = false,
                )
            }
        }
    }

    fun onCleared() {
        scope.cancel()
    }
}
