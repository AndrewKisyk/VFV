package com.wrapper.composechat.data.requirements

import com.wrapper.composechat.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
suspend fun loadRequirementsSeedText(): String {
    val bytes = Res.readBytes("files/requirements")
    return bytes.decodeToString()
}
