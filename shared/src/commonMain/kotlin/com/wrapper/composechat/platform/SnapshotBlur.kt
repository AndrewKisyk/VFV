package com.wrapper.composechat.platform

import androidx.compose.ui.graphics.ImageBitmap

/** CPU/GPU blur for a captured snapshot (used when live [Modifier.blur] is unavailable). */
expect fun ImageBitmap.withSnapshotBlur(radiusDp: Float): ImageBitmap
