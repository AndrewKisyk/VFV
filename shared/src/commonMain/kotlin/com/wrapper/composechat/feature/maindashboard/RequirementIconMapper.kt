package com.wrapper.composechat.feature.maindashboard

import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.*
import org.jetbrains.compose.resources.DrawableResource

fun requirementIconDrawable(imageKey: String): DrawableResource? = when (imageKey) {
    "swimming" -> Res.drawable.swimming
    "g1_gymnastics" -> Res.drawable.g1_gymnastics
    "g2_diving" -> Res.drawable.g2_diving
    "g2_highjump" -> Res.drawable.g2_highjump
    "g2_longjump" -> Res.drawable.g2_longjump
    "g3_running" -> Res.drawable.g3_running
    "g3_sckeing" -> Res.drawable.g3_sckeing
    "g4_abs" -> Res.drawable.g4_abs
    "g4_lsit" -> Res.drawable.g4_lsit
    "g4_pullups" -> Res.drawable.g4_pullups
    "g4_pushups" -> Res.drawable.g4_pushups
    "g4_rope" -> Res.drawable.g4_rope
    "g4_throwing" -> Res.drawable.g4_throwing
    "g6_canoe" -> Res.drawable.g6_canoe
    else -> null
}
