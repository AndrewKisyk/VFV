package com.wrapper.composechat.feature.auth

import com.wrapper.composechat.auth.AuthEvent
import com.wrapper.composechat.auth.AuthRepository
import com.wrapper.composechat.auth.AuthState
import com.wrapper.composechat.auth.AuthValidation
import com.wrapper.composechat.auth.Sex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _effects = Channel<AuthEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onCleared() {
        scope.cancel()
    }

    /**
     * After change-age: empty the form and drop a stale [AuthEffect.NavigateToHome]
     * that [ScreenStarted] may have queued while a session still existed.
     */
    fun resetForNewSession() {
        _state.value = AuthState()
        while (_effects.tryReceive().isSuccess) {
            // drop queued home navigation
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.ScreenStarted -> {
                // Session routing is owned by RootNavHost. Auto-skip here queued
                // NavigateToHome before AuthScreen collected it, so change-age
                // immediately bounced back to the dashboard.
            }
            is AuthEvent.AgeChanged -> {
                _state.value = _state.value.copy(
                    ageInput = event.value,
                    ageError = AuthValidation.shouldShowAgeError(event.value),
                )
            }
            is AuthEvent.SexSelected -> {
                _state.value = _state.value.copy(selectedSex = event.sex, sexError = false)
            }
            is AuthEvent.ContinueClicked -> {
                val s = _state.value
                val v = AuthValidation.validate(s.ageInput, s.selectedSex)
                // Button should already be disabled when invalid; keep guard for safety.
                if (!v.isValid) {
                    _state.value = s.copy(
                        ageError = AuthValidation.shouldShowAgeError(s.ageInput),
                        sexError = v.sexError,
                    )
                    return
                }
                scope.launch {
                    val sex = s.selectedSex ?: return@launch
                    repository.saveProfile(s.ageInput.trim(), sex)
                    _effects.send(AuthEffect.NavigateToHome)
                }
            }
        }
    }
}
