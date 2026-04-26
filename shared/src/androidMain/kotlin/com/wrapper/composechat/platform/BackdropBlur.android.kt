package com.wrapper.composechat.platform

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp

actual fun isBackdropBlurAvailable(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

actual fun Modifier.optionalBackdropBlur(radiusDp: Float): Modifier =
    if (isBackdropBlurAvailable()) blur(radiusDp.dp) else this
