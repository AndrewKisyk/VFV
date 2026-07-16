package com.wrapper.composechat.di

import com.wrapper.composechat.data.DatabaseDriverFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosPlatformDataModule = module {
    single { DatabaseDriverFactory() }
}

private var koinIosStarted = false

fun initKoinIos() {
    if (koinIosStarted) return
    koinIosStarted = true
    val koinApp = startKoin {
        modules(koinAppModule, commonDataModule, iosPlatformDataModule)
    }
    koinApp.koin.seedDatabaseOnFirstLaunch()
}
