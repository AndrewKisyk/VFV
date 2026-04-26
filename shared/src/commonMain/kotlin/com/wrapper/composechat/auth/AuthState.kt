package com.wrapper.composechat.auth

data class AuthState(
    val ageInput: String = "",
    val selectedSex: Sex? = null,
    val ageError: Boolean = false,
    val sexError: Boolean = false,
)
