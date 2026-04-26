package com.wrapper.composechat.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wrapper.composechat.navigation.RootNavHost
import com.wrapper.composechat.ui.theme.VfvMultiplatformTheme

@Composable
fun VfvMultiplatformAppRoot() {
    VfvMultiplatformTheme {
        RootNavHost(modifier = Modifier.fillMaxSize())
    }
}
