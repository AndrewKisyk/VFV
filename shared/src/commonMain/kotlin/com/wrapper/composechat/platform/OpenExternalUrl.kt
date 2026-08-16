package com.wrapper.composechat.platform

import androidx.compose.runtime.Composable

/** Opens an https/http URL in the system browser. */
@Composable
expect fun rememberOpenExternalUrl(): (String) -> Unit
