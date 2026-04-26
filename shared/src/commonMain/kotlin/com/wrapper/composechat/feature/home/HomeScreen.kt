package com.wrapper.composechat.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.ui.components.ChatBottomDockCard
import com.wrapper.composechat.ui.components.ChatIconAccentButtonCard
import com.wrapper.composechat.ui.components.ChatListItemCard
import com.wrapper.composechat.ui.theme.ChatColors
import com.wrapper.composechat.ui.theme.ChatDimens
import com.wrapper.composechat.ui.theme.Glassmorphism
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@Composable
fun HomeScreen(
    selectedIndex: Int,
    shouldAnimate: Boolean,
    onTabSelected: (Int) -> Unit,
    onChatSelected: (RecentMessage) -> Unit
) {
    val displayFont = LocalVfvDisplayFontFamily.current
    // Layout Constants
    val topSpace = 80.dp
    val tabTextStyle = TextStyle(
        fontFamily = displayFont,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
    )
    val flareWidth = 56.dp
    val flareHeight = 36.dp
    val bottomCornerRadius = 26.dp

    val tabs = listOf(
        TabItem("Recents", ChatColors.primary),
        TabItem("Favorites", ChatColors.favorites),
        TabItem("Groups", ChatColors.online),
    )

    // Animate header background color based on selection
    val headerColor by animateColorAsState(
        targetValue = tabs[selectedIndex].color,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 200f),
        label = "headerColor"
    )

    val density = LocalDensity.current
    val noRipple = remember { MutableInteractionSource() }

    // Dynamic tab sizing for the indicator
    var tabBounds by remember(tabs.size) { mutableStateOf(List(tabs.size) { Rect.Zero }) }
    val target = tabBounds.getOrNull(selectedIndex) ?: Rect.Zero

    val isFirst = selectedIndex == 0
    val isLast = selectedIndex == tabs.size - 1
    val hasStartFlare = !isFirst
    val hasEndFlare = !isLast

    // Calculate Indicator Position and Width
    val targetX =
        if (hasStartFlare) target.left.toDp(density) - flareWidth else target.left.toDp(density)
    val indicatorX by animateDpAsState(
        targetValue = targetX,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 200f),
        label = "X"
    )

    val widthAdjustment =
        (if (hasStartFlare) flareWidth else 0.dp) + (if (hasEndFlare) flareWidth else 0.dp)
    val indicatorW by animateDpAsState(
        targetValue = target.width.toDp(density) + widthAdjustment,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 200f),
        label = "W"
    )

    val screenColor = ChatColors.screen

    // Search Logic
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Playful Search Expansion Animation
    val searchWidthFraction by animateFloatAsState(
        targetValue = if (isSearchActive) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.7f, // Jelly-like bounce
            stiffness = 200f     // Smooth speed
        ),
        label = "SearchWidth"
    )

    // Persist scroll states
    val recentsState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val favoritesState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val groupsState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val maxSearchWidth = maxWidth - 24.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(screenColor)
        ) {
        // --- Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerColor)
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topSpace)
                    .padding(horizontal = 8.dp)
            ) {
                // Add Button (Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .graphicsLayer {
                            // Shrink add button when search expands
                            val s = 1f - searchWidthFraction
                            scaleX = s
                            scaleY = s
                            alpha = s
                        }
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = noRipple,
                            indication = null
                        ) { /* Add Action */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.requiredSize(28.dp)
                    )
                }

                // Search Bar (Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(60.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    // Search pill (glass)
                    val pillShape = RoundedCornerShape(ChatDimens.pillCorner)
                    Box(
                        modifier = Modifier
                            .width(maxSearchWidth * searchWidthFraction)
                            .height(50.dp)
                            .clip(pillShape)
                            .background(Glassmorphism.searchPillBrush)
                            .border(1.dp, Glassmorphism.outlineSubtle, pillShape)
                    ) {}

                    // Text Field
                    if (searchWidthFraction > 0.1f) {
                        Row(
                            modifier = Modifier
                                .width(maxSearchWidth * searchWidthFraction)
                                .height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 20.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester)
                                        .alpha(searchWidthFraction.coerceIn(0f, 1f)),
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontFamily = displayFont,
                                    ),
                                    cursorBrush = SolidColor(Color.White),
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        if (searchText.isEmpty()) {
                                            Text(
                                                text = "Search...",
                                                color = Color.White.copy(0.6f),
                                                fontSize = 20.sp,
                                                fontFamily = displayFont,
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                            // Spacer to prevent text overlapping button area
                            Spacer(modifier = Modifier.width(60.dp))
                        }
                    }

                    LaunchedEffect(isSearchActive) {
                        if (isSearchActive) {
                            delay(100)
                            focusRequester.requestFocus()
                        }
                    }

                    // Search/Close Icon Button
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .clickable(interactionSource = noRipple, indication = null) {
                                isSearchActive = !isSearchActive
                                if (!isSearchActive) searchText = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // We use AnimatedContent with Scale+Fade to prevent visual overlapping
                        AnimatedContent(
                            targetState = isSearchActive,
                            transitionSpec = {
                                (scaleIn(animationSpec = tween(300)) + fadeIn(
                                    animationSpec = tween(
                                        300
                                    )
                                ))
                                    .togetherWith(
                                        scaleOut(animationSpec = tween(300)) + fadeOut(
                                            animationSpec = tween(
                                                300
                                            )
                                        )
                                    )
                            },
                            label = "IconAnim"
                        ) { active ->
                            Icon(
                                imageVector = if (active) Icons.Filled.Close else Icons.Filled.Search,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.requiredSize(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- Tabs Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(screenColor)
        ) {
            // Animated Indicator (The "Gooey" Background)
            if (target.width > 0f) {
                Box(
                    modifier = Modifier
                        .offset(x = indicatorX, y = (-1).dp)
                        .width(indicatorW)
                        .height(57.dp)
                        .background(
                            color = headerColor,
                            shape = getUltraSmoothedEdgesShape(
                                flareWidth = with(density) { flareWidth.toPx() },
                                flareHeight = with(density) { flareHeight.toPx() },
                                cornerSize = with(density) { bottomCornerRadius.toPx() },
                                hasStartFlare = hasStartFlare,
                                hasEndFlare = hasEndFlare
                            )
                        )
                )
            }

            // Tab Text Items
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = index == selectedIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(interactionSource = noRipple, indication = null) {
                                onTabSelected(index)
                            }
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInParent()
                                tabBounds = tabBounds
                                    .toMutableList()
                                    .also { list ->
                                        list[index] = Rect(
                                            pos.x,
                                            pos.y,
                                            pos.x + coords.size.width,
                                            pos.y + coords.size.height
                                        )
                                    }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.title,
                            color = if (selected) Color.White else Color.White.copy(alpha = 0.5f),
                            style = tabTextStyle
                        )
                    }
                }
            }
        }

        // --- Content Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(screenColor)
        ) {
            when (selectedIndex) {
                0 -> RecentsListShared(
                    items = dummyRecents,
                    state = recentsState,
                    onChatSelected = onChatSelected,
                    shouldAnimate = shouldAnimate
                )

                1 -> FavoritesListShared(
                    items = dummyFavorites,
                    state = favoritesState,
                    onChatSelected = onChatSelected,
                    shouldAnimate = shouldAnimate
                )

                2 -> GroupsListShared(
                    items = dummyGroups,
                    state = groupsState,
                    onChatSelected = onChatSelected,
                    shouldAnimate = shouldAnimate
                )
            }
            BottomNavBar(modifier = Modifier.align(Alignment.BottomCenter))
        }
        }
    }
}



@Composable
fun RecentsListShared(
    items: List<RecentMessage>,
    state: LazyListState,
    onChatSelected: (RecentMessage) -> Unit,
    shouldAnimate: Boolean
) {
    BoxWithConstraints {
        val startOffset = -maxWidth
        LazyColumn(
            state = state,
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(ChatDimens.listItemVerticalGap)
        ) {
            itemsIndexed(items) { index, item ->
                // Animation Logic:
                // If shouldAnimate is true, we initialize at 0f/offset and animate to 1f/0.
                // If false, we initialize directly at 1f/0 (Static).
                val alphaAnim = remember { Animatable(if (shouldAnimate) 0f else 1f) }
                val slideAnim =
                    remember { Animatable(if (shouldAnimate) startOffset.value else 0f) }

                if (shouldAnimate) {
                    LaunchedEffect(Unit) {
                        delay(index * 60L)
                        launch { alphaAnim.animateTo(1f, tween(400)) }
                        launch { slideAnim.animateTo(0f, spring(0.8f, Spring.StiffnessLow)) }
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(x = slideAnim.value.dp)
                        .alpha(alphaAnim.value)
                ) {
                    SharedRecentItemRow(item = item, onChatSelected = onChatSelected)
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FavoritesListShared(
    items: List<RecentMessage>,
    state: LazyListState,
    onChatSelected: (RecentMessage) -> Unit,
    shouldAnimate: Boolean
) {
    var favorites by remember { mutableStateOf(items) }

    LazyColumn(
        state = state,
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(ChatDimens.listItemVerticalGap)
    ) {
        itemsIndexed(favorites, key = { _, item -> item.id }) { index, item ->
            val alphaAnim = remember { Animatable(if (shouldAnimate) 0f else 1f) }
            val offsetYAnim = remember { Animatable(if (shouldAnimate) -100f else 0f) }

            if (shouldAnimate) {
                LaunchedEffect(Unit) {
                    delay(index * 100L)
                    launch { alphaAnim.animateTo(1f, tween(500)) }
                    launch {
                        offsetYAnim.animateTo(
                            0f,
                            spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = alphaAnim.value
                        translationY = offsetYAnim.value.dp.toPx()
                    }
            ) {
                DraggableFavoriteItemShared(
                    item = item,
                    onDelete = {
                        favorites = favorites.toMutableList().also { it.remove(item) }
                    },
                    onChatSelected = onChatSelected
                )
            }
        }
    }
}

@Composable
fun GroupsListShared(
    items: List<RecentMessage>,
    state: LazyListState,
    onChatSelected: (RecentMessage) -> Unit,
    shouldAnimate: Boolean
) {
    BoxWithConstraints {
        val startOffset = maxWidth
        LazyColumn(
            state = state,
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(ChatDimens.listItemVerticalGap)
        ) {
            itemsIndexed(items) { index, item ->
                val alphaAnim = remember { Animatable(if (shouldAnimate) 0f else 1f) }
                val slideAnim =
                    remember { Animatable(if (shouldAnimate) startOffset.value else 0f) }

                if (shouldAnimate) {
                    LaunchedEffect(Unit) {
                        delay(index * 60L)
                        launch { alphaAnim.animateTo(1f, tween(400)) }
                        launch { slideAnim.animateTo(0f, spring(0.8f, Spring.StiffnessLow)) }
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(x = slideAnim.value.dp)
                        .alpha(alphaAnim.value)
                ) {
                    SharedRecentItemRow(item = item, onChatSelected = onChatSelected)
                }
            }
        }
    }
}



@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedRecentItemRow(
    item: RecentMessage,
    onChatSelected: (RecentMessage) -> Unit
) {
    val displayFont = LocalVfvDisplayFontFamily.current
    // Safe Scope Access: We render the item even if shared transitions aren't active.
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    ChatListItemCard(
        onClick = { onChatSelected(item) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ChatDimens.listRowOuterHorizontal),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ChatDimens.listRowPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
        // AVATAR (Shared Element)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(ChatDimens.mediumAvatarCorner))
                .background(ChatColors.avatar)
                .then(
                    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                        with(sharedTransitionScope) {
                            Modifier.sharedElement(
                                state = rememberSharedContentState(key = "avatar-${item.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> playfulSpring }
                            )
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // NAME (Shared Bounds + ScaleToBounds)
            Text(
                text = item.name,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = displayFont,
                ),
                maxLines = 1,
                modifier = Modifier.then(
                    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                        with(sharedTransitionScope) {
                            Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "name-${item.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> playfulSpring },
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(
                                    ContentScale.Fit,
                                    Alignment.CenterStart
                                )
                            )
                        }
                    } else Modifier
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.message,
                style = TextStyle(
                    color = Color.White.copy(0.6f),
                    fontSize = 14.sp,
                    fontFamily = displayFont,
                ),
                maxLines = 1
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = item.time,
                style = TextStyle(
                    color = Color.White.copy(0.4f),
                    fontSize = 12.sp,
                    fontFamily = displayFont,
                )
            )
            if (item.isOnline) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(ChatColors.online, CircleShape)
                )
            }
        }
        }
    }
}

@Composable
fun DraggableFavoriteItemShared(
    item: RecentMessage,
    onDelete: () -> Unit,
    onChatSelected: (RecentMessage) -> Unit
) {
    val density = LocalDensity.current
    val revealSizeDp = 100.dp
    val maxRevealPx = with(density) { -revealSizeDp.toPx() }
    val snapThreshold = maxRevealPx / 2
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Reveal Background
        Box(
            modifier = Modifier
                .width(revealSizeDp)
                .height(72.dp),
            contentAlignment = Alignment.Center
        ) {
            val progress = (offsetX.value / maxRevealPx).coerceIn(0f, 1.2f)
            ChatIconAccentButtonCard(
                onClick = onDelete,
                modifier = Modifier.scale(progress),
                containerColor = ChatColors.favorites,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }

        // Draggable Content
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val newVal = (offsetX.value + delta).coerceIn(maxRevealPx * 1.5f, 0f)
                        scope.launch { offsetX.snapTo(newVal) }
                    },
                    onDragStopped = {
                        val targetOffset = if (offsetX.value < snapThreshold) maxRevealPx else 0f
                        scope.launch {
                            offsetX.animateTo(
                                targetValue = targetOffset,
                                animationSpec = spring(
                                    Spring.DampingRatioMediumBouncy,
                                    Spring.StiffnessLow
                                )
                            )
                        }
                    }
                )
        ) {
            SharedRecentItemRow(item = item, onChatSelected = onChatSelected)
        }
    }
}

fun getUltraSmoothedEdgesShape(
    flareWidth: Float,
    flareHeight: Float,
    cornerSize: Float,
    hasStartFlare: Boolean,
    hasEndFlare: Boolean
) = GenericShape { size, _ ->
    val fw = flareWidth
    val fh = flareHeight
    val cs = cornerSize
    val w = size.width
    val h = size.height

    if (hasStartFlare) {
        moveTo(0f, 0f)
        cubicTo(fw * 0.8f, 0f, fw, fh * 0.4f, fw, fh)
        lineTo(fw, h - cs)
    } else {
        moveTo(0f, 0f)
        lineTo(0f, h - cs)
    }

    val lx = if (hasStartFlare) fw else 0f
    cubicTo(lx, h - (cs * 0.4f), lx + (cs * 0.4f), h, lx + cs, h)

    val rx = w - (if (hasEndFlare) fw else 0f)
    lineTo(rx - cs, h)
    cubicTo(rx - (cs * 0.4f), h, rx, h - (cs * 0.4f), rx, h - cs)

    if (hasEndFlare) {
        lineTo(rx, fh)
        cubicTo(rx, fh * 0.4f, rx + (fw * 0.2f), 0f, w, 0f)
    } else {
        lineTo(w, 0f)
    }
    close()
}

@Composable
fun BottomNavBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ChatDimens.contentHorizontalLoose, vertical = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        ChatBottomDockCard {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val navSlotShape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier
                        .size(ChatDimens.bottomBarIconSlot)
                        .clip(navSlotShape)
                        .background(Glassmorphism.primaryActionBrush)
                        .border(0.5.dp, Glassmorphism.outline, navSlotShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MailOutline,
                        contentDescription = "Chat",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp),
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.Call,
                    contentDescription = "Call",
                    tint = Color.White.copy(0.4f),
                    modifier = Modifier.size(28.dp),
                )
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    tint = Color.White.copy(0.4f),
                    modifier = Modifier.size(28.dp),
                )
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = Color.White.copy(0.4f),
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

fun Float.toDp(density: Density): Dp = with(density) { this@toDp.toDp() }


val dummyRecents = listOf(
    RecentMessage(
        1,
        "Max Hall",
        "Hello Friend! How are you?",
        "08:30 pm",
        true,
        Icons.Rounded.Person
    ),
    RecentMessage(2, "Dan Martin", "Hi man! Do you know?...", "04:12 pm", true, Icons.Rounded.Face),
    RecentMessage(
        3,
        "Stephen Green",
        "Yes! I like it!",
        "02:05 pm",
        true,
        Icons.Rounded.AccountCircle
    ),
    RecentMessage(
        4,
        "Sarah Woodman",
        "How about my work?",
        "Yesterday",
        false,
        Icons.Rounded.Face
    ),
    RecentMessage(5, "Peter Hopper", "At 5 pm", "01.22.201", false, Icons.Rounded.AccountCircle),
    RecentMessage(
        6,
        "Denis Ivanov",
        "Oh, no! Are you sure?",
        "01.16.201",
        false,
        Icons.Rounded.Person
    ),
    RecentMessage(7, "Alice Silver", "Hello Alex!", "01.12.201", false, Icons.Rounded.Face),
)
val dummyFavorites = listOf(
    RecentMessage(
        4,
        "Sarah Woodman",
        "How about my work?",
        "Yesterday",
        false,
        Icons.Rounded.Person
    ),
    RecentMessage(5, "Peter Hopper", "At 5 pm", "01.22.201", false, Icons.Rounded.AccountCircle),
    RecentMessage(
        6,
        "Denis Ivanov",
        "Oh, no! Are you sure?",
        "01.16.201",
        false,
        Icons.Rounded.Person
    ),
    RecentMessage(7, "Alice Silver", "Hello Alex!", "01.12.201", false, Icons.Rounded.Face),
)
val dummyGroups = listOf(
    RecentMessage(
        10,
        "Design Team",
        "New mockups are ready!",
        "10:30 am",
        true,
        Icons.Rounded.Person
    ),
    RecentMessage(
        11,
        "Weekend Trip",
        "Who is bringing the snacks?",
        "09:15 am",
        true,
        Icons.Rounded.Person
    ),
    RecentMessage(
        12,
        "Family Group",
        "Mom: Call me when you can",
        "Yesterday",
        false,
        Icons.Rounded.Home
    ),
    RecentMessage(13, "Project Alpha", "Meeting delayed to 4 PM", "Mon", true, Icons.Rounded.Home),
    RecentMessage(14, "Gaming Squad", "Online tonight?", "Sun", false, Icons.Rounded.Favorite),
)

