package com.wrapper.composechat.platform

import androidx.compose.runtime.Composable

/** Device vibration that works outside a pointer event (unlike [androidx.compose.ui.platform.LocalHapticFeedback]). */
@Composable
expect fun rememberCelebrationHaptic(): () -> Unit
