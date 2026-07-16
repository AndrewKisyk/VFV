package com.wrapper.composechat.data

import app.cash.sqldelight.db.SqlDriver

/**
 * Ensures tables exist when opening an existing SQLite file (e.g. legacy Room `vfv.db`
 * that only had `user_profile`). SQLDelight [onCreate] is skipped for existing databases.
 */
object VfvDatabaseBootstrap {
    fun ensureSchema(driver: SqlDriver) {
        driver.execute(null, REQUIREMENTS_TABLE, 0)
        driver.execute(null, USER_PROFILE_TABLE, 0)
    }

    private const val REQUIREMENTS_TABLE = """
        CREATE TABLE IF NOT EXISTS requirements (
          id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          sex TEXT NOT NULL,
          vfv_group INTEGER NOT NULL,
          name TEXT NOT NULL,
          a12 TEXT NOT NULL,
          a13 TEXT NOT NULL,
          a14 TEXT NOT NULL,
          a15 TEXT NOT NULL,
          a16 TEXT NOT NULL,
          a17 TEXT NOT NULL,
          image_key TEXT NOT NULL,
          is_done INTEGER NOT NULL DEFAULT 0,
          UNIQUE(sex, name)
        )
    """

    private const val USER_PROFILE_TABLE = """
        CREATE TABLE IF NOT EXISTS user_profile (
          id INTEGER NOT NULL PRIMARY KEY,
          age TEXT NOT NULL,
          sex TEXT NOT NULL
        )
    """
}
