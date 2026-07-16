package com.wrapper.composechat.data

import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.auth.Sex
import com.wrapper.composechat.auth.UserProfile
import com.wrapper.composechat.db.VfvSqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightAuthRepository(
    private val database: VfvSqlDatabase,
) : AuthRepository {

    override suspend fun hasSession(): Boolean = getProfile() != null

    override suspend fun getProfile(): UserProfile? = withContext(Dispatchers.Default) {
        database.userProfileQueries.selectProfile().executeAsOneOrNull()?.let { row ->
            if (row.age.isBlank() || row.sex.isBlank()) return@withContext null
            UserProfile(age = row.age, sex = row.sex)
        }
    }

    override suspend fun saveProfile(age: String, sex: Sex) = withContext(Dispatchers.Default) {
        val sexCode = when (sex) {
            Sex.Male -> "m"
            Sex.Female -> "f"
        }
        database.userProfileQueries.upsertProfile(age = age, sex = sexCode)
    }
}
