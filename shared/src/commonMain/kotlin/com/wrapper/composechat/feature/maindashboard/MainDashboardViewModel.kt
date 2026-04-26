package com.wrapper.composechat.feature.maindashboard

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
    val requirementsPercent: Int = 0,
    val recommendationsPercent: Int = 0,
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
                    requirementsPercent = p.requirementsPercent.coerceIn(0, 100),
                    recommendationsPercent = p.recommendationsPercent.coerceIn(0, 100),
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
