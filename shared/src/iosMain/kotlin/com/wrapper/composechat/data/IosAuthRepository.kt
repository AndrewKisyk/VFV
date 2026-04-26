package com.wrapper.composechat.data

import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.auth.Sex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Placeholder until iOS local persistence is added (e.g. SQLDelight / settings). */
class IosAuthRepository : AuthRepository {
    override suspend fun hasSession(): Boolean = false
    override suspend fun saveProfile(age: String, sex: Sex) = withContext(Dispatchers.Default) { }
}
