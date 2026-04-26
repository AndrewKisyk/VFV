package com.wrapper.composechat.di

import com.wrapper.composechat.feature.auth.AuthViewModel
import com.wrapper.composechat.feature.maindashboard.DefaultMainDashboardRepository
import com.wrapper.composechat.feature.maindashboard.MainDashboardRepository
import com.wrapper.composechat.feature.maindashboard.MainDashboardViewModel
import org.koin.dsl.module

val koinAppModule = module {
    single { AuthViewModel(get()) }
    single<MainDashboardRepository> { DefaultMainDashboardRepository() }
    single { MainDashboardViewModel(get()) }
}
