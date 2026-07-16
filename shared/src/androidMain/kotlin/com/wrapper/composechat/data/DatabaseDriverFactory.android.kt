package com.wrapper.composechat.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.wrapper.composechat.db.VfvSqlDatabase

actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver {
        val driver = AndroidSqliteDriver(VfvSqlDatabase.Schema, context, "vfv.db")
        VfvDatabaseBootstrap.ensureSchema(driver)
        return driver
    }
}
