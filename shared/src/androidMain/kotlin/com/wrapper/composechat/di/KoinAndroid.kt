package com.wrapper.composechat.di

import com.wrapper.composechat.data.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

val androidPlatformDataModule = module {
    single { DatabaseDriverFactory(androidContext()) }
}

fun initKoinAndroid(context: android.content.Context) {
    val koinApp = startKoin {
        androidContext(context)
        modules(koinAppModule, commonDataModule, androidPlatformDataModule)
    }
    koinApp.koin.seedDatabaseOnFirstLaunch()
}
