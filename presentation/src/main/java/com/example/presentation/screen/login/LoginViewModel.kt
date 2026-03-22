package com.example.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun processCommand(command: LoginCommand) {
        when (command) {
            is LoginCommand.InputEmail -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        previousState.copy(email = command.value)
                    }
                }
            }

            is LoginCommand.InputPassword -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        previousState.copy(password = command.value)
                    }
                }
            }

            LoginCommand.Submit -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        loginUseCase(
                            email = previousState.email,
                            password = previousState.password
                        ).fold(
                            onSuccess = {
                                previousState
                            },
                            onFailure = {
                                previousState.copy(error = it.message)
                            }
                        )
                    }
                }
            }

            LoginCommand.ChangePasswordVisibility -> {
                _state.update { previousState ->
                    previousState.copy(passwordVisibility = !previousState.passwordVisibility)
                }
            }
        }
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val passwordVisibility: Boolean = false,
    val error: String? = null
) {
    val isSubmitButtonEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

sealed interface LoginCommand {
    data class InputEmail(
        val value: String
    ) : LoginCommand

    data class InputPassword(
        val value: String
    ) : LoginCommand

    data object ChangePasswordVisibility : LoginCommand

    data object Submit : LoginCommand
}