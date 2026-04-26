package com.wrapper.composechat.feature.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val title: String,
    val color: Color,
)

data class RecentMessage(
    val id: Int,
    val name: String,
    val message: String,
    val time: String,
    val isOnline: Boolean,
    val icon: ImageVector,
)

data class ChatMessage(
    val id: Int,
    val text: String,
    val isFromMe: Boolean,
    val time: String,
)

/**
 * In-app stack after auth: home (lists) ↔ conversation.
 */
sealed class MainStackScreen {
    data object Home : MainStackScreen()
    data class ChatWithUser(val user: RecentMessage) : MainStackScreen()
}
