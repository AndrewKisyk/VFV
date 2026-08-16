package com.wrapper.composechat.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shared visual tokens for auth and app chrome.
 */
object ChatColors {
    val primary = Color(0xFF6C63FF)
    val onContent = Color.White
    val onContentMuted = Color.White.copy(alpha = 0.6f)
    val onContentHint = Color.White.copy(alpha = 0.4f)
    val border = Color.White.copy(alpha = 0.08f)
}

object ChatDimens {
    val screenEdgeHorizontal = 20.dp
    val screenEdgeVertical = 16.dp
    val formFieldSpacing = 16.dp
    val formCardInnerPadding = 20.dp
    /** [VfvPillAgeTextField] outline. */
    val vfvAuthPillCorner = 28.dp
    val vfvAuthPillFieldHeight = 56.dp
}
