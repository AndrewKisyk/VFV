package com.wrapper.composechat.data

import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.auth.Sex
import com.wrapper.composechat.data.local.UserProfileEntity
import com.wrapper.composechat.data.local.VfvDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val database: VfvDatabase,
) : AuthRepository {

    private val dao get() = database.userProfileDao()

    override suspend fun hasSession(): Boolean = withContext(Dispatchers.IO) {
        val p = dao.getProfile() ?: return@withContext false
        val age = p.age
        val sex = p.sex
        (age + sex).isNotBlank() && age.isNotBlank() && sex.isNotBlank()
    }

    override suspend fun saveProfile(age: String, sex: Sex) = withContext(Dispatchers.IO) {
        val sexCode = when (sex) {
            Sex.Male -> "m"
            Sex.Female -> "f"
        }
        dao.upsertProfile(UserProfileEntity(age = age, sex = sexCode))
    }
}
