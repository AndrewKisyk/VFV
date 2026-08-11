package com.wrapper.composechat.progress

/**
 * Port of legacy [com.plstudio.a123.vfv.helpers.ProgressCulculator] + [com.plstudio.a123.vfv.model.User].
 */
object VfvProgressCalculator {

    /** Home ring: requirements arc sweeps up to 75% of the circle (0–75 points). */
    const val REQUIREMENTS_RING_MAX = 75

    /** Home ring: recommendations arc sweeps up to 25% of the circle (0–25 points). */
    const val RECOMMENDATIONS_RING_MAX = 25

    fun userMin(sex: String): Int = if (sex == "m") 12 else 10

    fun userMax(sex: String, age: String): Int {
        val a = age.toIntOrNull() ?: 16
        return when (sex) {
            "m" -> if (a < 16) 20 else 22
            "f" -> if (a < 15) 19 else 20
            else -> 20
        }
    }

    /**
     * Points for the home dual-ring gauge (CW arc). Scales legacy main-bar todo math to [REQUIREMENTS_RING_MAX].
     */
    fun requirementsRingPoints(doneCount: Int, sex: String, age: String): Int {
        val min = userMin(sex)
        val max = userMax(sex, age)
        val current = computeTodoResultForMainBar(doneCount, min, max)
        val atFull = computeTodoResultForMainBar(max, min, max)
        if (atFull <= 0) return 0
        return (current * REQUIREMENTS_RING_MAX / atFull).coerceIn(0, REQUIREMENTS_RING_MAX)
    }

    /** Points for the home dual-ring gauge (CCW arc), spread evenly across [totalTopics] articles. */
    fun recommendationsRingPoints(readTopicCount: Int, totalTopics: Int): Int {
        if (totalTopics <= 0) return 0
        return (readTopicCount * RECOMMENDATIONS_RING_MAX / totalTopics)
            .coerceIn(0, RECOMMENDATIONS_RING_MAX)
    }

    /** Legacy circular progress on requirement nav card — [ProgressCulculator.getToDoRes]. */
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
        // Legacy divides ints, so the multipliers truncate (m/max=20 → 20/8 = 2, not 2.5).
        // The `allRes == 96` / `allRes == 98` top-ups below only line up with that truncation.
        if (res <= min) {
            res *= 60 / min.coerceAtLeast(1)
        } else {
            val span = (max - min).coerceAtLeast(1)
            res = (res - min) * (20 / span)
            res += 60
        }
        return res
    }
}
