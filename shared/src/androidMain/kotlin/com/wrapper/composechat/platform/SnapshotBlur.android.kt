package com.wrapper.composechat.platform

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.max
import kotlin.math.roundToInt

actual fun ImageBitmap.withSnapshotBlur(radiusDp: Float): ImageBitmap {
    val radius = max(1, (radiusDp * 0.65f).roundToInt())
    val src = asAndroidBitmap()
    val blurred = stackBlur(src, radius)
    return blurred.asImageBitmap()
}

private fun stackBlur(bitmap: Bitmap, radius: Int): Bitmap {
    val w = bitmap.width
    val h = bitmap.height
    val output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(w * h)
    bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
    stackBlurHorizontal(pixels, w, h, radius)
    stackBlurVertical(pixels, w, h, radius)
    output.setPixels(pixels, 0, w, 0, 0, w, h)
    return output
}

private fun stackBlurHorizontal(pixels: IntArray, w: Int, h: Int, radius: Int) {
    val div = radius + radius + 1
    val temp = IntArray(w)
    for (y in 0 until h) {
        var sumR = 0
        var sumG = 0
        var sumB = 0
        var sumA = 0
        val rowOffset = y * w
        for (i in -radius..radius) {
            val px = pixels[rowOffset + i.coerceIn(0, w - 1)]
            sumA += px ushr 24 and 0xFF
            sumR += px ushr 16 and 0xFF
            sumG += px ushr 8 and 0xFF
            sumB += px and 0xFF
        }
        var stackIn = 0
        var stackOut = -radius
        for (x in 0 until w) {
            val outX = (x - radius).coerceAtLeast(0)
            val inX = (x + radius).coerceAtMost(w - 1)
            val outPx = pixels[rowOffset + outX]
            val inPx = pixels[rowOffset + inX]
            sumA += (inPx ushr 24 and 0xFF) - (outPx ushr 24 and 0xFF)
            sumR += (inPx ushr 16 and 0xFF) - (outPx ushr 16 and 0xFF)
            sumG += (inPx ushr 8 and 0xFF) - (outPx ushr 8 and 0xFF)
            sumB += (inPx and 0xFF) - (outPx and 0xFF)
            temp[x] =
                (sumA / div shl 24) or
                    (sumR / div shl 16) or
                    (sumG / div shl 8) or
                    (sumB / div)
            stackIn = inX
            stackOut = outX
        }
        System.arraycopy(temp, 0, pixels, rowOffset, w)
    }
}

private fun stackBlurVertical(pixels: IntArray, w: Int, h: Int, radius: Int) {
    val div = radius + radius + 1
    val temp = IntArray(h)
    for (x in 0 until w) {
        var sumR = 0
        var sumG = 0
        var sumB = 0
        var sumA = 0
        for (i in -radius..radius) {
            val px = pixels[i.coerceIn(0, h - 1) * w + x]
            sumA += px ushr 24 and 0xFF
            sumR += px ushr 16 and 0xFF
            sumG += px ushr 8 and 0xFF
            sumB += px and 0xFF
        }
        for (y in 0 until h) {
            val outY = (y - radius).coerceAtLeast(0)
            val inY = (y + radius).coerceAtMost(h - 1)
            val outPx = pixels[outY * w + x]
            val inPx = pixels[inY * w + x]
            sumA += (inPx ushr 24 and 0xFF) - (outPx ushr 24 and 0xFF)
            sumR += (inPx ushr 16 and 0xFF) - (outPx ushr 16 and 0xFF)
            sumG += (inPx ushr 8 and 0xFF) - (outPx ushr 8 and 0xFF)
            sumB += (inPx and 0xFF) - (outPx and 0xFF)
            temp[y] =
                (sumA / div shl 24) or
                    (sumR / div shl 16) or
                    (sumG / div shl 8) or
                    (sumB / div)
        }
        for (y in 0 until h) {
            pixels[y * w + x] = temp[y]
        }
    }
}
