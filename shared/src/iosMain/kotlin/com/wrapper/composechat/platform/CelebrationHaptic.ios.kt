package com.wrapper.composechat.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

@Composable
actual fun rememberCelebrationHaptic(): () -> Unit {
    return remember {
        {
            val notification = UINotificationFeedbackGenerator()
            notification.prepare()
            notification.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
            val impact = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
            impact.prepare()
            impact.impactOccurred()
        }
    }
}
