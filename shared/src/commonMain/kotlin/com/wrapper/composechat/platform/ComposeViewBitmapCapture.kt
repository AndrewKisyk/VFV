package com.wrapper.composechat.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Returns a [capture] function that, when invoked, copies the current Compose host view to an
 * [ImageBitmap] (or null on failure / unsupported). Call **before** showing an overlay that should
 * not be part of the capture.
 */
@Composable
expect fun rememberComposeViewBitmapCapture(): () -> ImageBitmap?
