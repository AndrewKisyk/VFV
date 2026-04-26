package com.wrapper.composechat.feature.chat

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.feature.home.ChatMessage
import com.wrapper.composechat.feature.home.LocalAnimatedVisibilityScope
import com.wrapper.composechat.feature.home.LocalSharedTransitionScope
import com.wrapper.composechat.feature.home.RecentMessage
import com.wrapper.composechat.feature.home.playfulSpring
import com.wrapper.composechat.ui.components.ChatMessageBubbleCard
import com.wrapper.composechat.ui.components.ChatPillInputBar
import com.wrapper.composechat.ui.theme.ChatColors
import com.wrapper.composechat.ui.theme.ChatDimens
import com.wrapper.composechat.ui.theme.Glassmorphism
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChatThreadScreen(
    user: RecentMessage,
    onBack: () -> Unit,
) {
    val displayFont = LocalVfvDisplayFontFamily.current
    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current ?: return
    val screenColor = ChatColors.screen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Glassmorphism.headerBarBrush)
                .border(0.5.dp, Glassmorphism.outlineSubtle, RectangleShape),
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .height(70.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                with(sharedTransitionScope) {
                    Box(
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = "avatar-${user.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> playfulSpring },
                            )
                            .size(40.dp)
                            .clip(RoundedCornerShape(ChatDimens.smallAvatarCorner))
                            .background(ChatColors.avatar),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = user.icon,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                with(sharedTransitionScope) {
                    Text(
                        text = user.name,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = displayFont,
                        ),
                        maxLines = 1,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "name-${user.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ -> playfulSpring },
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(
                                contentScale = ContentScale.Fit,
                                alignment = Alignment.CenterStart,
                            ),
                        ),
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 16.dp),
                )
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            itemsIndexed(chatDummyData) { index, msg ->
                MessageBubble(msg = msg, index = index)
            }
        }
        ChatPillInputBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(ChatDimens.composerHeight),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ChatDimens.composerHeight)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = "Attach",
                    tint = Color.White.copy(0.5f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Write",
                    color = Color.White.copy(0.3f),
                    fontFamily = displayFont,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Glassmorphism.primaryActionBrush)
                        .border(0.5.dp, Glassmorphism.outline, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage, index: Int) {
    val displayFont = LocalVfvDisplayFontFamily.current
    val startOffsetX = if (msg.isFromMe) 200f else -200f
    val slideAnim = remember { Animatable(startOffsetX) }
    val alphaAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * 100L + 300L)
        launch {
            slideAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = Spring.StiffnessLow,
                ),
            )
        }
        launch {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400),
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = slideAnim.value
                alpha = alphaAnim.value
            },
        contentAlignment = if (msg.isFromMe) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Column(horizontalAlignment = if (msg.isFromMe) Alignment.End else Alignment.Start) {
            val bubbleShape = if (msg.isFromMe) {
                RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
            } else {
                RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
            }
            ChatMessageBubbleCard(
                modifier = Modifier.widthIn(max = ChatDimens.messageBubbleMaxWidth),
                isFromMe = msg.isFromMe,
                shape = bubbleShape,
            ) {
                Box(Modifier.padding(16.dp)) {
                    Text(
                        text = msg.text,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = displayFont,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = msg.time,
                color = Color.White.copy(0.4f),
                fontSize = 12.sp,
                fontFamily = displayFont,
            )
        }
    }
}

val chatDummyData = listOf(
    ChatMessage(1, "Hello Frank! How are you?", false, "12:30"),
    ChatMessage(2, "Hello I'm fine. Thanks! And you?", true, "12:28"),
    ChatMessage(3, "Fine! I have a question", false, "12:30"),
    ChatMessage(4, "Question?", true, "12:28"),
    ChatMessage(5, "How about my work?", false, "12:30"),
)
