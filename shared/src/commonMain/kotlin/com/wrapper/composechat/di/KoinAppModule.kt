package com.wrapper.composechat.di

import com.wrapper.composechat.feature.auth.AuthViewModel
import com.wrapper.composechat.feature.maindashboard.DefaultMainDashboardRepository
import com.wrapper.composechat.feature.maindashboard.MainDashboardRepository
import com.wrapper.composechat.feature.maindashboard.MainDashboardViewModel
import com.wrapper.composechat.feature.maindashboard.VfvGroupsViewModel
import com.wrapper.composechat.feature.maindashboard.VfvRecommendationsViewModel
import org.koin.dsl.module

val koinAppModule = module {
    single { AuthViewModel(get()) }
    single<MainDashboardRepository> { DefaultMainDashboardRepository(get(), get(), get()) }
    single { MainDashboardViewModel(get()) }
    factory { VfvGroupsViewModel(get(), get()) }
    factory { VfvRecommendationsViewModel(get()) }
}
