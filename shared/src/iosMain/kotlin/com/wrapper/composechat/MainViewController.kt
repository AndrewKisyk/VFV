package com.wrapper.composechat

import androidx.compose.ui.window.ComposeUIViewController
import com.wrapper.composechat.di.initKoinIos
import com.wrapper.composechat.ui.VfvMultiplatformAppRoot

/**
 * iOS host: add this to your Swift `UIHostingController` / root after linking the `shared` framework.
 */
fun MainViewController() = ComposeUIViewController {
    initKoinIos()
    VfvMultiplatformAppRoot()
}
