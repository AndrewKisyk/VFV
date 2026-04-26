package com.wrapper.composechat.feature.auth

import kotlin.math.sin

/**
 * Shared wobble for blob path edges, radial orbs, and auth shape decorations.
 * Same three-sine sum everywhere so motion feels coherent.
 */
internal fun blobMagnitude(phase: Float, freq: Float, t: Float): Float =
    0.11f * sin(2f * phase * freq + 3f * t) +
        0.07f * sin(5f * phase - 2.5f * t) +
        0.04f * sin(7f * phase * 0.5f + t * 1.2f)
