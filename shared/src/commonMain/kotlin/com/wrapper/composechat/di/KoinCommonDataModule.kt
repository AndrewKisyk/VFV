package com.wrapper.composechat.di

import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.data.DatabaseDriverFactory
import com.wrapper.composechat.data.SqlDelightAuthRepository
import com.wrapper.composechat.data.SqlDelightRequirementsRepository
import com.wrapper.composechat.data.requirements.RequirementsRepository
import com.wrapper.composechat.db.VfvSqlDatabase
import org.koin.dsl.module

val commonDataModule = module {
    single { VfvSqlDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<AuthRepository> { SqlDelightAuthRepository(get()) }
    single<RequirementsRepository> { SqlDelightRequirementsRepository(get()) }
}
