package com.wrapper.composechat.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.wrapper.composechat.feature.chat.ChatThreadScreen
import com.wrapper.composechat.ui.theme.ChatColors

/**
 * After auth: home (tabbed lists) and conversation, with shared-element transitions.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainStackFlow() {
    var currentScreen by remember { mutableStateOf<MainStackScreen>(MainStackScreen.Home) }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var shouldAnimateList by rememberSaveable { mutableStateOf(true) }

    SharedTransitionLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = ChatColors.screen),
    ) {
        AnimatedContent(
            targetState = currentScreen,
            label = "ScreenTransition",
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 600)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 600))
            },
        ) { targetScreen ->
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this@SharedTransitionLayout,
                LocalAnimatedVisibilityScope provides this@AnimatedContent,
            ) {
                when (targetScreen) {
                    is MainStackScreen.Home -> {
                        HomeScreen(
                            selectedIndex = selectedTabIndex,
                            shouldAnimate = shouldAnimateList,
                            onTabSelected = { newIndex ->
                                selectedTabIndex = newIndex
                                shouldAnimateList = true
                            },
                            onChatSelected = { user ->
                                shouldAnimateList = false
                                currentScreen = MainStackScreen.ChatWithUser(user)
                            },
                        )
                    }
                    is MainStackScreen.ChatWithUser -> {
                        ChatThreadScreen(
                            user = targetScreen.user,
                            onBack = { currentScreen = MainStackScreen.Home },
                        )
                    }
                }
            }
        }
    }
}
