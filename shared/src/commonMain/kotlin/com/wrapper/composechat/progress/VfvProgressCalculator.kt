package com.wrapper.composechat.progress

import kotlin.math.roundToInt

/**
 * Port of legacy [com.plstudio.a123.vfv.helpers.ProgressCulculator] + [com.plstudio.a123.vfv.model.User].
 */
object VfvProgressCalculator {

    fun userMin(sex: String): Int = if (sex == "m") 12 else 10

    fun userMax(sex: String, age: String): Int {
        val a = age.toIntOrNull() ?: 16
        return when (sex) {
            "m" -> if (a < 16) 20 else 22
            "f" -> if (a < 15) 19 else 20
            else -> 20
        }
    }

    /** Requirements ring gauge — legacy [ProgressCulculator.getToDoRes]. */
    fun getToDoRes(doneCount: Int, sex: String, age: String): Int {
        val max = userMax(sex, age).coerceAtLeast(1)
        return doneCount * 100 / max
    }

    /**
     * Recommendations ring gauge — legacy [ProgressCulculator.getRecomStatus].
     *
     * @param legacyRecomCount Same as legacy `reading.txt` token count (`split(" ").length`), i.e. [readTopicCount] + 1.
     */
    fun getRecomStatus(legacyRecomCount: Int): Int = legacyRecomCount * 25 - 25

    /** Overall steps bar — legacy [ProgressCulculator.computAllRes]. */
    fun computeAllRes(
        doneCount: Int,
        legacyRecomCount: Int,
        sex: String,
        age: String,
    ): Int {
        val min = userMin(sex)
        val max = userMax(sex, age)
        var requirResult = computeTodoResultForMainBar(doneCount, min, max)
        var recomResult = getRecomStatus(legacyRecomCount) / 5
        var allRes = requirResult + recomResult
        if (sex == "f" && allRes == 98) allRes += 2
        if (sex == "m" && max == 20 && allRes == 96) allRes += 4
        return allRes
    }

    /** Legacy [ProgressCulculator.compareAllVfvRes] — per-group done counts for groups 1…5. */
    fun compareAllVfvRes(groupsDone: List<Int>): Boolean {
        if (groupsDone.size < 5) return false
        if (groupsDone[0] < 2) return false
        if (groupsDone[1] < 2) return false
        if (groupsDone[2] < 2) return false
        if (groupsDone[3] < 3) return false
        if (groupsDone[4] < 3) return false
        return true
    }

    /** Maps persisted read-topic count to legacy `reading.txt` split length. */
    fun legacyRecomCount(readTopicCount: Int): Int = readTopicCount + 1

    private fun computeTodoResultForMainBar(doneCount: Int, min: Int, max: Int): Int {
        var res = doneCount
        if (res <= min) {
            res *= (60f / min).roundToInt()
        } else {
            val span = (max - min).coerceAtLeast(1)
            res = (res - min) * (20f / span).roundToInt()
            res += 60
        }
        return res
    }
}
