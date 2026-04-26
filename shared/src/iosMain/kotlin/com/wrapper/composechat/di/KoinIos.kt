package com.wrapper.composechat.di

import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.data.IosAuthRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosDataModule = module {
    single<AuthRepository> { IosAuthRepository() }
}

private var koinIosStarted = false

fun initKoinIos() {
    if (koinIosStarted) return
    koinIosStarted = true
    startKoin {
        modules(koinAppModule, iosDataModule)
    }
}
