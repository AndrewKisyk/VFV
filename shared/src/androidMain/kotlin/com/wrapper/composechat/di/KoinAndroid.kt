package com.wrapper.composechat.di

import androidx.room.Room
import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.data.AuthRepositoryImpl
import com.wrapper.composechat.data.local.VfvDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

val androidDataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            VfvDatabase::class.java,
            "vfv.db",
        ).build()
    }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}

fun initKoinAndroid(context: android.content.Context) {
    startKoin {
        androidContext(context)
        modules(koinAppModule, androidDataModule)
    }
}
