package com.wrapper.composechat.data.requirements

data class VfvRequirement(
    val id: Long = 0L,
    val sex: String,
    val group: Int,
    /** Display title; underscores from seed are replaced with spaces. */
    val name: String,
    val threshold: String,
    val imageKey: String,
    val isDone: Boolean,
)

data class VfvGroupProgress(
    val group: Int,
    val done: Int,
    val minRequired: Int,
)

fun minRequiredForGroup(group: Int): Int = if (group <= 3) 2 else 3
