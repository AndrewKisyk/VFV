package com.wrapper.composechat.feature.auth

sealed interface AuthEffect {
    data object NavigateToHome : AuthEffect
}
