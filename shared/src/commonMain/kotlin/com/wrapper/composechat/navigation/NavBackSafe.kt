package com.wrapper.composechat.navigation

import androidx.navigation.NavController

/** Pop until [AppDestinations.MainDashboard] is on top. No-op if already there or missing. */
fun NavController.popToMainDashboard() {
    if (currentDestination?.route == AppDestinations.MainDashboard) return
    popBackStack(AppDestinations.MainDashboard, inclusive = false)
}

/**
 * Pops only while [expectedRoute] is still the current destination.
 * Extra taps during the exit transition are ignored — avoids emptying the back stack.
 */
fun NavController.popCurrentOnce(expectedRoute: String) {
    if (currentDestination?.route != expectedRoute) return
    popBackStack()
}
