package com.wrapper.composechat.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.ui.theme.ChatColors
import com.wrapper.composechat.ui.theme.ChatDimens
import com.wrapper.composechat.ui.theme.Glassmorphism

@Composable
fun ChatListItemCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(ChatDimens.listRowCorner),
    content: @Composable () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.shadow(2.dp, shape, spotColor = Color.Black.copy(alpha = 0.28f)),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = ChatColors.onContent,
        ),
        border = BorderStroke(1.dp, Glassmorphism.outline),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
    ) {
        Box(Modifier.background(Glassmorphism.listItemBrush, shape)) {
            content()
        }
    }
}

@Composable
fun ChatPillInputBar(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(ChatDimens.composerBarCorner),
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.shadow(3.dp, shape, spotColor = Color.Black.copy(alpha = 0.22f)),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = ChatColors.onContent,
        ),
        border = BorderStroke(1.dp, Glassmorphism.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.background(Glassmorphism.composerBrush, shape)) {
            content()
        }
    }
}

@Composable
fun ChatMessageBubbleCard(
    modifier: Modifier = Modifier,
    isFromMe: Boolean,
    shape: Shape,
    content: @Composable () -> Unit,
) {
    val baseBrush = if (isFromMe) {
        Glassmorphism.bubbleFromMeBrush()
    } else {
        Glassmorphism.bubbleOtherBrush()
    }
    val border = if (isFromMe) {
        Glassmorphism.outline
    } else {
        Glassmorphism.outlineSubtle
    }
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = ChatColors.onContent,
        ),
        border = BorderStroke(0.5.dp, border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.background(baseBrush, shape)) {
            content()
        }
    }
}

@Composable
fun ChatBottomDockCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(ChatDimens.bottomBarCorner)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(ChatDimens.bottomBarHeight)
            .shadow(18.dp, shape, spotColor = Color.Black.copy(alpha = 0.4f)),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = ChatColors.onContent,
        ),
        border = BorderStroke(1.dp, Glassmorphism.outlineStrong),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
        ),
    ) {
        Box(Modifier.background(Glassmorphism.dockBrush, shape)) {
            content()
        }
    }
}

@Composable
fun ChatFormCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(ChatDimens.formCardCorner)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, shape, spotColor = Color.Black.copy(alpha = 0.3f)),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = ChatColors.onContent,
        ),
        border = BorderStroke(1.dp, Glassmorphism.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    ) {
        Box(Modifier.background(Glassmorphism.formBrush, shape)) {
            content()
        }
    }
}

@Composable
fun ChatIconAccentButtonCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    shape: Shape = RoundedCornerShape(ChatDimens.listRowCorner),
    containerColor: Color = ChatColors.favorites,
    content: @Composable () -> Unit,
) {
    val brush = Glassmorphism.favoritesSwipeBrush(containerColor)
    Card(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .shadow(2.dp, shape, spotColor = Color.Black.copy(alpha = 0.25f)),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = ChatColors.onContent,
        ),
        border = BorderStroke(0.5.dp, Glassmorphism.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    ) {
        Box(Modifier.background(brush, shape)) {
            content()
        }
    }
}
