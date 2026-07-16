package com.wrapper.composechat.feature.maindashboard

import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.data.requirements.RequirementsRepository
import com.wrapper.composechat.data.requirements.VfvRequirement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VfvGroupsScreenState(
    val donePerGroup: List<Int> = List(5) { 0 },
    val selectedGroupId: Int? = null,
    val sheetRequirements: List<VfvRequirement> = emptyList(),
    val sheetDoneCount: Int = 0,
    val sheetMinRequired: Int = 2,
    val isLoading: Boolean = true,
)

class VfvGroupsViewModel(
    private val requirementsRepository: RequirementsRepository,
    private val authRepository: AuthRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(VfvGroupsScreenState())
    val state: StateFlow<VfvGroupsScreenState> = _state.asStateFlow()

    init {
        refreshGroups()
    }

    fun refreshGroups() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            val profile = authRepository.getProfile()
            if (profile == null) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            requirementsRepository.ensureSeeded()
            val progress = requirementsRepository.getAllGroupsProgress(profile.sex, profile.age)
            _state.update {
                it.copy(
                    donePerGroup = progress.map { p -> p.done },
                    isLoading = false,
                )
            }
            val openGroup = _state.value.selectedGroupId
            if (openGroup != null) {
                loadSheet(openGroup, profile.sex, profile.age)
            }
        }
    }

    fun openGroup(groupId: Int) {
        scope.launch {
            val profile = authRepository.getProfile() ?: return@launch
            _state.update { it.copy(selectedGroupId = groupId) }
            loadSheet(groupId, profile.sex, profile.age)
        }
    }

    fun closeSheet() {
        _state.update {
            it.copy(
                selectedGroupId = null,
                sheetRequirements = emptyList(),
            )
        }
    }

    fun toggleRequirement(requirement: VfvRequirement) {
        scope.launch {
            requirementsRepository.setRequirementDone(requirement.id, !requirement.isDone)
            refreshGroups()
        }
    }

    fun resetAllRequirements() {
        scope.launch {
            requirementsRepository.resetAllProgress()
            refreshGroups()
        }
    }

    fun onCleared() {
        scope.cancel()
    }

    private suspend fun loadSheet(groupId: Int, sex: String, age: String) {
        val items = requirementsRepository.getRequirementsForGroup(groupId, sex, age)
        val progress = requirementsRepository.getGroupProgress(groupId, sex, age)
        _state.update {
            it.copy(
                sheetRequirements = items,
                sheetDoneCount = progress.done,
                sheetMinRequired = progress.minRequired,
            )
        }
    }
}
