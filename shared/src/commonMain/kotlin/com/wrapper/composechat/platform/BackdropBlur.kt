package com.wrapper.composechat.platform

import androidx.compose.ui.Modifier

/** When true, [Modifier.blur] is supported for backdrops (API 31+ on Android). */
expect fun isBackdropBlurAvailable(): Boolean

expect fun Modifier.optionalBackdropBlur(radiusDp: Float): Modifier
