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

    LaunchedEffect(authRepository) {
        needAuth = !authRepository.hasSession()
        bootstrapped = true
    }

    when {
        !bootstrapped -> Box(modifier.fillMaxSize())
        else -> NavHost(
            navController = navController,
            startDestination = if (needAuth) {
                AppDestinations.Auth
            } else {
                AppDestinations.MainDashboard
            },
            modifier = modifier,
        ) {
            composable(AppDestinations.Auth) {
                AuthScreen(
                    onNavigateToHome = {
                        navController.navigate(AppDestinations.MainDashboard) {
                            popUpTo(AppDestinations.Auth) { inclusive = true }
                        }
                    },
                )
            }
            composable(AppDestinations.MainDashboard) {
                MainDashboardScreen(
                    onOpenChats = { navController.navigate(AppDestinations.Chats) },
                    onOpenRequirements = { navController.navigate(AppDestinations.Groups) },
                    onOpenRecommendations = { navController.navigate(AppDestinations.Recommendations) },
                )
            }
            composable(AppDestinations.Chats) {
                MainStackFlow()
            }
            composable(AppDestinations.Groups) {
                VfvGroupsPlaceholderScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(AppDestinations.Recommendations) {
                VfvRecommendationsPlaceholderScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
