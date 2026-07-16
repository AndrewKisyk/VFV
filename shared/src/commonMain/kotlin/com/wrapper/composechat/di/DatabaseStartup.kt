package com.wrapper.composechat.di

import com.wrapper.composechat.data.requirements.RequirementsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.Koin

private val startupScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

fun Koin.seedDatabaseOnFirstLaunch() {
    startupScope.launch {
        get<RequirementsRepository>().ensureSeeded()
    }
}
