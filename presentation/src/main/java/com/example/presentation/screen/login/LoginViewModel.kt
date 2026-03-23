package com.example.presentation.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.LoginUseCase
import com.example.presentation.mapper.toUserMessage
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

    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Input -> {
                _state.update { previousState ->
                    when (intent.typeField) {
                        TypeField.EMAIL -> previousState.copy(email = intent.value)
                        TypeField.PASSWORD -> previousState.copy(password = intent.value)
                    }
                }
            }

            LoginIntent.Submit -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        loginUseCase(
                            email = previousState.email,
                            password = previousState.password
                        ).fold(
                            onSuccess = { previousState },
                            onFailure = {
                                Log.e("App", it.toString())
                                previousState.copy(error = it.toUserMessage())
                            }
                        )
                    }
                }
            }

            LoginIntent.ChangePasswordVisibility -> {
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

sealed interface LoginIntent {
    data class Input(
        val typeField: TypeField,
        val value: String
    ) : LoginIntent

    data object ChangePasswordVisibility : LoginIntent

    data object Submit : LoginIntent
}

enum class TypeField { EMAIL, PASSWORD }