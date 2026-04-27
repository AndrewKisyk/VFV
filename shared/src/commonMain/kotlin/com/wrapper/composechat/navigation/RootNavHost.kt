package com.wrapper.composechat.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.feature.auth.AuthScreen
import com.wrapper.composechat.feature.home.MainStackFlow
import com.wrapper.composechat.feature.maindashboard.MainDashboardScreen
import com.wrapper.composechat.feature.maindashboard.VfvGroupsPlaceholderScreen
import com.wrapper.composechat.feature.maindashboard.VfvRecommendationsPlaceholderScreen
import com.wrapper.composechat.feature.splash.SplashScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

object AppDestinations {
    const val Auth = "auth"
    /** VFV [com.plstudio.a123.vfv.fragments.MainFragment] — steps + two cards. */
    const val MainDashboard = "main_dashboard"
    /** Compose chat hub (old “home”). */
    const val Chats = "chats"
    const val Groups = "vfv_groups"
    const val Recommendations = "vfv_recommendations"
}

@Composable
fun RootNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val authRepository = koinInject<AuthRepository>()
    var bootstrapped by remember { mutableStateOf(false) }
    var needAuth by remember { mutableStateOf(true) }
    var splashProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(authRepository) {
        coroutineScope {
            val progressTick = async {
                val steps = 52
                repeat(steps) { i ->
                    splashProgress = (i + 1) / steps.toFloat() * 0.92f
                    delay(18)
                }
            }
            needAuth = !authRepository.hasSession()
            progressTick.await()
        }
        splashProgress = 1f
        delay(100)
        bootstrapped = true
    }
    SplashScreen(loadProgress = splashProgress, modifier = modifier.fillMaxSize())
//    when {
//        !bootstrapped -> SplashScreen(loadProgress = splashProgress, modifier = modifier.fillMaxSize())
//        else -> NavHost(
//            navController = navController,
//            startDestination = if (needAuth) {
//                AppDestinations.Auth
//            } else {
//                AppDestinations.MainDashboard
//            },
//            modifier = modifier,
//        ) {
//            composable(AppDestinations.Auth) {
//                AuthScreen(
//                    onNavigateToHome = {
//                        navController.navigate(AppDestinations.MainDashboard) {
//                            popUpTo(AppDestinations.Auth) { inclusive = true }
//                        }
//                    },
//                )
//            }
//            composable(AppDestinations.MainDashboard) {
//                MainDashboardScreen(
//                    onOpenChats = { navController.navigate(AppDestinations.Chats) },
//                    onOpenRequirements = { navController.navigate(AppDestinations.Groups) },
//                    onOpenRecommendations = { navController.navigate(AppDestinations.Recommendations) },
//                )
//            }
//            composable(AppDestinations.Chats) {
//                MainStackFlow()
//            }
//            composable(AppDestinations.Groups) {
//                VfvGroupsPlaceholderScreen(
//                    onBack = { navController.popBackStack() },
//                )
//            }
//            composable(AppDestinations.Recommendations) {
//                VfvRecommendationsPlaceholderScreen(
//                    onBack = { navController.popBackStack() },
//                )
//            }
//        }
//    }
}
