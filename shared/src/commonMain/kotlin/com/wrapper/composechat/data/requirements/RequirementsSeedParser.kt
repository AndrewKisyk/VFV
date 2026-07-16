package com.wrapper.composechat.data.requirements

data class RequirementsSeedRow(
    val sex: String,
    val group: Int,
    val name: String,
    val a12: String,
    val a13: String,
    val a14: String,
    val a15: String,
    val a16: String,
    val a17: String,
    val imageKey: String,
)

object RequirementsSeedParser {

    fun parse(text: String): List<RequirementsSeedRow> {
        return text.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { parseLine(it) }
            .toList()
    }

    fun parseLine(line: String): RequirementsSeedRow? {
        val parts = line.split(Regex("\\s+"))
        if (parts.size < 10) return null
        val sex = parts[0]
        val group = parts[1].toIntOrNull() ?: return null
        val imageKey = parts.last()
        val ageValues = parts.subList(parts.size - 7, parts.size - 1)
        val name = parts.subList(2, parts.size - 7).joinToString(" ")
        return RequirementsSeedRow(
            sex = sex,
            group = group,
            name = name,
            a12 = ageValues[0],
            a13 = ageValues[1],
            a14 = ageValues[2],
            a15 = ageValues[3],
            a16 = ageValues[4],
            a17 = ageValues[5],
            imageKey = imageKey,
        )
    }

    fun thresholdForAge(row: RequirementsSeedRow, age: String): String? {
        val value = when (age) {
            "12" -> row.a12
            "13" -> row.a13
            "14" -> row.a14
            "15" -> row.a15
            "16" -> row.a16
            "17" -> row.a17
            else -> null
        } ?: return null
        return value.takeIf { it != "0" }
    }

    fun displayName(rawName: String): String = rawName.replace('_', ' ')
}
