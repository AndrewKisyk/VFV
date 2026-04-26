package com.wrapper.composechat.platform

import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberComposeViewBitmapCapture(): () -> ImageBitmap? {
    val view = LocalView.current
    return remember(view) {
        { captureViewToImageBitmapInternal(view) }
    }
}

private fun captureViewToImageBitmapInternal(view: View): ImageBitmap? {
    val w = view.width
    val h = view.height
    if (w <= 0 || h <= 0) return null
    return try {
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        view.draw(canvas)
        bitmap.asImageBitmap()
    } catch (_: OutOfMemoryError) {
        null
    } catch (_: Exception) {
        null
    }
}
