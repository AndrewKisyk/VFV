package com.wrapper.composechat.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.zIndex
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.feature.auth.AuthScreen
import com.wrapper.composechat.feature.auth.VfvAuthTitleSharedElementKey
import com.wrapper.composechat.feature.home.LocalAnimatedVisibilityScope
import com.wrapper.composechat.feature.home.LocalSharedTransitionScope
import com.wrapper.composechat.feature.home.LocalVfvTransitionInteractor
import com.wrapper.composechat.feature.home.MainStackFlow
import com.wrapper.composechat.feature.home.navigateSettled
import com.wrapper.composechat.feature.home.playfulSpring
import com.wrapper.composechat.feature.home.rememberVfvTransitionInteractor
import com.wrapper.composechat.feature.maindashboard.MainDashboardScreen
import com.wrapper.composechat.feature.maindashboard.VfvGroupsScreen
import com.wrapper.composechat.feature.maindashboard.VfvRecommendationDetailScreen
import com.wrapper.composechat.feature.maindashboard.VfvRecommendationsScreen
import com.wrapper.composechat.feature.splash.SplashScreen
import com.wrapper.composechat.platform.rememberComposeViewBitmapCapture
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

/**
 * Destination fade length. Kept >= the hero shared-element settle time so the outgoing screen
 * stays composed until the morph finishes (otherwise the element snaps at the end).
 */
const val VfvNavTransitionDurationMs = 420

object AppDestinations {
    const val MainDashboard = "main_dashboard"
    const val Chats = "chats"
    const val Groups = "vfv_groups"
    const val Recommendations = "vfv_recommendations"
    const val RecommendationDetail = "vfv_recommendation_detail/{topicId}"

    fun recommendationDetail(topicId: String): String = "vfv_recommendation_detail/$topicId"
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RootNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val authRepository = koinInject<AuthRepository>()
    var bootstrapped by remember { mutableStateOf(false) }
    var needAuth by remember { mutableStateOf(true) }
    var splashProgress by remember { mutableStateOf(0f) }
    val splashProgressAnimated by animateFloatAsState(
        targetValue = splashProgress,
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing,
        ),
        label = "splashLoadProgress",
    )
    var authFlowCompleted by remember { mutableStateOf(false) }
    var authSheetBackground: ImageBitmap? by remember { mutableStateOf(null) }
    val captureForAuthSheet = rememberComposeViewBitmapCapture()

    LaunchedEffect(authRepository) {
        coroutineScope {
            val progressTick = async {
                val steps = 82
                repeat(steps) { i ->
                    splashProgress = (i + 1) / steps.toFloat() * 0.92f
                    delay(18)
                }
            }
            needAuth = !authRepository.hasSession()
            progressTick.await()
        }
        splashProgress = 1f
        delay(1_500)
        if (needAuth) {
            authSheetBackground = captureForAuthSheet()
        }
        bootstrapped = true
    }

    val showBootstrapShell = !bootstrapped || (needAuth && !authFlowCompleted)
    val authVisible = bootstrapped && needAuth && !authFlowCompleted

    when {
        showBootstrapShell && needAuth -> {
            SharedTransitionLayout(Modifier.fillMaxSize()) {
                val st = this@SharedTransitionLayout
                Box(Modifier.fillMaxSize()) {
                    SplashScreen(
                        loadProgress = splashProgressAnimated,
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(0f),
                    )
                    AnimatedVisibility(
                        visible = authVisible,
                        enter = fadeIn(animationSpec = tween(420)),
                        exit = fadeOut(animationSpec = tween(420)),
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f),
                    ) {
                        val av = this@AnimatedVisibility
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides st,
                            LocalAnimatedVisibilityScope provides av,
                        ) {
                            val titleMod = with(st) {
                                Modifier.sharedElement(
                                    sharedContentState = rememberSharedContentState(key = VfvAuthTitleSharedElementKey),
                                    animatedVisibilityScope = av,
                                    boundsTransform = { _, _ -> playfulSpring },
                                )
                            }
                            AuthScreen(
                                onNavigateToHome = { authFlowCompleted = true },
                                backgroundSnapshot = authSheetBackground,
                                titleImageModifier = titleMod,
                            )
                        }
                    }
                }
            }
        }
        showBootstrapShell && !needAuth -> {
            SplashScreen(
                loadProgress = splashProgressAnimated,
                modifier = modifier.fillMaxSize(),
            )
        }
        else -> {
            SharedTransitionLayout(modifier) {
                val sharedShell = this@SharedTransitionLayout
                val transitionInteractor = rememberVfvTransitionInteractor(sharedShell)
                // Dashboard cards animate in once; on return the hero morph owns the motion.
                var dashboardEntrancePlayed by remember { mutableStateOf(false) }
                CompositionLocalProvider(
                    LocalVfvTransitionInteractor provides transitionInteractor,
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AppDestinations.MainDashboard,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        composable(
                            route = AppDestinations.MainDashboard,
                            enterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            exitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popExitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                        ) { _: NavBackStackEntry ->
                            CompositionLocalProvider(
                                LocalSharedTransitionScope provides sharedShell,
                                LocalAnimatedVisibilityScope provides this,
                            ) {
                                MainDashboardScreen(
                                    animateCardsEntrance = !dashboardEntrancePlayed,
                                    onOpenChats = {
                                        dashboardEntrancePlayed = true
                                        transitionInteractor.navigateSettled {
                                            navController.navigate(AppDestinations.Chats)
                                        }
                                    },
                                    onOpenRequirements = {
                                        dashboardEntrancePlayed = true
                                        navController.navigate(AppDestinations.Groups)
                                    },
                                    onOpenRecommendations = {
                                        dashboardEntrancePlayed = true
                                        navController.navigate(AppDestinations.Recommendations)
                                    },
                                )
                            }
                        }
                        composable(
                            route = AppDestinations.Chats,
                            enterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            exitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popExitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                        ) { _: NavBackStackEntry ->
                            CompositionLocalProvider(
                                LocalSharedTransitionScope provides sharedShell,
                                LocalAnimatedVisibilityScope provides this,
                            ) {
                                MainStackFlow()
                            }
                        }
                        composable(
                            route = AppDestinations.Groups,
                            enterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            exitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popExitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                        ) { _: NavBackStackEntry ->
                            CompositionLocalProvider(
                                LocalSharedTransitionScope provides sharedShell,
                                LocalAnimatedVisibilityScope provides this,
                            ) {
                                VfvGroupsScreen(
                                    onBack = {
                                        navController.popToMainDashboard()
                                    },
                                )
                            }
                        }
                        composable(
                            route = AppDestinations.Recommendations,
                            enterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            exitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popExitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                        ) { _: NavBackStackEntry ->
                            CompositionLocalProvider(
                                LocalSharedTransitionScope provides sharedShell,
                                LocalAnimatedVisibilityScope provides this,
                            ) {
                            VfvRecommendationsScreen(
                                onBack = {
                                    navController.popToMainDashboard()
                                },
                                onTopicClick = { topicId ->
                                    navController.navigate(AppDestinations.recommendationDetail(topicId))
                                },
                            )
                            }
                        }
                        composable(
                            route = AppDestinations.RecommendationDetail,
                            arguments = listOf(
                                navArgument("topicId") { type = NavType.StringType },
                            ),
                            enterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            exitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(VfvNavTransitionDurationMs)) },
                            popExitTransition = { fadeOut(animationSpec = tween(VfvNavTransitionDurationMs)) },
                        ) { backStackEntry ->
                            val topicId = backStackEntry.arguments?.getString("topicId").orEmpty()
                            CompositionLocalProvider(
                                LocalSharedTransitionScope provides sharedShell,
                                LocalAnimatedVisibilityScope provides this,
                            ) {
                            VfvRecommendationDetailScreen(
                                topicId = topicId,
                                onBack = {
                                    navController.popCurrentOnce(AppDestinations.RecommendationDetail)
                                },
                            )
                            }
                        }
                    }
                }
            }
        }
    }
}
