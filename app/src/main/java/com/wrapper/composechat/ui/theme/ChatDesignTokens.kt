package com.wrapper.composechat.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Visual tokens for the VFV / chat experience (shared across chat lists, auth, and chrome).
 */
object ChatColors {
    val screen = Color(0xFF1C1B2A)
    val card = Color(0xFF2B2939)
    val header = Color(0xFF2B2939)
    val avatar = Color(0xFF3E3C4E)
    val primary = Color(0xFF6C63FF)
    val online = Color(0xFF2ED3B7)
    val favorites = Color(0xFFFF4D86)
    val searchPill = Color.White.copy(alpha = 0.25f)
    val onContent = Color.White
    val onContentMuted = Color.White.copy(alpha = 0.6f)
    val onContentHint = Color.White.copy(alpha = 0.4f)
    val border = Color.White.copy(alpha = 0.08f)
    val selfBubble = Color(0xFF6C63FF)
    val otherBubble = Color(0xFF3E3C4E)
}

object ChatDimens {
    val listRowCorner = 18.dp
    val pillCorner = 25.dp
    val composerBarCorner = 28.dp
    val bottomBarCorner = 36.dp
    val formCardCorner = 20.dp
    val smallAvatarCorner = 12.dp
    val mediumAvatarCorner = 14.dp
    val messageBubbleMaxWidth = 280.dp
    val bottomBarHeight = 72.dp
    val bottomBarIconSlot = 52.dp
    val composerHeight = 56.dp
    val listRowPadding = 16.dp
    val listRowOuterHorizontal = 16.dp
    val listItemVerticalGap = 12.dp
    val screenEdgeHorizontal = 20.dp
    val screenEdgeVertical = 16.dp
    val formFieldSpacing = 16.dp
    val formCardInnerPadding = 20.dp
    val contentHorizontalLoose = 24.dp
    /** [VfvPillAgeTextField] outline and [SwipeToActionButton] in auth. */
    val vfvAuthPillCorner = 28.dp
    val vfvAuthPillFieldHeight = 56.dp
}
