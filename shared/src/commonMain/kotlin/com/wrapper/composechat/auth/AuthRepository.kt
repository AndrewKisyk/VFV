package com.wrapper.composechat.auth

interface AuthRepository {

    /**
     * Same idea as [com.plstudio.a123.vfv.datadriven.PreferenceUtils.getToken]:
     * non-empty age and sex means the user already completed auth.
     */
    suspend fun hasSession(): Boolean

    suspend fun saveProfile(age: String, sex: Sex)
}
