package com.wrapper.composechat.di

import com.wrapper.composechat.feature.auth.AuthViewModel
import com.wrapper.composechat.feature.dashboard.DefaultMainDashboardRepository
import com.wrapper.composechat.feature.dashboard.MainDashboardRepository
import com.wrapper.composechat.feature.dashboard.MainDashboardViewModel
import com.wrapper.composechat.feature.groups.VfvGroupsViewModel
import com.wrapper.composechat.feature.recommendations.VfvRecommendationsViewModel
import org.koin.dsl.module

val koinAppModule = module {
    single { AuthViewModel(get()) }
    single<MainDashboardRepository> { DefaultMainDashboardRepository(get(), get(), get()) }
    single { MainDashboardViewModel(get()) }
    factory { VfvGroupsViewModel(get(), get()) }
    factory { VfvRecommendationsViewModel(get()) }
}
