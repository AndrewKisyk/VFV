package com.wrapper.composechat.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap

@Composable
actual fun rememberComposeViewBitmapCapture(): () -> ImageBitmap? = remember { { null } }
