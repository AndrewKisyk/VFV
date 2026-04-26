package com.wrapper.composechat.platform

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp

actual fun isBackdropBlurAvailable(): Boolean = true

actual fun Modifier.optionalBackdropBlur(radiusDp: Float): Modifier = blur(radiusDp.dp)
