package com.wrapper.composechat.data

import com.wrapper.composechat.data.requirements.RequirementsRepository
import com.wrapper.composechat.data.requirements.RequirementsSeedParser
import com.wrapper.composechat.data.requirements.VfvGroupProgress
import com.wrapper.composechat.data.requirements.VfvRequirement
import com.wrapper.composechat.data.requirements.loadRequirementsSeedText
import com.wrapper.composechat.data.requirements.minRequiredForGroup
import com.wrapper.composechat.db.Requirements
import com.wrapper.composechat.db.VfvSqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightRequirementsRepository(
    private val database: VfvSqlDatabase,
) : RequirementsRepository {

    override suspend fun ensureSeeded() = withContext(Dispatchers.Default) {
        if (database.requirementQueries.countRequirements().executeAsOne() > 0L) return@withContext
        val seedText = loadRequirementsSeedText()
        val rows = RequirementsSeedParser.parse(seedText)
        database.transaction {
            rows.forEach { row ->
                database.requirementQueries.insertRequirement(
                    sex = row.sex,
                    vfv_group = row.group.toLong(),
                    name = row.name,
                    a12 = row.a12,
                    a13 = row.a13,
                    a14 = row.a14,
                    a15 = row.a15,
                    a16 = row.a16,
                    a17 = row.a17,
                    image_key = row.imageKey,
                )
            }
        }
    }

    override suspend fun getRequirementsForGroup(
        group: Int,
        sex: String,
        age: String,
    ): List<VfvRequirement> = withContext(Dispatchers.Default) {
        ensureSeeded()
        database.requirementQueries
            .selectByGroup(sex = sex, vfv_group = group.toLong())
            .executeAsList()
            .mapNotNull { it.toDomain(age) }
    }

    override suspend fun getGroupProgress(
        group: Int,
        sex: String,
        age: String,
    ): VfvGroupProgress = withContext(Dispatchers.Default) {
        ensureSeeded()
        val applicable = database.requirementQueries
            .selectByGroup(sex = sex, vfv_group = group.toLong())
            .executeAsList()
            .mapNotNull { it.toDomain(age) }
        VfvGroupProgress(
            group = group,
            done = applicable.count { it.isDone },
            minRequired = minRequiredForGroup(group),
        )
    }

    override suspend fun getAllGroupsProgress(
        sex: String,
        age: String,
    ): List<VfvGroupProgress> = (1..5).map { getGroupProgress(it, sex, age) }

    override suspend fun setRequirementDone(requirementId: Long, done: Boolean) = withContext(Dispatchers.Default) {
        database.requirementQueries.updateDone(
            is_done = if (done) 1L else 0L,
            id = requirementId,
        )
    }

    override suspend fun resetAllProgress() = withContext(Dispatchers.Default) {
        database.requirementQueries.resetAllDone()
    }

    private fun Requirements.toDomain(age: String): VfvRequirement? {
        val threshold = when (age) {
            "12" -> a12
            "13" -> a13
            "14" -> a14
            "15" -> a15
            "16" -> a16
            "17" -> a17
            else -> return null
        }
        if (threshold == "0") return null
        return VfvRequirement(
            id = id,
            sex = sex,
            group = vfv_group.toInt(),
            name = RequirementsSeedParser.displayName(name),
            threshold = threshold,
            imageKey = image_key,
            isDone = is_done != 0L,
        )
    }
}
