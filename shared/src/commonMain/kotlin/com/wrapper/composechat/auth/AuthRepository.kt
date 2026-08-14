package com.wrapper.composechat.auth

data class UserProfile(
    val age: String,
    val sex: String,
)

interface AuthRepository {

    /**
     * Same idea as [com.plstudio.a123.vfv.datadriven.PreferenceUtils.getToken]:
     * non-empty age and sex means the user already completed auth.
     */
    suspend fun hasSession(): Boolean

    suspend fun getProfile(): UserProfile?

    suspend fun saveProfile(age: String, sex: Sex)

    /**
     * Same as [com.plstudio.a123.vfv.fragments.MenuListFragment.makeUserDataEmpty]:
     * empty age/sex so [hasSession] is false and the auth screen is shown again.
     */
    suspend fun clearProfile()
}
