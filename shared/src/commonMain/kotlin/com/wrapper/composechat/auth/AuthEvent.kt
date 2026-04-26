package com.wrapper.composechat.auth

sealed interface AuthEvent {
    data object ScreenStarted : AuthEvent
    data class AgeChanged(val value: String) : AuthEvent
    data class SexSelected(val sex: Sex) : AuthEvent
    data object ContinueClicked : AuthEvent
}
