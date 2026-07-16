package com.wrapper.composechat.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.wrapper.composechat.db.VfvSqlDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver = NativeSqliteDriver(VfvSqlDatabase.Schema, "vfv.db")
        VfvDatabaseBootstrap.ensureSchema(driver)
        return driver
    }
}
