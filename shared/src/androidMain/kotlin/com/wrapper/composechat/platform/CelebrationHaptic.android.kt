package com.wrapper.composechat.platform

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberCelebrationHaptic(): () -> Unit {
    val view = LocalView.current
    return remember(view) {
        { view.vibrateStrong() }
    }
}

private fun View.vibrateStrong() = reallyPerformHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

private fun View.reallyPerformHapticFeedback(feedbackConstant: Int) {
    if (context.isTouchExplorationEnabled()) return
    isHapticFeedbackEnabled = true
    performHapticFeedback(feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
}

private fun Context.isTouchExplorationEnabled(): Boolean {
    val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    return accessibilityManager?.isTouchExplorationEnabled ?: false
}
