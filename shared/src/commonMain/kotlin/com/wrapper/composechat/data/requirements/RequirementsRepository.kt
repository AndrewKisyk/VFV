package com.wrapper.composechat.data.requirements

interface RequirementsRepository {
    suspend fun ensureSeeded()
    suspend fun getRequirementsForGroup(group: Int, sex: String, age: String): List<VfvRequirement>
    suspend fun getGroupProgress(group: Int, sex: String, age: String): VfvGroupProgress
    suspend fun getAllGroupsProgress(sex: String, age: String): List<VfvGroupProgress>
    suspend fun setRequirementDone(requirementId: Long, done: Boolean)
    suspend fun resetAllProgress()
}
