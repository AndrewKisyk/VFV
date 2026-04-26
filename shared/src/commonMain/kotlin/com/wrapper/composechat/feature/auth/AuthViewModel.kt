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

    init {
        onEvent(AuthEvent.ScreenStarted)
    }

    fun onCleared() {
        scope.cancel()
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.ScreenStarted -> {
                scope.launch {
                    if (repository.hasSession()) {
                        _effects.send(AuthEffect.NavigateToHome)
                    }
                }
            }
            is AuthEvent.AgeChanged -> {
                _state.value = _state.value.copy(ageInput = event.value, ageError = false)
            }
            is AuthEvent.SexSelected -> {
                _state.value = _state.value.copy(selectedSex = event.sex, sexError = false)
            }
            is AuthEvent.ContinueClicked -> {
                val s = _state.value
                val v = AuthValidation.validate(s.ageInput, s.selectedSex)
                _state.value = s.copy(ageError = v.ageError, sexError = v.sexError)
                if (!v.isValid) return
                scope.launch {
                    val sex = s.selectedSex ?: return@launch
                    repository.saveProfile(s.ageInput.trim(), sex)
                    _effects.send(AuthEffect.NavigateToHome)
                }
            }
        }
    }
}
