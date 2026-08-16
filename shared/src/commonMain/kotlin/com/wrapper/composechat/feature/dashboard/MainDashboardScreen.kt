package com.wrapper.composechat.feature.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.wrapper.composechat.core.VfvLegalUrls
import com.wrapper.composechat.resources.*
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.wrapper.composechat.feature.home.LocalVfvTransitionInteractor
import com.wrapper.composechat.feature.home.navigateSettled
import com.wrapper.composechat.platform.isBackdropBlurAvailable
import com.wrapper.composechat.platform.rememberCelebrationHaptic
import com.wrapper.composechat.platform.rememberComposeViewBitmapCapture
import com.wrapper.composechat.platform.rememberOpenExternalUrl
import com.wrapper.composechat.platform.withSnapshotBlur
import com.wrapper.composechat.ui.components.VfvChromeBackIconButton
import com.wrapper.composechat.ui.components.VfvChromeTrashIconButton
import com.wrapper.composechat.ui.components.VfvConfirmDeleteProgressDialog
import com.wrapper.composechat.ui.components.VfvGlassFullScreenBottomSheet
import com.wrapper.composechat.ui.components.VfvListItemEntrance
import com.wrapper.composechat.ui.liquidglass.LocalVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.rememberVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.vfvScreenLayerBackdrop
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val ChatsSheetBackdropBlurRadiusDp = 20f
/**
 * VFV main dashboard: night sky, ring gauge, two nav cards. Callbacks and [MainDashboardViewModel] unchanged.
 */
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MainDashboardScreen(
    onOpenRequirements: () -> Unit,
    onOpenRecommendations: () -> Unit,
    onOpenSettings: () -> Unit = {},
    // The hero image lives inside the entrance wrapper; replaying slide/fade on return would
    // move the shared-element target mid-morph and make it stutter.
    animateCardsEntrance: Boolean = true,
    viewModel: MainDashboardViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    // Ring sweep: each point = 1% of the full track (75 req + 25 rec = closed circle).
    val reqAnimated by animateFloatAsState(
        targetValue = state.requirementsRingPoints / 100f,
        animationSpec = tween(800),
        label = "req",
    )
    val recAnimated by animateFloatAsState(
        targetValue = state.recommendationsRingPoints / 100f,
        animationSpec = tween(800),
        label = "rec",
    )
    val cardBg = Color(0xFF020725)

    val requirementsImage = painterResource(Res.drawable.main_dashboard_requirements)
    val recommendationsImage = painterResource(Res.drawable.main_dashboard_recommendations)
    var showChatsAccessSheet by remember { mutableStateOf(false) }
    var chatsSheetBackground: ImageBitmap? by remember { mutableStateOf(null) }
    var chatsSheetOpenProgress by remember { mutableFloatStateOf(0f) }
    var infoSheetDismiss by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var confirmOpensAuth by remember { mutableStateOf(false) }
    val captureForSheet = rememberComposeViewBitmapCapture()
    val liveBackdropBlur = isBackdropBlurAvailable()
    val dashboardBlurRadiusDp = chatsSheetOpenProgress * ChatsSheetBackdropBlurRadiusDp
    val transitionInteractor = LocalVfvTransitionInteractor.current
    val openExternalUrl = rememberOpenExternalUrl()
    val screenBackdrop = rememberVfvScreenBackdrop()
    var celebrateDone by remember { mutableStateOf(false) }
    var previousAllDone by remember { mutableStateOf<Boolean?>(null) }
    val doneConfettiParties = remember { vfvDoneConfettiParties() }
    val celebrationHaptic = rememberCelebrationHaptic()

    LaunchedEffect(state.vfvAllDone, state.isLoading) {
        if (state.isLoading) return@LaunchedEffect
        val previous = previousAllDone
        previousAllDone = state.vfvAllDone
        // Fire on first paint when already done (app open) and on false → true.
        if (state.vfvAllDone && previous != true) {
            celebrateDone = true
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose {
            // Leaving dashboard (nav to Groups/Recommendations/etc.) must not leave a
            // half-open sheet / blur progress that shows as an empty overlay on return.
            showChatsAccessSheet = false
            chatsSheetBackground = null
            chatsSheetOpenProgress = 0f
            infoSheetDismiss = null
            showConfirmDelete = false
            confirmOpensAuth = false
        }
    }

    // Root screen: never finish the activity on back. Only dismiss InfoDialog if open.
    BackHandler {
        when {
            showConfirmDelete -> {
                showConfirmDelete = false
                confirmOpensAuth = false
            }
            showChatsAccessSheet -> {
                infoSheetDismiss?.invoke() ?: run {
                    showChatsAccessSheet = false
                    chatsSheetBackground = null
                    chatsSheetOpenProgress = 0f
                }
            }
        }
    }

    CompositionLocalProvider(LocalVfvScreenBackdrop provides screenBackdrop) {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .vfvScreenLayerBackdrop(screenBackdrop)
                .then(
                    if (liveBackdropBlur && chatsSheetOpenProgress > 0f) {
                        Modifier.blur(dashboardBlurRadiusDp.dp)
                    } else {
                        Modifier
                    },
                ),
        ) {
        MainDashboardSkyBackdrop(Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = VfvListChromeHorizontalPadding),
        ) {
            MainDashboardTopBar(
                statusText = stringResource(
                    if (state.vfvAllDone) {
                        Res.string.main_dashboard_vfv_done
                    } else {
                        Res.string.main_dashboard_in_progress
                    },
                ),
                onOpenInfo = {
                    if (showChatsAccessSheet) {
                        // Already open / closing — ignore spam taps on the chrome button.
                        return@MainDashboardTopBar
                    }
                    transitionInteractor.navigateSettled {
                        if (!liveBackdropBlur) {
                            chatsSheetBackground = captureForSheet()
                                ?.withSnapshotBlur(ChatsSheetBackdropBlurRadiusDp)
                        }
                        showChatsAccessSheet = true
                    }
                },
                onClearProgress = {
                    confirmOpensAuth = false
                    showConfirmDelete = true
                },
                infoContentDescription = stringResource(Res.string.main_dashboard_info),
                settingsContentDescription = stringResource(Res.string.main_dashboard_settings),
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                contentAlignment = Alignment.Center,
            ) {
                MainDashboardRingGauge(
                    recommendationsProgress = recAnimated,
                    requirementsProgress = reqAnimated,
                    vfvAllDone = state.vfvAllDone,
                )
            }
            Spacer(Modifier.height(12.dp))
            VfvListItemEntrance(index = 0, shouldAnimate = animateCardsEntrance) {
                DashboardNavCardV2(
                    onClick = onOpenRequirements,
                    title = stringResource(Res.string.main_dashboard_requirements_title),
                    subtitle = stringResource(Res.string.main_dashboard_requirements_subtitle),
                    progress = reqAnimated,
                    leadingImage = requirementsImage,
                    cardBg = cardBg,
                    leadingImageModifier = Modifier.requirementsHeroSharedElement(),
                )
            }
            Spacer(Modifier.height(12.dp))
            VfvListItemEntrance(index = 1, shouldAnimate = animateCardsEntrance) {
                DashboardNavCardV2(
                    onClick = onOpenRecommendations,
                    title = stringResource(Res.string.main_dashboard_recommendations_title),
                    subtitle = stringResource(Res.string.main_dashboard_recommendations_subtitle),
                    progress = recAnimated,
                    leadingImage = recommendationsImage,
                    cardBg = cardBg,
                    leadingImageModifier = Modifier.recommendationsHeroSharedElement(),
                )
            }
            Spacer(Modifier.height(24.dp))
        }
        }
        VfvGlassFullScreenBottomSheet(
            visible = showChatsAccessSheet,
            onDismissRequest = {
                showChatsAccessSheet = false
                chatsSheetBackground = null
                chatsSheetOpenProgress = 0f
                infoSheetDismiss = null
            },
            backgroundSnapshot = if (liveBackdropBlur) null else chatsSheetBackground,
            revealLiveBackdrop = liveBackdropBlur,
            blurBackgroundSnapshot = false,
            backdropBlurRadiusDp = ChatsSheetBackdropBlurRadiusDp,
            onOpenProgressChange = { chatsSheetOpenProgress = it },
            contentFullScreen = true,
        ) { onAnimatedDismiss ->
            DisposableEffect(onAnimatedDismiss) {
                infoSheetDismiss = onAnimatedDismiss
                onDispose {
                    if (infoSheetDismiss === onAnimatedDismiss) {
                        infoSheetDismiss = null
                    }
                }
            }
            InfoDialogSheetContent(
                onClose = onAnimatedDismiss,
                onChangeAge = {
                    confirmOpensAuth = true
                    showConfirmDelete = true
                },
                onTermsClick = {
                    openExternalUrl(VfvLegalUrls.PrivacyAndTerms)
                },
            )
        }
        if (celebrateDone) {
            ConfettiKit(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10_000f),
                parties = doneConfettiParties,
                onParticleSystemStarted = { _, activeSystems ->
                    if (activeSystems == 1) {
                        celebrationHaptic()
                    }
                },
                onParticleSystemEnded = { _, activeSystems ->
                    if (activeSystems == 0) {
                        celebrateDone = false
                    }
                },
            )
        }
        VfvConfirmDeleteProgressDialog(
            visible = showConfirmDelete,
            onConfirm = {
                val openAuth = confirmOpensAuth
                showConfirmDelete = false
                confirmOpensAuth = false
                if (openAuth) {
                    transitionInteractor.navigateSettled {
                        showChatsAccessSheet = false
                        chatsSheetBackground = null
                        chatsSheetOpenProgress = 0f
                        infoSheetDismiss = null
                        onOpenSettings()
                    }
                } else {
                    viewModel.resetAllProgress()
                }
            },
            onDismiss = {
                showConfirmDelete = false
                confirmOpensAuth = false
            },
        )
    }
    }
}

@Composable
private fun MainDashboardTopBar(
    statusText: String,
    onOpenInfo: () -> Unit,
    onClearProgress: () -> Unit,
    infoContentDescription: String,
    settingsContentDescription: String,
) {
    val family = LocalVfvDisplayFontFamily.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VfvChromeBackIconButton(
            onClick = onOpenInfo,
            contentDescription = infoContentDescription,
        )
        Text(
            text = statusText,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light,
            fontFamily = family,
            letterSpacing = 0.sp,
            maxLines = 1,
        )
        VfvChromeTrashIconButton(
            onClick = onClearProgress,
            contentDescription = settingsContentDescription,
        )
    }
}

@Composable
private fun DashboardNavCardV2(
    onClick: () -> Unit,
    title: String,
    subtitle: String,
    progress: Float,
    leadingImage: Painter,
    cardBg: Color,
    leadingImageModifier: Modifier = Modifier,
) {
    val family = LocalVfvDisplayFontFamily.current
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = leadingImageModifier
                        .size(width = 48.dp, height = 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.12f),
                            RoundedCornerShape(14.dp),
                        )
                        .background(Color(0xFF0A0F1C)),
                ) {
                    Image(
                        painter = leadingImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.65f),
                        fontWeight = FontWeight.Light,
                        fontFamily = family,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = family,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

private val VfvConfettiColors = listOf(0xfce18a, 0xff726d, 0xb53ffe, 0xb48def, 0x42d4ff)

private fun vfvDoneConfettiParties(): List<Party> {
    val burst = Party(
        speed = 0f,
        maxSpeed = 32f,
        damping = 0.9f,
        spread = 360,
        colors = VfvConfettiColors,
        emitter = Emitter(duration = 120.milliseconds).max(120),
        position = Position.Relative(0.5, 0.32),
    )
    val fromLeft = Party(
        speed = 12f,
        maxSpeed = 28f,
        damping = 0.9f,
        angle = Angle.RIGHT - 45,
        spread = Spread.SMALL,
        colors = VfvConfettiColors,
        emitter = Emitter(duration = 1.6.seconds).perSecond(36),
        position = Position.Relative(0.0, 0.22),
    )
    return listOf(
        burst,
        fromLeft,
        fromLeft.copy(
            angle = Angle.LEFT + 45,
            position = Position.Relative(1.0, 0.22),
        ),
    )
}
