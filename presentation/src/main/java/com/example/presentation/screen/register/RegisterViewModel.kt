package com.example.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun processCommand(command: RegisterCommand) {
        when (command) {
            is RegisterCommand.InputEmail -> {
                _state.update { previousState ->
                    previousState.copy(email = command.value)
                }
            }

            is RegisterCommand.InputPassword -> {
                _state.update { previousState ->
                    previousState.copy(password = command.value)
                }
            }

            is RegisterCommand.InputBio -> {
                _state.update { previousState ->
                    previousState.copy(bio = command.value)
                }
            }
            is RegisterCommand.InputDisplayName -> {
                _state.update { previousState ->
                    previousState.copy(displayName = command.value)
                }
            }
            is RegisterCommand.InputPhone -> {
                _state.update { previousState ->
                    previousState.copy(phone = command.value)
                }
            }

            RegisterCommand.Submit -> {
                var response: Result<Unit>? = null

                viewModelScope.launch {
                    response = registerUseCase(
                        email = _state.value.email,
                        password = _state.value.password,
                        displayName = state.value.displayName,
                        phone = state.value.password,
                        bio = state.value.bio
                    )
                }

                _state.update { previousState ->
                    response?.fold(
                        onSuccess = {
                            previousState
                        },
                        onFailure = {
                            previousState.copy(error = it.message)
                        }
                    ) ?: previousState
                }
            }

            RegisterCommand.ChangePasswordVisibility -> {
                _state.update { previousState ->
                    previousState.copy(passwordVisibility = !previousState.passwordVisibility)
                }
            }
        }
    }
}

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val phone: String = "",
    val bio: String = "",
    val passwordVisibility: Boolean = false,
    val error: String? = null
) {
    val isSubmitButtonEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && displayName.isNotBlank() && phone.isNotBlank() && bio.isNotBlank()
}

sealed interface RegisterCommand {
    data class InputEmail(val value: String) : RegisterCommand

    data class InputPassword(val value: String) : RegisterCommand
    data class InputDisplayName(val value: String) : RegisterCommand
    data class InputPhone(val value: String) : RegisterCommand
    data class InputBio(val value: String) : RegisterCommand

    data object ChangePasswordVisibility : RegisterCommand

    data object Submit : RegisterCommand
}