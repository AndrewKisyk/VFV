package com.wrapper.composechat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserProfileEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class VfvDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
}
