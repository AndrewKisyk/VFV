package com.wrapper.composechat.platform

import android.os.Build

actual fun isLiquidGlassAvailable(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
