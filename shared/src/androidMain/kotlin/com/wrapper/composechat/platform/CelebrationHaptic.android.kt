package com.wrapper.composechat.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberCelebrationHaptic(): () -> Unit {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        { context.vibrateStrong() }
    }
}

/**
 * Strong celebration pulse via [Vibrator] (needs `android.permission.VIBRATE`).
 * [android.view.View.performHapticFeedback] is often a no-op when system touch
 * haptics are off or the OEM ignores FLAG_IGNORE_GLOBAL_SETTING.
 */
private fun Context.vibrateStrong() {
    if (isTouchExplorationEnabled()) return
    val vibrator = vibratorOrNull() ?: return
    if (!vibrator.hasVibrator()) return

    val effect = VibrationEffect.createWaveform(
        /* timings */ longArrayOf(0, 40, 45, 70),
        /* amplitudes */ intArrayOf(0, 255, 0, 220),
        /* repeat */ -1,
    )
    vibrator.vibrate(effect)
}

private fun Context.vibratorOrNull(): Vibrator? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
    }

private fun Context.isTouchExplorationEnabled(): Boolean {
    val accessibilityManager =
        getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    return accessibilityManager?.isTouchExplorationEnabled ?: false
}
