package com.wrapper.composechat.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun rememberOpenExternalUrl(): (String) -> Unit {
    return remember {
        { url ->
            val nsUrl = NSURL.URLWithString(url) ?: return@remember
            UIApplication.sharedApplication.openURL(
                nsUrl,
                emptyMap<Any?, Any>(),
                null,
            )
        }
    }
}
